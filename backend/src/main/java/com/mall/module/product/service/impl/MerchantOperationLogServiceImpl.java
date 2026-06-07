package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantOperationLog;
import com.mall.module.product.mapper.MerchantOperationLogMapper;
import com.mall.module.product.service.MerchantOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MerchantOperationLogServiceImpl implements MerchantOperationLogService {

    @Autowired
    private MerchantOperationLogMapper merchantOperationLogMapper;

    @Override
    public List<MerchantOperationLog> getLogsByMerchantId(Integer merchantId, String operationType, String startTime, String endTime) {
        return merchantOperationLogMapper.selectByMerchantId(merchantId, operationType, startTime, endTime);
    }

    @Override
    @Transactional
    public void createLog(MerchantOperationLog log) {
        log.setCreateTime(LocalDateTime.now());
        merchantOperationLogMapper.insert(log);
    }
}
