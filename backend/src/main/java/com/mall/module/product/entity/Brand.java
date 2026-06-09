package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Brand {
    private Integer id;
    private Integer merchantId;
    private String name;
    private String logo;
    private String description;
    private String firstLetter;
    private Integer sortOrder;
    private Integer productCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}