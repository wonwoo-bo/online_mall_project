package com.mall.module.product.mapper;

import com.mall.module.product.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantMapper {

    Merchant selectById(@Param("id") Integer id);
    
    Merchant findById(@Param("id") Integer id);
    
    List<Merchant> findByStatus(@Param("status") Integer status);
    
    List<Merchant> findAll();
    
    void insert(Merchant merchant);
    
    void update(Merchant merchant);
    
    void deleteById(@Param("id") Integer id);
    
    Merchant findByUsername(@Param("username") String username);
    
    void updatePassword(@Param("id") Integer id, @Param("password") String password);
    
    Merchant findByShopName(@Param("shopName") String shopName, @Param("excludeId") Integer excludeId);
}
