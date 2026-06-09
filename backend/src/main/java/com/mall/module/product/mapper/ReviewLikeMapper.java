package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewLikeMapper {

    int existsByReviewIdAndUserId(@Param("reviewId") Integer reviewId, @Param("userId") Integer userId);

    void insert(ReviewLike reviewLike);
}
