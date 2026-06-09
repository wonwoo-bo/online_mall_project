package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Category {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer level;
    private Integer sortOrder;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;

    // 子分类列表（非数据库字段）
    private List<Category> children;
}
