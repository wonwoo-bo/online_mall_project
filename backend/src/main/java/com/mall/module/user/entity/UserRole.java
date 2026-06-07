package com.mall.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole {
    private Integer id;
    private Integer userId;
    private String roleCode;
    private String roleName;
    private String permissions;
    private LocalDateTime createTime;
}
