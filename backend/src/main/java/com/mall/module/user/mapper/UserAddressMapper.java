package com.mall.module.user.mapper;

import com.mall.module.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAddressMapper {
    void insert(UserAddress address);
    List<UserAddress> selectByUserId(@Param("userId") Integer userId);
    UserAddress selectById(@Param("id") Integer id);
    int update(UserAddress address);
    int delete(@Param("id") Integer id);
    int clearDefault(@Param("userId") Integer userId);
    int setDefault(@Param("id") Integer id, @Param("isDefault") Integer isDefault);
}