package com.mall.module.product.service;

import com.mall.module.product.entity.DisputeApplication;

import java.util.List;

public interface DisputeApplicationService {
    List<DisputeApplication> getDisputeList(Integer merchantId);
    DisputeApplication getDisputeById(Integer id);
    int createDispute(DisputeApplication disputeApplication);
    int updateDispute(DisputeApplication disputeApplication);
    int deleteDispute(Integer id);
}
