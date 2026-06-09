package com.mall.module.order.controller;

import com.mall.common.Result;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.entity.OrderTag;
import com.mall.module.order.mapper.OrderTagMapper;
import com.mall.module.order.service.OrderService;
import com.mall.module.user.entity.User;
import com.mall.module.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/merchant/orders")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderTagMapper orderTagMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 商家订单分页列表（多条件搜索）
     * 支持：订单号、收货人、手机号搜索；多状态筛选；时间范围筛选
     */
    @GetMapping
    public Result<Map<String, Object>> getOrderList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("merchantId", merchantId);
        params.put("page", page);
        params.put("pageSize", pageSize);

        // 多状态筛选（支持逗号分隔，如: 0,1 表示待发货+已发货）
        if (status != null && !status.isEmpty()) {
            List<Integer> statusList = new ArrayList<>();
            for (String s : status.split(",")) {
                statusList.add(Integer.parseInt(s.trim()));
            }
            params.put("statusList", statusList);
        }

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", keyword.trim());
        }

        // 时间范围
        if (startDate != null && !startDate.isEmpty()) {
            params.put("startDate", LocalDateTime.parse(startDate + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endDate", LocalDateTime.parse(endDate + " 23:59:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        Map<String, Object> result = orderService.getOrdersByMerchantPaged(params);
        return Result.success(result);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        // 校验订单归属
        if (!orderService.validateOrderBelongsToMerchant(id, merchantId)) {
            return Result.error(403, "无权访问该订单");
        }

        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderService.getOrderItems(id);
        List<Map<String, Object>> logs = orderService.getOrderOperationLogs(id);

        // 加载买家信息
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                order.setUserName(user.getUsername());
            }
        }

        // 加载标签信息
        List<OrderTag> tags = orderTagMapper.selectByOrderId(id);
        if (tags != null && !tags.isEmpty()) {
            String tagNames = tags.stream()
                .map(OrderTag::getTagName)
                .collect(Collectors.joining(","));
            order.setTags(tagNames);
            order.setTagList(tags);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        result.put("logs", logs);

        return Result.success(result);
    }

    /**
     * 商家接单确认
     * 校验订单，流转至待发货状态
     */
    @PostMapping("/{id}/confirm")
    public Result<Map<String, Object>> confirmOrder(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id) {

        try {
            orderService.confirmOrderByMerchant(id, merchantId, merchantId, merchantName);

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("status", 1);
            return Result.success("接单成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家发货
     * 包含参数校验、重复发货拦截
     */
    @PostMapping("/{id}/ship")
    public Result<Map<String, Object>> shipOrder(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        System.out.println("=== 收到发货请求 ===");
        System.out.println("订单ID: " + id);
        System.out.println("商家ID: " + merchantId);
        System.out.println("请求参数: " + data);

        String expressCompany = data.get("expressCompany");
        String trackingNo = data.get("trackingNo");

        // 参数校验
        if (expressCompany == null || expressCompany.trim().isEmpty()) {
            System.out.println("失败：物流公司为空");
            return Result.error("请选择物流公司");
        }
        if (trackingNo == null || trackingNo.trim().isEmpty()) {
            System.out.println("失败：运单号为空");
            return Result.error("请填写运单号");
        }

        try {
            System.out.println("开始调用发货服务...");
            orderService.shipOrderByMerchant(id, merchantId, expressCompany.trim(),
                    trackingNo.trim(), merchantId, merchantName);
            System.out.println("发货服务调用成功！");

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("expressCompany", expressCompany);
            result.put("trackingNo", trackingNo);
            result.put("shipTime", LocalDateTime.now());
            return Result.success("发货成功", result);
        } catch (RuntimeException e) {
            System.out.println("发货失败，异常信息: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 商家手动关单（状态不可回滚）
     * 只能关闭待发货状态的订单
     */
    @PostMapping("/{id}/close")
    public Result<Map<String, Object>> closeOrder(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id) {

        try {
            orderService.closeOrderByMerchant(id, merchantId, merchantId, merchantName);

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("status", -1);
            return Result.success("订单已关闭", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 恢复已关闭订单（取消自动关单）
     * 只能恢复已关闭状态的订单
     */
    @PostMapping("/{id}/reopen")
    public Result<Map<String, Object>> reopenOrder(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id) {

        try {
            orderService.reopenOrderByMerchant(id, merchantId, merchantId, merchantName);

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("status", 0);
            return Result.success("订单已恢复", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改订单备注
     */
    @PutMapping("/{id}/remark")
    public Result<Map<String, Object>> updateRemark(
            @RequestAttribute("userId") Integer merchantId,
            @RequestAttribute("username") String merchantName,
            @PathVariable Integer id,
            @RequestBody Map<String, String> data) {

        String remark = data.get("remark");

        try {
            orderService.updateOrderRemark(id, merchantId, remark, merchantId, merchantName);

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", id);
            result.put("remark", remark);
            return Result.success("备注已更新", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单操作日志
     */
    @GetMapping("/{id}/logs")
    public Result<List<Map<String, Object>>> getOrderLogs(
            @RequestAttribute("userId") Integer merchantId,
            @PathVariable Integer id) {

        // 校验订单归属
        if (!orderService.validateOrderBelongsToMerchant(id, merchantId)) {
            return Result.error(403, "无权访问该订单");
        }

        List<Map<String, Object>> logs = orderService.getOrderOperationLogs(id);
        return Result.success(logs);
    }
}
