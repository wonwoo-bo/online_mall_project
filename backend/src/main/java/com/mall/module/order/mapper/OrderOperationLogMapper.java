package com.mall.module.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderOperationLogMapper {

    /**
     * 新增操作日志
     */
    int insert(Map<String, Object> log);

    /**
     * 查询订单操作日志列表
     */
    List<Map<String, Object>> selectByOrderId(@Param("orderId") Integer orderId);

    /**
     * 查询商家操作日志列表（分页）
     */
    List<Map<String, Object>> selectByMerchantPaged(Map<String, Object> params);

    /**
     * 统计商家操作日志数量
     */
    Integer countByMerchantPaged(Map<String, Object> params);
}
