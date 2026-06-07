package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.entity.*;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.product.mapper.ProductImageMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.mapper.ReviewAppendMapper;
import com.mall.module.product.mapper.ReviewImageMapper;
import com.mall.module.product.mapper.ReviewLikeMapper;
import com.mall.module.product.mapper.ReviewMapper;
import com.mall.module.product.service.BrowseHistoryService;
import com.mall.module.product.service.CategoryService;
import com.mall.module.product.service.FavoriteService;
import com.mall.module.product.service.ProductService;
import com.mall.module.product.service.ReturnRequestService;
import com.mall.module.product.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品模块 API（前后端分离）
 * 所有接口返回 JSON，路径统一 /api 前缀
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private ReturnRequestService returnRequestService;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Autowired
    private ReviewLikeMapper reviewLikeMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ReviewAppendMapper reviewAppendMapper;
    @Autowired
    private ReviewImageMapper reviewImageMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    // ==================== 商品相关 ====================

    /**
     * 商品列表（分页、筛选、排序）
     */
    @GetMapping("/products")
    public Result<PageResult<Product>> getProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Product> result = productService.getProductList(categoryId, keyword, minPrice, maxPrice, sort, page, size);
        return Result.success(result);
    }

    /**
     * 商品详情
     */
    @GetMapping("/products/{id}")
    public Result<Map<String, Object>> getProductDetail(@PathVariable Integer id,
                                                        @RequestParam(defaultValue = "1") Integer userId) {
        // 增加浏览量
        try { productService.incrementViews(id); } catch (Exception ignored) {}

        // 记录浏览历史
        try { browseHistoryService.addHistory(userId, id); } catch (Exception ignored) {}

        Map<String, Object> detail = productService.getProductDetail(id);
        if (detail == null) {
            return Result.error("商品不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("product", detail.get("product"));
        result.put("images", detail.get("images"));
        result.put("skus", detail.get("skus"));
        result.put("merchant", detail.get("merchant"));

        // 评价统计
        Map<String, Object> reviewStatsRaw = (Map<String, Object>) detail.get("reviewStats");
        Map<String, Object> reviewStats = new HashMap<>();
        if (reviewStatsRaw != null) {
            int total = toInt(reviewStatsRaw.get("total"));
            int fiveStar = toInt(reviewStatsRaw.get("fiveStar"));
            int fourStar = toInt(reviewStatsRaw.get("fourStar"));
            int threeStar = toInt(reviewStatsRaw.get("threeStar"));
            int twoStar = toInt(reviewStatsRaw.get("twoStar"));
            int oneStar = toInt(reviewStatsRaw.get("oneStar"));

            reviewStats.put("total", total);
            reviewStats.put("avgRating", toDouble(reviewStatsRaw.get("avgRating")));
            reviewStats.put("hasImageCount", toInt(reviewStatsRaw.get("hasImageCount")));
            reviewStats.put("goodRate", total > 0 ? Math.round((float)(fiveStar + fourStar) / total * 100) : 100);
            reviewStats.put("star5", total > 0 ? Math.round((float)fiveStar / total * 100) : 0);
            reviewStats.put("star5Count", fiveStar);
            reviewStats.put("star4", total > 0 ? Math.round((float)fourStar / total * 100) : 0);
            reviewStats.put("star4Count", fourStar);
            reviewStats.put("star3", total > 0 ? Math.round((float)threeStar / total * 100) : 0);
            reviewStats.put("star3Count", threeStar);
            reviewStats.put("star2", total > 0 ? Math.round((float)twoStar / total * 100) : 0);
            reviewStats.put("star2Count", twoStar);
            reviewStats.put("star1", total > 0 ? Math.round((float)oneStar / total * 100) : 0);
            reviewStats.put("star1Count", oneStar);
        }
        result.put("reviewStats", reviewStats);

        // 收藏状态
        try {
            result.put("isFavorite", favoriteService.isFavorite(userId, id));
        } catch (Exception e) {
            result.put("isFavorite", false);
        }

        return Result.success(result);
    }

    /**
     * 推荐商品
     */
    @GetMapping("/products/recommended")
    public Result<List<Product>> getRecommended(@RequestParam(defaultValue = "10") int limit) {
        List<Product> list = productService.getRecommended(limit);
        return Result.success(list);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/products/search")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Product> result = productService.getProductList(null, keyword, null, null, null, page, size);
        return Result.success(result);
    }

    // ==================== 分类相关 ====================

    /**
     * 分类树（含子分类）
     */
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        List<Category> list = categoryService.getCategoryTree();
        return Result.success(list);
    }

    /**
     * 分类下的商品
     */
    @GetMapping("/categories/{id}/products")
    public Result<PageResult<Product>> getCategoryProducts(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Product> result = productService.getProductList(id, null, null, null, null, page, size);
        return Result.success(result);
    }

    // ==================== 评价相关 ====================

    /**
     * 评价列表
     */
    @GetMapping("/reviews")
    public Result<Map<String, Object>> getReviews(
            @RequestParam Integer productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer hasImage,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = new HashMap<>();

        // 评价统计
        Map<String, Object> statsRaw = reviewService.getReviewStats(productId);
        Map<String, Object> stats = new HashMap<>();
        int total = toInt(statsRaw.get("total"));
        int fiveStar = toInt(statsRaw.get("fiveStar"));
        int fourStar = toInt(statsRaw.get("fourStar"));
        int threeStar = toInt(statsRaw.get("threeStar"));
        int twoStar = toInt(statsRaw.get("twoStar"));
        int oneStar = toInt(statsRaw.get("oneStar"));
        stats.put("totalCount", total);
        stats.put("goodCount", fiveStar + fourStar);
        stats.put("mediumCount", threeStar);
        stats.put("badCount", twoStar + oneStar);
        stats.put("imageCount", toInt(statsRaw.get("hasImageCount")));
        stats.put("goodRate", total > 0 ? Math.round((float)(fiveStar + fourStar) / total * 100) : 100);
        stats.put("avgRating", toDouble(statsRaw.get("avgRating")));
        result.put("stats", stats);

        // 评价列表
        PageResult<Map<String, Object>> reviewPage = reviewService.getProductReviews(productId, rating, hasImage, page, size);
        List<Map<String, Object>> flatList = new ArrayList<>();
        for (Map<String, Object> item : reviewPage.getList()) {
            Map<String, Object> flat = new HashMap<>();
            Review review = (Review) item.get("review");
            if (review != null) {
                flat.put("id", review.getId());
                flat.put("orderItemId", review.getOrderItemId());
                flat.put("userName", review.getIsAnonymous() != null && review.getIsAnonymous() == 1 ? "匿名用户" : "用户" + review.getUserId());
                flat.put("rating", review.getRating());
                flat.put("content", review.getContent());
                flat.put("createTime", review.getCreateTime());
                flat.put("likeCount", review.getLikeCount());
                flat.put("hasAppend", review.getHasAppend());
            }
            Object imagesObj = item.get("images");
            if (imagesObj instanceof List) {
                List<String> urls = new ArrayList<>();
                for (Object img : (List<?>) imagesObj) {
                    if (img instanceof ReviewImage) urls.add(((ReviewImage) img).getImageUrl());
                }
                flat.put("images", urls);
            }
            Object appendObj = item.get("append");
            if (appendObj instanceof ReviewAppend) {
                ReviewAppend append = (ReviewAppend) appendObj;
                flat.put("appendContent", append.getContent());
                flat.put("appendTime", append.getCreateTime());
            }
            Object replyObj = item.get("reply");
            if (replyObj instanceof ReviewReply) {
                flat.put("reply", ((ReviewReply) replyObj).getContent());
            }
            flatList.add(flat);
        }
        result.put("list", flatList);
        result.put("total", reviewPage.getTotal());

        return Result.success(result);
    }

    /**
     * 发布评价
     */
    @PostMapping("/reviews")
    public Result<String> submitReview(@RequestBody Map<String, Object> params) {
        try {
            System.out.println("=== 提交评价 ===");
            System.out.println("params: " + params);

            Review review = new Review();
            review.setOrderItemId(toInt(params.get("orderItemId")));
            review.setProductId(toInt(params.get("productId")));
            review.setUserId(toInt(params.get("userId")));
            review.setMerchantId(toInt(params.get("merchantId")));
            review.setContent((String) params.get("content"));
            review.setRating(toInt(params.get("rating")));
            review.setIsAnonymous(toInt(params.get("isAnonymous")));

            System.out.println("  orderItemId=" + review.getOrderItemId()
                + ", productId=" + review.getProductId()
                + ", userId=" + review.getUserId()
                + ", merchantId=" + review.getMerchantId()
                + ", content=" + review.getContent()
                + ", rating=" + review.getRating());

            // 校验必填字段
            if (review.getOrderItemId() == null || review.getOrderItemId() <= 0) {
                return Result.error("订单明细ID无效");
            }
            if (review.getProductId() == null || review.getProductId() <= 0) {
                return Result.error("商品ID无效");
            }
            if (review.getMerchantId() == null || review.getMerchantId() <= 0) {
                return Result.error("商家ID无效");
            }
            if (review.getContent() == null || review.getContent().trim().isEmpty()) {
                return Result.error("评价内容不能为空");
            }
            // BUG-003: 评分范围校验（1-5星）
            if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
                return Result.error("评分必须在1-5之间");
            }

            // BUG-001: 购买校验 - 验证该用户是否购买过该商品（通过订单明细ID关联）
            OrderItem orderItem = orderItemMapper.selectById(review.getOrderItemId());
            if (orderItem == null) {
                return Result.error("订单明细不存在");
            }
            Order order = orderMapper.selectById(orderItem.getOrderId());
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (!order.getUserId().equals(review.getUserId())) {
                return Result.error("只能评价自己购买的商品");
            }
            // 订单状态需为已完成（status=3）才能评价
            if (order.getStatus() == null || order.getStatus() < 3) {
                return Result.error("订单尚未完成，无法评价");
            }

            // BUG-002: 重复评价校验 - 同一订单明细只能评价一次（追评通过 append 接口）
            if (reviewMapper.existsByOrderItemId(review.getOrderItemId()) > 0) {
                return Result.error("该订单已评价，不能重复评价，如需补充请使用追评功能");
            }

            List<String> imageUrls = new ArrayList<>();
            String urls = (String) params.get("imageUrls");
            if (urls != null && !urls.isEmpty()) {
                imageUrls = Arrays.asList(urls.split(","));
            }

            reviewService.submitReview(review, imageUrls);
            System.out.println("评价提交成功，reviewId=" + review.getId());
            return Result.success("评价成功");
        } catch (Exception e) {
            System.out.println("评价提交失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("评价失败：" + e.getMessage());
        }
    }

    /**
     * 我的评价列表（按用户ID查询）
     */
    @GetMapping("/my/reviews")
    public Result<List<Map<String, Object>>> getMyReviews(@RequestParam Integer userId) {
        try {
            System.out.println("=== 我的评价 === userId=" + userId);
            List<Review> reviews = reviewMapper.selectByUserId(userId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Review review : reviews) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", review.getId());
                item.put("productId", review.getProductId());
                item.put("content", review.getContent());
                item.put("rating", review.getRating());
                item.put("likeCount", review.getLikeCount());
                item.put("isAnonymous", review.getIsAnonymous());
                item.put("hasAppend", review.getHasAppend());
                item.put("merchantReply", review.getMerchantReply());
                item.put("replyTime", review.getReplyTime());
                item.put("createTime", review.getCreateTime());
                // 查询评价图片
                List<ReviewImage> images = reviewImageMapper.selectByReviewId(review.getId());
                item.put("images", images);
                // 查询追评
                ReviewAppend append = reviewAppendMapper.selectByReviewId(review.getId());
                item.put("append", append);
                // 查询商品名称
                Product product = productMapper.selectById(review.getProductId());
                item.put("productName", product != null ? product.getName() : "未知商品");
                item.put("productImage", product != null ? product.getCoverImg() : "");
                list.add(item);
            }
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取我的评价失败：" + e.getMessage());
        }
    }

    /**
     * 追加评价
     */
    @PostMapping("/reviews/{id}/append")
    public Result<String> appendReview(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        try {
            String content = params.get("content");
            if (content == null || content.trim().isEmpty()) {
                return Result.error("追评内容不能为空");
            }
            reviewService.appendReview(id, content);
            return Result.success("追评成功");
        } catch (Exception e) {
            return Result.error("追评失败：" + e.getMessage());
        }
    }

    /**
     * 点赞评价
     */
    @PostMapping("/reviews/{id}/like")
    public Result<String> likeReview(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> params) {
        try {
            // BUG-005: 防重复点赞 - 需要用户ID
            Integer userId = params != null ? toInt(params.get("userId")) : null;
            if (userId == null || userId <= 0) {
                return Result.error("请先登录");
            }
            // 检查是否已点赞
            if (reviewLikeMapper.existsByReviewIdAndUserId(id, userId) > 0) {
                return Result.error("您已点赞过该评价");
            }
            // 记录点赞
            ReviewLike like = new ReviewLike();
            like.setReviewId(id);
            like.setUserId(userId);
            like.setCreateTime(java.time.LocalDateTime.now());
            reviewLikeMapper.insert(like);
            // 更新评价点赞数
            reviewService.likeReview(id);
            return Result.success("点赞成功");
        } catch (Exception e) {
            return Result.error("点赞失败：" + e.getMessage());
        }
    }

    // ==================== 收藏相关 ====================

    /**
     * 收藏/取消收藏
     */
    @PostMapping("/favorites/toggle")
    public Result<String> toggleFavorite(@RequestBody Map<String, Object> params) {
        try {
            String action = (String) params.get("action");
            int userId = toInt(params.get("userId"));
            int productId = toInt(params.get("productId"));
            if ("add".equals(action)) {
                favoriteService.addFavorite(userId, productId);
                return Result.success("收藏成功");
            } else if ("remove".equals(action)) {
                favoriteService.removeFavorite(userId, productId);
                return Result.success("取消收藏成功");
            }
            return Result.error("无效的操作类型");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 检查收藏状态
     */
    @GetMapping("/favorites/check")
    public Result<Boolean> checkFavorite(@RequestParam Integer userId, @RequestParam Integer productId) {
        boolean isFavorite = favoriteService.isFavorite(userId, productId);
        return Result.success(isFavorite);
    }

    /**
     * 收藏列表
     */
    @GetMapping("/favorites")
    public Result<Map<String, Object>> getFavorites(
            @RequestParam(defaultValue = "1") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Map<String, Object>> result = favoriteService.getFavoriteList(userId, page, size);
        // 扁平化数据
        List<Map<String, Object>> flatList = new ArrayList<>();
        for (Map<String, Object> item : result.getList()) {
            Map<String, Object> flat = new HashMap<>();
            Favorite fav = (Favorite) item.get("favorite");
            Product prod = (Product) item.get("product");
            if (fav != null) {
                flat.put("id", fav.getId());
                flat.put("productId", fav.getProductId());
            }
            if (prod != null) {
                flat.put("productName", prod.getName());
                flat.put("price", prod.getPrice());
                flat.put("coverImg", prod.getCoverImg());
            }
            flatList.add(flat);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", flatList);
        data.put("total", result.getTotal());
        return Result.success(data);
    }

    /**
     * 删除收藏
     */
    @DeleteMapping("/favorites/{id}")
    public Result<String> deleteFavorite(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer userId) {
        try {
            favoriteService.removeFavorite(userId, id);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }

    // ==================== 浏览历史 ====================

    /**
     * 浏览历史列表
     */
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory(
            @RequestParam(defaultValue = "1") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> list = browseHistoryService.getHistoryList(userId, page, size);
        // 扁平化数据
        List<Map<String, Object>> flatList = new ArrayList<>();
        for (Map<String, Object> item : list) {
            Map<String, Object> flat = new HashMap<>();
            BrowseHistory history = (BrowseHistory) item.get("history");
            Product prod = (Product) item.get("product");
            if (history != null) {
                flat.put("id", history.getId());
                flat.put("productId", history.getProductId());
                if (history.getBrowseTime() != null) {
                    flat.put("viewTime", history.getBrowseTime().toString().substring(0, 16));
                }
            }
            if (prod != null) {
                flat.put("productName", prod.getName());
                flat.put("price", prod.getPrice());
                flat.put("coverImg", prod.getCoverImg());
            }
            flatList.add(flat);
        }
        return Result.success(flatList);
    }

    /**
     * 删除浏览记录
     */
    @DeleteMapping("/history/{id}")
    public Result<String> deleteHistory(@PathVariable Integer id) {
        try {
            browseHistoryService.deleteHistory(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 清空浏览历史
     */
    @DeleteMapping("/history")
    public Result<String> clearHistory(@RequestParam(defaultValue = "1") Integer userId) {
        try {
            browseHistoryService.clearHistory(userId);
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空失败：" + e.getMessage());
        }
    }

    // ==================== 订单相关 ====================

    /**
     * 我的订单列表（只返回已签收的订单，可评价/退换货）
     */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> getOrders(
            @RequestParam(defaultValue = "1") Integer userId,
            @RequestParam(required = false) Integer status) {
        List<Order> orders;
        if (status != null) {
            orders = orderMapper.selectByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.selectByUserId(userId);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", order.getId());
            item.put("orderNo", order.getOrderNo());
            item.put("totalAmount", order.getTotalAmount());
            item.put("status", order.getStatus());
            item.put("statusText", convertOrderStatus(order.getStatus()));
            item.put("createTime", order.getCreateTime());
            item.put("payTime", order.getPayTime());
            item.put("shipTime", order.getShipTime());
            item.put("receiveTime", order.getReceiveTime());
            item.put("shippingAddress", order.getShippingAddress());

            // 查询订单明细
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (OrderItem oi : items) {
                Map<String, Object> oiMap = new HashMap<>();
                oiMap.put("id", oi.getId());
                oiMap.put("productId", oi.getProductId());
                oiMap.put("productName", oi.getProductName());
                oiMap.put("productPrice", oi.getProductPrice());
                oiMap.put("quantity", oi.getQuantity());
                oiMap.put("merchantId", oi.getMerchantId());
                oiMap.put("merchantName", oi.getMerchantName());
                oiMap.put("productImage", oi.getProductImage());
                oiMap.put("specs", oi.getSpecs());
                // 获取商品封面图
                List<ProductImage> images = productImageMapper.selectByProductId(oi.getProductId());
                if (images != null && !images.isEmpty()) {
                    oiMap.put("coverImg", images.get(0).getImageUrl());
                }
                itemList.add(oiMap);
            }
            item.put("items", itemList);
            list.add(item);
        }
        return Result.success(list);
    }

    private String convertOrderStatus(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已发货";
            case 3: return "已收货";
            case 4: return "已完成";
            default: return "未知";
        }
    }

    // ==================== 退换货 ====================

    /**
     * 退换货列表
     */
    @GetMapping("/returns")
    public Result<List<Map<String, Object>>> getReturns(
            @RequestParam(defaultValue = "1") Integer userId,
            @RequestParam(required = false) String status) {
        System.out.println("=== 获取退换货列表 ===");
        System.out.println("userId: " + userId);
        System.out.println("status: " + status);
        
        Integer statusCode = null;
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "pending": statusCode = 0; break;
                case "approved": statusCode = 1; break;
                case "rejected": statusCode = 2; break;
                case "shipping": statusCode = 3; break;
                case "completed": statusCode = 4; break;
                case "cancelled": statusCode = 5; break;
            }
        }
        
        System.out.println("statusCode: " + statusCode);
        
        List<Map<String, Object>> list = returnRequestService.getReturnList(userId, statusCode);
        
        System.out.println("返回记录数: " + list.size());
        
        return Result.success(list);
    }

    /**
     * 退换货详情
     */
    @GetMapping("/returns/{id}")
    public Result<Map<String, Object>> getReturnDetail(@PathVariable Integer id) {
        Map<String, Object> detail = returnRequestService.getReturnDetail(id);
        if (detail == null || detail.isEmpty()) {
            return Result.error("退换货申请不存在");
        }
        return Result.success(detail);
    }

    /**
     * 提交退换货申请
     */
    @PostMapping("/returns")
    public Result<String> submitReturn(@RequestBody Map<String, Object> params) {
        System.out.println("=== 提交退换货申请 ===");
        System.out.println("params: " + params);
        
        try {
            ReturnRequest request = new ReturnRequest();
            request.setOrderId(toInt(params.get("orderId")));
            request.setUserId(toInt(params.get("userId")));
            request.setProductId(toInt(params.get("productId")));
            request.setReason((String) params.get("reason"));
            String type = (String) params.get("type");
            if ("refund".equals(type)) request.setType(1);
            else if ("return".equals(type)) request.setType(2);
            else if ("exchange".equals(type)) request.setType(3);
            request.setReasonType((String) params.get("reasonType"));
            request.setStatus(0); // 默认待审核状态
            
            // 获取商品信息（用于退款金额验证和商家ID）
            Integer productId = toInt(params.get("productId"));
            Product product = null;
            if (productId != null && productId > 0) {
                product = productService.getProductById(productId);
            }

            // 设置退款金额
            Object amount = params.get("refundAmount");
            if (amount != null) {
                BigDecimal refundAmount;
                if (amount instanceof BigDecimal) {
                    refundAmount = (BigDecimal) amount;
                } else {
                    refundAmount = new BigDecimal(amount.toString());
                }

                // 验证退款金额不能超过商品价格
                if (product != null && refundAmount.compareTo(product.getPrice()) > 0) {
                    return Result.error("退款金额不能超过商品价格");
                }

                request.setRefundAmount(refundAmount);
            }

            // 从商品表获取商家ID
            if (product != null) {
                request.setMerchantId(product.getMerchantId());
            }

            List<String> imageUrls = new ArrayList<>();
            String urls = (String) params.get("imageUrls");
            if (urls != null && !urls.isEmpty()) {
                imageUrls = Arrays.asList(urls.split(","));
            }
            
            System.out.println("准备插入数据库...");
            System.out.println("  orderId: " + request.getOrderId());
            System.out.println("  userId: " + request.getUserId());
            System.out.println("  productId: " + request.getProductId());
            System.out.println("  merchantId: " + request.getMerchantId());
            System.out.println("  type: " + request.getType());
            System.out.println("  status: " + request.getStatus());
            System.out.println("  refundAmount: " + request.getRefundAmount());
            System.out.println("  reasonType: " + request.getReasonType());
            System.out.println("  createTime: " + request.getCreateTime());
            returnRequestService.submitReturn(request, imageUrls);
            System.out.println("插入成功，返回id: " + request.getId());

            return Result.success("申请提交成功");
        } catch (Exception e) {
            System.out.println("提交失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("申请失败：" + e.getMessage());
        }
    }

    /**
     * 填写物流信息
     */
    @PostMapping("/returns/{id}/shipping")
    public Result<String> submitShipping(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        try {
            returnRequestService.updateLogistics(id, params.get("logisticsCompany"), params.get("logisticsNo"));
            return Result.success("物流信息提交成功");
        } catch (Exception e) {
            return Result.error("提交失败：" + e.getMessage());
        }
    }

    /**
     * 取消退换货申请
     */
    @PutMapping("/returns/{id}/cancel")
    public Result<String> cancelReturn(@PathVariable Integer id) {
        try {
            returnRequestService.cancelReturn(id);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    // ==================== 工具方法 ====================

    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch (Exception e) { return 0; }
    }

    private double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try { return Double.parseDouble(obj.toString()); } catch (Exception e) { return 0.0; }
    }
}
