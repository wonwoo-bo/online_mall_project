package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家规格回收站实体类
 */
@Data
public class MerchantSpecRecycle {
    private Integer id;
    private Integer specTypeId;
    private Integer merchantId;
    private String name;
    private Integer productCount;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime originalDeleteTime;
    private LocalDateTime createTime;
}
