package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.order.service.OrderService;
import com.mall.module.product.service.ProductService;
import com.mall.module.product.service.ReturnRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/statistics")
public class MerchantStatisticsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReturnRequestService returnRequestService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(@RequestAttribute("userId") Integer merchantId) {
        Map<String, Object> result = new HashMap<>();
        
        int productCount = productService.countByMerchant(merchantId);
        result.put("totalProducts", productCount);
        
        int orderCount = orderService.countByMerchant(merchantId);
        result.put("totalOrders", orderCount);
        
        double totalSales = orderService.sumAmountByMerchantAndTime(merchantId, null, null);
        result.put("totalSales", String.format("%.2f", totalSales));
        
        int pendingReturns = returnRequestService.countPendingByMerchant(merchantId);
        result.put("totalReturns", pendingReturns);
        
        return Result.success(result);
    }

    @GetMapping("/sales")
    public Result<Map<String, Object>> getSalesData(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "7") Integer days) {
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> salesList = orderService.getSalesStatisticsByMerchant(merchantId, days);
        result.put("salesList", salesList);
        
        return Result.success(result);
    }

    @GetMapping("/product-ranking")
    public Result<Map<String, Object>> getProductRanking(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "0") Integer sortType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            startTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
        }
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> ranking = orderService.getProductRankingByMerchant(merchantId, sortType, startTime, endTime);
        result.put("list", ranking);
        
        return Result.success(result);
    }

    @GetMapping("/financial")
    public Result<Map<String, Object>> getFinancialReport(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "0") Integer reportType,
            @RequestParam(defaultValue = "0") Integer typeFilter,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            startTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
        }
        
        double totalSales = orderService.sumAmountByMerchantAndTime(merchantId, startTime, endTime);
        int orderCount = orderService.countByMerchant(merchantId);
        
        List<Map<String, Object>> financialList = orderService.getFinancialReport(merchantId, startTime, endTime);
        
        result.put("list", financialList);
        result.put("total", financialList.size());
        result.put("totalIncome", String.format("%.2f", totalSales));
        result.put("orderCount", orderCount);
        result.put("productSales", String.format("%.2f", totalSales * 0.94));
        result.put("shippingIncome", String.format("%.2f", totalSales * 0.035));
        result.put("refundAmount", "0.00");
        
        return Result.success(result);
    }

    @GetMapping("/financial/summary")
    public Result<Map<String, Object>> getFinancialSummary(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            startTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
        }
        
        double totalSales = orderService.sumAmountByMerchantAndTime(merchantId, startTime, endTime);
        int orderCount = orderService.countByMerchant(merchantId);
        
        result.put("totalIncome", totalSales);
        result.put("orderCount", orderCount);
        result.put("productSales", totalSales * 0.94);
        result.put("shippingIncome", totalSales * 0.035);
        result.put("refundAmount", 0.0);
        
        return Result.success(result);
    }

    @GetMapping("/product-conversion")
    public Result<Map<String, Object>> getProductConversion(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "0") Integer sortType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> conversionList = productService.getProductConversion(merchantId);
        
        if (sortType == 1) {
            conversionList.sort((a, b) -> Double.compare((Double) b.getOrDefault("conversionRate", 0.0), 
                    (Double) a.getOrDefault("conversionRate", 0.0)));
        } else if (sortType == 2) {
            conversionList.sort((a, b) -> Integer.compare((Integer) b.getOrDefault("visits", 0), 
                    (Integer) a.getOrDefault("visits", 0)));
        }
        
        result.put("list", conversionList);
        return Result.success(result);
    }

    @GetMapping("/overstock")
    public Result<Map<String, Object>> getOverstockWarning(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "0") Integer warningLevel) {
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> overstockList = productService.getOverstockWarning(merchantId);
        
        if (warningLevel != 0) {
            overstockList = overstockList.stream()
                    .filter(item -> warningLevel.equals(item.get("warningLevel")))
                    .toList();
        }
        
        result.put("list", overstockList);
        return Result.success(result);
    }
}
