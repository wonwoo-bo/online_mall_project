package com.mall.module.admin.mapper;

import com.mall.module.admin.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BannerMapper {
    List<Banner> selectAll();
    List<Banner> selectActive();
    Banner selectById(@Param("id") Integer id);
    void insert(Banner banner);
    void update(Banner banner);
    void delete(@Param("id") Integer id);
}
