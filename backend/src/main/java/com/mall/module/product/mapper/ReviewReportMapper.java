package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewReportMapper {

    void insert(ReviewReport reviewReport);

    ReviewReport selectByReviewIdAndMerchantId(@Param("reviewId") Integer reviewId,
                                                @Param("merchantId") Integer merchantId);
}
