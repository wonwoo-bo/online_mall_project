package com.mall.module.order.service.impl;

import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.Payment;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.mapper.PaymentMapper;
import com.mall.module.order.service.PaymentService;
import com.mall.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    @Override
    public Payment createAndPay(Integer orderId, String payMethod) {
        Payment existing = paymentMapper.selectByOrderId(orderId);
        if (existing != null) {
            throw new RuntimeException("该订单已存在支付记录");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPayMethod(payMethod);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(1);
        payment.setPayTime(LocalDateTime.now());
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        paymentMapper.insert(payment);

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.update(order);

        int points = payment.getAmount().intValue();
        userService.addPoints(order.getUserId(), points);

        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Integer orderId) {
        return paymentMapper.selectByOrderId(orderId);
    }

    // 以下为额外保留的接口方法

    @Override
    public Payment createPayment(Integer orderId, BigDecimal amount) {
        Payment existing = paymentMapper.selectByOrderId(orderId);
        if (existing != null) {
            throw new RuntimeException("该订单已存在支付记录");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(0);
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        paymentMapper.insert(payment);
        return payment;
    }

    @Override
    public Payment pay(Integer orderId, String payMethod) {
        Payment payment = paymentMapper.selectByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }

        if (payment.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }

        payment.setPayMethod(payMethod);
        payment.setStatus(1);
        payment.setPayTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        Order order = orderMapper.selectById(orderId);
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.update(order);

        paymentMapper.update(payment);
        return payment;
    }

    @Override
    public Payment refund(Integer orderId, BigDecimal refundAmount) {
        Payment payment = paymentMapper.selectByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }

        if (payment.getStatus() != 1) {
            throw new RuntimeException("只有支付成功的订单才能退款");
        }

        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("退款金额不能超过支付金额");
        }

        BigDecimal currentRefund = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
        payment.setRefundAmount(currentRefund.add(refundAmount));
        payment.setRefundTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        if (payment.getRefundAmount().compareTo(payment.getAmount()) >= 0) {
            payment.setStatus(3);

            Order order = orderMapper.selectById(orderId);
            if (order != null) {
                order.setStatus(4);
                orderMapper.update(order);

                // 退款时扣减积分（之前支付时添加的积分）
                int deductPoints = payment.getAmount().intValue();
                if (deductPoints > 0) {
                    userService.deductPoints(order.getUserId(), deductPoints, "订单退款，扣减订单积分");
                }
            }
        }

        paymentMapper.update(payment);
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Integer id) {
        Payment payment = paymentMapper.selectById(id);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentListByStatus(Integer status) {
        return paymentMapper.selectByStatus(status);
    }
}
