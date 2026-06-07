package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserCoupon {
    private Integer id;
    private Integer userId;
    private Integer couponId;
    private Integer status;
    private Integer orderId;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;
}