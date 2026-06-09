package com.mall.module.order.mapper;

import com.mall.module.order.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    void insert(Cart cart);

    void update(Cart cart);

    void deleteById(Integer id);

    void deleteByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    void deleteByUserId(Integer userId);

    Cart selectById(Integer id);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    Cart selectByUserIdAndProductIdAndSkuId(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("skuId") Integer skuId);

    List<Cart> selectByUserId(Integer userId);

    Integer countByUserId(Integer userId);
}
