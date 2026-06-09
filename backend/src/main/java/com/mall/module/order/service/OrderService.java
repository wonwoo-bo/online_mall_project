package com.mall.module.order.service;

import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Integer userId, List<OrderItem> items, String address);

    List<Order> createOrderFromCart(Integer userId, List<Integer> cartIds, String address);

    List<Order> createOrderDirect(Integer userId, List<Map<String, Object>> items, String address);

    Order getOrderById(Integer id);

    Map<String, Object> getOrderListPaged(Integer userId, Integer status, Integer page, Integer size);

    // 以下为额外保留的接口方法
    Order getOrderByOrderNo(String orderNo);

    List<Order> getOrderListByUserId(Integer userId);

    List<Order> getOrderListByStatus(Integer status);

    Order updateOrderStatus(Integer id, Integer status);

    void cancelOrder(Integer id);

    List<OrderItem> getOrderItems(Integer orderId);

    /**
     * 统计商家订单数量
     */
    int countByMerchant(Integer merchantId);

    /**
     * 统计商家指定时间内销售额
     */
    double sumAmountByMerchantAndTime(Integer merchantId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * 获取商家待处理订单列表
     */
    List<Map<String, Object>> getPendingOrdersByMerchant(Integer merchantId, int limit);

    /**
     * 获取商家订单列表
     */
    List<Order> getOrdersByMerchant(Integer merchantId, Integer status);

    /**
     * 获取商家销售统计
     */
    List<Map<String, Object>> getSalesStatisticsByMerchant(Integer merchantId, Integer days);

    // ========== 商家订单管理新功能 ==========

    /**
     * 商家订单分页查询（多条件）
     * @param params merchantId, statusList, keyword, startDate, endDate, page, pageSize
     */
    Map<String, Object> getOrdersByMerchantPaged(Map<String, Object> params);

    /**
     * 商家订单发货（带参数校验和重复发货拦截）
     * @param orderId 订单ID
     * @param merchantId 商家ID（用于校验订单归属）
     * @param expressCompany 物流公司
     * @param trackingNo 运单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void shipOrderByMerchant(Integer orderId, Integer merchantId, String expressCompany, String trackingNo, Integer operatorId, String operatorName);

    /**
     * 商家接单确认（校验订单，流转至待发货状态）
     * @param orderId 订单ID
     * @param merchantId 商家ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void confirmOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName);

    /**
     * 商家手动关单（只能关待付款/待发货状态订单）
     * @param orderId 订单ID
     * @param merchantId 商家ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void closeOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName);

    /**
     * 恢复已关闭订单（取消自动关单）
     * @param orderId 订单ID
     * @param merchantId 商家ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void reopenOrderByMerchant(Integer orderId, Integer merchantId, Integer operatorId, String operatorName);

    /**
     * 修改订单备注
     * @param orderId 订单ID
     * @param merchantId 商家ID
     * @param remark 备注内容
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void updateOrderRemark(Integer orderId, Integer merchantId, String remark, Integer operatorId, String operatorName);

    /**
     * 获取订单操作日志
     */
    List<Map<String, Object>> getOrderOperationLogs(Integer orderId);

    /**
     * 校验订单归属商家
     */
    boolean validateOrderBelongsToMerchant(Integer orderId, Integer merchantId);

    /**
     * 获取商品销量排行
     */
    List<Map<String, Object>> getProductRankingByMerchant(Integer merchantId, Integer sortType,
                                                          java.time.LocalDateTime startTime,
                                                          java.time.LocalDateTime endTime);

    /**
     * 获取财务报表数据
     */
    List<Map<String, Object>> getFinancialReport(Integer merchantId, 
                                                 java.time.LocalDateTime startTime,
                                                 java.time.LocalDateTime endTime);
}
