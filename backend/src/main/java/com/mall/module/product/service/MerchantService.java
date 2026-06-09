package com.mall.module.product.service;

import com.mall.module.product.dto.MerchantRegisterDTO;
import com.mall.module.product.entity.Merchant;

public interface MerchantService {
    void register(MerchantRegisterDTO registerDTO);
    Merchant login(String username, String password);
    Merchant getMerchantInfo(Integer id);
    void updateMerchantInfo(Merchant merchant);
    void updatePassword(Integer id, String oldPassword, String newPassword);
    void updateShopInfo(Merchant merchant);
    void toggleBusinessStatus(Integer id, Integer status);
}
