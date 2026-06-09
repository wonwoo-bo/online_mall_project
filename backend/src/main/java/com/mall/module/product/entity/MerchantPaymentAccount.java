package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantPaymentAccount {
    private Integer id;
    private Integer merchantId;
    private String accountType;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private Integer isDefault;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
