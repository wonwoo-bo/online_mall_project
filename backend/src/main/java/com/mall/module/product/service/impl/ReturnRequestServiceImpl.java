package com.mall.module.product.service.impl;

import com.mall.module.product.entity.*;
import com.mall.module.product.mapper.*;
import com.mall.module.product.service.MerchantReturnAddressService;
import com.mall.module.product.service.ReturnRequestService;
import com.mall.module.order.entity.Order;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReturnRequestServiceImpl implements ReturnRequestService {

    @Autowired
    private ReturnRequestMapper returnRequestMapper;

    @Autowired
    private ReturnRequestImageMapper returnRequestImageMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MerchantReturnAddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private RefundOperationLogMapper logMapper;

    @Override
    public List<Map<String, Object>> getReturnList(Integer userId, Integer status) {
        List<ReturnRequest> returnRequests = returnRequestMapper.selectByUserId(userId, status);

        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnRequest request : returnRequests) {
            Map<String, Object> item = new HashMap<>();
            // 扁平化数据
            item.put("id", request.getId());
            item.put("orderId", request.getOrderId());
            item.put("productId", request.getProductId());
            item.put("reason", request.getReason());
            item.put("status", convertStatusToString(request.getStatus()));
            item.put("type", convertTypeToString(request.getType()));
            item.put("createTime", request.getCreateTime());
            item.put("refundAmount", request.getRefundAmount());

            // 关联查询商品信息
            Product product = productMapper.selectById(request.getProductId());
            if (product != null) {
                item.put("productName", product.getName());
                item.put("price", product.getPrice());
                item.put("coverImg", product.getCoverImg());
            }

            list.add(item);
        }

        return list;
    }

    private String convertStatusToString(Integer status) {
        if (status == null) return "pending";
        switch (status) {
            case 0: return "pending";
            case 1: return "approved";
            case 2: return "rejected";
            case 3: return "shipping";
            case 4: return "completed";
            case 5: return "cancelled";
            default: return "pending";
        }
    }

    private String convertTypeToString(Integer type) {
        if (type == null) return "refund";
        switch (type) {
            case 1: return "refund";
            case 2: return "return";
            case 3: return "exchange";
            default: return "refund";
        }
    }

    @Override
    public Map<String, Object> getReturnDetail(Integer id) {
        Map<String, Object> result = new HashMap<>();

        // 查询退换货申请基本信息
        ReturnRequest request = returnRequestMapper.selectById(id);
        if (request == null) {
            return result;
        }

        // 扁平化数据
        result.put("id", request.getId());
        result.put("orderId", request.getOrderId());
        result.put("productId", request.getProductId());
        result.put("reason", request.getReason());
        result.put("status", convertStatusToString(request.getStatus()));
        result.put("type", convertTypeToString(request.getType()));
        result.put("reasonType", request.getReasonType());
        result.put("createTime", request.getCreateTime());
        result.put("refundAmount", request.getRefundAmount());
        result.put("merchantRemark", request.getMerchantRemark());
        result.put("logisticsCompany", request.getLogisticsCompany());
        result.put("logisticsNo", request.getLogisticsNo());

        // 查询退换货图片
        List<ReturnRequestImage> imageList = returnRequestImageMapper.selectByReturnRequestId(id);
        List<String> images = new ArrayList<>();
        for (ReturnRequestImage img : imageList) {
            images.add(img.getImageUrl());
        }
        result.put("images", images);

        // 查询商品信息
        Product product = productMapper.selectById(request.getProductId());
        if (product != null) {
            result.put("productName", product.getName());
            result.put("price", product.getPrice());
            result.put("coverImg", product.getCoverImg());
        }

        return result;
    }

    @Override
    public void submitReturn(ReturnRequest returnRequest, List<String> imageUrls) {
        // 检查该订单是否已有进行中（待审核/已通过/退货中）的退换货记录，防止重复提交
        if (returnRequest.getOrderId() != null) {
            List<ReturnRequest> existingRequests = returnRequestMapper.selectByUserId(
                    returnRequest.getUserId(), null);
            for (ReturnRequest existing : existingRequests) {
                if (existing.getOrderId().equals(returnRequest.getOrderId())
                        && existing.getStatus() != null
                        && (existing.getStatus() == 0 || existing.getStatus() == 1 || existing.getStatus() == 3)) {
                    throw new RuntimeException("该订单已有进行中的退换货申请，请勿重复提交");
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        returnRequest.setCreateTime(now);
        returnRequest.setUpdateTime(now);
        returnRequestMapper.insert(returnRequest);

        // 将订单状态改为"退换货中"(5)
        if (returnRequest.getOrderId() != null) {
            orderMapper.updateStatus(returnRequest.getOrderId(), 5);
        }

        // 批量插入退换货图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                ReturnRequestImage image = new ReturnRequestImage();
                image.setReturnRequestId(returnRequest.getId());
                image.setImageUrl(imageUrl);
                image.setCreateTime(LocalDateTime.now());
                returnRequestImageMapper.insert(image);
            }
        }
    }

    @Override
    public void updateLogistics(Integer id, String logisticsCompany, String logisticsNo) {
        ReturnRequest returnRequest = returnRequestMapper.selectById(id);
        if (returnRequest == null) {
            throw new RuntimeException("退换货申请不存在");
        }
        // BUG-007: 只有已通过（status=1）的申请才能填写物流
        if (returnRequest.getStatus() != 1) {
            throw new RuntimeException("只有审核通过的申请才能填写物流信息");
        }
        returnRequest.setLogisticsCompany(logisticsCompany);
        returnRequest.setLogisticsNo(logisticsNo);
        returnRequest.setStatus(3); // 退货中/待收货
        returnRequestMapper.update(returnRequest);
    }

    @Override
    public int countPendingByMerchant(Integer merchantId) {
        return returnRequestMapper.countPendingByMerchant(merchantId);
    }

    @Override
    public List<Map<String, Object>> getReturnListByMerchant(Integer merchantId, Integer status) {
        return returnRequestMapper.selectByMerchantId(merchantId, status);
    }

    @Override
    public void handleReturn(Integer id, Integer status, String remark) {
        ReturnRequest request = returnRequestMapper.selectById(id);
        if (request == null) {
            throw new RuntimeException("退换货申请不存在");
        }
        // BUG-008: 状态流转校验
        // 合法流转: 0(待审核) -> 1(已通过) 或 0(待审核) -> 2(已拒绝) 或 0(待审核) -> 4(已完成，仅退款直接完成)
        //          3(退货中) -> 4(已完成)
        //          1(已通过) -> 2(已拒绝) (撤销通过)
        Integer currentStatus = request.getStatus();
        boolean validTransition = false;
        switch (currentStatus) {
            case 0: // 待审核 -> 已通过/已拒绝/已完成(仅退款)
                validTransition = (status == 1 || status == 2 || status == 4);
                break;
            case 1: // 已通过 -> 已拒绝(撤销) 或 保持已通过
                validTransition = (status == 2 || status == 1);
                break;
            case 2: // 已拒绝 -> 已通过(重新审核通过)
                validTransition = (status == 1);
                break;
            case 3: // 退货中 -> 已完成
                validTransition = (status == 4);
                break;
            default:
                validTransition = false;
        }
        if (!validTransition) {
            throw new RuntimeException("无效的状态变更：当前状态(" + currentStatus + ")不能变更为(" + status + ")");
        }
        request.setStatus(status);
        request.setMerchantRemark(remark);
        returnRequestMapper.update(request);

        // 退换货完成时扣减用户积分 + 恢复订单状态
        if (status != null && status == 4) {
            System.out.println("=== 退换货完成，开始处理 ===");
            Integer orderId = request.getOrderId();
            System.out.println("  orderId=" + orderId);
            // 恢复订单状态为已完成(3)
            if (orderId != null) {
                try {
                    orderMapper.updateStatus(orderId, 3);
                    System.out.println("  订单状态已恢复为已完成");
                } catch (Exception e) {
                    System.out.println("  恢复订单状态失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // 扣减积分
            try {
                Order order = orderMapper.selectById(orderId);
                System.out.println("  order=" + (order != null ? "存在, userId=" + order.getUserId() + ", amount=" + order.getTotalAmount() : "null"));
                if (order != null && order.getUserId() != null) {
                    int deductPoints = order.getTotalAmount() != null ? order.getTotalAmount().intValue() : 0;
                    System.out.println("  准备扣减积分: userId=" + order.getUserId() + ", points=" + deductPoints);
                    if (deductPoints > 0) {
                        userService.deductPoints(order.getUserId(), deductPoints, "退换货完成，扣减订单积分");
                        System.out.println("  积分扣减成功！");
                    } else {
                        System.out.println("  扣减积分为0，跳过");
                    }
                } else {
                    System.out.println("  订单或用户ID为空，跳过积分扣减");
                }
            } catch (Exception e) {
                System.out.println("  积分扣减异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("=== 退换货处理 status=" + status + "，非完成状态，跳过积分扣减 ===");
        }
    }

    @Override
    public void cancelReturn(Integer id) {
        ReturnRequest request = returnRequestMapper.selectById(id);
        if (request == null) {
            throw new RuntimeException("退换货申请不存在");
        }
        if (request.getStatus() != 0) {
            throw new RuntimeException("只有待审核的申请才能取消");
        }

        // 设置状态为已取消（使用状态5表示已取消）
        request.setStatus(5);
        returnRequestMapper.update(request);

        // 恢复订单状态为已完成（状态3）
        if (request.getOrderId() != null && request.getOrderId() > 0) {
            Order order = orderMapper.selectById(request.getOrderId());
            if (order != null && order.getStatus() == 5) {
                order.setStatus(3); // 恢复为已完成状态
                orderMapper.update(order);
            }
        }
    }

    @Override
    public Map<String, Object> getMerchantRefundPage(
            Integer merchantId, Integer status, Integer type,
            String startTime, String endTime, String orderNo,
            String userId, String userName,
            Integer page, Integer pageSize) {

        List<Map<String, Object>> allList = returnRequestMapper.selectPageByMerchantId(
                merchantId, status, type, startTime, endTime, orderNo, userId, userName);

        int total = allList.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<Map<String, Object>> pageList = fromIndex < total ? allList.subList(fromIndex, toIndex) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    @Override
    public Map<String, Object> getRefundDetail(Integer id, Integer merchantId) {
        Map<String, Object> detail = returnRequestMapper.selectDetailById(id);
        if (detail == null) {
            throw new RuntimeException("售后记录不存在");
        }
        
        Integer refundMerchantId = (Integer) detail.get("merchantId");
        if (refundMerchantId == null) {
            refundMerchantId = (Integer) detail.get("merchant_id");
        }
        
        if (refundMerchantId == null || !refundMerchantId.equals(merchantId)) {
            throw new RuntimeException("无权查看该售后记录");
        }
        
        return detail;
    }

    @Override
    @Transactional
    public void auditRefund(Integer id, Integer merchantId, Integer agree, String rejectReason, String remark) {
        ReturnRequest refund = returnRequestMapper.selectById(id);
        if (refund == null) {
            throw new RuntimeException("售后记录不存在");
        }
        if (!refund.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该售后记录");
        }
        if (refund.getStatus() != 0) {
            throw new RuntimeException("只有待审核的售后才能审核");
        }

        Integer oldStatus = refund.getStatus();
        Integer newStatus;

        if (agree == 1) {
            // 同意
            if (refund.getType() == 1) {
                // 仅退款，直接进入待退款状态
                newStatus = 3;
            } else {
                // 退货退款，设置退货地址并进入待退货状态
                MerchantReturnAddress defaultAddress = addressService.getDefaultAddress(merchantId);
                if (defaultAddress != null) {
                    refund.setReturnAddressId(defaultAddress.getId());
                    refund.setReturnReceiverName(defaultAddress.getReceiverName());
                    refund.setReturnReceiverPhone(defaultAddress.getReceiverPhone());
                    refund.setReturnAddress(defaultAddress.getProvince() + defaultAddress.getCity() + defaultAddress.getDistrict() + defaultAddress.getDetailAddress());
                }
                newStatus = 1; // 已同意
            }
        } else {
            // 拒绝
            if (rejectReason == null || rejectReason.trim().isEmpty()) {
                throw new RuntimeException("拒绝理由不能为空");
            }
            refund.setRejectReason(rejectReason);
            newStatus = 2; // 已拒绝
        }

        refund.setStatus(newStatus);
        refund.setMerchantRemark(remark);
        refund.setAuditTime(LocalDateTime.now());
        returnRequestMapper.update(refund);

        // 商家拒绝时，恢复订单状态（从"退换货中5"恢复为"已完成3"），允许用户再次申请
        if (newStatus == 2 && refund.getOrderId() != null) {
            try {
                Order order = orderMapper.selectById(refund.getOrderId());
                if (order != null && order.getStatus() != null && order.getStatus() == 5) {
                    order.setStatus(3); // 恢复为已完成
                    orderMapper.update(order);
                }
            } catch (Exception e) {
                System.out.println("恢复订单状态失败: " + e.getMessage());
            }
        }

        // 记录操作日志
        Merchant merchant = merchantMapper.selectById(merchantId);
        RefundOperationLog log = new RefundOperationLog();
        log.setRefundId(id);
        log.setMerchantId(merchantId);
        log.setOperatorType("merchant");
        log.setOperatorId(merchantId);
        log.setOperatorName(merchant != null ? merchant.getShopName() : "");
        log.setOperationType("audit");
        log.setRemark(remark);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    @Transactional
    public void confirmReceive(Integer id, Integer merchantId) {
        ReturnRequest refund = returnRequestMapper.selectById(id);
        if (refund == null) {
            throw new RuntimeException("售后记录不存在");
        }
        if (!refund.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该售后记录");
        }
        if (refund.getStatus() != 2) {
            throw new RuntimeException("只有退货中的售后才能确认收货");
        }

        Integer oldStatus = refund.getStatus();
        refund.setStatus(3); // 已收货待退款
        refund.setReceiveTime(LocalDateTime.now());
        returnRequestMapper.update(refund);

        // 记录操作日志
        Merchant merchant = merchantMapper.selectById(merchantId);
        RefundOperationLog log = new RefundOperationLog();
        log.setRefundId(id);
        log.setMerchantId(merchantId);
        log.setOperatorType("merchant");
        log.setOperatorId(merchantId);
        log.setOperatorName(merchant != null ? merchant.getShopName() : "");
        log.setOperationType("confirm_receive");
        log.setOldStatus(oldStatus);
        log.setNewStatus(3);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    @Transactional
    public void confirmRefund(Integer id, Integer merchantId) {
        ReturnRequest refund = returnRequestMapper.selectById(id);
        if (refund == null) {
            throw new RuntimeException("售后记录不存在");
        }
        if (!refund.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权操作该售后记录");
        }
        if (refund.getStatus() != 3) {
            throw new RuntimeException("只有待退款的售后才能确认退款");
        }

        Integer oldStatus = refund.getStatus();
        refund.setStatus(4); // 已完成
        refund.setActualRefundAmount(refund.getRefundAmount());
        refund.setRefundTime(LocalDateTime.now());
        returnRequestMapper.update(refund);

        // 记录操作日志
        Merchant merchant = merchantMapper.selectById(merchantId);
        RefundOperationLog log = new RefundOperationLog();
        log.setRefundId(id);
        log.setMerchantId(merchantId);
        log.setOperatorType("merchant");
        log.setOperatorId(merchantId);
        log.setOperatorName(merchant != null ? merchant.getShopName() : "");
        log.setOperationType("confirm_refund");
        log.setOldStatus(oldStatus);
        log.setNewStatus(4);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);

        // 退款完成时恢复订单状态为已完成，并扣减积分
        try {
            Order order = orderMapper.selectById(refund.getOrderId());
            if (order != null) {
                // 恢复订单状态为已完成（3）
                if (order.getStatus() == 5) {
                    order.setStatus(3);
                    orderMapper.update(order);
                }
                if (order.getUserId() != null) {
                    int deductPoints = refund.getRefundAmount() != null ? refund.getRefundAmount().intValue() : 0;
                    if (deductPoints > 0) {
                        userService.deductPoints(order.getUserId(), deductPoints, "退款完成，扣减订单积分");
                    }
                }
            }
        } catch (Exception e) {
            // 积分扣减异常不影响退款流程
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getRefundStatistics(Integer merchantId) {
        return returnRequestMapper.selectStatisticsByMerchantId(merchantId);
    }

    @Override
    public List<RefundOperationLog> getRefundLogs(Integer id, Integer merchantId) {
        ReturnRequest refund = returnRequestMapper.selectById(id);
        if (refund == null) {
            throw new RuntimeException("售后记录不存在");
        }
        if (!refund.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权查看该售后记录");
        }
        return logMapper.selectByRefundId(id);
    }

    @Override
    public List<Map<String, Object>> getReasonStatistics(Integer merchantId, String startTime, String endTime) {
        return returnRequestMapper.selectReasonStatisticsByMerchantId(merchantId, startTime, endTime);
    }
}
