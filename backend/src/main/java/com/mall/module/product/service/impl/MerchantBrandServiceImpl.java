package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantBrand;
import com.mall.module.product.mapper.MerchantBrandMapper;
import com.mall.module.product.service.MerchantBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantBrandServiceImpl implements MerchantBrandService {

    @Autowired
    private MerchantBrandMapper merchantBrandMapper;

    @Override
    public List<MerchantBrand> getBrandList(Integer merchantId) {
        return merchantBrandMapper.selectByMerchantId(merchantId);
    }

    @Override
    public MerchantBrand getBrandById(Integer merchantId, Integer id) {
        return merchantBrandMapper.selectById(id, merchantId);
    }

    @Override
    public MerchantBrand addBrand(Integer merchantId, MerchantBrand brand) {
        int exists = merchantBrandMapper.checkNameExists(merchantId, brand.getName(), null);
        if (exists > 0) {
            throw new RuntimeException("品牌名称已存在");
        }
        brand.setMerchantId(merchantId);
        brand.setStatus(brand.getStatus() != null ? brand.getStatus() : 1);
        merchantBrandMapper.insert(brand);
        return brand;
    }

    @Override
    public MerchantBrand updateBrand(Integer merchantId, Integer id, MerchantBrand brand) {
        MerchantBrand existing = merchantBrandMapper.selectById(id, merchantId);
        if (existing == null) {
            throw new RuntimeException("品牌不存在");
        }
        int exists = merchantBrandMapper.checkNameExists(merchantId, brand.getName(), id);
        if (exists > 0) {
            throw new RuntimeException("品牌名称已存在");
        }
        brand.setId(id);
        brand.setMerchantId(merchantId);
        merchantBrandMapper.updateById(brand);
        return merchantBrandMapper.selectById(id, merchantId);
    }

    @Override
    public void deleteBrand(Integer merchantId, Integer id) {
        MerchantBrand existing = merchantBrandMapper.selectById(id, merchantId);
        if (existing == null) {
            throw new RuntimeException("品牌不存在");
        }
        merchantBrandMapper.deleteById(id, merchantId);
    }

    @Override
    public void updateBrandStatus(Integer merchantId, Integer id, Integer status) {
        MerchantBrand existing = merchantBrandMapper.selectById(id, merchantId);
        if (existing == null) {
            throw new RuntimeException("品牌不存在");
        }
        merchantBrandMapper.updateStatus(id, merchantId, status);
    }
}
