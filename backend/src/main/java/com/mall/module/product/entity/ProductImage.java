package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImage {
    private Integer id;
    private Integer productId;
    private Integer merchantId;
    private String imageUrl;
    private Integer sortOrder;
    private Integer isMain;
    private Integer imageType;
    private Integer fileSize;
    private String fileName;
    private LocalDateTime createTime;
}
