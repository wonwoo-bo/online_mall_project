package com.mall.module.admin.controller;

import com.mall.common.JwtUtil;
import com.mall.common.Result;
import com.mall.module.admin.entity.Admin;
import com.mall.module.admin.entity.Banner;
import com.mall.module.admin.entity.SystemConfig;
import com.mall.module.admin.mapper.BannerMapper;
import com.mall.module.admin.service.AdminService;
import com.mall.module.product.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginMap) {
        String username = loginMap.get("username");
        String password = loginMap.get("password");
        
        Admin admin = adminService.login(username, password);
        String token = JwtUtil.generateToken(admin.getId(), admin.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<Admin> getAdminInfo(@RequestAttribute("userId") Integer adminId) {
        Admin admin = adminService.getAdminInfo(adminId);
        return Result.success(admin);
    }

    @PutMapping("/info")
    public Result<Map<String, Object>> updateAdminInfo(
            @RequestAttribute("userId") Integer adminId,
            @RequestBody Admin admin) {
        adminService.updateAdminInfo(adminId, admin);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("更新成功", result);
    }

    @PutMapping("/password")
    public Result<Map<String, Object>> updatePassword(
            @RequestAttribute("userId") Integer adminId,
            @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        
        adminService.updatePassword(adminId, oldPassword, newPassword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("密码修改成功", result);
    }

    @GetMapping("/list")
    public Result<List<Admin>> getAdminList() {
        List<Admin> admins = adminService.getAllAdmins();
        return Result.success(admins);
    }

    @PostMapping("/add")
    public Result<Map<String, Object>> addAdmin(@RequestBody Admin admin) {
        adminService.addAdmin(admin);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("添加成功", result);
    }

    @DeleteMapping("/{adminId}")
    public Result<Map<String, Object>> deleteAdmin(@PathVariable Integer adminId) {
        adminService.deleteAdmin(adminId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("删除成功", result);
    }

    @GetMapping("/merchants")
    public Result<List<Merchant>> getMerchantList(@RequestParam(required = false) Integer status) {
        List<Merchant> merchants;
        if (status != null) {
            merchants = adminService.getMerchantsByStatus(status);
        } else {
            merchants = adminService.getAllMerchants();
        }
        return Result.success(merchants);
    }

    @PutMapping("/merchants/{merchantId}/status")
    public Result<Map<String, Object>> approveMerchant(
            @PathVariable Integer merchantId,
            @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        adminService.approveMerchant(merchantId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("操作成功", result);
    }

    @GetMapping("/configs")
    public Result<List<SystemConfig>> getConfigList(@RequestParam(required = false) String category) {
        List<SystemConfig> configs;
        if (category != null) {
            configs = adminService.getConfigsByCategory(category);
        } else {
            configs = adminService.getAllConfigs();
        }
        return Result.success(configs);
    }

    @GetMapping("/configs/{configKey}")
    public Result<SystemConfig> getConfigByKey(@PathVariable String configKey) {
        SystemConfig config = adminService.getConfigByKey(configKey);
        return Result.success(config);
    }

    @PutMapping("/configs/{id}")
    public Result<Map<String, Object>> updateConfig(
            @PathVariable Integer id,
            @RequestBody Map<String, String> valueMap) {
        String configValue = valueMap.get("configValue");
        adminService.updateConfig(id, configValue);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("更新成功", result);
    }

    @PostMapping("/configs")
    public Result<Map<String, Object>> addConfig(@RequestBody SystemConfig config) {
        adminService.addConfig(config);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("添加成功", result);
    }

    @DeleteMapping("/configs/{id}")
    public Result<Map<String, Object>> deleteConfig(@PathVariable Integer id) {
        adminService.deleteConfig(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("删除成功", result);
    }

    // ==================== 首页Banner管理 ====================

    @Autowired
    private BannerMapper bannerMapper;

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> list = bannerMapper.selectAll();
        return Result.success(list);
    }

    @GetMapping("/banners/active")
    public Result<List<Banner>> getActiveBanners() {
        List<Banner> list = bannerMapper.selectActive();
        return Result.success(list);
    }

    @PostMapping("/banners")
    public Result<String> createBanner(@RequestBody Banner banner) {
        if (banner.getTitle() == null || banner.getTitle().isEmpty()) {
            return Result.error("标题不能为空");
        }
        bannerMapper.insert(banner);
        return Result.success("创建成功");
    }

    @PutMapping("/banners/{id}")
    public Result<String> updateBanner(@PathVariable Integer id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerMapper.update(banner);
        return Result.success("更新成功");
    }

    @DeleteMapping("/banners/{id}")
    public Result<String> deleteBanner(@PathVariable Integer id) {
        bannerMapper.delete(id);
        return Result.success("删除成功");
    }
}