package com.mall.module.admin.mapper;

import com.mall.module.admin.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemConfigMapper {
    SystemConfig findById(@Param("id") Integer id);
    
    SystemConfig findByConfigKey(@Param("configKey") String configKey);
    
    List<SystemConfig> findByCategory(@Param("category") String category);
    
    List<SystemConfig> findAll();
    
    void insert(SystemConfig config);
    
    void update(SystemConfig config);
    
    void deleteById(@Param("id") Integer id);
    
    void updateByConfigKey(@Param("configKey") String configKey, @Param("configValue") String configValue);
}