package com.mall.module.product.service;

import com.mall.module.product.entity.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    Brand addBrand(Integer merchantId, Brand brand);
    Brand updateBrand(Integer merchantId, Integer brandId, Brand brand);
    boolean deleteBrand(Integer merchantId, Integer brandId);
    Brand getBrandById(Integer merchantId, Integer brandId);
    List<Brand> getBrandList(Integer merchantId);
    boolean updateBrandStatus(Integer merchantId, Integer brandId, Integer status);
    List<Map<String, Object>> getRecycleList(Integer merchantId);
    void restoreBrand(Integer merchantId, Integer brandId);
    void forceDeleteBrand(Integer merchantId, Integer brandId);
}