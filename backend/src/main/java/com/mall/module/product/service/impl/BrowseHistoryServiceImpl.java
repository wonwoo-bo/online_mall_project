package com.mall.module.product.service.impl;

import com.mall.module.product.entity.BrowseHistory;
import com.mall.module.product.entity.Product;
import com.mall.module.product.mapper.BrowseHistoryMapper;
import com.mall.module.product.mapper.ProductMapper;
import com.mall.module.product.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Map<String, Object>> getHistoryList(Integer userId, int page, int size) {
        int offset = (page - 1) * size;
        List<BrowseHistory> histories = browseHistoryMapper.selectByUserId(userId, offset, size);

        List<Map<String, Object>> list = new ArrayList<>();
        for (BrowseHistory history : histories) {
            Map<String, Object> item = new HashMap<>();
            item.put("history", history);

            // 关联查询商品信息
            Product product = productMapper.selectById(history.getProductId());
            item.put("product", product);

            list.add(item);
        }

        return list;
    }

    @Override
    public void addHistory(Integer userId, Integer productId) {
        // 先删除已存在的浏览记录，然后插入新记录，这样可以把该商品移到最前面
        browseHistoryMapper.deleteByUserIdAndProductId(userId, productId);

        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setProductId(productId);
        history.setBrowseTime(LocalDateTime.now());
        browseHistoryMapper.insert(history);
    }

    @Override
    public void deleteHistory(Integer id) {
        browseHistoryMapper.deleteById(id);
    }

    @Override
    public void clearHistory(Integer userId) {
        browseHistoryMapper.deleteByUserId(userId);
    }
}
