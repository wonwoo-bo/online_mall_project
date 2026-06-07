package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Integer id;
    private Integer merchantId;
    private String name;
    private Integer type;
    private BigDecimal faceValue;
    private BigDecimal minAmount;
    private Integer totalCount;
    private Integer receivedCount;
    private Integer usedCount;
    private Integer perUserLimit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}