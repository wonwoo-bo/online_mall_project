package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Integer id;
    private Integer orderId;
    private String payMethod;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime payTime;
    private BigDecimal refundAmount;
    private LocalDateTime refundTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
