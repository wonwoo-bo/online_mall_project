package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存操作日志实体类
 * 记录库存的每一次变动
 */
@Data
public class StockOperationLog {
    private Long id;
    private Integer merchantId;
    private Integer productId;
    private Integer orderId;
    private String operationType;
    private Integer quantity;
    private Integer beforeStock;
    private Integer afterStock;
    private Integer operatorId;
    private String operatorName;
    private String operationReason;
    private String remark;
    private LocalDateTime createTime;
}
