package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品回收站实体类
 */
@Data
public class ProductRecycle {
    private Integer id;
    private Integer productId;
    private Integer merchantId;
    private String productName;
    private String coverImg;
    private BigDecimal price;
    private Integer stock;
    private Integer deleteType;
    private String deleteReason;
    private Integer hasTransaction;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime originalDeleteTime;
    private LocalDateTime createTime;
}
