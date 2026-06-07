package com.mall.module.product.mapper;

import com.mall.module.product.entity.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BrandMapper {
    int insert(Brand brand);
    int updateById(Brand brand);
    int deleteById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    Brand selectById(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<Brand> selectByMerchantId(@Param("merchantId") Integer merchantId);
    int checkNameExists(@Param("merchantId") Integer merchantId, @Param("name") String name, @Param("excludeId") Integer excludeId);
    int checkBrandHasProducts(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int updateStatus(@Param("id") Integer id, @Param("merchantId") Integer merchantId, @Param("status") Integer status);
    int moveToRecycle(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    List<Map<String, Object>> selectRecycleList(@Param("merchantId") Integer merchantId);
    int restoreBrand(@Param("id") Integer id, @Param("merchantId") Integer merchantId);
    int forceDeleteBrand(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    // ========== 商品中心管理模块新增方法 ==========

    /**
     * 检查品牌是否已停用（停用品牌无法新增绑定本店商品）
     */
    int checkBrandStatus(@Param("id") Integer id);

    /**
     * 更新品牌商品数量
     */
    int updateProductCount(@Param("merchantId") Integer merchantId);
}