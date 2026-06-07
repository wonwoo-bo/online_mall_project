package com.mall.module.product.service;

import com.mall.module.product.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    /**
     * 获取完整分类树（递归构建）
     */
    List<Category> getCategoryTree();

    /**
     * 根据父级ID获取子分类列表
     */
    List<Category> getCategoriesByParentId(Integer parentId);

    /**
     * 获取所有分类（扁平列表）
     */
    List<Category> getAllCategories();

    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Integer id);

    /**
     * 创建分类
     */
    void createCategory(Category category);

    /**
     * 更新分类
     */
    void updateCategory(Category category);

    /**
     * 删除分类（移动到回收站）
     */
    void deleteCategory(Integer id);

    /**
     * 获取回收站分类列表
     */
    List<Map<String, Object>> getRecycleList(Integer merchantId);

    /**
     * 恢复分类
     */
    void restoreCategory(Integer id);

    /**
     * 彻底删除分类
     */
    void forceDeleteCategory(Integer id);
}
