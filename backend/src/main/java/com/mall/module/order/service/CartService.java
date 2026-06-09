package com.mall.module.order.service;

import com.mall.module.order.entity.Cart;

import java.util.List;

public interface CartService {
    Cart addCart(Integer userId, Integer productId, Integer quantity, Integer skuId, String specs);

    Cart updateCartById(Integer id, Integer quantity);

    void deleteCartById(Integer id);

    List<Cart> getCartList(Integer userId);

    void clearCart(Integer userId);
}
