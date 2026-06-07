package com.mall.module.user.mapper;

import com.mall.module.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    UserRole selectByUserId(Integer userId);
    void insert(UserRole userRole);
    void updateByUserId(UserRole userRole);
}
