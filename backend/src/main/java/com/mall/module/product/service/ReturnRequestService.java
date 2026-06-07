package com.mall.module.product.service;

import com.mall.module.product.entity.RefundOperationLog;
import com.mall.module.product.entity.ReturnRequest;

import java.util.List;
import java.util.Map;

public interface ReturnRequestService {

    /**
     * 退换货列表
     */
    List<Map<String, Object>> getReturnList(Integer userId, Integer status);

    /**
     * 退换货详情（含图片）
     */
    Map<String, Object> getReturnDetail(Integer id);

    /**
     * 提交退换货申请
     */
    void submitReturn(ReturnRequest returnRequest, List<String> imageUrls);

    /**
     * 更新物流信息
     */
    void updateLogistics(Integer id, String logisticsCompany, String logisticsNo);

    /**
     * 统计商家待处理退换货数量
     */
    int countPendingByMerchant(Integer merchantId);

    /**
     * 获取商家退换货列表
     */
    List<Map<String, Object>> getReturnListByMerchant(Integer merchantId, Integer status);

    /**
     * 处理退换货申请
     */
    void handleReturn(Integer id, Integer status, String remark);

    /**
     * 取消退换货申请
     */
    void cancelReturn(Integer id);

    /**
     * 获取商家售后列表（分页）
     */
    Map<String, Object> getMerchantRefundPage(
            Integer merchantId, Integer status, Integer type,
            String startTime, String endTime, String orderNo,
            String userId, String userName,
            Integer page, Integer pageSize);

    /**
     * 获取售后详情
     */
    Map<String, Object> getRefundDetail(Integer id, Integer merchantId);

    /**
     * 审核售后申请
     */
    void auditRefund(Integer id, Integer merchantId, Integer agree, String rejectReason, String remark);

    /**
     * 确认收货
     */
    void confirmReceive(Integer id, Integer merchantId);

    /**
     * 确认退款
     */
    void confirmRefund(Integer id, Integer merchantId);

    /**
     * 获取售后统计
     */
    Map<String, Object> getRefundStatistics(Integer merchantId);

    /**
     * 获取售后操作日志
     */
    List<RefundOperationLog> getRefundLogs(Integer id, Integer merchantId);

    /**
     * 获取售后原因统计
     */
    List<Map<String, Object>> getReasonStatistics(Integer merchantId, String startTime, String endTime);
}
