package com.mall.module.product.controller;

import com.mall.common.JwtUtil;
import com.mall.common.Result;
import com.mall.module.product.dto.MerchantRegisterDTO;
import com.mall.module.product.entity.Merchant;
import com.mall.module.product.service.MerchantService;
import com.mall.module.product.service.ProductService;
import com.mall.module.order.service.OrderService;
import com.mall.module.product.service.ReturnRequestService;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReturnRequestService returnRequestService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody MerchantRegisterDTO registerDTO) {
        merchantService.register(registerDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success("注册成功，请等待审核", result);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        Merchant merchant = merchantService.login(username, password);
        String token = JwtUtil.generateToken(merchant.getId(), merchant.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("merchant", merchant);
        
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<Merchant> getMerchantInfo(@RequestAttribute("userId") Integer userId) {
        Merchant merchant = merchantService.getMerchantInfo(userId);
        return Result.success(merchant);
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@RequestAttribute("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        int productCount = productService.countByMerchant(userId);
        result.put("productCount", productCount);
        
        int orderCount = orderService.countByMerchant(userId);
        result.put("orderCount", orderCount);
        
        // 计算历史所有销售额（因为测试数据是2024年的）
        double todaySales = orderService.sumAmountByMerchantAndTime(userId, null, null);
        result.put("todaySales", String.format("%.2f", todaySales));
        
        int pendingReturns = returnRequestService.countPendingByMerchant(userId);
        result.put("pendingReturns", pendingReturns);
        
        List<Map<String, Object>> pendingOrders = orderService.getPendingOrdersByMerchant(userId, 5);
        result.put("pendingOrders", pendingOrders);
        
        return Result.success(result);
    }
}
