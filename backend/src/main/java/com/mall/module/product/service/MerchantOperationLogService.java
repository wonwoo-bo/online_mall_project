package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantOperationLog;

import java.util.List;

public interface MerchantOperationLogService {
    List<MerchantOperationLog> getLogsByMerchantId(Integer merchantId, String operationType, String startTime, String endTime);
    void createLog(MerchantOperationLog log);
}
