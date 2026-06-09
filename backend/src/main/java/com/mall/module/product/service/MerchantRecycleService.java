package com.mall.module.product.service;

import java.util.List;
import java.util.Map;

public interface MerchantRecycleService {

    List<Map<String, Object>> getRecycleList(Integer merchantId, String type);

    void restoreItem(Integer merchantId, String type, Integer id);

    void forceDeleteItem(Integer merchantId, String type, Integer id);
}
