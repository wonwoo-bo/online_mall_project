package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewAppend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewAppendMapper {

    ReviewAppend selectByReviewId(@Param("reviewId") Integer reviewId);

    void insert(ReviewAppend reviewAppend);
}
