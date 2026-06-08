package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReturnRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReturnRequestMapper {

    List<ReturnRequest> selectByUserId(@Param("userId") Integer userId,
                                       @Param("status") Integer status);

    ReturnRequest selectById(@Param("id") Integer id);

    void insert(ReturnRequest returnRequest);

    void update(ReturnRequest returnRequest);

    int countPendingByMerchant(@Param("merchantId") Integer merchantId);

    List<Map<String, Object>> selectByMerchantId(@Param("merchantId") Integer merchantId, @Param("status") Integer status);

    Map<String, Object> selectDetailById(@Param("id") Integer id);

    List<Map<String, Object>> selectPageByMerchantId(
            @Param("merchantId") Integer merchantId,
            @Param("status") Integer status,
            @Param("type") Integer type,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("orderNo") String orderNo,
            @Param("userId") String userId,
            @Param("userName") String userName);

    Map<String, Object> selectStatisticsByMerchantId(@Param("merchantId") Integer merchantId);

    List<Map<String, Object>> selectReasonStatisticsByMerchantId(
            @Param("merchantId") Integer merchantId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * 检查某订单下是否有已完成的退换货记录（status=4表示已完成退款）
     */
    int countCompletedByOrderId(@Param("orderId") Integer orderId);
}
