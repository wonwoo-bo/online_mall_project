package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderInvoice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderInvoiceMapper {
    void insert(OrderInvoice invoice);
    
    void update(OrderInvoice invoice);
    
    void cancelInvoice(Integer id);
    
    OrderInvoice selectById(Integer id);
    
    OrderInvoice selectByOrderId(Integer orderId);
    
    List<OrderInvoice> selectByMerchantId(Integer merchantId);
    
    List<OrderInvoice> selectByMerchantIdWithStatus(Integer merchantId, Integer status);
}