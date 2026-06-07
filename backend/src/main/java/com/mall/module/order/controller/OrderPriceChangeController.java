package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.OrderPriceChange;
import com.mall.module.order.service.OrderPriceChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/price-changes")
public class OrderPriceChangeController {

    @Autowired
    private OrderPriceChangeService orderPriceChangeService;

    @GetMapping("/order/{orderId}")
    public Result<List<OrderPriceChange>> getByOrderId(@PathVariable Integer orderId) {
        List<OrderPriceChange> list = orderPriceChangeService.getByOrderId(orderId);
        return Result.success(list);
    }

    @GetMapping
    public Result<List<OrderPriceChange>> getByMerchantId(@RequestAttribute("userId") Integer merchantId) {
        List<OrderPriceChange> list = orderPriceChangeService.getByMerchantId(merchantId);
        return Result.success(list);
    }
}
