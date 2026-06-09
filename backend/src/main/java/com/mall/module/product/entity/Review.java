package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Review {
    private Integer id;
    private Integer orderItemId;
    private Integer productId;
    private Integer userId;
    private Integer merchantId;
    private String content;
    private Integer rating;
    private Integer likeCount;
    private Integer hasAppend;
    private Integer isAnonymous;
    private Integer isTop;
    private LocalDateTime topTime;
    private String merchantReply;
    private LocalDateTime replyTime;
    private LocalDateTime createTime;
}
