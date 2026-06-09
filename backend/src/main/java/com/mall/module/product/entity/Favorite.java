package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private LocalDateTime createTime;
}
