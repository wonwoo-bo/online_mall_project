package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer merchantId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String specs;
    private LocalDateTime createTime;

    // 关联查询的商品信息
    private String productImg;
    private String merchantName;
    private String brandName;
}
