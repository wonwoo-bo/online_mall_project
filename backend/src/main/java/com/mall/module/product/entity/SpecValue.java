package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SpecValue {
    private Integer id;
    private Integer merchantId;
    private Integer typeId;
    private String value;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}