package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewImageMapper {
    List<ReviewImage> selectByReviewId(@Param("reviewId") Integer reviewId);

    void insert(ReviewImage reviewImage);

    void deleteByReviewId(@Param("reviewId") Integer reviewId);
}
