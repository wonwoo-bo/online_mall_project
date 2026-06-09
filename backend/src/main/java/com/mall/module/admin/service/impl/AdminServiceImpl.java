package com.mall.module.admin.service.impl;

import com.mall.module.admin.entity.Admin;
import com.mall.module.admin.entity.SystemConfig;
import com.mall.module.admin.mapper.AdminMapper;
import com.mall.module.admin.mapper.SystemConfigMapper;
import com.mall.module.admin.service.AdminService;
import com.mall.module.product.entity.Merchant;
import com.mall.module.product.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public Admin login(String username, String password) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (admin.getStatus() == 0) {
            throw new RuntimeException("管理员已被禁用");
        }
        if (!password.equals(admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return admin;
    }

    @Override
    public Admin getAdminInfo(Integer adminId) {
        return adminMapper.findById(adminId);
    }

    @Override
    public void updateAdminInfo(Integer adminId, Admin admin) {
        Admin existing = adminMapper.findById(adminId);
        if (existing == null) {
            throw new RuntimeException("管理员不存在");
        }
        existing.setNickname(admin.getNickname());
        existing.setPhone(admin.getPhone());
        existing.setUpdateTime(LocalDateTime.now());
        adminMapper.update(existing);
    }

    @Override
    public void updatePassword(Integer adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.findById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (!oldPassword.equals(admin.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        admin.setPassword(newPassword);
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminMapper.findAll();
    }

    @Override
    @Transactional
    public void addAdmin(Admin admin) {
        Admin existing = adminMapper.findByUsername(admin.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insert(admin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Integer adminId) {
        Admin admin = adminMapper.findById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        adminMapper.deleteById(adminId);
    }

    @Override
    public List<Merchant> getMerchantsByStatus(Integer status) {
        return merchantMapper.findByStatus(status);
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return merchantMapper.findAll();
    }

    @Override
    @Transactional
    public void approveMerchant(Integer merchantId, Integer status) {
        Merchant merchant = merchantMapper.findById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        merchant.setStatus(status);
        merchant.setUpdateTime(LocalDateTime.now());
        merchantMapper.update(merchant);
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return systemConfigMapper.findAll();
    }

    @Override
    public List<SystemConfig> getConfigsByCategory(String category) {
        return systemConfigMapper.findByCategory(category);
    }

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        return systemConfigMapper.findByConfigKey(configKey);
    }

    @Override
    @Transactional
    public void updateConfig(Integer id, String configValue) {
        SystemConfig config = systemConfigMapper.findById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }
        config.setConfigValue(configValue);
        config.setUpdateTime(LocalDateTime.now());
        systemConfigMapper.update(config);
    }

    @Override
    @Transactional
    public void updateConfigByKey(String configKey, String configValue) {
        systemConfigMapper.updateByConfigKey(configKey, configValue);
    }

    @Override
    @Transactional
    public void addConfig(SystemConfig config) {
        SystemConfig existing = systemConfigMapper.findByConfigKey(config.getConfigKey());
        if (existing != null) {
            throw new RuntimeException("配置键已存在");
        }
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        systemConfigMapper.insert(config);
    }

    @Override
    @Transactional
    public void deleteConfig(Integer id) {
        SystemConfig config = systemConfigMapper.findById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }
        systemConfigMapper.deleteById(id);
    }
}