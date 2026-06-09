package com.mall.module.product.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReturnRequest {
    private Integer id;
    private Integer orderId;
    private Integer orderItemId;
    private Integer userId;
    private Integer productId;
    private Integer merchantId;
    private String reason;
    private Integer status;
    private Integer type;
    private String reasonType;
    private String merchantRemark;
    private String rejectReason;
    private String userEvidenceUrls;
    private Integer returnAddressId;
    private String returnReceiverName;
    private String returnReceiverPhone;
    private String returnAddress;
    private BigDecimal refundAmount;
    private BigDecimal actualRefundAmount;
    private String logisticsNo;
    private String logisticsCompany;
    private LocalDateTime auditTime;
    private LocalDateTime receiveTime;
    private LocalDateTime refundTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
