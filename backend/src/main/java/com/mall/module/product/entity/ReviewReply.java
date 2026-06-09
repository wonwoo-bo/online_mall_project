package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewReply {
    private Integer id;
    private Integer reviewId;
    private Integer merchantId;
    private String content;
    private LocalDateTime createTime;
}
