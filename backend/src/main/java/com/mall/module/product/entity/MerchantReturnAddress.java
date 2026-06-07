package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantReturnAddress {
    private Integer id;
    private Integer merchantId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
