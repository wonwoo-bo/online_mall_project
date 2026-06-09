package com.mall.module.product.service.impl;

import com.mall.module.product.mapper.*;
import com.mall.module.product.service.MerchantRecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MerchantRecycleServiceImpl implements MerchantRecycleService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpecTypeMapper specTypeMapper;

    @Autowired
    private SpecValueMapper specValueMapper;

    @Override
    public List<Map<String, Object>> getRecycleList(Integer merchantId, String type) {
        List<Map<String, Object>> result;
        switch (type) {
            case "product":
                result = productMapper.selectRecycleList(merchantId);
                break;
            case "category":
                result = categoryMapper.selectRecycleList(merchantId);
                break;
            case "brand":
                result = brandMapper.selectRecycleList(merchantId);
                break;
            case "spec":
                result = specTypeMapper.selectRecycleList(merchantId);
                break;
            default:
                result = new ArrayList<>();
        }
        return result;
    }

    @Override
    public void restoreItem(Integer merchantId, String type, Integer id) {
        switch (type) {
            case "product":
                productMapper.restoreProduct(id);
                break;
            case "category":
                categoryMapper.restoreCategory(id);
                break;
            case "brand":
                brandMapper.restoreBrand(id, merchantId);
                break;
            case "spec":
                specTypeMapper.restoreSpecType(id, merchantId);
                break;
            default:
                throw new IllegalArgumentException("不支持的类型: " + type);
        }
    }

    @Override
    public void forceDeleteItem(Integer merchantId, String type, Integer id) {
        switch (type) {
            case "product":
                productMapper.forceDeleteProduct(id);
                break;
            case "category":
                categoryMapper.forceDeleteCategory(id);
                break;
            case "brand":
                brandMapper.forceDeleteBrand(id, merchantId);
                break;
            case "spec":
                specValueMapper.deleteByTypeId(id, merchantId);
                specTypeMapper.forceDeleteSpecType(id, merchantId);
                break;
            default:
                throw new IllegalArgumentException("不支持的类型: " + type);
        }
    }
}
