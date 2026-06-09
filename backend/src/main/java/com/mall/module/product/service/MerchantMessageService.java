package com.mall.module.product.service;

import com.mall.module.product.entity.MerchantMessage;

import java.util.List;

public interface MerchantMessageService {
    List<MerchantMessage> getMessagesByMerchantId(Integer merchantId, String messageType, Integer isRead);
    MerchantMessage getMessageById(Long id);
    void createMessage(MerchantMessage message);
    void markAsRead(Long id);
    void batchMarkAsRead(Integer merchantId, List<Long> ids);
    int countUnreadMessages(Integer merchantId);
    void deleteMessage(Long id);
}
