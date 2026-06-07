package com.mall.module.order.service.impl;

import com.mall.module.order.entity.OrderTag;
import com.mall.module.order.mapper.OrderTagMapper;
import com.mall.module.order.service.OrderTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderTagServiceImpl implements OrderTagService {

    @Autowired
    private OrderTagMapper orderTagMapper;

    @Override
    public void addTag(Integer orderId, Integer merchantId, String tagName, String tagColor) {
        int count = orderTagMapper.countByOrderIdAndTagName(orderId, tagName);
        if (count > 0) {
            return;
        }
        
        OrderTag tag = new OrderTag();
        tag.setOrderId(orderId);
        tag.setMerchantId(merchantId);
        tag.setTagName(tagName);
        tag.setTagColor(tagColor != null ? tagColor : "#409EFF");
        tag.setCreateTime(LocalDateTime.now());
        orderTagMapper.insert(tag);
    }

    @Override
    public void removeTag(Integer orderId, Integer merchantId, String tagName) {
        orderTagMapper.deleteByOrderIdAndTagName(orderId, tagName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderTag> getTagsByOrderId(Integer orderId) {
        return orderTagMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTagStatistics(Integer merchantId) {
        return orderTagMapper.selectTagsByMerchantIdGrouped(merchantId);
    }

    @Override
    public void batchAddTags(List<Integer> orderIds, Integer merchantId, String tagName, String tagColor) {
        for (Integer orderId : orderIds) {
            addTag(orderId, merchantId, tagName, tagColor);
        }
    }
}