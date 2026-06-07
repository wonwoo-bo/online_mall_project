package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewExplanation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewExplanationMapper {

    void insert(ReviewExplanation explanation);

    void update(ReviewExplanation explanation);

    ReviewExplanation selectByReviewId(@Param("reviewId") Integer reviewId);
}
