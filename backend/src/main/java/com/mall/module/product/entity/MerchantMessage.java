package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantMessage {
    private Long id;
    private Integer merchantId;
    private String messageType;
    private String title;
    private String content;
    private Integer relatedId;
    private Integer isRead;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
}
