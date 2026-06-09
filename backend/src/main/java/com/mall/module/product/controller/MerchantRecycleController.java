package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.service.MerchantRecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/recycle")
public class MerchantRecycleController {

    @Autowired
    private MerchantRecycleService merchantRecycleService;

    @GetMapping
    public Result<Map<String, Object>> getRecycleList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "product") String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        List<Map<String, Object>> recycleList = merchantRecycleService.getRecycleList(merchantId, type);
        
        int total = recycleList.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Map<String, Object>> pagedList = fromIndex < total ? recycleList.subList(fromIndex, toIndex) : List.of();
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pagedList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return Result.success(result);
    }

    @PutMapping("/{type}/{id}/restore")
    public Result<String> restoreItem(
            @PathVariable String type,
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        
        merchantRecycleService.restoreItem(merchantId, type, id);
        return Result.success("恢复成功");
    }

    @DeleteMapping("/{type}/{id}")
    public Result<String> deleteFromRecycle(
            @PathVariable String type,
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        
        merchantRecycleService.forceDeleteItem(merchantId, type, id);
        return Result.success("删除成功");
    }
}
