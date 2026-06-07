package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家分类回收站实体类
 */
@Data
public class MerchantCategoryRecycle {
    private Integer id;
    private Integer categoryId;
    private Integer merchantId;
    private String name;
    private Integer parentId;
    private Integer level;
    private Integer productCount;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime originalDeleteTime;
    private LocalDateTime createTime;
}
