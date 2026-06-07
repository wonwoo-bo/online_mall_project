package com.mall.module.product.mapper;

import com.mall.module.product.entity.SpecValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpecValueMapper {
    int insert(SpecValue specValue);
    int updateById(SpecValue specValue);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int deleteByTypeId(@Param("typeId") Integer typeId, @Param("merchantId") Integer merchantId);
    SpecValue selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<SpecValue> selectByTypeId(@Param("merchantId") Integer merchantId, @Param("typeId") Integer typeId);
    List<SpecValue> selectByMerchantId(@Param("merchantId") Integer merchantId);
    int checkValueExists(@Param("merchantId") Integer merchantId, @Param("typeId") Integer typeId, @Param("value") String value, @Param("excludeId") Integer excludeId);
}