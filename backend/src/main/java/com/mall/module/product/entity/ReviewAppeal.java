package com.mall.module.product.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewAppeal {
    private Integer id;
    private Integer reviewId;
    private Integer merchantId;
    private String reason;
    private String description;
    private String evidenceUrls;
    private Integer status;
    private LocalDateTime handleTime;
    private String handleRemark;
    private LocalDateTime createTime;
}
