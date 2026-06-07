package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantPaymentAccount;

import java.util.List;

public interface MerchantPaymentAccountService {
    List<MerchantPaymentAccount> getAccountsByMerchantId(Integer merchantId);
    MerchantPaymentAccount getAccountById(Integer id);
    void createAccount(MerchantPaymentAccount account);
    void updateAccount(MerchantPaymentAccount account);
    void deleteAccount(Integer id);
    void setDefaultAccount(Integer merchantId, Integer id);
    MerchantPaymentAccount getDefaultAccount(Integer merchantId);
}
