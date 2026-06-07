package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Merchant {
    private Integer id;
    private String shopName;
    private String username;
    private String password;
    private String contactPhone;
    private String shopDesc;
    private String shopLogo;
    private String avatar;
    private String phone;
    private String intro;
    private String mainCategory;
    private String shopNotice;
    private Integer businessStatus;
    private String customerServicePhone;
    private String customerServiceOnline;
    private String shopAddress;
    private Integer auditStatus;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
