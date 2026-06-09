package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantPaymentAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MerchantPaymentAccountMapper {
    
    List<MerchantPaymentAccount> selectByMerchantId(@Param("merchantId") Integer merchantId);
    
    MerchantPaymentAccount selectById(@Param("id") Integer id);
    
    int insert(MerchantPaymentAccount account);
    
    int update(MerchantPaymentAccount account);
    
    int deleteById(@Param("id") Integer id);
    
    int updateDefault(@Param("merchantId") Integer merchantId, @Param("id") Integer id);
    
    MerchantPaymentAccount selectDefaultByMerchantId(@Param("merchantId") Integer merchantId);
}
