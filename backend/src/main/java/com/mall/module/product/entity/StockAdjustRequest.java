package com.mall.module.product.entity;

import lombok.Data;

@Data
public class StockAdjustRequest {
    private Integer quantity;
    private String reason;
}