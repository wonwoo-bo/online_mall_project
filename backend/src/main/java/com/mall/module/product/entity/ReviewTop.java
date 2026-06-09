package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewTop {
    private Integer id;
    private Integer reviewId;
    private Integer merchantId;
    private Integer productId;
    private LocalDateTime topTime;
}
