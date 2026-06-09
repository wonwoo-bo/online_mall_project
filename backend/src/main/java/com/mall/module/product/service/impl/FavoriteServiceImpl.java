package com.mall.module.product.service.impl;

import com.mall.common.PageResult;
import com.mall.module.product.entity.Favorite;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.FavoriteMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Map<String, Object>> getFavoriteList(Integer userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId, offset, size);
        int total = favoriteMapper.countByUserId(userId);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Map<String, Object> item = new HashMap<>();
            item.put("favorite", favorite);

            // 关联查询商品信息
            Product product = productMapper.selectById(favorite.getProductId());
            item.put("product", product);

            list.add(item);
        }

        return new PageResult<>(total, list, page, size);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer productId) {
        return favoriteMapper.exists(userId, productId) > 0;
    }

    @Override
    public void addFavorite(Integer userId, Integer productId) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Integer userId, Integer productId) {
        favoriteMapper.delete(userId, productId);
    }
}
