package com.mall.module.product.mapper;

import com.mall.module.product.entity.DisputeApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DisputeApplicationMapper {
    List<DisputeApplication> findByMerchantId(@Param("merchantId") Integer merchantId);
    DisputeApplication findById(@Param("id") Integer id);
    int insert(DisputeApplication disputeApplication);
    int update(DisputeApplication disputeApplication);
    int deleteById(@Param("id") Integer id);
}
