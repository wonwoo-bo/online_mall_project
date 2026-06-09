package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpecType {
    private Integer id;
    private Integer merchantId;
    private String name;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
    private List<SpecValue> values;
}