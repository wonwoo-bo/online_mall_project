package com.mall.module.order.service;

import com.mall.module.order.entity.OrderTag;

import java.util.List;
import java.util.Map;

public interface OrderTagService {
    void addTag(Integer orderId, Integer merchantId, String tagName, String tagColor);
    
    void removeTag(Integer orderId, Integer merchantId, String tagName);
    
    List<OrderTag> getTagsByOrderId(Integer orderId);
    
    List<Map<String, Object>> getTagStatistics(Integer merchantId);
    
    void batchAddTags(List<Integer> orderIds, Integer merchantId, String tagName, String tagColor);
}