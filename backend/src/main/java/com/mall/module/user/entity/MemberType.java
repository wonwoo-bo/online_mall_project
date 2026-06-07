package com.mall.module.user.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberType {
    private Integer id;
    private String levelName;
    private String levelCode;
    private BigDecimal price;
    private Integer durationDays;
    private Integer pointsBonus;
    private String privileges;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
