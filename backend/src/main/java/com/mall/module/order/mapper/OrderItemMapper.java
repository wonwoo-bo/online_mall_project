package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItem orderItem);
    
    void batchInsert(@Param("items") List<OrderItem> items);
    
    void deleteById(Integer id);
    
    void deleteByOrderId(Integer orderId);
    
    OrderItem selectById(Integer id);
    
    List<OrderItem> selectByOrderId(Integer orderId);
    
    List<OrderItem> selectByMerchantId(Integer merchantId);
    
    Integer countByOrderId(Integer orderId);
}