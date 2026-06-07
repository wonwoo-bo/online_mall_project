package com.mall.module.product.service.impl;

import com.mall.module.product.entity.Category;
import com.mall.module.product.mapper.CategoryMapper;
import com.mall.module.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getCategoryTree() {
        List<Category> allCategories = categoryMapper.selectAll();
        return buildTree(allCategories, 0);
    }

    @Override
    public List<Category> getCategoriesByParentId(Integer parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void createCategory(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryMapper.moveToRecycle(id);
    }

    @Override
    public List<Map<String, Object>> getRecycleList(Integer merchantId) {
        return categoryMapper.selectRecycleList(merchantId);
    }

    @Override
    public void restoreCategory(Integer id) {
        categoryMapper.restoreCategory(id);
    }

    @Override
    public void forceDeleteCategory(Integer id) {
        categoryMapper.deleteById(id);
    }

    /**
     * 递归构建分类树
     */
    private List<Category> buildTree(List<Category> allCategories, Integer parentId) {
        List<Category> tree = new ArrayList<>();
        for (Category category : allCategories) {
            if (parentId.equals(category.getParentId())) {
                // 递归查找子分类
                List<Category> children = buildTree(allCategories, category.getId());
                category.setChildren(children);
                tree.add(category);
            }
        }
        return tree;
    }
}
