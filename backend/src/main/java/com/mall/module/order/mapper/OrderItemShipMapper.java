package com.mall.module.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface OrderItemShipMapper {

    /**
     * 新增或更新物流信息
     */
    void upsert(@Param("orderId") Integer orderId, @Param("expressCompany") String expressCompany, @Param("trackingNo") String trackingNo);

    /**
     * 查询物流信息
     */
    Map<String, Object> selectByOrderId(@Param("orderId") Integer orderId);
}
