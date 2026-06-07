package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.OrderInvoice;
import com.mall.module.order.entity.OrderTag;
import com.mall.module.order.service.OrderCenterService;
import com.mall.module.order.service.OrderInvoiceService;
import com.mall.module.order.service.OrderTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/order-center")
public class OrderCenterController {

    @Autowired
    private OrderCenterService orderCenterService;

    @Autowired
    private OrderTagService orderTagService;

    @Autowired
    private OrderInvoiceService orderInvoiceService;

    @PutMapping("/orders/{id}/price")
    public Result<Map<String, Object>> modifyOrderPrice(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id,
            @RequestBody Map<String, Object> data) {

        BigDecimal newAmount = new BigDecimal(data.get("newAmount").toString());
        String reason = data.get("reason") != null ? data.get("reason").toString() : "";

        try {
            orderCenterService.modifyOrderPrice(id, merchantId, newAmount, reason, merchantId, merchantName);
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("newAmount", newAmount);
            return Result.success("价格修改成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/orders/{id}/tag")
    public Result<String> addOrderTag(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id,
            @RequestBody Map<String, Object> data) {

        String tagName = data.get("tagName").toString();
        String tagColor = data.get("tagColor") != null ? data.get("tagColor").toString() : "#409EFF";

        try {
            orderTagService.addTag(id, merchantId, tagName, tagColor);
            return Result.success("标签添加成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/orders/{id}/tag")
    public Result<String> removeOrderTag(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id,
            @RequestBody Map<String, Object> data) {

        String tagName = data.get("tagName").toString();

        try {
            orderTagService.removeTag(id, merchantId, tagName);
            return Result.success("标签移除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/orders/{id}/tags")
    public Result<List<OrderTag>> getOrderTags(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        try {
            List<OrderTag> tags = orderTagService.getTagsByOrderId(id);
            return Result.success(tags);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/tags/statistics")
    public Result<List<Map<String, Object>>> getTagStatistics(
            @RequestAttribute("userId") Integer merchantId) {

        List<Map<String, Object>> stats = orderTagService.getTagStatistics(merchantId);
        return Result.success(stats);
    }

    @PostMapping("/orders/batch/tags")
    public Result<String> batchAddTags(
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody Map<String, Object> data) {

        @SuppressWarnings("unchecked")
        List<Integer> orderIds = ((List<Number>) data.get("orderIds")).stream()
                .map(Number::intValue)
                .toList();
        String tagName = data.get("tagName").toString();
        String tagColor = data.get("tagColor") != null ? data.get("tagColor").toString() : "#409EFF";

        try {
            orderTagService.batchAddTags(orderIds, merchantId, tagName, tagColor);
            return Result.success("批量添加标签成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/orders/batch/close")
    public Result<Map<String, Object>> batchCloseOrders(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @RequestBody Map<String, Object> data) {

        @SuppressWarnings("unchecked")
        List<Integer> orderIds = ((List<Number>) data.get("orderIds")).stream()
                .map(Number::intValue)
                .toList();

        try {
            orderCenterService.batchCloseOrders(orderIds, merchantId, merchantId, merchantName);
            Map<String, Object> result = new HashMap<>();
            result.put("count", orderIds.size());
            return Result.success("批量关单成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/orders/batch/remark")
    public Result<Map<String, Object>> batchUpdateRemark(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @RequestBody Map<String, Object> data) {

        @SuppressWarnings("unchecked")
        List<Integer> orderIds = ((List<Number>) data.get("orderIds")).stream()
                .map(Number::intValue)
                .toList();
        String remark = data.get("remark") != null ? data.get("remark").toString() : "";

        try {
            orderCenterService.batchUpdateRemark(orderIds, merchantId, remark, merchantId, merchantName);
            Map<String, Object> result = new HashMap<>();
            result.put("count", orderIds.size());
            return Result.success("批量修改备注成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/orders/{id}/tracking")
    public Result<Map<String, Object>> getTrackingInfo(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        try {
            Map<String, Object> tracking = orderCenterService.getTrackingInfo(id, merchantId);
            return Result.success(tracking);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/orders/{id}/invoice")
    public Result<OrderInvoice> generateInvoice(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id,
            @RequestBody Map<String, Object> data) {

        String title = data.get("title").toString();
        String taxNo = data.get("taxNo") != null ? data.get("taxNo").toString() : "";
        Integer invoiceType = data.get("invoiceType") != null ? 
                ((Number) data.get("invoiceType")).intValue() : 1;

        try {
            OrderInvoice invoice = orderInvoiceService.generateInvoice(id, merchantId, title, taxNo, invoiceType);
            return Result.success("发票生成成功", invoice);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/orders/{id}/invoice")
    public Result<OrderInvoice> getInvoice(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        OrderInvoice invoice = orderInvoiceService.getInvoiceByOrderId(id);
        return Result.success(invoice);
    }

    @PutMapping("/invoices/{id}/cancel")
    public Result<String> cancelInvoice(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        try {
            orderInvoiceService.cancelInvoice(id, merchantId);
            return Result.success("发票已作废");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/invoices")
    public Result<List<OrderInvoice>> getInvoices(
            @RequestAttribute("userId") Integer merchantId) {

        List<OrderInvoice> invoices = orderInvoiceService.getInvoicesByMerchantId(merchantId);
        return Result.success(invoices);
    }

    @GetMapping("/invoices/statistics")
    public Result<Map<String, Object>> getInvoiceStatistics(
            @RequestAttribute("userId") Integer merchantId) {

        Map<String, Object> stats = orderInvoiceService.getInvoiceStatistics(merchantId);
        return Result.success(stats);
    }

    @GetMapping("/profit")
    public Result<Map<String, Object>> getProfitStatistics(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        Map<String, Object> stats = orderCenterService.getProfitStatistics(merchantId, startTime, endTime);
        return Result.success(stats);
    }

    @PostMapping("/orders/auto-close")
    public Result<String> triggerAutoClose(
            @RequestAttribute("userId") Integer merchantId) {

        try {
            orderCenterService.autoCloseTimeoutOrders();
            return Result.success("已触发超时订单自动关闭");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}