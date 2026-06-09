package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewAppeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewAppealMapper {

    void insert(ReviewAppeal appeal);

    List<ReviewAppeal> selectByMerchantId(@Param("merchantId") Integer merchantId);

    ReviewAppeal selectByReviewId(@Param("reviewId") Integer reviewId);
}
