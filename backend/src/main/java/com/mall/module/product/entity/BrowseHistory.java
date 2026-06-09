package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BrowseHistory {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private LocalDateTime browseTime;
}
