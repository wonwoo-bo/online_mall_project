package com.mall.module.order.service.impl;

import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.TrackingLog;
import com.mall.module.order.mapper.*;
import com.mall.module.order.service.OrderCenterService;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderCenterServiceImpl implements OrderCenterService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderOperationLogMapper orderOperationLogMapper;

    @Autowired
    private TrackingLogMapper trackingLogMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void modifyOrderPrice(Integer orderId, Integer merchantId, BigDecimal newAmount, String reason, Integer operatorId, String operatorName) {
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权操作该订单");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (newAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("改价金额必须大于0");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待付款订单才能修改价格");
        }

        BigDecimal oldAmount = order.getTotalAmount();
        
        Map<String, Object> priceChange = new HashMap<>();
        priceChange.put("orderId", orderId);
        priceChange.put("merchantId", merchantId);
        priceChange.put("oldAmount", oldAmount);
        priceChange.put("newAmount", newAmount);
        priceChange.put("changeReason", reason);
        priceChange.put("operatorId", operatorId);
        priceChange.put("operatorName", operatorName);
        priceChange.put("createTime", LocalDateTime.now());
        
        orderMapper.updateOrderPrice(orderId, newAmount);
        
        saveOperationLog(order, "PRICE_MODIFY", "修改订单价格",
                order.getStatus(), order.getStatus(), merchantId, operatorId, operatorName,
                String.format("价格修改：¥%s -> ¥%s，原因：%s", oldAmount, newAmount, reason));
    }

    @Override
    public void batchCloseOrders(List<Integer> orderIds, Integer merchantId, Integer operatorId, String operatorName) {
        for (Integer orderId : orderIds) {
            if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
                continue;
            }
            
            Order order = orderMapper.selectById(orderId);
            if (order != null && (order.getStatus() == 0 || order.getStatus() == 1)) {
                int beforeStatus = order.getStatus();
                orderMapper.closeOrderByMerchant(orderId, merchantId);
                
                saveOperationLog(order, "BATCH_CLOSE", "批量关单",
                        beforeStatus, -1, merchantId, operatorId, operatorName,
                        "批量关闭订单");
            }
        }
    }

    @Override
    public void batchUpdateRemark(List<Integer> orderIds, Integer merchantId, String remark, Integer operatorId, String operatorName) {
        for (Integer orderId : orderIds) {
            if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
                continue;
            }
            
            Order order = orderMapper.selectById(orderId);
            if (order != null) {
                orderMapper.updateRemark(orderId, remark);
                
                saveOperationLog(order, "BATCH_REMARK", "批量修改备注",
                        order.getStatus(), order.getStatus(), merchantId, operatorId, operatorName,
                        String.format("批量修改备注：%s", remark));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTrackingInfo(Integer orderId, Integer merchantId) {
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权查看该订单物流");
        }

        // 获取订单信息，检查订单状态
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 如果订单未发货，直接返回空
        if (order.getStatus() == null || order.getStatus() < 2) {
            Map<String, Object> result = new HashMap<>();
            result.put("expressCompany", null);
            result.put("trackingNo", null);
            result.put("trackingList", new ArrayList<>());
            return result;
        }

        Map<String, Object> shipInfo = orderMapper.selectShipInfoByOrderId(orderId);
        if (shipInfo == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("expressCompany", null);
            result.put("trackingNo", null);
            result.put("trackingList", new ArrayList<>());
            return result;
        }

        String expressCompany = (String) shipInfo.get("expressCompany");
        String trackingNo = (String) shipInfo.get("trackingNo");
        
        // 检查是否有真实的物流信息
        if (expressCompany == null || expressCompany.trim().isEmpty() 
            || trackingNo == null || trackingNo.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("expressCompany", null);
            result.put("trackingNo", null);
            result.put("trackingList", new ArrayList<>());
            return result;
        }

        List<TrackingLog> existingLogs = trackingLogMapper.selectByTrackingNo(trackingNo);
        
        if (existingLogs != null && !existingLogs.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("expressCompany", expressCompany);
            result.put("trackingNo", trackingNo);
            result.put("shippingAddress", order.getShippingAddress());
            result.put("shipLocation", existingLogs.get(existingLogs.size() - 1).getLocation());
            result.put("trackingList", existingLogs.stream().map(log -> {
                Map<String, Object> item = new HashMap<>();
                item.put("location", log.getLocation());
                item.put("status", log.getStatus());
                item.put("description", log.getDescription());
                item.put("createTime", log.getCreateTime());
                return item;
            }).collect(Collectors.toList()));
            return result;
        }

        // 如果数据库中没有物流轨迹数据，返回空列表（不自动生成模拟数据）
        Map<String, Object> result = new HashMap<>();
        result.put("expressCompany", expressCompany);
        result.put("trackingNo", trackingNo);
        result.put("shippingAddress", order.getShippingAddress());
        result.put("shipLocation", null);
        result.put("trackingList", new ArrayList<>());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProfitStatistics(Integer merchantId, String startTime, String endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchantId", merchantId);
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startDate", LocalDateTime.parse(startTime + " 00:00:00", 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endDate", LocalDateTime.parse(endTime + " 23:59:59",
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        List<Map<String, Object>> orders = orderMapper.selectMerchantOrderProfit(params);
        
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int orderCount = 0;
        
        for (Map<String, Object> order : orders) {
            BigDecimal amount = (BigDecimal) order.get("totalAmount");
            Integer productId = (Integer) order.get("productId");
            
            totalSales = totalSales.add(amount);
            orderCount++;
            
            Product product = productMapper.selectById(productId);
            if (product != null) {
                BigDecimal cost = product.getPrice().multiply(BigDecimal.valueOf(0.6));
                totalCost = totalCost.add(cost);
            }
        }
        
        BigDecimal profit = totalSales.subtract(totalCost);
        BigDecimal profitRate = totalSales.compareTo(BigDecimal.ZERO) > 0 
                ? profit.multiply(BigDecimal.valueOf(100)).divide(totalSales, 2, BigDecimal.ROUND_HALF_UP) 
                : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("orderCount", orderCount);
        result.put("totalSales", totalSales);
        result.put("totalCost", totalCost);
        result.put("profit", profit);
        result.put("profitRate", profitRate);
        result.put("details", orders);
        
        return result;
    }

    @Override
    public void autoCloseTimeoutOrders() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        List<Order> timeoutOrders = orderMapper.selectTimeoutPendingOrders(timeoutTime);
        
        for (Order order : timeoutOrders) {
            Integer merchantId = orderMapper.selectMerchantIdByOrderId(order.getId());
            if (merchantId == null) continue;
            
            int beforeStatus = order.getStatus();
            orderMapper.closeOrderByMerchant(order.getId(), merchantId);
            
            saveOperationLog(order, "AUTO_CLOSE", "系统自动关闭超时订单",
                    beforeStatus, -1, merchantId, 0, "系统",
                    "订单超过30分钟未付款，系统自动关闭");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMerchantOrdersWithTags(Integer merchantId, Map<String, Object> params) {
        params.put("merchantId", merchantId);
        Integer page = params.get("page") != null ? (Integer) params.get("page") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        params.put("offset", (page - 1) * pageSize);

        List<Map<String, Object>> orders = orderMapper.selectMerchantOrdersWithTags(params);
        return orders;
    }

    private boolean validateOrderBelongsToMerchant(Integer orderId, Integer merchantId) {
        Integer orderMerchantId = orderMapper.selectMerchantIdByOrderId(orderId);
        return merchantId.equals(orderMerchantId);
    }

    private void saveOperationLog(Order order, String operationType, String operationDesc,
                                  Integer beforeStatus, Integer afterStatus,
                                  Integer merchantId, Integer operatorId, String operatorName,
                                  String extendData) {
        Map<String, Object> log = new HashMap<>();
        log.put("orderId", order.getId());
        log.put("orderNo", order.getOrderNo());
        log.put("merchantId", merchantId);
        log.put("operatorId", operatorId);
        log.put("operatorName", operatorName);
        log.put("operationType", operationType);
        log.put("operationDesc", operationDesc);
        log.put("beforeStatus", beforeStatus);
        log.put("afterStatus", afterStatus);
        log.put("extendData", extendData);
        log.put("createTime", LocalDateTime.now());
        orderOperationLogMapper.insert(log);
    }

    /**
     * 获取商家发货地址（仓库地址）
     * 根据商家ID生成虚拟的仓库地址
     */
    private String getMerchantShipLocation(Integer merchantId) {
        // 根据商家ID生成不同的发货仓库地址
        String[] warehouseLocations = {
            "广东省深圳市福田区电商仓库",
            "浙江省杭州市余杭区物流中心",
            "上海市浦东新区仓储基地",
            "江苏省南京市江宁区配送中心",
            "北京市朝阳区物流仓库"
        };
        int index = merchantId % warehouseLocations.length;
        return warehouseLocations[index];
    }

    /**
     * 从地址中提取城市信息
     */
    private String extractCityFromAddress(String address) {
        if (address == null || address.isEmpty()) {
            return "目的地";
        }
        // 尝试提取省份和城市信息
        String[] provinceKeywords = {"省", "市", "自治区", "特别行政区"};
        for (String keyword : provinceKeywords) {
            int index = address.indexOf(keyword);
            if (index > 0) {
                // 提取省份/直辖市名称
                String province = address.substring(0, index + 1);
                // 尝试提取城市
                int cityIndex = address.indexOf(keyword, index + 1);
                if (cityIndex > index + 1) {
                    return address.substring(0, cityIndex + 1);
                }
                return province;
            }
        }
        // 如果无法提取，返回地址的前20个字符
        return address.length() > 20 ? address.substring(0, 20) + "..." : address;
    }

    /**
     * 生成模拟物流轨迹（从发货地到收货地的完整流程）
     */
    private List<Map<String, Object>> generateMockTracking(String trackingNo, String shipLocation, String shippingAddress) {
        List<Map<String, Object>> trackingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // 提取收货地的城市信息
        String destinationCity = extractCityFromAddress(shippingAddress);
        
        // 生成完整的物流轨迹节点（从发货到收货）
        // 1. 发货地揽收
        Map<String, Object> pickup = new HashMap<>();
        pickup.put("location", shipLocation);
        pickup.put("status", "已揽收");
        pickup.put("description", "快件已从商家仓库发出，等待快递员揽收");
        pickup.put("createTime", now.minusHours(48).minusMinutes(30));
        trackingList.add(pickup);
        
        // 2. 发货地网点
        Map<String, Object> originStation = new HashMap<>();
        String originCity = extractCityFromAddress(shipLocation);
        originStation.put("location", originCity + "快递网点");
        originStation.put("status", "已到达网点");
        originStation.put("description", "快件已到达发货地快递网点，准备发往中转站");
        originStation.put("createTime", now.minusHours(47).minusMinutes(15));
        trackingList.add(originStation);
        
        // 3. 发货地中转站
        Map<String, Object> originHub = new HashMap<>();
        originHub.put("location", originCity + "物流中转中心");
        originHub.put("status", "运输中");
        originHub.put("description", "快件已到达发货地中转中心，正在进行分拣");
        originHub.put("createTime", now.minusHours(44).minusMinutes(45));
        trackingList.add(originHub);
        
        // 4. 中途运输节点（根据发货地和收货地生成）
        // 如果发货地和收货地不在同一个城市，添加中途节点
        if (!originCity.equals(destinationCity)) {
            Map<String, Object> transit = new HashMap<>();
            transit.put("location", "干线运输途中");
            transit.put("status", "运输中");
            transit.put("description", "快件正在通过干线运输，从" + originCity + "发往" + destinationCity);
            transit.put("createTime", now.minusHours(36).minusMinutes(20));
            trackingList.add(transit);
            
            // 5. 收货地中转站
            Map<String, Object> destHub = new HashMap<>();
            destHub.put("location", destinationCity + "物流中转中心");
            destHub.put("status", "已到达中转站");
            destHub.put("description", "快件已到达收货地中转中心，正在进行分拣派送");
            destHub.put("createTime", now.minusHours(24).minusMinutes(10));
            trackingList.add(destHub);
        }
        
        // 6. 收货地网点
        Map<String, Object> destStation = new HashMap<>();
        destStation.put("location", destinationCity + "快递网点");
        destStation.put("status", "派送中");
        destStation.put("description", "快件已到达收货地快递网点，快递员正在派送");
        destStation.put("createTime", now.minusHours(12).minusMinutes(5));
        trackingList.add(destStation);
        
        // 7. 派送中
        Map<String, Object> delivering = new HashMap<>();
        delivering.put("location", shippingAddress);
        delivering.put("status", "派送中");
        delivering.put("description", "快递员正在派送，请保持电话畅通");
        delivering.put("createTime", now.minusHours(6).minusMinutes(30));
        trackingList.add(delivering);
        
        // 8. 已签收
        Map<String, Object> signed = new HashMap<>();
        signed.put("location", shippingAddress);
        signed.put("status", "已签收");
        signed.put("description", "快件已送达并由收件人签收，感谢使用我们的服务");
        signed.put("createTime", now.minusHours(2).minusMinutes(15));
        trackingList.add(signed);
        
        // 按时间倒序排列（最新的在最前面）
        Collections.reverse(trackingList);
        return trackingList;
    }
}