package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantOperationLog {
    private Long id;
    private Integer merchantId;
    private Integer operatorId;
    private String operatorName;
    private String operationType;
    private String operationDesc;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String ipAddress;
    private Integer status;
    private String errorMsg;
    private Long executeTime;
    private LocalDateTime createTime;
}
