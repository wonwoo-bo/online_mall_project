package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewTop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewTopMapper {

    void insert(ReviewTop top);

    void delete(@Param("reviewId") Integer reviewId, @Param("productId") Integer productId);

    List<ReviewTop> selectByMerchantId(@Param("merchantId") Integer merchantId);
}
