package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantSubAccountPermission {
    private Integer id;
    private Integer subAccountId;
    private Integer merchantId;
    private String permissionCode;
    private String permissionName;
    private LocalDateTime createTime;
}
