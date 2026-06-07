package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantBrand;

import java.util.List;

public interface MerchantBrandService {
    List<MerchantBrand> getBrandList(Integer merchantId);
    MerchantBrand getBrandById(Integer merchantId, Integer id);
    MerchantBrand addBrand(Integer merchantId, MerchantBrand brand);
    MerchantBrand updateBrand(Integer merchantId, Integer id, MerchantBrand brand);
    void deleteBrand(Integer merchantId, Integer id);
    void updateBrandStatus(Integer merchantId, Integer id, Integer status);
}
