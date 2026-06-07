package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantSubAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MerchantSubAccountMapper {
    
    List<MerchantSubAccount> selectByMerchantId(@Param("merchantId") Integer merchantId);
    
    MerchantSubAccount selectById(@Param("id") Integer id);
    
    MerchantSubAccount selectByUsername(@Param("username") String username);
    
    int insert(MerchantSubAccount subAccount);
    
    int update(MerchantSubAccount subAccount);
    
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
    
    int deleteById(@Param("id") Integer id);
}
