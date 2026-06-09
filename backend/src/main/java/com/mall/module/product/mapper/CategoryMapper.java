package com.mall.module.product.mapper;

import com.mall.module.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {

    List<Category> selectByParentId(@Param("parentId") Integer parentId);

    List<Category> selectAll();

    Category selectById(@Param("id") Integer id);

    void insert(Category category);

    void update(Category category);

    void deleteById(@Param("id") Integer id);

    void moveToRecycle(@Param("id") Integer id);

    List<Map<String, Object>> selectRecycleList(@Param("merchantId") Integer merchantId);

    void restoreCategory(@Param("id") Integer id);
    
    void forceDeleteCategory(@Param("id") Integer id);
}
