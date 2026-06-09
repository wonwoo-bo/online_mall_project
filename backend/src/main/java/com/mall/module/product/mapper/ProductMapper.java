package com.mall.module.product.mapper;

import com.mall.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    List<Product> selectProductList(@Param("categoryId") Integer categoryId,
                                    @Param("keyword") String keyword,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("sort") String sort,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    int countProductList(@Param("categoryId") Integer categoryId,
                         @Param("keyword") String keyword,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice);

    Product selectById(@Param("id") Integer id);

    void incrementViews(@Param("id") Integer id);

    void incrementSales(@Param("id") Integer id, @Param("quantity") Integer quantity);

    List<Product> selectRecommended(@Param("limit") int limit);

    int countByMerchantId(@Param("merchantId") Integer merchantId);

    List<Product> selectByMerchantId(@Param("merchantId") Integer merchantId,
                                     @Param("keyword") String keyword,
                                     @Param("categoryId") Integer categoryId,
                                     @Param("brandId") Integer brandId,
                                     @Param("status") Integer status,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    int countByMerchantIdWithKeyword(@Param("merchantId") Integer merchantId,
                                     @Param("keyword") String keyword,
                                     @Param("categoryId") Integer categoryId,
                                     @Param("brandId") Integer brandId,
                                     @Param("status") Integer status);

    void insert(Product product);

    void update(Product product);

    void deleteById(@Param("id") Integer id);

    void moveToRecycle(@Param("id") Integer id);

    void updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    List<Map<String, Object>> selectRecycleList(@Param("merchantId") Integer merchantId);

    void restoreProduct(@Param("id") Integer id);

    void forceDeleteProduct(@Param("id") Integer id);

    int updateStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    // ========== 商品中心管理模块新增方法 ==========

    /**
     * 根据商家ID和商品ID查询商品（带数据隔离校验）
     */
    Product selectByIdAndMerchant(@Param("id") Integer id, @Param("merchantId") Integer merchantId);

    /**
     * 更新商品价格
     */
    int updatePrice(@Param("id") Integer id, @Param("merchantId") Integer merchantId,
                    @Param("price") BigDecimal price, @Param("originalPrice") BigDecimal originalPrice,
                    @Param("costPrice") BigDecimal costPrice);

    /**
     * 手动调整库存
     */
    int adjustStock(@Param("id") Integer id, @Param("merchantId") Integer merchantId, @Param("quantity") Integer quantity);

    /**
     * 检查商品是否有交易记录（订单/售后）
     */
    int checkProductHasTransaction(@Param("productId") Integer productId);

    /**
     * 批量更新商品状态
     */
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("merchantId") Integer merchantId, @Param("status") Integer status);

    /**
     * 获取商品数量（按状态）
     */
    int countByMerchantIdAndStatus(@Param("merchantId") Integer merchantId, @Param("status") Integer status);

    /**
     * 锁定库存（下单时调用）
     */
    int lockStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 扣减库存（付款时调用）
     */
    int deductStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 回滚库存（取消订单/退款时调用）
     */
    int rollbackStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 获取商品库存
     */
    int getProductStock(@Param("id") Integer id);

    /**
     * 根据商家ID获取所有商品（不带分页和筛选）
     */
    List<Product> selectAllByMerchantId(@Param("merchantId") Integer merchantId);
}
