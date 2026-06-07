package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantReturnAddress;

import java.util.List;

public interface MerchantReturnAddressService {

    List<MerchantReturnAddress> getAddressList(Integer merchantId);

    MerchantReturnAddress getAddressById(Integer id);

    MerchantReturnAddress getDefaultAddress(Integer merchantId);

    void addAddress(MerchantReturnAddress address);

    void updateAddress(MerchantReturnAddress address);

    void deleteAddress(Integer id, Integer merchantId);

    void setDefaultAddress(Integer id, Integer merchantId);
}
