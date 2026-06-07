package com.mall.module.product.service;

import com.mall.module.product.entity.SpecType;
import com.mall.module.product.entity.SpecValue;

import java.util.List;
import java.util.Map;

public interface SpecService {
    SpecType addSpecType(Integer merchantId, SpecType specType);
    SpecType updateSpecType(Integer merchantId, Integer typeId, SpecType specType);
    boolean deleteSpecType(Integer merchantId, Integer typeId);
    SpecType getSpecTypeById(Integer merchantId, Integer typeId);
    List<SpecType> getSpecTypeList(Integer merchantId);
    List<SpecType> getSpecTypeTree(Integer merchantId);

    SpecValue addSpecValue(Integer merchantId, SpecValue specValue);
    SpecValue updateSpecValue(Integer merchantId, Integer valueId, SpecValue specValue);
    boolean deleteSpecValue(Integer merchantId, Integer valueId);
    SpecValue getSpecValueById(Integer merchantId, Integer valueId);
    List<SpecValue> getSpecValueList(Integer merchantId, Integer typeId);

    List<Map<String, Object>> getRecycleList(Integer merchantId);
    void restoreSpecType(Integer merchantId, Integer typeId);
    void forceDeleteSpecType(Integer merchantId, Integer typeId);
}