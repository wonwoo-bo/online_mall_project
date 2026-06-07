package com.mall.module.product.mapper;

import com.mall.module.product.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponMapper {
    int insert(UserCoupon userCoupon);
    int updateById(UserCoupon userCoupon);
    int deleteById(@Param("id") Integer id);
    UserCoupon selectById(@Param("id") Integer id);
    List<UserCoupon> selectByUserId(@Param("userId") Integer userId);
    List<UserCoupon> selectAvailableByUserId(@Param("userId") Integer userId);
    int countReceived(@Param("userId") Integer userId, @Param("couponId") Integer couponId);
    UserCoupon selectByOrderId(@Param("orderId") Integer orderId);
}