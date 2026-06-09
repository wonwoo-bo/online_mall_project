package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Promotion {
    private Integer id;
    private Integer merchantId;
    private String name;
    private Integer type;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal minAmount;
    private BigDecimal reduceAmount;
    private BigDecimal discountRate;
    private BigDecimal freeShippingThreshold;
    private Integer status;
    private Integer isRecommend;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}