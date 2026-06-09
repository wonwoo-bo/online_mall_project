package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/reviews")
public class MerchantReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getReviewStats(@RequestAttribute("userId") Integer merchantId) {
        Map<String, Object> stats = reviewService.getMerchantReviewStats(merchantId);
        return Result.success(stats);
    }

    @GetMapping
    public Result<PageResult<Map<String, Object>>> getReviewList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer hasReply,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<Map<String, Object>> result = reviewService.getMerchantReviewList(
            merchantId, rating, hasReply, startTime, endTime, page, pageSize);
        return Result.success(result);
    }

    @PostMapping("/{id}/reply")
    public Result<String> replyReview(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, String> data) {
        String replyContent = data.get("content");
        reviewService.merchantReplyReview(id, merchantId, replyContent);
        return Result.success("回复成功");
    }

    @PostMapping("/{id}/report")
    public Result<String> reportReview(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, String> data) {
        String reason = data.get("reason");
        String description = data.get("description");
        reviewService.reportReview(id, merchantId, reason, description);
        return Result.success("举报提交成功");
    }

    @GetMapping("/stats/advanced")
    public Result<Map<String, Object>> getAdvancedReviewStats(@RequestAttribute("userId") Integer merchantId) {
        Map<String, Object> stats = reviewService.getAdvancedReviewStats(merchantId);
        return Result.success(stats);
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getReviewTrend(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> trend = reviewService.getReviewTrend(merchantId, days);
        return Result.success(trend);
    }

    @GetMapping("/appeals")
    public Result<List<Map<String, Object>>> getAppealList(@RequestAttribute("userId") Integer merchantId) {
        List<Map<String, Object>> appealList = reviewService.getAppealList(merchantId);
        return Result.success(appealList);
    }

    @PostMapping("/append/{appendId}/reply")
    public Result<String> replyAppend(
            @PathVariable Integer appendId,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, Object> data) {
        Integer reviewId = (Integer) data.get("reviewId");
        String content = (String) data.get("content");
        reviewService.replyAppend(appendId, reviewId, merchantId, content);
        return Result.success("回复成功");
    }

    @PostMapping("/{id}/explanation")
    public Result<String> saveExplanation(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, String> data) {
        String content = data.get("content");
        reviewService.saveExplanation(id, merchantId, content);
        return Result.success("保存成功");
    }

    @PostMapping("/{id}/top")
    public Result<String> topReview(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, Object> data) {
        Integer productId = (Integer) data.get("productId");
        reviewService.topReview(id, merchantId, productId);
        return Result.success("置顶成功");
    }

    @PostMapping("/{id}/cancel-top")
    public Result<String> cancelTopReview(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, Object> data) {
        Integer productId = (Integer) data.get("productId");
        reviewService.cancelTopReview(id, merchantId, productId);
        return Result.success("取消置顶成功");
    }

    @PostMapping("/{id}/appeal")
    public Result<String> submitAppeal(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, String> data) {
        String reason = data.get("reason");
        String description = data.get("description");
        String evidenceUrls = data.get("evidenceUrls");
        reviewService.submitAppeal(id, merchantId, reason, description, evidenceUrls);
        return Result.success("申诉提交成功");
    }
}
