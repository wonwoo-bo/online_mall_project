package com.mall.module.product.service.impl;

import com.mall.module.product.entity.Brand;
import com.mall.module.product.mapper.BrandMapper;
import com.mall.module.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public Brand addBrand(Integer merchantId, Brand brand) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new RuntimeException("品牌名称不能为空");
        }

        if (brandMapper.checkNameExists(merchantId, brand.getName().trim(), null) > 0) {
            throw new RuntimeException("品牌名称已存在");
        }

        brand.setMerchantId(merchantId);
        if (brand.getStatus() == null) {
            brand.setStatus(1);
        }

        brandMapper.insert(brand);
        return brandMapper.selectById(brand.getId(), merchantId);
    }

    @Override
    public Brand updateBrand(Integer merchantId, Integer brandId, Brand brand) {
        Brand existing = brandMapper.selectById(brandId, merchantId);
        if (existing == null) {
            throw new RuntimeException("品牌不存在");
        }

        if (brand.getName() != null) {
            String name = brand.getName().trim();
            if (name.isEmpty()) {
                throw new RuntimeException("品牌名称不能为空");
            }
            if (brandMapper.checkNameExists(merchantId, name, brandId) > 0) {
                throw new RuntimeException("品牌名称已存在");
            }
            brand.setName(name);
        }

        if (brand.getStatus() == null) {
            brand.setStatus(existing.getStatus());
        }

        brand.setId(brandId);
        brand.setMerchantId(merchantId);
        brandMapper.updateById(brand);
        return brandMapper.selectById(brandId, merchantId);
    }

    @Override
    public boolean deleteBrand(Integer merchantId, Integer brandId) {
        Brand brand = brandMapper.selectById(brandId, merchantId);
        if (brand == null) {
            throw new RuntimeException("品牌不存在");
        }

        if (brandMapper.checkBrandHasProducts(brandId, merchantId) > 0) {
            throw new RuntimeException("该品牌下存在商品，禁止删除");
        }

        return brandMapper.moveToRecycle(brandId, merchantId) > 0;
    }

    @Override
    public Brand getBrandById(Integer merchantId, Integer brandId) {
        return brandMapper.selectById(brandId, merchantId);
    }

    @Override
    public List<Brand> getBrandList(Integer merchantId) {
        return brandMapper.selectByMerchantId(merchantId);
    }

    @Override
    public boolean updateBrandStatus(Integer merchantId, Integer brandId, Integer status) {
        Brand brand = brandMapper.selectById(brandId, merchantId);
        if (brand == null) {
            throw new RuntimeException("品牌不存在");
        }
        return brandMapper.updateStatus(brandId, merchantId, status) > 0;
    }

    @Override
    public List<Map<String, Object>> getRecycleList(Integer merchantId) {
        return brandMapper.selectRecycleList(merchantId);
    }

    @Override
    public void restoreBrand(Integer merchantId, Integer brandId) {
        brandMapper.restoreBrand(brandId, merchantId);
    }

    @Override
    public void forceDeleteBrand(Integer merchantId, Integer brandId) {
        brandMapper.deleteById(brandId, merchantId);
    }
}