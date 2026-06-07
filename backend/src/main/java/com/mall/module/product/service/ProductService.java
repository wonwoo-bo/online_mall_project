package com.mall.module.product.service;

import com.mall.common.PageResult;
import com.mall.module.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {

    /**
     * 分页查询商品列表
     */
    PageResult<Product> getProductList(Integer categoryId, String keyword,
                                       BigDecimal minPrice, BigDecimal maxPrice,
                                       String sort, int page, int size);

    /**
     * 获取商品详情（含图片、SKU、评价统计、商家信息）
     */
    Map<String, Object> getProductDetail(Integer id);

    /**
     * 搜索商品
     */
    List<Product> searchProducts(String keyword);

    /**
     * 获取推荐商品
     */
    List<Product> getRecommended(int limit);

    /**
     * 增加浏览量
     */
    void incrementViews(Integer id);

    /**
     * 统计商家商品数量
     */
    int countByMerchant(Integer merchantId);

    /**
     * 获取商家商品列表
     */
    List<Product> getProductsByMerchant(Integer merchantId, Integer page, Integer pageSize, String keyword);

    /**
     * 统计商家商品总数
     */
    int countProductsByMerchant(Integer merchantId, String keyword);

    /**
     * 创建商品
     */
    void createProduct(Product product);

    /**
     * 创建商品（含图片）
     */
    void createProductWithImages(Product product, String coverImg, List<String> mainImages, List<String> detailImages);

    /**
     * 创建商品（含图片和SKU）
     */
    void createProductWithImagesAndSkus(Product product, String coverImg, List<String> mainImages, 
                                        List<String> detailImages, List<com.mall.module.product.entity.ProductSku> skus);

    /**
     * 更新商品
     */
    void updateProduct(Product product);

    /**
     * 更新商品（含图片）
     */
    void updateProductWithImages(Product product, String coverImg, List<String> mainImages, List<String> detailImages);

    /**
     * 删除商品
     */
    void deleteProduct(Integer id);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Integer id, Integer status);

    /**
     * 获取回收站列表
     */
    List<Map<String, Object>> getRecycleList(Integer merchantId);

    /**
     * 恢复商品
     */
    void restoreProduct(Integer id);

    /**
     * 彻底删除商品
     */
    void forceDeleteProduct(Integer id);

    /**
     * 根据ID获取商品
     */
    Product getProductById(Integer id);

    // ========== 商品中心管理模块新增方法 ==========

    /**
     * 根据商家ID和商品ID获取商品（带数据隔离校验）
     */
    Product getProductByIdAndMerchant(Integer id, Integer merchantId);

    /**
     * 商家商品筛选查询（支持多条件筛选）
     */
    PageResult<Product> getMerchantProductList(Integer merchantId, Integer page, Integer pageSize,
                                              String keyword, Integer categoryId, Integer brandId,
                                              Integer status, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 更新商品价格
     */
    void updateProductPrice(Integer id, Integer merchantId, BigDecimal price,
                            BigDecimal originalPrice, BigDecimal costPrice);

    /**
     * 手动调整库存
     */
    void adjustStock(Integer id, Integer merchantId, Integer quantity,
                     Integer operatorId, String operatorName, String reason);

    /**
     * 检查商品是否有交易记录
     */
    boolean checkProductHasTransaction(Integer productId);

    /**
     * 批量更新商品状态
     */
    void batchUpdateStatus(List<Integer> ids, Integer merchantId, Integer status);

    /**
     * 获取商品数量（按状态）
     */
    int countByMerchantAndStatus(Integer merchantId, Integer status);

    /**
     * 更新商品状态（带商家ID校验）
     */
    void updateProductStatusWithAuth(Integer id, Integer merchantId, Integer status);

    /**
     * 删除商品（带商家ID校验）
     */
    void deleteProductWithAuth(Integer id, Integer merchantId);

    /**
     * 恢复商品（带商家ID校验）
     */
    void restoreProductWithAuth(Integer id, Integer merchantId);

    /**
     * 彻底删除商品（带商家ID校验）
     */
    void forceDeleteProductWithAuth(Integer id, Integer merchantId);

    /**
     * 获取商品操作日志
     */
    List<Map<String, Object>> getProductOperationLogs(Integer productId, Integer merchantId);

    /**
     * 获取库存操作日志
     */
    List<Map<String, Object>> getStockOperationLogs(Integer productId, Integer merchantId);

    // ========== 订单模块库存管理方法 ==========

    /**
     * 下单锁库存
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void lockStock(Integer productId, Integer quantity, Integer orderId,
                   Integer operatorId, String operatorName);

    /**
     * 付款扣库存（将锁定库存转为实际扣减）
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void deductStock(Integer productId, Integer quantity, Integer orderId,
                     Integer operatorId, String operatorName);

    /**
     * 取消订单回滚库存（释放锁定的库存）
     * @param productId 商品ID
     * @param quantity 回滚数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void unlockStock(Integer productId, Integer quantity, Integer orderId,
                     Integer operatorId, String operatorName);

    /**
     * 退款退货回滚库存
     * @param productId 商品ID
     * @param quantity 回滚数量
     * @param orderId 订单ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     */
    void refundRestoreStock(Integer productId, Integer quantity, Integer orderId,
                            Integer operatorId, String operatorName);

    /**
     * 获取商品转化数据
     */
    List<Map<String, Object>> getProductConversion(Integer merchantId);

    /**
     * 获取库存预警数据
     */
    List<Map<String, Object>> getOverstockWarning(Integer merchantId);
}
