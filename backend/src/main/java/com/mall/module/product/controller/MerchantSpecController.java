package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.SpecType;
import com.mall.module.product.entity.SpecValue;
import com.mall.module.product.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/specs")
public class MerchantSpecController {

    @Autowired
    private SpecService specService;

    @GetMapping("/types")
    public Result<List<SpecType>> getSpecTypeList(@RequestAttribute("userId") Integer merchantId) {
        List<SpecType> list = specService.getSpecTypeList(merchantId);
        return Result.success(list);
    }

    @GetMapping("/types/tree")
    public Result<List<SpecType>> getSpecTypeTree(@RequestAttribute("userId") Integer merchantId) {
        List<SpecType> tree = specService.getSpecTypeTree(merchantId);
        return Result.success(tree);
    }

    @GetMapping("/types/{id}")
    public Result<SpecType> getSpecTypeById(@PathVariable Integer id, @RequestAttribute("userId") Integer merchantId) {
        SpecType specType = specService.getSpecTypeById(merchantId, id);
        return Result.success(specType);
    }

    @PostMapping("/types")
    public Result<SpecType> createSpecType(@RequestBody SpecType specType, @RequestAttribute("userId") Integer merchantId) {
        SpecType saved = specService.addSpecType(merchantId, specType);
        return Result.success("创建成功", saved);
    }

    @PutMapping("/types/{id}")
    public Result<SpecType> updateSpecType(@PathVariable Integer id, @RequestBody SpecType specType, @RequestAttribute("userId") Integer merchantId) {
        SpecType updated = specService.updateSpecType(merchantId, id, specType);
        return Result.success("更新成功", updated);
    }

    @DeleteMapping("/types/{id}")
    public Result<String> deleteSpecType(@PathVariable Integer id, @RequestAttribute("userId") Integer merchantId) {
        specService.deleteSpecType(merchantId, id);
        return Result.success("删除成功");
    }

    @GetMapping("/values/{typeId}")
    public Result<List<SpecValue>> getSpecValueList(@PathVariable Integer typeId, @RequestAttribute("userId") Integer merchantId) {
        List<SpecValue> list = specService.getSpecValueList(merchantId, typeId);
        return Result.success(list);
    }

    @PostMapping("/values")
    public Result<SpecValue> createSpecValue(@RequestBody SpecValue specValue, @RequestAttribute("userId") Integer merchantId) {
        SpecValue saved = specService.addSpecValue(merchantId, specValue);
        return Result.success("创建成功", saved);
    }

    @PutMapping("/values/{id}")
    public Result<SpecValue> updateSpecValue(@PathVariable Integer id, @RequestBody SpecValue specValue, @RequestAttribute("userId") Integer merchantId) {
        SpecValue updated = specService.updateSpecValue(merchantId, id, specValue);
        return Result.success("更新成功", updated);
    }

    @DeleteMapping("/values/{id}")
    public Result<String> deleteSpecValue(@PathVariable Integer id, @RequestAttribute("userId") Integer merchantId) {
        specService.deleteSpecValue(merchantId, id);
        return Result.success("删除成功");
    }
}
