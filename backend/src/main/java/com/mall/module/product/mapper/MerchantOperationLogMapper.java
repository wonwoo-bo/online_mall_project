package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MerchantOperationLogMapper {
    
    List<MerchantOperationLog> selectByMerchantId(@Param("merchantId") Integer merchantId,
                                                   @Param("operationType") String operationType,
                                                   @Param("startTime") String startTime,
                                                   @Param("endTime") String endTime);
    
    int insert(MerchantOperationLog log);
}
