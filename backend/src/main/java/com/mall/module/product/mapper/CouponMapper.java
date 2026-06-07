package com.mall.module.product.mapper;

import com.mall.module.product.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {
    int insert(Coupon coupon);
    int updateById(Coupon coupon);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    Coupon selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<Coupon> selectByMerchantId(@Param("merchantId") Integer merchantId);
    List<Coupon> selectActiveCoupons(@Param("merchantId") Integer merchantId);
    int updateStatus(@Param("id") Integer id, @Param("merchantId") Integer merchantId, @Param("status") Integer status);
    int incrementReceivedCount(@Param("id") Integer id);
    int incrementUsedCount(@Param("id") Integer id);
}