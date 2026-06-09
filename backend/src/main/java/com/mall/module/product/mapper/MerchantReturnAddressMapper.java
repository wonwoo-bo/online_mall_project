package com.mall.module.product.mapper;

import com.mall.module.product.entity.MerchantReturnAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantReturnAddressMapper {

    List<MerchantReturnAddress> selectByMerchantId(@Param("merchantId") Integer merchantId);

    MerchantReturnAddress selectById(@Param("id") Integer id);

    MerchantReturnAddress selectDefaultByMerchantId(@Param("merchantId") Integer merchantId);

    void insert(MerchantReturnAddress address);

    void update(MerchantReturnAddress address);

    void deleteById(@Param("id") Integer id);

    void updateNonDefaultByMerchantId(@Param("merchantId") Integer merchantId);
}
