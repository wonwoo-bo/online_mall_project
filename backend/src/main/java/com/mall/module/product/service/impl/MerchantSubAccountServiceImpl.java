package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantSubAccount;
import com.mall.module.product.entity.MerchantSubAccountPermission;
import com.mall.module.product.mapper.MerchantSubAccountMapper;
import com.mall.module.product.mapper.MerchantSubAccountPermissionMapper;
import com.mall.module.product.service.MerchantSubAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantSubAccountServiceImpl implements MerchantSubAccountService {

    @Autowired
    private MerchantSubAccountMapper merchantSubAccountMapper;

    @Autowired
    private MerchantSubAccountPermissionMapper merchantSubAccountPermissionMapper;

    private static final Map<String, String> PERMISSION_MAP = new HashMap<>();
    static {
        PERMISSION_MAP.put("PRODUCT", "商品管理");
        PERMISSION_MAP.put("ORDER", "订单管理");
        PERMISSION_MAP.put("REFUND", "售后管理");
        PERMISSION_MAP.put("FINANCE", "财务管理");
        PERMISSION_MAP.put("SETTING", "店铺设置");
    }

    @Override
    public List<MerchantSubAccount> getSubAccountsByMerchantId(Integer merchantId) {
        return merchantSubAccountMapper.selectByMerchantId(merchantId);
    }

    @Override
    public MerchantSubAccount getSubAccountById(Integer id) {
        return merchantSubAccountMapper.selectById(id);
    }

    @Override
    public MerchantSubAccount getSubAccountByUsername(String username) {
        return merchantSubAccountMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public void createSubAccount(MerchantSubAccount subAccount, List<String> permissionCodes) {
        MerchantSubAccount existing = merchantSubAccountMapper.selectByUsername(subAccount.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        subAccount.setCreateTime(LocalDateTime.now());
        subAccount.setUpdateTime(LocalDateTime.now());
        if (subAccount.getStatus() == null) {
            subAccount.setStatus(1);
        }
        merchantSubAccountMapper.insert(subAccount);
        
        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            List<MerchantSubAccountPermission> permissions = new ArrayList<>();
            for (String code : permissionCodes) {
                if (PERMISSION_MAP.containsKey(code)) {
                    MerchantSubAccountPermission perm = new MerchantSubAccountPermission();
                    perm.setSubAccountId(subAccount.getId());
                    perm.setMerchantId(subAccount.getMerchantId());
                    perm.setPermissionCode(code);
                    perm.setPermissionName(PERMISSION_MAP.get(code));
                    perm.setCreateTime(LocalDateTime.now());
                    permissions.add(perm);
                }
            }
            if (!permissions.isEmpty()) {
                merchantSubAccountPermissionMapper.batchInsert(permissions);
            }
        }
    }

    @Override
    @Transactional
    public void updateSubAccount(MerchantSubAccount subAccount, List<String> permissionCodes) {
        merchantSubAccountMapper.update(subAccount);
        
        if (permissionCodes != null) {
            merchantSubAccountPermissionMapper.deleteBySubAccountId(subAccount.getId());
            
            if (!permissionCodes.isEmpty()) {
                List<MerchantSubAccountPermission> permissions = new ArrayList<>();
                for (String code : permissionCodes) {
                    if (PERMISSION_MAP.containsKey(code)) {
                        MerchantSubAccountPermission perm = new MerchantSubAccountPermission();
                        perm.setSubAccountId(subAccount.getId());
                        perm.setMerchantId(subAccount.getMerchantId());
                        perm.setPermissionCode(code);
                        perm.setPermissionName(PERMISSION_MAP.get(code));
                        perm.setCreateTime(LocalDateTime.now());
                        permissions.add(perm);
                    }
                }
                if (!permissions.isEmpty()) {
                    merchantSubAccountPermissionMapper.batchInsert(permissions);
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateSubAccountPassword(Integer id, String newPassword) {
        merchantSubAccountMapper.updatePassword(id, newPassword);
    }

    @Override
    @Transactional
    public void deleteSubAccount(Integer id) {
        merchantSubAccountPermissionMapper.deleteBySubAccountId(id);
        merchantSubAccountMapper.deleteById(id);
    }

    @Override
    public List<MerchantSubAccountPermission> getPermissionsBySubAccountId(Integer subAccountId) {
        return merchantSubAccountPermissionMapper.selectBySubAccountId(subAccountId);
    }
}
