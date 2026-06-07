package com.mall.module.product.service.impl;

import com.mall.module.product.entity.SpecType;
import com.mall.module.product.entity.SpecValue;
import com.mall.module.product.mapper.SpecTypeMapper;
import com.mall.module.product.mapper.SpecValueMapper;
import com.mall.module.product.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecTypeMapper specTypeMapper;

    @Autowired
    private SpecValueMapper specValueMapper;

    @Override
    public SpecType addSpecType(Integer merchantId, SpecType specType) {
        if (specType.getName() == null || specType.getName().trim().isEmpty()) {
            throw new RuntimeException("规格名称不能为空");
        }

        if (specTypeMapper.checkNameExists(merchantId, specType.getName().trim(), null) > 0) {
            throw new RuntimeException("规格名称已存在");
        }

        specType.setMerchantId(merchantId);
        if (specType.getSortOrder() == null) {
            specType.setSortOrder(0);
        }
        if (specType.getStatus() == null) {
            specType.setStatus(1);
        }

        specTypeMapper.insert(specType);
        return specTypeMapper.selectById(specType.getId(), merchantId);
    }

    @Override
    public SpecType updateSpecType(Integer merchantId, Integer typeId, SpecType specType) {
        SpecType existing = specTypeMapper.selectById(typeId, merchantId);
        if (existing == null) {
            throw new RuntimeException("规格类型不存在");
        }

        if (specType.getName() != null) {
            String name = specType.getName().trim();
            if (name.isEmpty()) {
                throw new RuntimeException("规格名称不能为空");
            }
            if (specTypeMapper.checkNameExists(merchantId, name, typeId) > 0) {
                throw new RuntimeException("规格名称已存在");
            }
            specType.setName(name);
        }

        specType.setId(typeId);
        specType.setMerchantId(merchantId);
        specTypeMapper.updateById(specType);
        return specTypeMapper.selectById(typeId, merchantId);
    }

    @Override
    public boolean deleteSpecType(Integer merchantId, Integer typeId) {
        SpecType specType = specTypeMapper.selectById(typeId, merchantId);
        if (specType == null) {
            throw new RuntimeException("规格类型不存在");
        }

        return specTypeMapper.moveToRecycle(typeId, merchantId) > 0;
    }

    @Override
    public SpecType getSpecTypeById(Integer merchantId, Integer typeId) {
        return specTypeMapper.selectById(typeId, merchantId);
    }

    @Override
    public List<SpecType> getSpecTypeList(Integer merchantId) {
        return specTypeMapper.selectByMerchantId(merchantId);
    }

    @Override
    public List<SpecType> getSpecTypeTree(Integer merchantId) {
        List<SpecType> types = specTypeMapper.selectByMerchantId(merchantId);
        for (SpecType type : types) {
            List<SpecValue> values = specValueMapper.selectByTypeId(merchantId, type.getId());
            type.setValues(values);
        }
        return types;
    }

    @Override
    public SpecValue addSpecValue(Integer merchantId, SpecValue specValue) {
        if (specValue.getValue() == null || specValue.getValue().trim().isEmpty()) {
            throw new RuntimeException("规格值不能为空");
        }

        SpecType specType = specTypeMapper.selectById(specValue.getTypeId(), merchantId);
        if (specType == null) {
            throw new RuntimeException("规格类型不存在");
        }

        if (specValueMapper.checkValueExists(merchantId, specValue.getTypeId(), specValue.getValue().trim(), null) > 0) {
            throw new RuntimeException("规格值已存在");
        }

        specValue.setMerchantId(merchantId);
        if (specValue.getSortOrder() == null) {
            specValue.setSortOrder(0);
        }
        if (specValue.getStatus() == null) {
            specValue.setStatus(1);
        }

        specValueMapper.insert(specValue);
        return specValueMapper.selectById(specValue.getId(), merchantId);
    }

    @Override
    public SpecValue updateSpecValue(Integer merchantId, Integer valueId, SpecValue specValue) {
        SpecValue existing = specValueMapper.selectById(valueId, merchantId);
        if (existing == null) {
            throw new RuntimeException("规格值不存在");
        }

        if (specValue.getValue() != null) {
            String value = specValue.getValue().trim();
            if (value.isEmpty()) {
                throw new RuntimeException("规格值不能为空");
            }
            if (specValueMapper.checkValueExists(merchantId, existing.getTypeId(), value, valueId) > 0) {
                throw new RuntimeException("规格值已存在");
            }
            specValue.setValue(value);
        }

        specValue.setId(valueId);
        specValue.setMerchantId(merchantId);
        specValue.setTypeId(existing.getTypeId());
        specValueMapper.updateById(specValue);
        return specValueMapper.selectById(valueId, merchantId);
    }

    @Override
    public boolean deleteSpecValue(Integer merchantId, Integer valueId) {
        SpecValue specValue = specValueMapper.selectById(valueId, merchantId);
        if (specValue == null) {
            throw new RuntimeException("规格值不存在");
        }
        return specValueMapper.deleteById(valueId, merchantId) > 0;
    }

    @Override
    public SpecValue getSpecValueById(Integer merchantId, Integer valueId) {
        return specValueMapper.selectById(valueId, merchantId);
    }

    @Override
    public List<SpecValue> getSpecValueList(Integer merchantId, Integer typeId) {
        return specValueMapper.selectByTypeId(merchantId, typeId);
    }

    @Override
    public List<Map<String, Object>> getRecycleList(Integer merchantId) {
        return specTypeMapper.selectRecycleList(merchantId);
    }

    @Override
    public void restoreSpecType(Integer merchantId, Integer typeId) {
        specTypeMapper.restoreSpecType(typeId, merchantId);
    }

    @Override
    public void forceDeleteSpecType(Integer merchantId, Integer typeId) {
        specValueMapper.deleteByTypeId(typeId, merchantId);
        specTypeMapper.deleteById(typeId, merchantId);
    }
}