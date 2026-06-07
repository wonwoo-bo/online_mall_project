package com.mall.module.product.service;

import com.mall.module.product.entity.Coupon;
import com.mall.module.product.entity.UserCoupon;

import java.util.List;

public interface CouponService {
    Coupon addCoupon(Integer merchantId, Coupon coupon);
    Coupon updateCoupon(Integer merchantId, Integer couponId, Coupon coupon);
    boolean deleteCoupon(Integer merchantId, Integer couponId);
    Coupon getCouponById(Integer merchantId, Integer couponId);
    List<Coupon> getCouponList(Integer merchantId);
    List<Coupon> getActiveCoupons(Integer merchantId);
    boolean updateCouponStatus(Integer merchantId, Integer couponId, Integer status);

    UserCoupon receiveCoupon(Integer userId, Integer couponId);
    boolean useCoupon(Integer userCouponId, Integer orderId);
    UserCoupon getUserCouponById(Integer id);
    List<UserCoupon> getUserCouponList(Integer userId);
    List<UserCoupon> getAvailableUserCoupons(Integer userId);
}