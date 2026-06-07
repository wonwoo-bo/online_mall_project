package com.mall.module.product.mapper;

import com.mall.module.product.entity.ProductOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductOperationLogMapper {

    /**
     * 插入操作日志
     */
    void insert(ProductOperationLog log);

    /**
     * 查询商品操作日志列表
     */
    List<ProductOperationLog> selectByProductId(@Param("productId") Integer productId,
                                                 @Param("merchantId") Integer merchantId);

    /**
     * 查询商家操作日志列表
     */
    List<ProductOperationLog> selectByMerchantId(@Param("merchantId") Integer merchantId,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);
}
