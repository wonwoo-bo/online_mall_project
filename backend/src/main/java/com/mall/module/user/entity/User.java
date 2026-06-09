package com.mall.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String nickname;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}