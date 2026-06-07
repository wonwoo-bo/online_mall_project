package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.Cart;
import com.mall.module.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Cart> addCart(@RequestAttribute("userId") Integer userId,
                                @RequestBody Map<String, Object> request) {
        try {
            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer quantity = request.containsKey("quantity") && request.get("quantity") != null ?
                    Integer.parseInt(request.get("quantity").toString()) : 1;
            Integer skuId = request.containsKey("skuId") && request.get("skuId") != null ?
                    Integer.parseInt(request.get("skuId").toString()) : null;
            String specs = request.containsKey("specs") && request.get("specs") != null ?
                    request.get("specs").toString() : null;

            System.out.println("=== 加入购物车 ===");
            System.out.println("userId=" + userId + ", productId=" + productId + ", skuId=" + skuId + ", specs=" + specs + ", qty=" + quantity);

            Cart cart = cartService.addCart(userId, productId, quantity, skuId, specs);
            return Result.success(cart);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCart(@PathVariable Integer id) {
        try {
            cartService.deleteCartById(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateCart(@RequestBody Map<String, Object> request) {
        try {
            Integer id = Integer.parseInt(request.get("id").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());

            Cart cart = cartService.updateCartById(id, quantity);
            if (cart == null) {
                return Result.success("已移除", null);
            }
            return Result.success(cart);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Cart>> getCartList(@RequestAttribute("userId") Integer userId) {
        List<Cart> cartList = cartService.getCartList(userId);
        System.out.println("=== 购物车列表 ===");
        System.out.println("userId: " + userId + ", 商品数: " + cartList.size());
        for (Cart c : cartList) {
            System.out.println("  id=" + c.getId() + ", productId=" + c.getProductId() + ", skuId=" + c.getSkuId() + ", specs=" + c.getSpecs() + ", qty=" + c.getQuantity());
        }
        return Result.success(cartList);
    }

    @DeleteMapping("/clear")
    public Result<String> clearCart(@RequestAttribute("userId") Integer userId) {
        try {
            cartService.clearCart(userId);
            return Result.success("购物车已清空", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
