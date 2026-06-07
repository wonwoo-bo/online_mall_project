package com.mall.module.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderCenterService {
    void modifyOrderPrice(Integer orderId, Integer merchantId, BigDecimal newAmount, String reason, Integer operatorId, String operatorName);
    
    void batchCloseOrders(List<Integer> orderIds, Integer merchantId, Integer operatorId, String operatorName);
    
    void batchUpdateRemark(List<Integer> orderIds, Integer merchantId, String remark, Integer operatorId, String operatorName);
    
    Map<String, Object> getTrackingInfo(Integer orderId, Integer merchantId);
    
    Map<String, Object> getProfitStatistics(Integer merchantId, String startTime, String endTime);
    
    void autoCloseTimeoutOrders();
    
    List<Map<String, Object>> getMerchantOrdersWithTags(Integer merchantId, Map<String, Object> params);
}