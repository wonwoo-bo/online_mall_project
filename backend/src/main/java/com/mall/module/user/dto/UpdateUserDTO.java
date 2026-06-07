package com.mall.module.user.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateUserDTO {
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @Size(max = 200, message = "地址长度不能超过200")
    private String address;
}