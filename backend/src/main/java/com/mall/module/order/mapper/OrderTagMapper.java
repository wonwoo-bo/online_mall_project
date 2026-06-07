package com.mall.module.order.mapper;

import com.mall.module.order.entity.OrderTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderTagMapper {
    void insert(OrderTag tag);
    
    void deleteById(Integer id);
    
    void deleteByOrderId(Integer orderId);
    
    void deleteByOrderIdAndTagName(@Param("orderId") Integer orderId, @Param("tagName") String tagName);
    
    OrderTag selectById(Integer id);
    
    List<OrderTag> selectByOrderId(Integer orderId);
    
    List<OrderTag> selectByMerchantId(Integer merchantId);
    
    List<Map<String, Object>> selectTagsByMerchantIdGrouped(Integer merchantId);
    
    int countByOrderIdAndTagName(@Param("orderId") Integer orderId, @Param("tagName") String tagName);
}