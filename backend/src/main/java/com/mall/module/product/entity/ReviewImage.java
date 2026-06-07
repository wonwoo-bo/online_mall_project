package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewImage {
    private Integer id;
    private Integer reviewId;
    private String imageUrl;
    private LocalDateTime createTime;
}
