package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MerchantMessageMapper {
    
    List<MerchantMessage> selectByMerchantId(@Param("merchantId") Integer merchantId, 
                                              @Param("messageType") String messageType,
                                              @Param("isRead") Integer isRead);
    
    MerchantMessage selectById(@Param("id") Long id);
    
    int insert(MerchantMessage message);
    
    int updateRead(@Param("id") Long id);
    
    int batchUpdateRead(@Param("merchantId") Integer merchantId, @Param("ids") List<Long> ids);
    
    int countUnreadByMerchantId(@Param("merchantId") Integer merchantId);
    
    int deleteById(@Param("id") Long id);
}
