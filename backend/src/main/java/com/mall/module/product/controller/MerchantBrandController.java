package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.MerchantBrand;
import com.mall.module.product.service.MerchantBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/merchant-brands")
public class MerchantBrandController {

    @Autowired
    private MerchantBrandService merchantBrandService;

    @GetMapping
    public Result<List<MerchantBrand>> getBrandList(@RequestAttribute("userId") Integer merchantId) {
        List<MerchantBrand> list = merchantBrandService.getBrandList(merchantId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<MerchantBrand> getBrandById(@PathVariable Integer id, @RequestAttribute("userId") Integer merchantId) {
        MerchantBrand brand = merchantBrandService.getBrandById(merchantId, id);
        return Result.success(brand);
    }

    @PostMapping
    public Result<MerchantBrand> createBrand(@RequestBody MerchantBrand brand, @RequestAttribute("userId") Integer merchantId) {
        MerchantBrand saved = merchantBrandService.addBrand(merchantId, brand);
        return Result.success("创建成功", saved);
    }

    @PutMapping("/{id}")
    public Result<MerchantBrand> updateBrand(@PathVariable Integer id, @RequestBody MerchantBrand brand, @RequestAttribute("userId") Integer merchantId) {
        MerchantBrand updated = merchantBrandService.updateBrand(merchantId, id, brand);
        return Result.success("更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteBrand(@PathVariable Integer id, @RequestAttribute("userId") Integer merchantId) {
        merchantBrandService.deleteBrand(merchantId, id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateBrandStatus(@PathVariable Integer id, @RequestParam Integer status, @RequestAttribute("userId") Integer merchantId) {
        merchantBrandService.updateBrandStatus(merchantId, id, status);
        return Result.success("状态更新成功");
    }
}
