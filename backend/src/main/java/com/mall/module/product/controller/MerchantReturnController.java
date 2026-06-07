package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.service.ReturnRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/returns")
public class MerchantReturnController {

    @Autowired
    private ReturnRequestService returnRequestService;

    @GetMapping
    public Result<Map<String, Object>> getReturnList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        
        List<Map<String, Object>> returns = returnRequestService.getReturnListByMerchant(merchantId, status);
        int total = returns.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Map<String, Object>> pagedReturns = fromIndex < total ? returns.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pagedReturns);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return Result.success(result);
    }

    @PutMapping("/{id}/handle")
    public Result<String> handleReturn(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        System.out.println("=== 商家处理退换货 ===");
        System.out.println("id=" + id + ", data=" + data);
        Integer status = null;
        Object statusObj = data.get("status");
        if (statusObj instanceof Number) {
            status = ((Number) statusObj).intValue();
        }
        System.out.println("status=" + status + " (type=" + (statusObj != null ? statusObj.getClass().getSimpleName() : "null") + ")");
        String remark = (String) data.get("remark");
        try {
            returnRequestService.handleReturn(id, status, remark);
            return Result.success("处理成功");
        } catch (Exception e) {
            System.out.println("处理退换货失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("处理失败：" + e.getMessage());
        }
    }
}
