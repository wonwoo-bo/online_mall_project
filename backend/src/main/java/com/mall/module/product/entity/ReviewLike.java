package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewLike {
    private Integer id;
    private Integer reviewId;
    private Integer userId;
    private LocalDateTime createTime;
}
