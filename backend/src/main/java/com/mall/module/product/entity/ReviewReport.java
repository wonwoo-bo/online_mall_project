package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewReport {
    private Integer id;
    private Integer reviewId;
    private Integer merchantId;
    private String reason;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
