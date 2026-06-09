package com.mall.module.order.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer skuId;
    private String specs;
    private Integer quantity;
    private Boolean selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联查询的商品信息
    private String productName;
    private java.math.BigDecimal productPrice;
    private String productImg;
    private Integer merchantId;
    private String merchantName;
}
