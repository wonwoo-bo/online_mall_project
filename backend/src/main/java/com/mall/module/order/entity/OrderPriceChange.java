package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderPriceChange {
    private Integer id;
    private Integer orderId;
    private Integer merchantId;
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private String changeReason;
    private Integer operatorId;
    private String operatorName;
    private LocalDateTime createTime;
}
