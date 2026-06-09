package com.mall.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberInfo {
    private Integer id;
    private Integer userId;
    private String memberLevel;
    private Integer points;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
