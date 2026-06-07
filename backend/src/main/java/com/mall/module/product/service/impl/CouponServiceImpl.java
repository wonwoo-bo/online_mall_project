package com.mall.module.product.service.impl;

import com.mall.module.product.entity.Coupon;
import com.mall.module.product.entity.UserCoupon;
import com.mall.module.product.mapper.CouponMapper;
import com.mall.module.product.mapper.UserCouponMapper;
import com.mall.module.product.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public Coupon addCoupon(Integer merchantId, Coupon coupon) {
        if (coupon.getName() == null || coupon.getName().trim().isEmpty()) {
            throw new RuntimeException("优惠券名称不能为空");
        }

        coupon.setMerchantId(merchantId);
        if (coupon.getStatus() == null) {
            coupon.setStatus(0);
        }
        if (coupon.getReceivedCount() == null) {
            coupon.setReceivedCount(0);
        }
        if (coupon.getUsedCount() == null) {
            coupon.setUsedCount(0);
        }
        if (coupon.getPerUserLimit() == null) {
            coupon.setPerUserLimit(1);
        }

        couponMapper.insert(coupon);
        return couponMapper.selectById(coupon.getId(), merchantId);
    }

    @Override
    public Coupon updateCoupon(Integer merchantId, Integer couponId, Coupon coupon) {
        Coupon existing = couponMapper.selectById(couponId, merchantId);
        if (existing == null) {
            throw new RuntimeException("优惠券不存在");
        }

        coupon.setId(couponId);
        coupon.setMerchantId(merchantId);
        couponMapper.updateById(coupon);
        return couponMapper.selectById(couponId, merchantId);
    }

    @Override
    public boolean deleteCoupon(Integer merchantId, Integer couponId) {
        Coupon coupon = couponMapper.selectById(couponId, merchantId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        return couponMapper.deleteById(couponId, merchantId) > 0;
    }

    @Override
    public Coupon getCouponById(Integer merchantId, Integer couponId) {
        return couponMapper.selectById(couponId, merchantId);
    }

    @Override
    public List<Coupon> getCouponList(Integer merchantId) {
        return couponMapper.selectByMerchantId(merchantId);
    }

    @Override
    public List<Coupon> getActiveCoupons(Integer merchantId) {
        return couponMapper.selectActiveCoupons(merchantId);
    }

    @Override
    public boolean updateCouponStatus(Integer merchantId, Integer couponId, Integer status) {
        Coupon coupon = couponMapper.selectById(couponId, merchantId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        return couponMapper.updateStatus(couponId, merchantId, status) > 0;
    }

    @Override
    public UserCoupon receiveCoupon(Integer userId, Integer couponId) {
        Coupon coupon = couponMapper.selectById(couponId, null);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        if (coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券未上架");
        }

        if (coupon.getStartTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("优惠券尚未开始");
        }

        if (coupon.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        int receivedCount = userCouponMapper.countReceived(userId, couponId);
        if (receivedCount >= coupon.getPerUserLimit()) {
            throw new RuntimeException("已达到领取上限");
        }

        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new RuntimeException("优惠券已领完");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCoupon.setExpireTime(coupon.getEndTime());

        userCouponMapper.insert(userCoupon);
        couponMapper.incrementReceivedCount(couponId);

        return userCouponMapper.selectById(userCoupon.getId());
    }

    @Override
    public boolean useCoupon(Integer userCouponId, Integer orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new RuntimeException("用户优惠券不存在");
        }

        if (userCoupon.getStatus() != 0) {
            throw new RuntimeException("优惠券已使用或已过期");
        }

        if (userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        userCoupon.setStatus(1);
        userCoupon.setOrderId(orderId);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
        couponMapper.incrementUsedCount(userCoupon.getCouponId());

        return true;
    }

    @Override
    public UserCoupon getUserCouponById(Integer id) {
        return userCouponMapper.selectById(id);
    }

    @Override
    public List<UserCoupon> getUserCouponList(Integer userId) {
        return userCouponMapper.selectByUserId(userId);
    }

    @Override
    public List<UserCoupon> getAvailableUserCoupons(Integer userId) {
        return userCouponMapper.selectAvailableByUserId(userId);
    }
}