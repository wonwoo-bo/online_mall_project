package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantSubAccount;
import com.mall.module.product.entity.MerchantSubAccountPermission;

import java.util.List;

public interface MerchantSubAccountService {
    List<MerchantSubAccount> getSubAccountsByMerchantId(Integer merchantId);
    MerchantSubAccount getSubAccountById(Integer id);
    MerchantSubAccount getSubAccountByUsername(String username);
    void createSubAccount(MerchantSubAccount subAccount, List<String> permissionCodes);
    void updateSubAccount(MerchantSubAccount subAccount, List<String> permissionCodes);
    void updateSubAccountPassword(Integer id, String newPassword);
    void deleteSubAccount(Integer id);
    List<MerchantSubAccountPermission> getPermissionsBySubAccountId(Integer subAccountId);
}
