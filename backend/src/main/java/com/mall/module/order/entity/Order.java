package com.mall.module.order.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private BigDecimal totalAmount;
    private Integer status;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String remark;
    private String groupOrderNo;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;

    // 关联查询的支付方式
    private String payMethod;
    
    // 关联查询的买家用户名
    private String userName;

    // 关联的订单商品列表
    private List<OrderItem> items;

    // 物流信息
    private String expressCompany;
    private String trackingNo;

    // 扩展数据（物流信息等）
    private String extendData;

    // 商家备注
    private String merchantRemark;

    // 订单标签（逗号分隔的标签名称）
    private String tags;

    // 订单标签列表（包含名称和颜色）
    private List<OrderTag> tagList;

    public Integer getMerchantId() {
        return 0;
    }
}
