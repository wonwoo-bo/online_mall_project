package com.mall.module.order.service;

import com.mall.module.order.entity.OrderInvoice;

import java.util.List;
import java.util.Map;

public interface OrderInvoiceService {
    OrderInvoice generateInvoice(Integer orderId, Integer merchantId, String title, String taxNo, Integer invoiceType);
    
    OrderInvoice getInvoiceByOrderId(Integer orderId);
    
    void cancelInvoice(Integer invoiceId, Integer merchantId);
    
    List<OrderInvoice> getInvoicesByMerchantId(Integer merchantId);
    
    Map<String, Object> getInvoiceStatistics(Integer merchantId);
}