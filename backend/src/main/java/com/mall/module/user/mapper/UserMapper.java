package com.mall.module.user.mapper;

import com.mall.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insert(User user);
    User selectByUsername(@Param("username") String username);
    User selectById(@Param("id") Integer id);
    int update(User user);
    int updatePassword(@Param("id") Integer id, @Param("password") String password, @Param("salt") String salt);
}