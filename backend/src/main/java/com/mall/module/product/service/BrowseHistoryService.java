package com.mall.module.product.service;

import java.util.List;
import java.util.Map;

public interface BrowseHistoryService {

    /**
     * 浏览历史（含商品信息）
     */
    List<Map<String, Object>> getHistoryList(Integer userId, int page, int size);

    /**
     * 添加浏览记录
     */
    void addHistory(Integer userId, Integer productId);

    /**
     * 删除单条浏览记录
     */
    void deleteHistory(Integer id);

    /**
     * 清空浏览历史
     */
    void clearHistory(Integer userId);
}
