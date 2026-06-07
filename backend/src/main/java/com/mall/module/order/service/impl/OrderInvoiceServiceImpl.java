package com.mall.module.order.service.impl;

import com.mall.module.order.entity.Order;
import com.mall.module.order.entity.OrderInvoice;
import com.mall.module.order.mapper.OrderInvoiceMapper;
import com.mall.module.order.mapper.OrderMapper;
import com.mall.module.order.service.OrderInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderInvoiceServiceImpl implements OrderInvoiceService {

    @Autowired
    private OrderInvoiceMapper orderInvoiceMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public OrderInvoice generateInvoice(Integer orderId, Integer merchantId, String title, String taxNo, Integer invoiceType) {
        OrderInvoice existing = orderInvoiceMapper.selectByOrderId(orderId);
        if (existing != null && existing.getStatus() == 1) {
            throw new RuntimeException("该订单已存在有效发票");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        OrderInvoice invoice;
        if (existing != null && existing.getStatus() == 2) {
            invoice = existing;
            invoice.setInvoiceNo(generateInvoiceNo());
            invoice.setInvoiceType(invoiceType != null ? invoiceType : 1);
            invoice.setTitle(title);
            invoice.setTaxNo(taxNo);
            invoice.setAmount(order.getTotalAmount());
            invoice.setStatus(1);
            invoice.setCancelTime(null);
            orderInvoiceMapper.update(invoice);
        } else {
            invoice = new OrderInvoice();
            invoice.setOrderId(orderId);
            invoice.setMerchantId(merchantId);
            invoice.setInvoiceNo(generateInvoiceNo());
            invoice.setInvoiceType(invoiceType != null ? invoiceType : 1);
            invoice.setTitle(title);
            invoice.setTaxNo(taxNo);
            invoice.setAmount(order.getTotalAmount());
            invoice.setStatus(1);
            invoice.setCreateTime(LocalDateTime.now());
            orderInvoiceMapper.insert(invoice);
        }
        return invoice;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderInvoice getInvoiceByOrderId(Integer orderId) {
        return orderInvoiceMapper.selectByOrderId(orderId);
    }

    @Override
    public void cancelInvoice(Integer invoiceId, Integer merchantId) {
        OrderInvoice invoice = orderInvoiceMapper.selectById(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("发票不存在");
        }
        if (!merchantId.equals(invoice.getMerchantId())) {
            throw new RuntimeException("无权操作该发票");
        }
        if (invoice.getStatus() == 2) {
            throw new RuntimeException("发票已作废");
        }
        orderInvoiceMapper.cancelInvoice(invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderInvoice> getInvoicesByMerchantId(Integer merchantId) {
        return orderInvoiceMapper.selectByMerchantId(merchantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInvoiceStatistics(Integer merchantId) {
        List<OrderInvoice> invoices = orderInvoiceMapper.selectByMerchantId(merchantId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", invoices.size());
        
        long validCount = invoices.stream().filter(i -> i.getStatus() == 1).count();
        long canceledCount = invoices.stream().filter(i -> i.getStatus() == 2).count();
        
        BigDecimal totalAmount = invoices.stream()
                .filter(i -> i.getStatus() == 1)
                .map(OrderInvoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("validCount", validCount);
        stats.put("canceledCount", canceledCount);
        stats.put("totalAmount", totalAmount);
        
        return stats;
    }

    private String generateInvoiceNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "INV" + timestamp + uuid;
    }
}