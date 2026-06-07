package com.mall.module.product.mapper;

import com.mall.module.product.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper {

    List<Review> selectByProductId(@Param("productId") Integer productId,
                                   @Param("rating") Integer rating,
                                   @Param("hasImage") Integer hasImage,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    int countByProductId(@Param("productId") Integer productId);

    int countByProductIdFiltered(@Param("productId") Integer productId, @Param("rating") Integer rating, @Param("hasImage") Integer hasImage);

    int countByProductIdAndRating(@Param("productId") Integer productId,
                                  @Param("rating") Integer rating);

    int countWithImage(@Param("productId") Integer productId);

    double avgRating(@Param("productId") Integer productId);

    Map<String, Object> selectReviewStats(@Param("productId") Integer productId);

    void insert(Review review);

    void updateLikeCount(@Param("id") Integer id);

    void updateHasAppend(@Param("id") Integer id);

    int existsByOrderItemId(@Param("orderItemId") Integer orderItemId);

    int countByProductIdAndUserId(@Param("productId") Integer productId, @Param("userId") Integer userId);
    
    Review selectByOrderItemId(@Param("orderItemId") Integer orderItemId);

    List<Review> selectByUserId(@Param("userId") Integer userId);

    List<Review> selectByMerchantId(@Param("merchantId") Integer merchantId);

    List<Map<String, Object>> selectMerchantReviewList(@Param("merchantId") Integer merchantId,
                                                        @Param("rating") Integer rating,
                                                        @Param("hasReply") Integer hasReply,
                                                        @Param("startTime") String startTime,
                                                        @Param("endTime") String endTime,
                                                        @Param("offset") int offset,
                                                        @Param("limit") int limit);

    int countMerchantReviewList(@Param("merchantId") Integer merchantId,
                                @Param("rating") Integer rating,
                                @Param("hasReply") Integer hasReply,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);

    Map<String, Object> selectMerchantReviewStats(@Param("merchantId") Integer merchantId);

    Review selectByIdAndMerchantId(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    Review selectById(@Param("id") Integer id);

    void updateTopStatus(@Param("id") Integer id, @Param("isTop") Integer isTop);

    void updateMerchantReply(@Param("id") Integer id, @Param("merchantReply") String merchantReply, @Param("replyTime") java.time.LocalDateTime replyTime);

    Map<String, Object> selectAdvancedReviewStats(@Param("merchantId") Integer merchantId);

    List<Map<String, Object>> selectReviewTrend(@Param("merchantId") Integer merchantId, @Param("days") Integer days);
}
