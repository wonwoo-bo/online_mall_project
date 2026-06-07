package com.mall.module.product.service.impl;

import com.mall.module.product.entity.MerchantMessage;
import com.mall.module.product.mapper.MerchantMessageMapper;
import com.mall.module.product.service.MerchantMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MerchantMessageServiceImpl implements MerchantMessageService {

    @Autowired
    private MerchantMessageMapper merchantMessageMapper;

    @Override
    public List<MerchantMessage> getMessagesByMerchantId(Integer merchantId, String messageType, Integer isRead) {
        return merchantMessageMapper.selectByMerchantId(merchantId, messageType, isRead);
    }

    @Override
    public MerchantMessage getMessageById(Long id) {
        return merchantMessageMapper.selectById(id);
    }

    @Override
    @Transactional
    public void createMessage(MerchantMessage message) {
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        merchantMessageMapper.insert(message);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        merchantMessageMapper.updateRead(id);
    }

    @Override
    @Transactional
    public void batchMarkAsRead(Integer merchantId, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            merchantMessageMapper.batchUpdateRead(merchantId, ids);
        }
    }

    @Override
    public int countUnreadMessages(Integer merchantId) {
        return merchantMessageMapper.countUnreadByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        merchantMessageMapper.deleteById(id);
    }
}
