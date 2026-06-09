package com.mall.module.product.service.impl;

import com.mall.module.product.entity.DisputeApplication;
import com.mall.module.product.mapper.DisputeApplicationMapper;
import com.mall.module.product.service.DisputeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisputeApplicationServiceImpl implements DisputeApplicationService {

    @Autowired
    private DisputeApplicationMapper disputeApplicationMapper;

    @Override
    public List<DisputeApplication> getDisputeList(Integer merchantId) {
        return disputeApplicationMapper.findByMerchantId(merchantId);
    }

    @Override
    public DisputeApplication getDisputeById(Integer id) {
        return disputeApplicationMapper.findById(id);
    }

    @Override
    public int createDispute(DisputeApplication disputeApplication) {
        return disputeApplicationMapper.insert(disputeApplication);
    }

    @Override
    public int updateDispute(DisputeApplication disputeApplication) {
        return disputeApplicationMapper.update(disputeApplication);
    }

    @Override
    public int deleteDispute(Integer id) {
        return disputeApplicationMapper.deleteById(id);
    }
}
