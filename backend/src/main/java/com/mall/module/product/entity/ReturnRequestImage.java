package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReturnRequestImage {
    private Integer id;
    private Integer returnRequestId;
    private String imageUrl;
    private LocalDateTime createTime;
}
