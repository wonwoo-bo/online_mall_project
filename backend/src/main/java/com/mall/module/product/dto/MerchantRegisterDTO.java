package com.mall.module.product.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String shopDesc;
}
