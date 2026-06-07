package com.mall.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddress {
    private Integer id;
    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}