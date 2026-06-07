package com.mall.module.product.mapper;

import com.mall.module.product.entity.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PromotionMapper {
    int insert(Promotion promotion);
    int updateById(Promotion promotion);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    Promotion selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<Promotion> selectByMerchantId(@Param("merchantId") Integer merchantId);
    List<Promotion> selectActivePromotions(@Param("merchantId") Integer merchantId);
    int updateStatus(@Param("id") Integer id, @Param("merchantId") Integer merchantId, @Param("status") Integer status);
}