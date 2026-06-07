package com.mall.module.order.service;

import com.mall.module.order.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Payment createAndPay(Integer orderId, String payMethod);

    Payment getPaymentByOrderId(Integer orderId);

    // 以下为额外保留的接口方法
    Payment createPayment(Integer orderId, BigDecimal amount);

    Payment pay(Integer orderId, String payMethod);

    Payment refund(Integer orderId, BigDecimal refundAmount);

    Payment getPaymentById(Integer id);

    List<Payment> getPaymentListByStatus(Integer status);
}
