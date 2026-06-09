package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.DisputeApplication;
import com.mall.module.product.service.DisputeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/merchant/dispute")
public class MerchantDisputeController {

    @Autowired
    private DisputeApplicationService disputeApplicationService;

    @GetMapping("/list")
    public Result<List<DisputeApplication>> getDisputeList(HttpServletRequest request) {
        Integer merchantId = (Integer) request.getAttribute("merchantId");
        if (merchantId == null) {
            return Result.error("请先登录");
        }
        List<DisputeApplication> list = disputeApplicationService.getDisputeList(merchantId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<DisputeApplication> getDisputeById(@PathVariable Integer id) {
        DisputeApplication dispute = disputeApplicationService.getDisputeById(id);
        return Result.success(dispute);
    }

    @PostMapping("/apply")
    public Result<String> applyDispute(@RequestBody DisputeApplication disputeApplication, HttpServletRequest request) {
        Integer merchantId = (Integer) request.getAttribute("merchantId");
        if (merchantId == null) {
            return Result.error("请先登录");
        }
        disputeApplication.setMerchantId(merchantId);
        disputeApplication.setStatus(0);
        int result = disputeApplicationService.createDispute(disputeApplication);
        if (result > 0) {
            return Result.success("申请提交成功");
        }
        return Result.error("申请提交失败");
    }

    @PutMapping("/{id}")
    public Result<String> updateDispute(@PathVariable Integer id, @RequestBody DisputeApplication disputeApplication) {
        disputeApplication.setId(id);
        int result = disputeApplicationService.updateDispute(disputeApplication);
        if (result > 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteDispute(@PathVariable Integer id) {
        int result = disputeApplicationService.deleteDispute(id);
        if (result > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
