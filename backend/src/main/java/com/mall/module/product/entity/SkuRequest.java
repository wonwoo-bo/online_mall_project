package com.mall.module.product.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuRequest {
    private Integer id;
    private String skuCode;
    private String skuName;
    private String specs;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal marketPrice;
    private Integer stock;
    private String specValues;
}
