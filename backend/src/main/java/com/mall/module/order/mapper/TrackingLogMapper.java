package com.mall.module.order.mapper;

import com.mall.module.order.entity.TrackingLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrackingLogMapper {
    void insert(TrackingLog log);
    
    void batchInsert(List<TrackingLog> logs);
    
    void deleteByOrderId(Integer orderId);
    
    List<TrackingLog> selectByOrderId(Integer orderId);
    
    List<TrackingLog> selectByTrackingNo(String trackingNo);
}