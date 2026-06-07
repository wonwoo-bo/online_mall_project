package com.mall.module.product.service.impl;

import com.mall.common.PageResult;
import com.mall.module.product.entity.*;
import com.mall.module.product.mapper.*;
import com.mall.module.product.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ReviewImageMapper reviewImageMapper;

    @Autowired
    private ReviewReplyMapper reviewReplyMapper;

    @Autowired
    private ReviewAppendMapper reviewAppendMapper;

    @Autowired
    private ReviewReportMapper reviewReportMapper;

    @Autowired
    private ReviewAppendReplyMapper reviewAppendReplyMapper;

    @Autowired
    private ReviewExplanationMapper reviewExplanationMapper;

    @Autowired
    private ReviewTopMapper reviewTopMapper;

    @Autowired
    private ReviewAppealMapper reviewAppealMapper;

    @Override
    public PageResult<Map<String, Object>> getProductReviews(Integer productId, Integer rating,
                                                              Integer hasImage, int page, int size) {
        int offset = (page - 1) * size;
        System.out.println("DEBUG ReviewService: productId=" + productId + ", rating=" + rating + ", hasImage=" + hasImage);
        List<Review> reviews = reviewMapper.selectByProductId(productId, rating, hasImage, offset, size);
        System.out.println("DEBUG ReviewService: found " + reviews.size() + " reviews");
        for (Review r : reviews) {
            System.out.println("DEBUG Review: id=" + r.getId() + ", productId=" + r.getProductId() + ", content=" + r.getContent().substring(0, Math.min(20, r.getContent().length())));
        }
        int total = reviewMapper.countByProductIdFiltered(productId, rating, hasImage);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> item = new HashMap<>();
            item.put("review", review);

            // 查询评价图片
            List<ReviewImage> images = reviewImageMapper.selectByReviewId(review.getId());
            item.put("images", images);

            // 查询商家回复
            ReviewReply reply = reviewReplyMapper.selectByReviewId(review.getId());
            item.put("reply", reply);

            // 查询追评
            ReviewAppend append = reviewAppendMapper.selectByReviewId(review.getId());
            item.put("append", append);

            list.add(item);
        }

        return new PageResult<>(total, list, page, size);
    }

    @Override
    public Map<String, Object> getReviewStats(Integer productId) {
        Map<String, Object> stats = new HashMap<>();
        int total = reviewMapper.countByProductId(productId);
        double avgRating = reviewMapper.avgRating(productId);
        int fiveStar = reviewMapper.countByProductIdAndRating(productId, 5);
        int fourStar = reviewMapper.countByProductIdAndRating(productId, 4);
        int threeStar = reviewMapper.countByProductIdAndRating(productId, 3);
        int twoStar = reviewMapper.countByProductIdAndRating(productId, 2);
        int oneStar = reviewMapper.countByProductIdAndRating(productId, 1);
        int hasImageCount = reviewMapper.countWithImage(productId);

        stats.put("total", total);
        stats.put("avgRating", avgRating);
        stats.put("fiveStar", fiveStar);
        stats.put("fourStar", fourStar);
        stats.put("threeStar", threeStar);
        stats.put("twoStar", twoStar);
        stats.put("oneStar", oneStar);
        stats.put("hasImageCount", hasImageCount);

        return stats;
    }

    @Override
    public void submitReview(Review review, List<String> imageUrls) {
        review.setCreateTime(LocalDateTime.now());
        review.setLikeCount(0);
        review.setHasAppend(0);
        reviewMapper.insert(review);

        // 批量插入评价图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setReviewId(review.getId());
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setCreateTime(LocalDateTime.now());
                reviewImageMapper.insert(reviewImage);
            }
        }
    }

    @Override
    public void appendReview(Integer reviewId, String content) {
        // 重复追评校验
        ReviewAppend existing = reviewAppendMapper.selectByReviewId(reviewId);
        if (existing != null) {
            throw new RuntimeException("该评价已追评，不能重复追评");
        }
        // 查询原评价获取merchantId
        Review review = reviewMapper.selectById(reviewId);
        ReviewAppend append = new ReviewAppend();
        append.setReviewId(reviewId);
        append.setMerchantId(review != null ? review.getMerchantId() : 0);
        append.setContent(content);
        append.setCreateTime(LocalDateTime.now());
        reviewAppendMapper.insert(append);

        // 更新review的has_append=1
        reviewMapper.updateHasAppend(reviewId);
    }

    @Override
    public void likeReview(Integer id) {
        reviewMapper.updateLikeCount(id);
    }

    @Override
    public List<Review> getReviewsByMerchant(Integer merchantId) {
        return reviewMapper.selectByMerchantId(merchantId);
    }

    @Override
    public void replyReview(Integer id, String reply) {
        ReviewReply reviewReply = new ReviewReply();
        reviewReply.setReviewId(id);
        reviewReply.setContent(reply);
        reviewReply.setCreateTime(LocalDateTime.now());
        reviewReplyMapper.insert(reviewReply);
    }

    @Override
    public PageResult<Map<String, Object>> getMerchantReviewList(Integer merchantId, Integer rating,
                                                                   Integer hasReply, String startTime,
                                                                   String endTime, int page, int size) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> list = reviewMapper.selectMerchantReviewList(merchantId, rating, hasReply,
                                                                                startTime, endTime, offset, size);
        int total = reviewMapper.countMerchantReviewList(merchantId, rating, hasReply, startTime, endTime);

        // 为每个评价补充图片和追评信息
        for (Map<String, Object> item : list) {
            Integer reviewId = (Integer) item.get("id");
            // 查询评价图片
            List<ReviewImage> images = reviewImageMapper.selectByReviewId(reviewId);
            item.put("images", images);
            // 查询追评
            ReviewAppend append = reviewAppendMapper.selectByReviewId(reviewId);
            item.put("append", append);
            // 查询举报状态
            ReviewReport report = reviewReportMapper.selectByReviewIdAndMerchantId(reviewId, merchantId);
            item.put("hasReport", report != null);
            // 查询追评回复
            if (append != null) {
                ReviewAppendReply appendReply = reviewAppendReplyMapper.selectByAppendId(append.getId());
                item.put("appendReply", appendReply);
            }
            // 查询差评解释
            ReviewExplanation explanation = reviewExplanationMapper.selectByReviewId(reviewId);
            item.put("explanation", explanation);
            // 查询申诉状态
            ReviewAppeal appeal = reviewAppealMapper.selectByReviewId(reviewId);
            item.put("hasAppeal", appeal != null);
        }

        return new PageResult<>(total, list, page, size);
    }

    @Override
    public Map<String, Object> getMerchantReviewStats(Integer merchantId) {
        return reviewMapper.selectMerchantReviewStats(merchantId);
    }

    @Override
    @Transactional
    public void merchantReplyReview(Integer reviewId, Integer merchantId, String replyContent) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验是否已经回复过
        ReviewReply existingReply = reviewReplyMapper.selectByReviewId(reviewId);
        if (existingReply != null) {
            throw new RuntimeException("该评价已回复，请勿重复回复");
        }

        // 校验回复内容不能为空
        if (replyContent == null || replyContent.trim().isEmpty()) {
            throw new RuntimeException("回复内容不能为空");
        }

        // 保存回复
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setMerchantId(merchantId);
        reply.setContent(replyContent);
        reply.setCreateTime(LocalDateTime.now());
        reviewReplyMapper.insert(reply);

        // 同步更新review主表的merchant_reply和reply_time字段，实现联动
        reviewMapper.updateMerchantReply(reviewId, replyContent, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void reportReview(Integer reviewId, Integer merchantId, String reason, String description) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验举报原因不能为空
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("举报原因不能为空");
        }

        // 保存举报记录
        ReviewReport report = new ReviewReport();
        report.setReviewId(reviewId);
        report.setMerchantId(merchantId);
        report.setReason(reason);
        report.setDescription(description);
        report.setStatus(0); // 0-待审核
        report.setCreateTime(LocalDateTime.now());
        reviewReportMapper.insert(report);
    }

    @Override
    @Transactional
    public void replyAppend(Integer appendId, Integer reviewId, Integer merchantId, String content) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验追评是否已回复
        ReviewAppendReply existingReply = reviewAppendReplyMapper.selectByAppendId(appendId);
        if (existingReply != null) {
            throw new RuntimeException("该追评已回复，请勿重复回复");
        }

        // 校验回复内容不能为空
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("回复内容不能为空");
        }

        // 保存追评回复
        ReviewAppendReply reply = new ReviewAppendReply();
        reply.setAppendId(appendId);
        reply.setReviewId(reviewId);
        reply.setMerchantId(merchantId);
        reply.setContent(content);
        reply.setCreateTime(LocalDateTime.now());
        reviewAppendReplyMapper.insert(reply);
    }

    @Override
    @Transactional
    public void saveExplanation(Integer reviewId, Integer merchantId, String content) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验是否为差评（1-3星）
        if (review.getRating() > 3) {
            throw new RuntimeException("只有差评才能添加解释");
        }

        // 校验解释内容不能为空
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("解释内容不能为空");
        }

        // 检查是否已存在解释
        ReviewExplanation existingExplanation = reviewExplanationMapper.selectByReviewId(reviewId);
        if (existingExplanation != null) {
            // 更新现有解释
            existingExplanation.setContent(content);
            reviewExplanationMapper.update(existingExplanation);
        } else {
            // 新增解释
            ReviewExplanation explanation = new ReviewExplanation();
            explanation.setReviewId(reviewId);
            explanation.setMerchantId(merchantId);
            explanation.setContent(content);
            explanation.setEditCount(0);
            explanation.setCreateTime(LocalDateTime.now());
            reviewExplanationMapper.insert(explanation);
        }
    }

    @Override
    @Transactional
    public void topReview(Integer reviewId, Integer merchantId, Integer productId) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验是否为优质评价（带图且5星）
        if (review.getRating() != 5) {
            throw new RuntimeException("只有5星好评才能置顶");
        }

        List<ReviewImage> images = reviewImageMapper.selectByReviewId(reviewId);
        if (images == null || images.isEmpty()) {
            throw new RuntimeException("只有带图评价才能置顶");
        }

        // 更新review的置顶状态
        reviewMapper.updateTopStatus(reviewId, 1);

        // 保存置顶记录
        ReviewTop top = new ReviewTop();
        top.setReviewId(reviewId);
        top.setMerchantId(merchantId);
        top.setProductId(productId);
        reviewTopMapper.insert(top);
    }

    @Override
    @Transactional
    public void cancelTopReview(Integer reviewId, Integer merchantId, Integer productId) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 更新review的置顶状态
        reviewMapper.updateTopStatus(reviewId, 0);

        // 删除置顶记录
        reviewTopMapper.delete(reviewId, productId);
    }

    @Override
    @Transactional
    public void submitAppeal(Integer reviewId, Integer merchantId, String reason, String description, String evidenceUrls) {
        // 校验评价是否属于该商家
        Review review = reviewMapper.selectByIdAndMerchantId(reviewId, merchantId);
        if (review == null) {
            throw new RuntimeException("评价不存在或无权操作");
        }

        // 校验是否为差评（1-3星）
        if (review.getRating() > 3) {
            throw new RuntimeException("只有差评才能申诉");
        }

        // 校验申诉原因不能为空
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("申诉原因不能为空");
        }

        // 检查是否已申诉
        ReviewAppeal existingAppeal = reviewAppealMapper.selectByReviewId(reviewId);
        if (existingAppeal != null) {
            throw new RuntimeException("该评价已申诉，请勿重复申诉");
        }

        // 保存申诉记录
        ReviewAppeal appeal = new ReviewAppeal();
        appeal.setReviewId(reviewId);
        appeal.setMerchantId(merchantId);
        appeal.setReason(reason);
        appeal.setDescription(description);
        appeal.setEvidenceUrls(evidenceUrls);
        appeal.setStatus(0); // 0-待审核
        appeal.setCreateTime(LocalDateTime.now());
        reviewAppealMapper.insert(appeal);
    }

    @Override
    public Map<String, Object> getAdvancedReviewStats(Integer merchantId) {
        return reviewMapper.selectAdvancedReviewStats(merchantId);
    }

    @Override
    public List<Map<String, Object>> getReviewTrend(Integer merchantId, Integer days) {
        return reviewMapper.selectReviewTrend(merchantId, days);
    }

    @Override
    public List<Map<String, Object>> getAppealList(Integer merchantId) {
        List<ReviewAppeal> appealList = reviewAppealMapper.selectByMerchantId(merchantId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ReviewAppeal appeal : appealList) {
            Map<String, Object> item = new HashMap<>();
            item.put("appeal", appeal);
            // 查询对应的评价信息
            Review review = reviewMapper.selectByIdAndMerchantId(appeal.getReviewId(), merchantId);
            if (review != null) {
                item.put("reviewContent", review.getContent());
                item.put("rating", review.getRating());
            }
            result.add(item);
        }
        return result;
    }
}
