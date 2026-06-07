package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderPriceChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderPriceChangeMapper {
    List<OrderPriceChange> selectByOrderId(@Param("orderId") Integer orderId);
    List<OrderPriceChange> selectByMerchantId(@Param("merchantId") Integer merchantId);
    int insert(OrderPriceChange record);
}
