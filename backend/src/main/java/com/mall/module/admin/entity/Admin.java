package com.mall.module.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String nickname;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}