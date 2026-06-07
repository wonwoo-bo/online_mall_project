package com.mall.module.product.service;

import com.mall.common.PageResult;

import java.util.Map;

public interface FavoriteService {

    /**
     * 收藏列表（含商品信息）
     */
    PageResult<Map<String, Object>> getFavoriteList(Integer userId, int page, int size);

    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Integer userId, Integer productId);

    /**
     * 添加收藏
     */
    void addFavorite(Integer userId, Integer productId);

    /**
     * 取消收藏
     */
    void removeFavorite(Integer userId, Integer productId);
}
