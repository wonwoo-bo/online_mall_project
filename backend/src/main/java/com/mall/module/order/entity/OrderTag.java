package com.mall.module.order.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderTag {
    private Integer id;
    private Integer orderId;
    private Integer merchantId;
    private String tagName;
    private String tagColor;
    private LocalDateTime createTime;
}