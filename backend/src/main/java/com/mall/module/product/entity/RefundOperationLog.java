package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RefundOperationLog {
    private Integer id;
    private Integer refundId;
    private Integer merchantId;
    private String operatorType;
    private Integer operatorId;
    private String operatorName;
    private String operationType;
    private String operationDetail;
    private String remark;
    private Integer oldStatus;
    private Integer newStatus;
    private LocalDateTime createTime;
}
