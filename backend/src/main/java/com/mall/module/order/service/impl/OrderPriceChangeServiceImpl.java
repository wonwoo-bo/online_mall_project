package com.mall.module.order.service.impl;

import com.mall.module.order.entity.OrderPriceChange;
import com.mall.module.order.mapper.OrderPriceChangeMapper;
import com.mall.module.order.service.OrderPriceChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderPriceChangeServiceImpl implements OrderPriceChangeService {

    @Autowired
    private OrderPriceChangeMapper orderPriceChangeMapper;

    @Override
    public List<OrderPriceChange> getByOrderId(Integer orderId) {
        return orderPriceChangeMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderPriceChange> getByMerchantId(Integer merchantId) {
        return orderPriceChangeMapper.selectByMerchantId(merchantId);
    }

    @Override
    public void record(Integer orderId, Integer merchantId, BigDecimal oldAmount, BigDecimal newAmount, String reason, Integer operatorId, String operatorName) {
        OrderPriceChange record = new OrderPriceChange();
        record.setOrderId(orderId);
        record.setMerchantId(merchantId);
        record.setOldAmount(oldAmount);
        record.setNewAmount(newAmount);
        record.setChangeReason(reason);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        orderPriceChangeMapper.insert(record);
    }
}
