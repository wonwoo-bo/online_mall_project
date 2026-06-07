package com.mall.module.product.mapper;

import com.mall.module.product.entity.PromotionProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PromotionProductMapper {
    int insert(PromotionProduct promotionProduct);
    int updateById(PromotionProduct promotionProduct);
    int deleteById(@Param("id") Integer id);
    int deleteByPromotionId(@Param("promotionId") Integer promotionId);
    PromotionProduct selectById(@Param("id") Integer id);
    List<PromotionProduct> selectByPromotionId(@Param("promotionId") Integer promotionId);
}