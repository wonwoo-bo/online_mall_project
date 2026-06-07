package com.mall.module.product.mapper;

import com.mall.module.product.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    List<ProductSku> selectByProductId(@Param("productId") Integer productId);

    ProductSku selectById(@Param("id") Integer id);

    void insert(ProductSku sku);
}
