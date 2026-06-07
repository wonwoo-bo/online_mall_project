package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSku {
    private Integer id;
    private Integer productId;
    private Integer merchantId;
    private String skuCode;
    private String skuName;
    private String specs;
    private String specsJson;
    private String specValues;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal marketPrice;
    private Integer stock;
    private Integer lowStock;
    private BigDecimal weight;
    private String barCode;
    private Integer sales;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
