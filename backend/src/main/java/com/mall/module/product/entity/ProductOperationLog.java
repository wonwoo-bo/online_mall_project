package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品操作日志实体类
 * 记录商家对商品的关键操作
 */
@Data
public class ProductOperationLog {
    private Long id;
    private Integer merchantId;
    private Integer productId;
    private Integer operatorId;
    private String operatorName;
    private String operationType;
    private String operationDesc;
    private String beforeValue;
    private String afterValue;
    private String ipAddress;
    private LocalDateTime createTime;
}
