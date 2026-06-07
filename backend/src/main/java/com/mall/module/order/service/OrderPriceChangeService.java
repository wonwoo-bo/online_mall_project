package com.mall.module.order.service;

import com.mall.module.order.entity.OrderPriceChange;

import java.util.List;

public interface OrderPriceChangeService {
    List<OrderPriceChange> getByOrderId(Integer orderId);
    List<OrderPriceChange> getByMerchantId(Integer merchantId);
    void record(Integer orderId, Integer merchantId, java.math.BigDecimal oldAmount, java.math.BigDecimal newAmount, String reason, Integer operatorId, String operatorName);
}
