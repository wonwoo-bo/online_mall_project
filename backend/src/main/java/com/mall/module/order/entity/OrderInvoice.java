package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderInvoice {
    private Integer id;
    private Integer orderId;
    private Integer merchantId;
    private String invoiceNo;
    private Integer invoiceType;
    private String title;
    private String taxNo;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime cancelTime;
}