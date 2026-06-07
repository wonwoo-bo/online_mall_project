package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantBrand {
    private Integer id;
    private Integer merchantId;
    private String name;
    private String logo;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
