package com.mall.module.order.mapper;

import com.mall.module.order.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentMapper {
    void insert(Payment payment);
    
    void update(Payment payment);
    
    void deleteById(Integer id);
    
    Payment selectById(Integer id);
    
    Payment selectByOrderId(Integer orderId);
    
    List<Payment> selectByStatus(Integer status);
    
    List<Payment> selectByPayMethod(String payMethod);
    
    Integer countByStatus(Integer status);
}
