package com.mall.module.product.controller;

import com.mall.module.product.entity.Coupon;
import com.mall.module.product.entity.UserCoupon;
import com.mall.module.product.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addCoupon(@RequestBody Coupon coupon) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Coupon saved = couponService.addCoupon(merchantId, coupon);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", saved);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Coupon updated = couponService.updateCoupon(merchantId, id, coupon);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCoupon(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean deleted = couponService.deleteCoupon(merchantId, id);
            result.put("code", 200);
            result.put("message", deleted ? "删除成功" : "删除失败");
            result.put("data", deleted);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCouponById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Coupon coupon = couponService.getCouponById(merchantId, id);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", coupon);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCouponList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            List<Coupon> list = couponService.getCouponList(merchantId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCoupons() {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            List<Coupon> list = couponService.getActiveCoupons(merchantId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCouponStatus(@PathVariable Integer id, @RequestParam Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean updated = couponService.updateCouponStatus(merchantId, id, status);
            result.put("code", 200);
            result.put("message", updated ? "状态更新成功" : "状态更新失败");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishCoupon(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean updated = couponService.updateCouponStatus(merchantId, id, 1);
            result.put("code", 200);
            result.put("message", updated ? "发布成功" : "发布失败");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/receive")
    public ResponseEntity<Map<String, Object>> receiveCoupon(@RequestBody Map<String, Integer> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userId = params.get("userId") != null ? params.get("userId") : 1;
            Integer couponId = params.get("couponId");
            UserCoupon userCoupon = couponService.receiveCoupon(userId, couponId);
            result.put("code", 200);
            result.put("message", "领取成功");
            result.put("data", userCoupon);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/use")
    public ResponseEntity<Map<String, Object>> useCoupon(@RequestBody Map<String, Integer> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userCouponId = params.get("userCouponId");
            Integer orderId = params.get("orderId");
            boolean used = couponService.useCoupon(userCouponId, orderId);
            result.put("code", 200);
            result.put("message", used ? "使用成功" : "使用失败");
            result.put("data", used);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserCouponList(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserCoupon> list = couponService.getUserCouponList(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}/available")
    public ResponseEntity<Map<String, Object>> getAvailableUserCoupons(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserCoupon> list = couponService.getAvailableUserCoupons(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
