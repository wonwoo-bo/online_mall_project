package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantPaymentAccount;
import com.mall.module.product.mapper.MerchantPaymentAccountMapper;
import com.mall.module.product.service.MerchantPaymentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MerchantPaymentAccountServiceImpl implements MerchantPaymentAccountService {

    @Autowired
    private MerchantPaymentAccountMapper merchantPaymentAccountMapper;

    @Override
    public List<MerchantPaymentAccount> getAccountsByMerchantId(Integer merchantId) {
        return merchantPaymentAccountMapper.selectByMerchantId(merchantId);
    }

    @Override
    public MerchantPaymentAccount getAccountById(Integer id) {
        return merchantPaymentAccountMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createAccount(MerchantPaymentAccount account) {
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        if (account.getIsDefault() == null) {
            account.setIsDefault(0);
        }
        if (account.getStatus() == null) {
            account.setStatus(1);
        }
        merchantPaymentAccountMapper.insert(account);
        
        if (account.getIsDefault() == 1) {
            merchantPaymentAccountMapper.updateDefault(account.getMerchantId(), account.getId());
        }
    }

    @Override
    @Transactional
    public void updateAccount(MerchantPaymentAccount account) {
        merchantPaymentAccountMapper.update(account);
        
        if (account.getIsDefault() != null && account.getIsDefault() == 1) {
            merchantPaymentAccountMapper.updateDefault(account.getMerchantId(), account.getId());
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        merchantPaymentAccountMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefaultAccount(Integer merchantId, Integer id) {
        merchantPaymentAccountMapper.updateDefault(merchantId, id);
    }

    @Override
    public MerchantPaymentAccount getDefaultAccount(Integer merchantId) {
        return merchantPaymentAccountMapper.selectDefaultByMerchantId(merchantId);
    }
}
