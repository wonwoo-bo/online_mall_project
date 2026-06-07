package com.mall.module.admin.mapper;

import com.mall.module.admin.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin findByUsername(@Param("username") String username);
    
    Admin findById(@Param("id") Integer id);
    
    void insert(Admin admin);
    
    void update(Admin admin);
    
    void deleteById(@Param("id") Integer id);
    
    List<Admin> findAll();
}