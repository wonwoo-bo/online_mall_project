package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantBrand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantBrandMapper {
    List<MerchantBrand> selectByMerchantId(@Param("merchantId") Integer merchantId);
    MerchantBrand selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int insert(MerchantBrand brand);
    int updateById(MerchantBrand brand);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int updateStatus(@Param("id") Integer id, @Param("merchantId") Integer merchantId, @Param("status") Integer status);
    int checkNameExists(@Param("merchantId") Integer merchantId, @Param("name") String name, @Param("excludeId") Integer excludeId);
}
