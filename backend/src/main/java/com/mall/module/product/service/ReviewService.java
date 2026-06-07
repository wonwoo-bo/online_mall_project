package com.mall.module.product.service;

import com.mall.common.PageResult;
import com.mall.module.product.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    /**
     * 获取评价列表（含图片、回复、追评）
     */
    PageResult<Map<String, Object>> getProductReviews(Integer productId, Integer rating,
                                                       Integer hasImage, int page, int size);

    /**
     * 获取评价统计
     */
    Map<String, Object> getReviewStats(Integer productId);

    /**
     * 发布评价
     */
    void submitReview(Review review, List<String> imageUrls);

    /**
     * 追评
     */
    void appendReview(Integer reviewId, String content);

    /**
     * 点赞
     */
    void likeReview(Integer id);

    /**
     * 获取商家评价列表
     */
    List<Review> getReviewsByMerchant(Integer merchantId);

    /**
     * 回复评价
     */
    void replyReview(Integer id, String reply);

    /**
     * 获取商家评价列表（带筛选）
     */
    PageResult<Map<String, Object>> getMerchantReviewList(Integer merchantId, Integer rating,
                                                           Integer hasReply, String startTime,
                                                           String endTime, int page, int size);

    /**
     * 获取商家评价统计
     */
    Map<String, Object> getMerchantReviewStats(Integer merchantId);

    /**
     * 商家回复评价（带校验）
     */
    void merchantReplyReview(Integer reviewId, Integer merchantId, String replyContent);

    /**
     * 举报违规评价
     */
    void reportReview(Integer reviewId, Integer merchantId, String reason, String description);

    /**
     * 回复追评
     */
    void replyAppend(Integer appendId, Integer reviewId, Integer merchantId, String content);

    /**
     * 保存/编辑差评解释
     */
    void saveExplanation(Integer reviewId, Integer merchantId, String content);

    /**
     * 置顶评价
     */
    void topReview(Integer reviewId, Integer merchantId, Integer productId);

    /**
     * 取消置顶评价
     */
    void cancelTopReview(Integer reviewId, Integer merchantId, Integer productId);

    /**
     * 提交恶意差评申诉
     */
    void submitAppeal(Integer reviewId, Integer merchantId, String reason, String description, String evidenceUrls);

    /**
     * 获取进阶评价统计
     */
    Map<String, Object> getAdvancedReviewStats(Integer merchantId);

    /**
     * 获取评价趋势
     */
    List<Map<String, Object>> getReviewTrend(Integer merchantId, Integer days);

    /**
     * 获取申诉记录列表
     */
    List<Map<String, Object>> getAppealList(Integer merchantId);
}
