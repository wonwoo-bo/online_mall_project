package com.mall.module.order.service.impl;

import com.mall.module.order.entity.Cart;
import com.mall.module.order.mapper.CartMapper;
import com.mall.module.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 用于调用成员3（商品模块）的接口
     * 接口地址需要根据成员3的实际部署情况修改
     */
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Cart addCart(Integer userId, Integer productId, Integer quantity, Integer skuId, String specs) {
        // 查询时需要同时考虑 skuId
        Cart existing = cartMapper.selectByUserIdAndProductIdAndSkuId(userId, productId, skuId);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdateTime(LocalDateTime.now());
            cartMapper.update(existing);
            return existing;
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setSkuId(skuId);
        cart.setSpecs(specs);
        cart.setQuantity(quantity);
        cart.setSelected(true);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());

        cartMapper.insert(cart);
        return cart;
    }

    @Override
    public Cart updateCartById(Integer id, Integer quantity) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null) {
            throw new RuntimeException("购物车项不存在");
        }

        if (quantity <= 0) {
            cartMapper.deleteById(id);
            return null;
        }

        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.update(cart);
        return cart;
    }

    @Override
    public void deleteCartById(Integer id) {
        cartMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getCartList(Integer userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public void clearCart(Integer userId) {
        cartMapper.deleteByUserId(userId);
    }
}
