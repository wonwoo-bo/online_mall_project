package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantSubAccount {
    private Integer id;
    private Integer merchantId;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
