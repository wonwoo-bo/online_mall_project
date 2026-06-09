package com.mall.module.admin.service;

import com.mall.module.admin.entity.Admin;
import com.mall.module.admin.entity.SystemConfig;
import com.mall.module.product.entity.Merchant;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Admin login(String username, String password);
    
    Admin getAdminInfo(Integer adminId);
    
    void updateAdminInfo(Integer adminId, Admin admin);
    
    void updatePassword(Integer adminId, String oldPassword, String newPassword);
    
    List<Admin> getAllAdmins();
    
    void addAdmin(Admin admin);
    
    void deleteAdmin(Integer adminId);
    
    List<Merchant> getMerchantsByStatus(Integer status);
    
    List<Merchant> getAllMerchants();
    
    void approveMerchant(Integer merchantId, Integer status);
    
    List<SystemConfig> getAllConfigs();
    
    List<SystemConfig> getConfigsByCategory(String category);
    
    SystemConfig getConfigByKey(String configKey);
    
    void updateConfig(Integer id, String configValue);
    
    void updateConfigByKey(String configKey, String configValue);
    
    void addConfig(SystemConfig config);
    
    void deleteConfig(Integer id);
}