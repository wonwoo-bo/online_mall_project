package com.mall.module.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemConfig {
    private Integer id;
    private String configKey;
    private String configValue;
    private String configName;
    private String description;
    private String category;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}