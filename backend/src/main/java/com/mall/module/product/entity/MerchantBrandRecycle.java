package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家品牌回收站实体类
 */
@Data
public class MerchantBrandRecycle {
    private Integer id;
    private Integer brandId;
    private Integer merchantId;
    private String name;
    private String logo;
    private Integer productCount;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime originalDeleteTime;
    private LocalDateTime createTime;
}
