package com.mall.module.user.controller;

import com.mall.common.JwtUtil;
import com.mall.common.Result;
import com.mall.module.user.dto.*;
import com.mall.module.user.entity.MemberInfo;
import com.mall.module.user.entity.MemberType;
import com.mall.module.user.entity.PointsRecord;
import com.mall.module.user.entity.User;
import com.mall.module.user.entity.UserAddress;
import com.mall.module.user.entity.UserRole;
import com.mall.module.user.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("注册成功", result);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Integer userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Map<String, Object>> updateUserInfo(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        userService.updateUserInfo(userId, updateUserDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("更新成功", result);
    }

    @PutMapping("/password")
    public Result<Map<String, Object>> updatePassword(
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        
        userService.updatePassword(userId, oldPassword, newPassword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("密码修改成功", result);
    }

    @PostMapping("/address")
    public Result<Map<String, Object>> addAddress(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody AddressDTO addressDTO) {
        userService.addAddress(userId, addressDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("添加地址成功", result);
    }

    @PutMapping("/address/{addressId}")
    public Result<Map<String, Object>> updateAddress(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressDTO addressDTO) {
        userService.updateAddress(userId, addressId, addressDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("更新地址成功", result);
    }

    @DeleteMapping("/address/{addressId}")
    public Result<Map<String, Object>> deleteAddress(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer addressId) {
        userService.deleteAddress(userId, addressId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("删除地址成功", result);
    }

    @GetMapping("/address")
    public Result<List<UserAddress>> getAddresses(@RequestAttribute("userId") Integer userId) {
        List<UserAddress> addresses = userService.getAddresses(userId);
        return Result.success(addresses);
    }

    @GetMapping("/address/{addressId}")
    public Result<UserAddress> getAddressById(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer addressId) {
        UserAddress address = userService.getAddressById(userId, addressId);
        return Result.success(address);
    }

    @PutMapping("/address/{addressId}/default")
    public Result<Map<String, Object>> setDefaultAddress(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer addressId) {
        userService.setDefaultAddress(userId, addressId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("设置默认地址成功", result);
    }

    @GetMapping("/member")
    public Result<MemberInfo> getMemberInfo(@RequestAttribute("userId") Integer userId) {
        MemberInfo memberInfo = userService.getMemberInfo(userId);
        return Result.success(memberInfo);
    }

    @PutMapping("/member")
    public Result<Map<String, Object>> updateMemberInfo(
            @RequestAttribute("userId") Integer userId,
            @RequestBody MemberInfo memberInfo) {
        userService.updateMemberInfo(userId, memberInfo);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("会员信息更新成功", result);
    }

    @PostMapping("/member/points")
    public Result<Map<String, Object>> addPoints(
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, Integer> pointsMap) {
        Integer points = pointsMap.get("points");
        userService.addPoints(userId, points);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("积分增加成功", result);
    }

    @GetMapping("/member/points/history")
    public Result<List<PointsRecord>> getPointsHistory(@RequestAttribute("userId") Integer userId) {
        List<PointsRecord> history = userService.getPointsHistory(userId);
        return Result.success(history);
    }

    @GetMapping("/role")
    public Result<UserRole> getUserRole(@RequestAttribute("userId") Integer userId) {
        UserRole userRole = userService.getUserRole(userId);
        return Result.success(userRole);
    }

    @PutMapping("/role")
    public Result<Map<String, Object>> updateUserRole(
            @RequestAttribute("userId") Integer userId,
            @RequestBody UserRole userRole) {
        userService.updateUserRole(userId, userRole);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("权限信息更新成功", result);
    }

    @GetMapping("/member/types")
    public Result<List<MemberType>> getMemberTypes() {
        List<MemberType> types = userService.getAllMemberTypes();
        return Result.success(types);
    }

    @PostMapping("/member/purchase")
    public Result<Map<String, Object>> purchaseMember(
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, String> request) {
        String levelCode = request.get("levelCode");
        userService.purchaseMember(userId, levelCode);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("会员购买成功", result);
    }

    /**
     * 扣减积分（供退换货完成时调用）
     */
    @PostMapping("/points/deduct")
    public Result<String> deductPoints(@RequestBody Map<String, Object> request) {
        Integer userId = request.get("userId") instanceof Number ? ((Number) request.get("userId")).intValue() : null;
        Integer points = request.get("points") instanceof Number ? ((Number) request.get("points")).intValue() : null;
        String description = (String) request.get("description");
        if (userId == null || points == null || points <= 0) {
            return Result.error("参数无效");
        }
        userService.deductPoints(userId, points, description != null ? description : "积分扣减");
        return Result.success("积分扣减成功");
    }
}
