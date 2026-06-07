package com.mall.module.product.mapper;

import com.mall.module.product.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    List<ProductImage> selectByProductId(@Param("productId") Integer productId);

    void insert(ProductImage productImage);

    void deleteByProductId(@Param("productId") Integer productId);
}
