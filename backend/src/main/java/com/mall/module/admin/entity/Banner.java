package com.mall.module.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Banner {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private String color;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
