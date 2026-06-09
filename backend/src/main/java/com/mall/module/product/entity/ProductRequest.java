package com.mall.module.product.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private Integer id;
    private Integer merchantId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private Integer status;
    private String coverImg;
    private String mainImage;
    private List<String> mainImages;
    private List<String> detailImages;
    // 规格相关
    private List<SkuRequest> skus;
    // 价格筛选
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    // 库存筛选
    private Integer minStock;
    private Integer maxStock;
    // 上下架状态筛选
    private Integer statusFilter;
}

