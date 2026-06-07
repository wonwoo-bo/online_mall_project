package com.mall.module.product.service.impl;

import com.mall.common.PasswordUtil;
import com.mall.module.product.dto.MerchantRegisterDTO;
import com.mall.module.product.entity.Merchant;
import com.mall.module.product.mapper.MerchantMapper;
import com.mall.module.product.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    @Transactional
    public void register(MerchantRegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        Merchant existingMerchant = merchantMapper.findByUsername(registerDTO.getUsername());
        if (existingMerchant != null) {
            throw new RuntimeException("用户名已存在");
        }

        Merchant merchant = new Merchant();
        merchant.setUsername(registerDTO.getUsername());
        merchant.setPassword(registerDTO.getPassword());
        merchant.setShopName(registerDTO.getShopName());
        merchant.setContactPhone(registerDTO.getContactPhone());
        merchant.setShopDesc(registerDTO.getShopDesc());
        merchant.setStatus(0);
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());

        merchantMapper.insert(merchant);
    }

    @Override
    public Merchant login(String username, String password) {
        Merchant merchant = merchantMapper.findByUsername(username);
        if (merchant == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!password.equals(merchant.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (merchant.getStatus() == 0) {
            throw new RuntimeException("账号待审核，请联系管理员");
        }

        return merchant;
    }

    @Override
    public Merchant getMerchantInfo(Integer id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        merchant.setPassword(null);
        return merchant;
    }

    @Override
    @Transactional
    public void updateMerchantInfo(Merchant merchant) {
        Merchant existingMerchant = merchantMapper.selectById(merchant.getId());
        if (existingMerchant == null) {
            throw new RuntimeException("商家不存在");
        }
        merchantMapper.update(merchant);
    }

    @Override
    @Transactional
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        if (!oldPassword.equals(merchant.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        merchantMapper.updatePassword(id, newPassword);
    }

    @Override
    @Transactional
    public void updateShopInfo(Merchant merchant) {
        Merchant existingMerchant = merchantMapper.selectById(merchant.getId());
        if (existingMerchant == null) {
            throw new RuntimeException("商家不存在");
        }
        
        if (merchant.getShopName() != null && !merchant.getShopName().equals(existingMerchant.getShopName())) {
            Merchant duplicateMerchant = merchantMapper.findByShopName(merchant.getShopName(), merchant.getId());
            if (duplicateMerchant != null) {
                throw new RuntimeException("店铺名称已被使用");
            }
        }
        
        merchantMapper.update(merchant);
    }

    @Override
    @Transactional
    public void toggleBusinessStatus(Integer id, Integer status) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setBusinessStatus(status);
        merchantMapper.update(merchant);
    }
}
