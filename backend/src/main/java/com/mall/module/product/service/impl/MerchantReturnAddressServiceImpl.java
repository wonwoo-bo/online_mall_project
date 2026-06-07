package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantReturnAddress;
import com.mall.module.product.mapper.MerchantReturnAddressMapper;
import com.mall.module.product.service.MerchantReturnAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MerchantReturnAddressServiceImpl implements MerchantReturnAddressService {

    @Autowired
    private MerchantReturnAddressMapper addressMapper;

    @Override
    public List<MerchantReturnAddress> getAddressList(Integer merchantId) {
        return addressMapper.selectByMerchantId(merchantId);
    }

    @Override
    public MerchantReturnAddress getAddressById(Integer id) {
        return addressMapper.selectById(id);
    }

    @Override
    public MerchantReturnAddress getDefaultAddress(Integer merchantId) {
        return addressMapper.selectDefaultByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void addAddress(MerchantReturnAddress address) {
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        if (address.getIsDefault() == 1) {
            addressMapper.updateNonDefaultByMerchantId(address.getMerchantId());
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(MerchantReturnAddress address) {
        MerchantReturnAddress existing = addressMapper.selectById(address.getId());
        if (existing == null || !existing.getMerchantId().equals(address.getMerchantId())) {
            throw new RuntimeException("地址不存在或无权修改");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.updateNonDefaultByMerchantId(address.getMerchantId());
        }
        addressMapper.update(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer id, Integer merchantId) {
        MerchantReturnAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("地址不存在或无权删除");
        }
        addressMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Integer id, Integer merchantId) {
        MerchantReturnAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("地址不存在或无权修改");
        }
        addressMapper.updateNonDefaultByMerchantId(merchantId);
        MerchantReturnAddress update = new MerchantReturnAddress();
        update.setId(id);
        update.setIsDefault(1);
        addressMapper.update(update);
    }
}
