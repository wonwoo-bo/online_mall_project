package com.mall.module.order.mapper;

import com.mall.module.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Order order);
    
    void update(Order order);

    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    void deleteById(Integer id);
    
    Order selectById(Integer id);
    
    Order selectByOrderNo(String orderNo);
    
    List<Order> selectByUserId(Integer userId);
    
    List<Order> selectByStatus(Integer status);
    
    List<Order> selectByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status);
    
    Integer countByUserId(Integer userId);
    
    Integer countByStatus(Integer status);
    
    int countByMerchantId(@Param("merchantId") Integer merchantId);
    
    BigDecimal sumAmountByMerchantAndTime(@Param("merchantId") Integer merchantId, 
                                          @Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
    
    List<Map<String, Object>> selectPendingOrdersByMerchant(@Param("merchantId") Integer merchantId, 
                                                            @Param("limit") int limit);
    
    List<Order> selectOrdersByMerchant(@Param("merchantId") Integer merchantId, @Param("status") Integer status);
    
    List<Map<String, Object>> selectSalesStatisticsByMerchant(@Param("merchantId") Integer merchantId, @Param("days") Integer days);

    void updateGroupOrderNo(@Param("id") Integer id, @Param("groupOrderNo") String groupOrderNo);

    /**
     * 商家订单分页查询（多条件搜索）
     * @param params 包含: merchantId, status, keyword, startDate, endDate, page, pageSize
     */
    List<Order> selectOrdersByMerchantPaged(Map<String, Object> params);

    /**
     * 商家订单总数统计（多条件）
     */
    Integer countOrdersByMerchantPaged(Map<String, Object> params);

    /**
     * 更新订单备注
     */
    int updateRemark(@Param("id") Integer id, @Param("remark") String remark);

    /**
     * 商家接单确认（待付款→待发货）
     */
    int confirmOrderByMerchant(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    /**
     * 商家手动关单
     */
    int closeOrderByMerchant(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    /**
     * 更新发货信息
     */
    int updateShipInfo(@Param("id") Integer id, @Param("expressCompany") String expressCompany, @Param("trackingNo") String trackingNo, @Param("shipTime") LocalDateTime shipTime);

    /**
     * 检查订单是否属于该商家
     */
    Integer selectMerchantIdByOrderId(@Param("orderId") Integer orderId);

    /**
     * 查询订单物流信息
     */
    Map<String, Object> selectShipInfoByOrderId(@Param("orderId") Integer orderId);

    /**
     * 更新订单价格
     */
    int updateOrderPrice(@Param("id") Integer id, @Param("totalAmount") BigDecimal totalAmount);

    /**
     * 查询超时未付款订单
     */
    List<Order> selectTimeoutPendingOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 恢复已关闭订单（取消自动关单）
     */
    int reopenOrderByMerchant(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    /**
     * 查询商家订单利润统计
     */
    List<Map<String, Object>> selectMerchantOrderProfit(Map<String, Object> params);

    /**
     * 查询商家订单（带标签信息）
     */
    List<Map<String, Object>> selectMerchantOrdersWithTags(Map<String, Object> params);

    /**
     * 商品销量排行查询
     */
    List<Map<String, Object>> selectProductRankingByMerchant(@Param("merchantId") Integer merchantId,
                                                              @Param("sortType") Integer sortType,
                                                              @Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);
}