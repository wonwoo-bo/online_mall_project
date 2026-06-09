package com.mall.module.product.controller;

import com.mall.common.Result;
import com.mall.module.product.entity.MerchantReturnAddress;
import com.mall.module.product.entity.RefundOperationLog;
import com.mall.module.product.service.MerchantReturnAddressService;
import com.mall.module.product.service.ReturnRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/refunds")
public class MerchantRefundController {

    @Autowired
    private ReturnRequestService returnRequestService;

    @Autowired
    private MerchantReturnAddressService addressService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestAttribute("userId") Integer merchantId) {
        Map<String, Object> statistics = returnRequestService.getRefundStatistics(merchantId);
        return Result.success(statistics);
    }

    @GetMapping
    public Result<Map<String, Object>> getRefundList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = returnRequestService.getMerchantRefundPage(
                merchantId, status, type, startTime, endTime, orderNo, userId, userName, page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getRefundDetail(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        Map<String, Object> detail = returnRequestService.getRefundDetail(id, merchantId);
        return Result.success(detail);
    }

    @PostMapping("/{id}/audit")
    public Result<String> auditRefund(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, Object> data) {
        Integer agree = (Integer) data.get("agree");
        String rejectReason = (String) data.get("rejectReason");
        String remark = (String) data.get("remark");
        returnRequestService.auditRefund(id, merchantId, agree, rejectReason, remark);
        return Result.success("审核成功");
    }

    @PostMapping("/{id}/confirm-receive")
    public Result<String> confirmReceive(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        returnRequestService.confirmReceive(id, merchantId);
        return Result.success("确认收货成功");
    }

    @PostMapping("/{id}/confirm-refund")
    public Result<String> confirmRefund(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        returnRequestService.confirmRefund(id, merchantId);
        return Result.success("确认退款成功");
    }

    @GetMapping("/{id}/logs")
    public Result<List<RefundOperationLog>> getRefundLogs(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        List<RefundOperationLog> logs = returnRequestService.getRefundLogs(id, merchantId);
        return Result.success(logs);
    }

    @GetMapping("/return-addresses")
    public Result<List<MerchantReturnAddress>> getAddressList(@RequestAttribute("userId") Integer merchantId) {
        List<MerchantReturnAddress> list = addressService.getAddressList(merchantId);
        return Result.success(list);
    }

    @GetMapping("/return-addresses/default")
    public Result<MerchantReturnAddress> getDefaultAddress(@RequestAttribute("userId") Integer merchantId) {
        MerchantReturnAddress address = addressService.getDefaultAddress(merchantId);
        return Result.success(address);
    }

    @PostMapping("/return-addresses")
    public Result<String> addAddress(
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody MerchantReturnAddress address) {
        address.setMerchantId(merchantId);
        addressService.addAddress(address);
        return Result.success("添加成功");
    }

    @PutMapping("/return-addresses/{id}")
    public Result<String> updateAddress(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody MerchantReturnAddress address) {
        address.setId(id);
        address.setMerchantId(merchantId);
        addressService.updateAddress(address);
        return Result.success("更新成功");
    }

    @DeleteMapping("/return-addresses/{id}")
    public Result<String> deleteAddress(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        addressService.deleteAddress(id, merchantId);
        return Result.success("删除成功");
    }

    @PostMapping("/return-addresses/{id}/set-default")
    public Result<String> setDefaultAddress(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        addressService.setDefaultAddress(id, merchantId);
        return Result.success("设置成功");
    }

    @GetMapping("/reason-statistics")
    public Result<List<Map<String, Object>>> getReasonStatistics(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<Map<String, Object>> statistics = returnRequestService.getReasonStatistics(merchantId, startTime, endTime);
        return Result.success(statistics);
    }
}
