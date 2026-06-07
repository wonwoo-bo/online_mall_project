package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.Payment;
import com.mall.module.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public Result<Payment> createPayment(@RequestBody Map<String, Object> request) {
        try {
            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            String payMethod = request.containsKey("payMethod") ?
                    request.get("payMethod").toString() : "虚拟支付宝";

            Payment payment = paymentService.createAndPay(orderId, payMethod);
            return Result.success(payment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{orderId}")
    public Result<Payment> getPaymentDetail(@PathVariable Integer orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment == null) {
            return Result.error("支付记录不存在");
        }
        return Result.success(payment);
    }
}
