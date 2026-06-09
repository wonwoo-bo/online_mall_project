package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewExplanation {
    private Integer id;
    private Integer reviewId;
    private Integer merchantId;
    private String content;
    private Integer editCount;
    private LocalDateTime lastEditTime;
    private LocalDateTime createTime;
}
