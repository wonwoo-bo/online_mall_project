package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantSubAccountPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MerchantSubAccountPermissionMapper {
    
    List<MerchantSubAccountPermission> selectBySubAccountId(@Param("subAccountId") Integer subAccountId);
    
    int insert(MerchantSubAccountPermission permission);
    
    int deleteBySubAccountId(@Param("subAccountId") Integer subAccountId);
    
    int batchInsert(@Param("permissions") List<MerchantSubAccountPermission> permissions);
}
