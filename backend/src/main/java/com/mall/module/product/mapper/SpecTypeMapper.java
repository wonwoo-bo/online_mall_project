package com.mall.module.product.mapper;

import com.mall.module.product.entity.SpecType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpecTypeMapper {
    int insert(SpecType specType);
    int updateById(SpecType specType);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    SpecType selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<SpecType> selectByMerchantId(@Param("merchantId") Integer merchantId);
    int checkNameExists(@Param("merchantId") Integer merchantId, @Param("name") String name, @Param("excludeId") Integer excludeId);
    int moveToRecycle(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<Map<String, Object>> selectRecycleList(@Param("merchantId") Integer merchantId);
    int restoreSpecType(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int forceDeleteSpecType(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
}