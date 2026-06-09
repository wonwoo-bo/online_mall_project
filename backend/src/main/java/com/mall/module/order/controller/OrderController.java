package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.Order;
import com.mall.module.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<List<Order>> createOrder(@RequestAttribute("userId") Integer userId,
                                     @RequestBody Map<String, Object> request) {
        try {
            String address = request.get("address").toString();

            List<Order> orders;

            if (request.containsKey("items")) {
                // 直接购买模式：前端传商品列表
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
                orders = orderService.createOrderDirect(userId, items, address);
            } else {
                // 购物车结算模式：前端传购物车ID列表
                @SuppressWarnings("unchecked")
                List<Integer> cartIds = (List<Integer>) request.get("cartIds");
                orders = orderService.createOrderFromCart(userId, cartIds, address);
            }

            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getOrderList(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> result = orderService.getOrderListPaged(userId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Integer id) {
        try {
            orderService.cancelOrder(id);
            return Result.success("取消成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm")
    public Result<Order> confirmReceive(@PathVariable Integer id) {
        try {
            Order order = orderService.updateOrderStatus(id, 3);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Order> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            Order order = orderService.updateOrderStatus(id, status);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
