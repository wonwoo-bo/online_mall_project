package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionProduct {
    private Integer id;
    private Integer promotionId;
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private BigDecimal promotionPrice;
    private Integer stock;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
}