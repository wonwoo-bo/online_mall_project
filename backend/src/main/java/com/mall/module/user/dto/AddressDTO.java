package com.mall.module.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddressDTO {
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @Size(max = 20, message = "收货人手机号长度不能超过20")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份长度不能超过50")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市长度不能超过50")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Size(max = 50, message = "区县长度不能超过50")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200")
    private String detailAddress;

    @NotNull(message = "是否默认不能为空")
    private Integer isDefault;
}