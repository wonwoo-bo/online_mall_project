package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Integer id;
    private Integer merchantId;
    private Integer categoryId;
    private Integer brandId;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private BigDecimal weight;
    private String unit;
    private String coverImg;
    private String mainImage;
    private String images;
    private String skuCode;
    private String barCode;
    private String productSn;
    private String description;
    private Integer status;
    private Integer isRecommended;
    private Integer isNew;
    private Integer freeShipping;
    private BigDecimal shippingFee;
    private Integer minStock;
    private Integer views;
    private Integer sellCount;
    private Integer actualSales;
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;

    // 评价数量（非数据库字段，用于关联查询）
    private Integer reviewCount;

    // 商家信息（非数据库字段，用于关联查询）
    private String storeName;
    private String storeAddress;

    // 分类名称（非数据库字段，用于关联查询）
    private String categoryName;

    // 品牌名称（非数据库字段，用于关联查询）
    private String brandName;
}
