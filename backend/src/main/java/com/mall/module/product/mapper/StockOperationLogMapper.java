package com.mall.module.product.mapper;

import com.mall.module.product.entity.StockOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockOperationLogMapper {

    /**
     * 插入库存操作日志
     */
    void insert(StockOperationLog log);

    /**
     * 查询商品库存操作日志
     */
    List<StockOperationLog> selectByProductId(@Param("productId") Integer productId,
                                              @Param("merchantId") Integer merchantId);

    /**
     * 查询商家库存操作日志
     */
    List<StockOperationLog> selectByMerchantId(@Param("merchantId") Integer merchantId,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);
}
