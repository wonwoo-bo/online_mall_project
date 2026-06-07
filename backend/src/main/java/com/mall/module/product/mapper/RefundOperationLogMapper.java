package com.mall.module.product.mapper;

import com.mall.module.product.entity.RefundOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RefundOperationLogMapper {

    List<RefundOperationLog> selectByRefundId(@Param("refundId") Integer refundId);

    void insert(RefundOperationLog log);
}
