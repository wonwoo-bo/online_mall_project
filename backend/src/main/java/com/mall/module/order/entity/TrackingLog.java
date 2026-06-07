package com.mall.module.order.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingLog {
    private Integer id;
    private Integer orderId;
    private String trackingNo;
    private String location;
    private String status;
    private String description;
    private LocalDateTime createTime;
    private Integer sortOrder;
}