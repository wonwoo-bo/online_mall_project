package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.Category;
import com.mall.module.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public Result<List<Category>> getCategoryTree() {
        List<Category> categories = categoryService.getCategoryTree();
        return Result.success(categories);
    }

    @GetMapping("/categories/first-level")
    public Result<List<Category>> getFirstLevelCategories() {
        List<Category> categories = categoryService.getCategoriesByParentId(0);
        return Result.success(categories);
    }

    @GetMapping("/categories/second-level/{parentId}")
    public Result<List<Category>> getSecondLevelCategories(@PathVariable Integer parentId) {
        List<Category> categories = categoryService.getCategoriesByParentId(parentId);
        return Result.success(categories);
    }

    @GetMapping("/categories/{id}")
    public Result<Category> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @PostMapping("/category")
    public Result<Category> createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return Result.success("创建成功", category);
    }

    @PutMapping("/category/{id}")
    public Result<Category> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success("更新成功", category);
    }

    @DeleteMapping("/category/{id}")
    public Result<String> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }
}
