package com.mall.module.order.service.impl;

import com.mall.module.order.entity.Cart;
import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;
import com.mall.module.order.entity.OrderTag;
import com.mall.module.order.mapper.CartMapper;
import com.mall.module.order.mapper.OrderItemMapper;
import com.mall.module.order.mapper.OrderItemShipMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.mapper.OrderTagMapper;
import com.mall.module.order.service.OrderService;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.mall.module.order.mapper.OrderOperationLogMapper;
import com.mall.module.product.mapper.ReturnRequestMapper;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderOperationLogMapper orderOperationLogMapper;

    @Autowired
    private ReturnRequestMapper returnRequestMapper;

    @Autowired
    private OrderItemShipMapper orderItemShipMapper;

    @Autowired
    private OrderTagMapper orderTagMapper;

    @Override
    public Order createOrder(Integer userId, List<OrderItem> items, String address) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品「" + product.getName() + "」库存不足");
            }
            totalAmount = totalAmount.add(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setShippingAddress(address);
        order.setCreateTime(LocalDateTime.now());

        String[] addressParts = address.split(" ");
        if (addressParts.length >= 2) {
            order.setReceiverName(addressParts[0]);
            order.setReceiverPhone(addressParts[1]);
        }

        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            item.setSubtotal(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setCreateTime(LocalDateTime.now());
        }
        orderItemMapper.batchInsert(items);

        for (OrderItem item : items) {
            int affected = productMapper.updateStock(item.getProductId(), -item.getQuantity());
            if (affected == 0) {
                throw new RuntimeException("商品库存不足，请稍后重试");
            }
            productMapper.incrementSales(item.getProductId(), item.getQuantity());
        }

        return order;
    }

    @Override
    public List<Order> createOrderFromCart(Integer userId, List<Integer> cartIds, String address) {
        if (cartIds == null || cartIds.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        List<Cart> cartItems = new ArrayList<>();
        for (Integer cartId : cartIds) {
            Cart cart = cartMapper.selectById(cartId);
            if (cart == null || !cart.getUserId().equals(userId)) {
                throw new RuntimeException("购物车记录不存在: " + cartId);
            }
            cartItems.add(cart);
        }

        // 构建所有订单项（实时查询商品最新信息）
        List<OrderItem> allItems = new ArrayList<>();
        for (Cart cart : cartItems) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + cart.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setQuantity(cart.getQuantity());
            item.setProductName(product.getName());

            // 优先使用SKU价格（从购物车关联查询获取），否则使用商品默认价格
            BigDecimal itemPrice = product.getPrice();
            if (cart.getProductPrice() != null) {
                itemPrice = cart.getProductPrice();
            }
            item.setProductPrice(itemPrice);

            item.setMerchantId(product.getMerchantId());
            item.setProductImage(product.getCoverImg());
            item.setSpecs(cart.getSpecs());
            allItems.add(item);
        }

        // 每个商品创建一个独立订单，使用相同的 groupOrderNo 关联同次购买
        String groupOrderNo = "GRP" + System.currentTimeMillis() + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        List<Order> orders = new ArrayList<>();
        for (OrderItem item : allItems) {
            List<OrderItem> singleItem = new ArrayList<>();
            singleItem.add(item);
            Order order = createOrder(userId, singleItem, address);
            order.setGroupOrderNo(groupOrderNo);
            orderMapper.updateGroupOrderNo(order.getId(), groupOrderNo);
            orders.add(order);
        }

        // 清空购物车
        for (Integer cartId : cartIds) {
            cartMapper.deleteById(cartId);
        }

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Integer id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 加载订单商品列表
        order.setItems(orderItemMapper.selectByOrderId(id));
        // 只在订单已发货时加载物流信息
        if (order.getStatus() != null && order.getStatus() >= 2) {
            Map<String, Object> shipInfo = orderItemShipMapper.selectByOrderId(id);
            if (shipInfo != null) {
                String expressCompany = (String) shipInfo.get("expressCompany");
                String trackingNo = (String) shipInfo.get("trackingNo");
                // 只有在有真实物流信息时才设置
                if (expressCompany != null && !expressCompany.trim().isEmpty() 
                    && trackingNo != null && !trackingNo.trim().isEmpty()) {
                    order.setExpressCompany(expressCompany);
                    order.setTrackingNo(trackingNo);
                }
            }
        }
        return order;
    }

    @Override
    public List<Order> createOrderDirect(Integer userId, List<Map<String, Object>> items, String address) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("商品列表为空");
        }

        // 构建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> itemMap : items) {
            Integer productId = Integer.parseInt(itemMap.get("productId").toString());
            Integer quantity = Integer.parseInt(itemMap.get("quantity").toString());
            Integer merchantId = itemMap.get("merchantId") != null ?
                    Integer.parseInt(itemMap.get("merchantId").toString()) : 0;

            // 查询商品信息
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在: " + productId);
            }

            // 优先使用前端传递的价格（SKU价格），否则使用商品默认价格
            java.math.BigDecimal price = product.getPrice();
            if (itemMap.containsKey("price") && itemMap.get("price") != null) {
                price = new java.math.BigDecimal(itemMap.get("price").toString());
            }

            OrderItem item = new OrderItem();
            item.setProductId(productId);
            item.setQuantity(quantity);
            item.setMerchantId(merchantId != null && merchantId > 0 ? merchantId : product.getMerchantId());
            item.setProductName(product.getName());
            item.setProductPrice(price);
            item.setProductImage(product.getCoverImg());
            // 传递规格信息
            if (itemMap.containsKey("specs") && itemMap.get("specs") != null) {
                item.setSpecs(itemMap.get("specs").toString());
            }
            orderItems.add(item);
        }

        // 按商家分组创建订单
        Map<Integer, List<OrderItem>> merchantGroups = new LinkedHashMap<>();
        for (OrderItem item : orderItems) {
            Integer merchantId = item.getMerchantId() != null ? item.getMerchantId() : 0;
            merchantGroups.computeIfAbsent(merchantId, k -> new ArrayList<>()).add(item);
        }

        System.out.println("=== 直接购买 - 按商家分组 ===");
        System.out.println("总商品数: " + orderItems.size());
        System.out.println("商家分组数: " + merchantGroups.size());

        List<Order> orders = new ArrayList<>();
        for (List<OrderItem> merchantItems : merchantGroups.values()) {
            Order order = createOrder(userId, merchantItems, address);
            orders.add(order);
        }

        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderListPaged(Integer userId, Integer status, Integer page, Integer size) {
        List<Order> allOrders;
        if (status != null) {
            allOrders = orderMapper.selectByUserIdAndStatus(userId, status);
        } else {
            allOrders = orderMapper.selectByUserId(userId);
        }

        // 为每个订单加载商品列表
        for (Order order : allOrders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
            // 检查该订单是否有已完成的退款记录
            order.setHasRefunded(returnRequestMapper.countCompletedByOrderId(order.getId()) > 0);
        }

        int total = allOrders.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<Order> pagedList = fromIndex < total
                ? allOrders.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("list", pagedList);
        return result;
    }

    // 以下为额外保留的接口方法

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderListByUserId(Integer userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderListByStatus(Integer status) {
        return orderMapper.selectByStatus(status);
    }

    @Override
    public Order updateOrderStatus(Integer id, Integer status) {
        Order order = getOrderById(id);

        if (status == 1 && order.getStatus() != 0) {
            throw new RuntimeException("只有待付款订单才能支付");
        }

        if (status == 2 && order.getStatus() != 1) {
            throw new RuntimeException("只有待发货订单才能发货");
        }

        if (status == 3 && order.getStatus() != 2) {
            throw new RuntimeException("只有待收货订单才能完成");
        }

        order.setStatus(status);

        if (status == 1) {
            order.setPayTime(LocalDateTime.now());
        } else if (status == 2) {
            order.setShipTime(LocalDateTime.now());
        } else if (status == 3) {
            order.setReceiveTime(LocalDateTime.now());
        }

        orderMapper.update(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Integer id) {
        Order order = getOrderById(id);
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待付款订单才能取消");
        }

        order.setStatus(4);
        orderMapper.update(order);

        List<OrderItem> items = orderItemMapper.selectByOrderId(id);
        for (OrderItem item : items) {
            productMapper.updateStock(item.getProductId(), item.getQuantity());
            productMapper.incrementSales(item.getProductId(), -item.getQuantity());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    private String generateOrderNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }

    @Override
    @Transactional(readOnly = true)
    public int countByMerchant(Integer merchantId) {
        return orderMapper.countByMerchantId(merchantId);
    }

    @Override
    @Transactional(readOnly = true)
    public double sumAmountByMerchantAndTime(Integer merchantId, LocalDateTime start, LocalDateTime end) {
        BigDecimal total = orderMapper.sumAmountByMerchantAndTime(merchantId, start, end);
        return total != null ? total.doubleValue() : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingOrdersByMerchant(Integer merchantId, int limit) {
        return orderMapper.selectPendingOrdersByMerchant(merchantId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByMerchant(Integer merchantId, Integer status) {
        return orderMapper.selectOrdersByMerchant(merchantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalesStatisticsByMerchant(Integer merchantId, Integer days) {
        return orderMapper.selectSalesStatisticsByMerchant(merchantId, days);
    }

    // ========== 商家订单管理新功能实现 ==========

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrdersByMerchantPaged(Map<String, Object> params) {
        Integer page = params.get("page") != null ? (Integer) params.get("page") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        int offset = (page - 1) * pageSize;
        params.put("offset", offset);

        // 查询订单列表
        List<Order> orders = orderMapper.selectOrdersByMerchantPaged(params);

        // 为每个订单加载商品列表和标签
        for (Order order : orders) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
            // 查询物流信息
            Map<String, Object> shipInfo = orderMapper.selectShipInfoByOrderId(order.getId());
            if (shipInfo != null) {
                order.setExtendData(formatShipInfo(shipInfo));
            }
            // 查询订单标签
            List<OrderTag> tags = orderTagMapper.selectByOrderId(order.getId());
            if (tags != null && !tags.isEmpty()) {
                String tagNames = tags.stream()
                        .map(OrderTag::getTagName)
                        .collect(Collectors.joining(","));
                order.setTags(tagNames);
                order.setTagList(tags);
            }
        }

        // 查询总数
        Integer total = orderMapper.countOrdersByMerchantPaged(params);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public void confirmOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName) {
        // 1. 校验订单归属
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权操作该订单");
        }

        // 2. 查询订单状态
        Order order = getOrderById(orderId);

        // 3. 状态校验：只有待付款(0)状态才能接单
        if (order.getStatus() != 0) {
            if (order.getStatus() == 1) {
                throw new RuntimeException("订单已接单，请勿重复操作");
            }
            if (order.getStatus() == -1) {
                throw new RuntimeException("订单已关闭，无法接单");
            }
            throw new RuntimeException("只有待付款状态的订单才能接单");
        }

        // 4. 执行接单确认（状态从待付款0变为待发货1）
        int affected = orderMapper.confirmOrderByMerchant(orderId, merchantId);
        if (affected == 0) {
            throw new RuntimeException("接单失败，订单状态可能已变更");
        }

        // 5. 记录操作日志
        saveOperationLog(order, "CONFIRM", "商家接单确认",
                0, 1, merchantId, operatorId, operatorName,
                "商家确认接单，订单流转至待发货状态");
    }

    @Override
    public void shipOrderByMerchant(Integer orderId, Integer merchantId, String expressCompany,
                                     String trackingNo, Integer operatorId, String operatorName) {
        System.out.println("=== shipOrderByMerchant 开始 ===");
        System.out.println("订单ID: " + orderId);
        System.out.println("商家ID: " + merchantId);
        
        // 1. 校验订单归属
        System.out.println("步骤1：校验订单归属");
        boolean belongs = validateOrderBelongsToMerchant(orderId, merchantId);
        System.out.println("订单归属校验结果: " + belongs);
        if (!belongs) {
            throw new RuntimeException("无权操作该订单");
        }

        // 2. 查询订单状态
        System.out.println("步骤2：查询订单信息");
        Order order = getOrderById(orderId);
        System.out.println("订单信息 - ID: " + order.getId() + ", 状态: " + order.getStatus());

        // 3. 状态校验：只有待发货(1)状态才能发货
        System.out.println("步骤3：校验订单状态");
        if (order.getStatus() != 1) {
            if (order.getStatus() == 2) {
                throw new RuntimeException("订单已发货，请勿重复操作");
            }
            if (order.getStatus() == -1) {
                throw new RuntimeException("订单已关闭，无法发货");
            }
            throw new RuntimeException("只有待发货状态的订单才能发货，当前状态：" + order.getStatus());
        }

        // 4. 发货参数校验
        System.out.println("步骤4：校验发货参数");
        if (expressCompany == null || expressCompany.trim().isEmpty()) {
            throw new RuntimeException("请选择物流公司");
        }
        if (trackingNo == null || trackingNo.trim().isEmpty()) {
            throw new RuntimeException("请填写运单号");
        }
        System.out.println("物流公司: " + expressCompany);
        System.out.println("运单号: " + trackingNo);

        // 5. 执行发货（状态从待发货1变为已发货2）
        System.out.println("步骤5：更新订单发货信息");
        LocalDateTime shipTime = LocalDateTime.now();
        int affected = orderMapper.updateShipInfo(orderId, expressCompany, trackingNo, shipTime);
        System.out.println("订单表更新影响行数: " + affected);

        // 6. 保存物流信息
        System.out.println("步骤6：保存物流信息");
        orderItemShipMapper.upsert(orderId, expressCompany, trackingNo);
        System.out.println("物流信息保存完成");

        // 7. 记录操作日志
        System.out.println("步骤7：记录操作日志");
        saveOperationLog(order, "SHIP", "商家发货",
                1, 2, merchantId, operatorId, operatorName,
                String.format("物流公司：%s，运单号：%s", expressCompany, trackingNo));
        
        System.out.println("=== shipOrderByMerchant 完成 ===");
    }

    @Override
    public void closeOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName) {
        // 1. 校验订单归属
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权操作该订单");
        }

        // 2. 查询订单状态
        Order order = getOrderById(orderId);
        Integer beforeStatus = order.getStatus();

        // 3. 状态校验：只有待付款(0)或待发货(1)状态才能关单（状态不可回滚）
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            if (order.getStatus() == -1) {
                throw new RuntimeException("订单已关闭，请勿重复操作");
            }
            if (order.getStatus() == 2 || order.getStatus() == 3) {
                throw new RuntimeException("订单已发货或已完成，无法关闭");
            }
            throw new RuntimeException("只有待付款或待发货状态的订单才能关闭");
        }

        // 4. 执行关单
        int affected = orderMapper.closeOrderByMerchant(orderId, merchantId);
        if (affected == 0) {
            throw new RuntimeException("关单失败，订单状态可能已变更");
        }

        // 5. 记录操作日志
        saveOperationLog(order, "CLOSE", "商家手动关单",
                beforeStatus, -1, merchantId, operatorId, operatorName,
                "商家手动关闭订单（订单状态不可回滚）");
    }

    @Override
    public void reopenOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName) {
        // 1. 校验订单归属
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权操作该订单");
        }

        // 2. 查询订单状态
        Order order = getOrderById(orderId);
        Integer beforeStatus = order.getStatus();

        // 3. 状态校验：只有已关闭(-1)状态才能恢复
        if (order.getStatus() != -1) {
            throw new RuntimeException("只有已关闭状态的订单才能恢复");
        }

        // 4. 执行恢复
        int affected = orderMapper.reopenOrderByMerchant(orderId, merchantId);
        if (affected == 0) {
            throw new RuntimeException("恢复订单失败，订单状态可能已变更");
        }

        // 5. 记录操作日志
        saveOperationLog(order, "REOPEN", "恢复已关闭订单",
                beforeStatus, 0, merchantId, operatorId, operatorName,
                "商家恢复已关闭订单，订单状态恢复为待付款");
    }

    @Override
    public void updateOrderRemark(Integer orderId, Integer merchantId, String remark,
                                   Integer operatorId, String operatorName) {
        // 1. 校验订单归属
        if (!validateOrderBelongsToMerchant(orderId, merchantId)) {
            throw new RuntimeException("无权操作该订单");
        }

        // 2. 查询订单
        Order order = getOrderById(orderId);
        String oldRemark = order.getRemark();

        // 3. 更新备注
        orderMapper.updateRemark(orderId, remark);

        // 4. 记录操作日志
        String operationDesc = String.format("修改备注：%s -> %s",
                oldRemark != null ? oldRemark : "", remark != null ? remark : "");
        saveOperationLog(order, "REMARK", "修改备注",
                order.getStatus(), order.getStatus(), merchantId, operatorId, operatorName,
                operationDesc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOrderOperationLogs(Integer orderId) {
        return orderOperationLogMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOrderBelongsToMerchant(Integer orderId, Integer merchantId) {
        Integer orderMerchantId = orderMapper.selectMerchantIdByOrderId(orderId);
        if (orderMerchantId == null) {
            return false;
        }
        return merchantId.equals(orderMerchantId);
    }

    /**
     * 保存操作日志（内部方法）
     */
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
        log.put("remark", null);
        log.put("extendData", extendData);
        log.put("createTime", LocalDateTime.now());
        orderOperationLogMapper.insert(log);
    }

    /**
     * 格式化物流信息
     */
    private String formatShipInfo(Map<String, Object> shipInfo) {
        String express = shipInfo.get("expressCompany") != null ? shipInfo.get("expressCompany").toString() : "";
        String tracking = shipInfo.get("trackingNo") != null ? shipInfo.get("trackingNo").toString() : "";
        return String.format("{\"expressCompany\":\"%s\",\"trackingNo\":\"%s\"}", express, tracking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProductRankingByMerchant(Integer merchantId, Integer sortType,
                                                                  LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> ranking = orderMapper.selectProductRankingByMerchant(merchantId, sortType, startTime, endTime);
        
        // 计算环比增长（简化处理，暂时设为0）
        for (Map<String, Object> item : ranking) {
            item.put("growth", 0.0);
        }
        
        return ranking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFinancialReport(Integer merchantId, 
                                                        LocalDateTime startTime,
                                                        LocalDateTime endTime) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Order> orders = orderMapper.selectOrdersByMerchant(merchantId, null);
        Map<String, List<Order>> ordersByDate = orders.stream()
                .filter(order -> order.getStatus() != 4) // 和仪表盘一致，排除状态4的订单
                .filter(order -> {
                    if (startTime != null && order.getCreateTime().isBefore(startTime)) {
                        return false;
                    }
                    if (endTime != null && order.getCreateTime().isAfter(endTime)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(order -> 
                        order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        
        for (Map.Entry<String, List<Order>> entry : ordersByDate.entrySet()) {
            String date = entry.getKey();
            List<Order> dayOrders = entry.getValue();
            
            double dayTotal = dayOrders.stream()
                    .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0)
                    .sum();
            
            Map<String, Object> incomeItem = new HashMap<>();
            incomeItem.put("date", date);
            incomeItem.put("type", "收入");
            incomeItem.put("description", "商品销售收入");
            incomeItem.put("income", String.format("%.2f", dayTotal));
            incomeItem.put("expense", "0.00");
            incomeItem.put("balance", String.format("%.2f", dayTotal));
            result.add(incomeItem);
            
            Map<String, Object> shippingItem = new HashMap<>();
            shippingItem.put("date", date);
            shippingItem.put("type", "收入");
            shippingItem.put("description", "运费收入");
            shippingItem.put("income", String.format("%.2f", dayTotal * 0.035));
            shippingItem.put("expense", "0.00");
            shippingItem.put("balance", String.format("%.2f", dayTotal * 1.035));
            result.add(shippingItem);
            
            // 平台服务费（假设为销售额的6%）
            Map<String, Object> feeItem = new HashMap<>();
            feeItem.put("date", date);
            feeItem.put("type", "支出");
            feeItem.put("description", "平台服务费");
            feeItem.put("income", "0.00");
            feeItem.put("expense", String.format("%.2f", dayTotal * 0.06));
            feeItem.put("balance", String.format("%.2f", dayTotal * 0.975));
            result.add(feeItem);
        }
        
        // 按日期排序
        result.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));
        
        // 计算累计余额
        double runningBalance = 0;
        for (Map<String, Object> item : result) {
            double income = Double.parseDouble((String) item.get("income"));
            double expense = Double.parseDouble((String) item.get("expense"));
            runningBalance = runningBalance + income - expense;
            item.put("balance", String.format("%.2f", runningBalance));
        }
        
        return result;
    }
}
