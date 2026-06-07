package com.mall.module.product.service.impl;

import com.mall.common.PageResult;
import com.mall.module.product.entity.*;
import com.mall.module.product.mapper.*;
import com.mall.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductOperationLogMapper productOperationLogMapper;

    @Autowired
    private StockOperationLogMapper stockOperationLogMapper;

    @Override
    public PageResult<Product> getProductList(Integer categoryId, String keyword,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              String sort, int page, int size) {
        int offset = (page - 1) * size;
        List<Product> list = productMapper.selectProductList(categoryId, keyword, minPrice, maxPrice, sort, offset, size);
        int total = productMapper.countProductList(categoryId, keyword, minPrice, maxPrice);
        return new PageResult<>(total, list, page, size);
    }

    @Autowired
    private SpecTypeMapper specTypeMapper;

    @Autowired
    private SpecValueMapper specValueMapper;

    @Override
    public Map<String, Object> getProductDetail(Integer id) {
        Map<String, Object> result = new HashMap<>();

        // 查询商品基本信息
        Product product = productMapper.selectById(id);
        result.put("product", product);

        // 查询商品图片列表
        List<ProductImage> images = productImageMapper.selectByProductId(id);
        result.put("images", images);

        // 查询SKU列表
        List<ProductSku> skus = productSkuMapper.selectByProductId(id);
        
        // 获取商品的规格信息（用于前端分组选择）
        List<Map<String, Object>> specGroups = new ArrayList<>();
        if (skus != null && !skus.isEmpty() && product != null) {
            // 获取商家的规格类型和规格值
            Map<Integer, String> typeIdToName = new HashMap<>();
            Map<Integer, String> valueIdToName = new HashMap<>();
            Map<Integer, List<Map<String, Object>>> typeValuesMap = new HashMap<>();
            
            try {
                // 获取规格类型
                List<SpecType> specTypes = specTypeMapper.selectByMerchantId(product.getMerchantId());
                if (specTypes != null) {
                    for (SpecType type : specTypes) {
                        typeIdToName.put(type.getId(), type.getName());
                        typeValuesMap.put(type.getId(), new ArrayList<>());
                    }
                }
                
                // 获取所有规格值
                List<SpecValue> allValues = specValueMapper.selectByMerchantId(product.getMerchantId());
                if (allValues != null) {
                    for (SpecValue value : allValues) {
                        valueIdToName.put(value.getId(), value.getValue());
                        List<Map<String, Object>> typeValues = typeValuesMap.get(value.getTypeId());
                        if (typeValues != null) {
                            Map<String, Object> valueMap = new HashMap<>();
                            valueMap.put("id", value.getId());
                            valueMap.put("name", value.getValue());
                            typeValues.add(valueMap);
                        }
                    }
                }
            } catch (Exception e) {
                // 如果获取规格数据失败，继续使用原始值
            }
            
            // 构建规格分组数据
            for (Map.Entry<Integer, List<Map<String, Object>>> entry : typeValuesMap.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    Map<String, Object> group = new HashMap<>();
                    group.put("id", entry.getKey());
                    group.put("name", typeIdToName.get(entry.getKey()));
                    group.put("values", entry.getValue());
                    specGroups.add(group);
                }
            }
            
            // 转换所有SKU规格：specsJson存储原始JSON用于匹配，specs存储可读文本
            for (ProductSku sku : skus) {
                String specs = sku.getSpecs();
                if (specs != null && !specs.isEmpty()) {
                    try {
                        if (specs.startsWith("{") && specs.endsWith("}")) {
                            // 保存原始JSON到specsJson字段
                            sku.setSpecsJson(specs);
                            // 转换为可读文本到specs字段
                            String readableSpecs = parseSpecsSimple(specs, typeIdToName, valueIdToName);
                            sku.setSpecs(readableSpecs);
                        } else if (sku.getSpecsJson() == null) {
                            // 如果specs不是JSON格式，检查specsJson
                            if (sku.getSpecsJson() != null) {
                                String readableSpecs = parseSpecsSimple(sku.getSpecsJson(), typeIdToName, valueIdToName);
                                sku.setSpecs(readableSpecs);
                            }
                        }
                    } catch (Exception e) {
                        // 保持原样
                    }
                }
            }
        }
        
        result.put("skus", skus);
        result.put("specGroups", specGroups);

        // 查询评价统计（优化：一次查询获取所有数据）
        Map<String, Object> reviewStats = reviewMapper.selectReviewStats(id);
        if (reviewStats == null) {
            reviewStats = new HashMap<>();
            reviewStats.put("total", 0);
            reviewStats.put("avgRating", 0.0);
            reviewStats.put("fiveStar", 0);
            reviewStats.put("fourStar", 0);
            reviewStats.put("threeStar", 0);
            reviewStats.put("twoStar", 0);
            reviewStats.put("oneStar", 0);
            reviewStats.put("hasImageCount", 0);
        }
        result.put("reviewStats", reviewStats);

        // 查询商家信息
        if (product != null && product.getMerchantId() != null) {
            Merchant merchant = merchantMapper.selectById(product.getMerchantId());
            result.put("merchant", merchant);
        }

        return result;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        int offset = 0;
        int limit = 20;
        return productMapper.selectProductList(null, keyword, null, null, null, offset, limit);
    }

    @Override
    public List<Product> getRecommended(int limit) {
        return productMapper.selectRecommended(limit);
    }

    @Override
    public void incrementViews(Integer id) {
        productMapper.incrementViews(id);
    }

    @Override
    public int countByMerchant(Integer merchantId) {
        return productMapper.countByMerchantId(merchantId);
    }

    @Override
    public List<Product> getProductsByMerchant(Integer merchantId, Integer page, Integer pageSize, String keyword) {
        int offset = (page - 1) * pageSize;
        return productMapper.selectByMerchantId(merchantId, keyword, null, null, null, null, null, offset, pageSize);
    }

    @Override
    public int countProductsByMerchant(Integer merchantId, String keyword) {
        return productMapper.countByMerchantIdWithKeyword(merchantId, keyword, null, null, null);
    }

    @Override
    public void createProduct(Product product) {
        productMapper.insert(product);
    }

    public void createProductWithImages(Product product, String coverImg, List<String> mainImages, List<String> detailImages) {
        product.setCoverImg(coverImg);
        productMapper.insert(product);
        saveProductImages(product.getId(), coverImg, mainImages, detailImages);
    }

    public void createProductWithImagesAndSkus(Product product, String coverImg, List<String> mainImages, 
                                               List<String> detailImages, List<ProductSku> skus) {
        product.setCoverImg(coverImg);
        productMapper.insert(product);
        
        saveProductImages(product.getId(), coverImg, mainImages, detailImages);
        
        if (skus != null && !skus.isEmpty()) {
            for (ProductSku sku : skus) {
                sku.setProductId(product.getId());
                sku.setStatus(1);
                sku.setSales(0);
                productSkuMapper.insert(sku);
            }
        }
    }

    private void saveProductImages(Integer productId, String coverImg, List<String> mainImages, List<String> detailImages) {
        int sortOrder = 1;

        if (coverImg != null && !coverImg.isEmpty()) {
            ProductImage coverImage = new ProductImage();
            coverImage.setProductId(productId);
            coverImage.setImageUrl(coverImg);
            coverImage.setIsMain(1);
            coverImage.setSortOrder(sortOrder++);
            productImageMapper.insert(coverImage);
        }

        if (mainImages != null) {
            for (String imgUrl : mainImages) {
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(productId);
                    image.setImageUrl(imgUrl);
                    image.setIsMain(0);
                    image.setSortOrder(sortOrder++);
                    productImageMapper.insert(image);
                }
            }
        }

        if (detailImages != null) {
            for (String imgUrl : detailImages) {
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(productId);
                    image.setImageUrl(imgUrl);
                    image.setIsMain(0);
                    image.setSortOrder(sortOrder++);
                    productImageMapper.insert(image);
                }
            }
        }
    }

    public void updateProductWithImages(Product product, String coverImg, List<String> mainImages, List<String> detailImages) {
        product.setCoverImg(coverImg);
        productMapper.update(product);

        productImageMapper.deleteByProductId(product.getId());

        int sortOrder = 1;

        if (coverImg != null && !coverImg.isEmpty()) {
            ProductImage coverImage = new ProductImage();
            coverImage.setProductId(product.getId());
            coverImage.setImageUrl(coverImg);
            coverImage.setIsMain(1);
            coverImage.setSortOrder(sortOrder++);
            productImageMapper.insert(coverImage);
        }

        if (mainImages != null) {
            for (String imgUrl : mainImages) {
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(product.getId());
                    image.setImageUrl(imgUrl);
                    image.setIsMain(0);
                    image.setSortOrder(sortOrder++);
                    productImageMapper.insert(image);
                }
            }
        }

        if (detailImages != null) {
            for (String imgUrl : detailImages) {
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    ProductImage image = new ProductImage();
                    image.setProductId(product.getId());
                    image.setImageUrl(imgUrl);
                    image.setIsMain(0);
                    image.setSortOrder(sortOrder++);
                    productImageMapper.insert(image);
                }
            }
        }
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.update(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productMapper.moveToRecycle(id);
    }

    @Override
    public void updateProductStatus(Integer id, Integer status) {
        productMapper.updateStatus(id, status);
    }

    @Override
    public List<Map<String, Object>> getRecycleList(Integer merchantId) {
        return productMapper.selectRecycleList(merchantId);
    }

    @Override
    public void restoreProduct(Integer id) {
        productMapper.restoreProduct(id);
    }

    @Override
    public void forceDeleteProduct(Integer id) {
        productMapper.forceDeleteProduct(id);
    }

    @Override
    public Product getProductById(Integer id) {
        return productMapper.selectById(id);
    }

    // ========== 商品中心管理模块新增方法实现 ==========

    @Override
    public Product getProductByIdAndMerchant(Integer id, Integer merchantId) {
        return productMapper.selectByIdAndMerchant(id, merchantId);
    }

    @Override
    public PageResult<Product> getMerchantProductList(Integer merchantId, Integer page, Integer pageSize,
                                                       String keyword, Integer categoryId, Integer brandId,
                                                       Integer status, BigDecimal minPrice, BigDecimal maxPrice) {
        int offset = (page - 1) * pageSize;
        List<Product> list = productMapper.selectByMerchantId(merchantId, keyword, categoryId, brandId, status, minPrice, maxPrice, offset, pageSize);
        int total = productMapper.countByMerchantIdWithKeyword(merchantId, keyword, categoryId, brandId, status);
        return new PageResult<>(total, list, page, pageSize);
    }

    @Override
    public void updateProductPrice(Integer id, Integer merchantId, BigDecimal price,
                                  BigDecimal originalPrice, BigDecimal costPrice) {
        // 校验价格参数
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("售价不能为负数");
        }
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("原价不能为负数");
        }
        if (costPrice != null && costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("成本价不能为负数");
        }

        // 价格校验：如果设置了价格，必须大于0
        if (price != null && price.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("售价不能为0");
        }

        // 校验商品归属
        Product product = productMapper.selectByIdAndMerchant(id, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        int rows = productMapper.updatePrice(id, merchantId, price, originalPrice, costPrice);
        if (rows == 0) {
            throw new RuntimeException("价格更新失败，商品不存在或无权操作");
        }

        // 记录操作日志
        ProductOperationLog log = new ProductOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("PRICE");
        log.setOperationDesc("更新商品价格");
        log.setBeforeValue("price=" + product.getPrice() + ",originalPrice=" + product.getOriginalPrice() + ",costPrice=" + product.getCostPrice());
        log.setAfterValue("price=" + price + ",originalPrice=" + originalPrice + ",costPrice=" + costPrice);
        productOperationLogMapper.insert(log);
    }

    @Override
    public void adjustStock(Integer id, Integer merchantId, Integer quantity,
                           Integer operatorId, String operatorName, String reason) {
        // 库存调整校验
        if (quantity == null) {
            throw new RuntimeException("库存调整数量不能为空");
        }

        Product product = productMapper.selectByIdAndMerchant(id, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        int beforeStock = product.getStock();
        int afterStock = beforeStock + quantity;

        // 库存不能为负数
        if (afterStock < 0) {
            throw new RuntimeException("库存不足，当前库存：" + beforeStock + "，本次调整：" + quantity);
        }

        int rows = productMapper.adjustStock(id, merchantId, quantity);
        if (rows == 0) {
            throw new RuntimeException("库存调整失败，商品不存在或无权操作");
        }

        // 记录库存操作日志
        StockOperationLog log = new StockOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("MANUAL");
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationReason(reason);
        stockOperationLogMapper.insert(log);

        // 记录商品操作日志
        ProductOperationLog productLog = new ProductOperationLog();
        productLog.setMerchantId(merchantId);
        productLog.setProductId(id);
        productLog.setOperatorId(operatorId);
        productLog.setOperatorName(operatorName);
        productLog.setOperationType("STOCK");
        productLog.setOperationDesc("手动调整库存：" + quantity);
        productLog.setBeforeValue(String.valueOf(beforeStock));
        productLog.setAfterValue(String.valueOf(afterStock));
        productOperationLogMapper.insert(productLog);
    }

    @Override
    public boolean checkProductHasTransaction(Integer productId) {
        return productMapper.checkProductHasTransaction(productId) > 0;
    }

    @Override
    public void batchUpdateStatus(List<Integer> ids, Integer merchantId, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        productMapper.batchUpdateStatus(ids, merchantId, status);
    }

    @Override
    public int countByMerchantAndStatus(Integer merchantId, Integer status) {
        return productMapper.countByMerchantIdAndStatus(merchantId, status);
    }

    @Override
    public void updateProductStatusWithAuth(Integer id, Integer merchantId, Integer status) {
        Product product = productMapper.selectByIdAndMerchant(id, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        // 状态幂等检查
        if (product.getStatus().equals(status)) {
            // 状态相同，不做操作但也不报错
            return;
        }

        productMapper.updateStatus(id, status);

        // 记录操作日志
        ProductOperationLog log = new ProductOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("STATUS");
        log.setOperationDesc("更新商品状态：" + (status == 1 ? "上架" : "下架"));
        log.setBeforeValue(String.valueOf(product.getStatus()));
        log.setAfterValue(String.valueOf(status));
        productOperationLogMapper.insert(log);
    }

    @Override
    public void deleteProductWithAuth(Integer id, Integer merchantId) {
        Product product = productMapper.selectByIdAndMerchant(id, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        productMapper.moveToRecycle(id);

        // 记录操作日志
        ProductOperationLog log = new ProductOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("DELETE");
        log.setOperationDesc("删除商品到回收站");
        log.setBeforeValue("");
        log.setAfterValue("");
        productOperationLogMapper.insert(log);
    }

    @Override
    public void restoreProductWithAuth(Integer id, Integer merchantId) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        productMapper.restoreProduct(id);

        // 记录操作日志
        ProductOperationLog log = new ProductOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("RECOVER");
        log.setOperationDesc("从回收站恢复商品");
        log.setBeforeValue("");
        log.setAfterValue("");
        productOperationLogMapper.insert(log);
    }

    @Override
    public void forceDeleteProductWithAuth(Integer id, Integer merchantId) {
        Product product = productMapper.selectById(id);
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        // 检查是否有交易记录
        if (checkProductHasTransaction(id)) {
            throw new RuntimeException("该商品有交易记录（订单/售后），禁止彻底删除");
        }

        productMapper.forceDeleteProduct(id);

        // 记录操作日志
        ProductOperationLog log = new ProductOperationLog();
        log.setMerchantId(merchantId);
        log.setProductId(id);
        log.setOperationType("FORCE_DELETE");
        log.setOperationDesc("彻底删除商品");
        log.setBeforeValue("");
        log.setAfterValue("");
        productOperationLogMapper.insert(log);
    }

    @Override
    public List<Map<String, Object>> getProductOperationLogs(Integer productId, Integer merchantId) {
        // 校验商品归属
        Product product = productMapper.selectByIdAndMerchant(productId, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        List<ProductOperationLog> logs = productOperationLogMapper.selectByProductId(productId, merchantId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ProductOperationLog log : logs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("operationType", log.getOperationType());
            map.put("operationDesc", log.getOperationDesc());
            map.put("beforeValue", log.getBeforeValue());
            map.put("afterValue", log.getAfterValue());
            map.put("operatorName", log.getOperatorName());
            map.put("createTime", log.getCreateTime());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getStockOperationLogs(Integer productId, Integer merchantId) {
        // 校验商品归属
        Product product = productMapper.selectByIdAndMerchant(productId, merchantId);
        if (product == null) {
            throw new RuntimeException("商品不存在或无权操作");
        }

        List<StockOperationLog> logs = stockOperationLogMapper.selectByProductId(productId, merchantId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (StockOperationLog log : logs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("operationType", log.getOperationType());
            map.put("quantity", log.getQuantity());
            map.put("beforeStock", log.getBeforeStock());
            map.put("afterStock", log.getAfterStock());
            map.put("operatorName", log.getOperatorName());
            map.put("operationReason", log.getOperationReason());
            map.put("createTime", log.getCreateTime());
            result.add(map);
        }
        return result;
    }

    // ========== 订单模块库存管理方法实现 ==========

    @Override
    public void lockStock(Integer productId, Integer quantity, Integer orderId,
                         Integer operatorId, String operatorName) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new RuntimeException("参数错误：商品ID和数量不能为空，且数量必须大于0");
        }

        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        int beforeStock = product.getStock();

        // 检查库存是否充足
        if (beforeStock < quantity) {
            throw new RuntimeException("库存不足，当前库存：" + beforeStock + "，需要：" + quantity);
        }

        // 扣减库存（锁定）
        int rows = productMapper.lockStock(productId, quantity);
        if (rows == 0) {
            throw new RuntimeException("库存锁定失败，可能商品已下架或库存不足");
        }

        // 记录库存操作日志
        StockOperationLog log = new StockOperationLog();
        log.setMerchantId(product.getMerchantId());
        log.setProductId(productId);
        log.setOrderId(orderId);
        log.setOperationType("LOCK");
        log.setQuantity(-quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(beforeStock - quantity);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationReason("下单锁定库存");
        stockOperationLogMapper.insert(log);
    }

    @Override
    public void deductStock(Integer productId, Integer quantity, Integer orderId,
                           Integer operatorId, String operatorName) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new RuntimeException("参数错误：商品ID和数量不能为空，且数量必须大于0");
        }

        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        int beforeStock = product.getStock();

        // 扣减库存（付款完成，实际扣减）
        int rows = productMapper.deductStock(productId, quantity);
        if (rows == 0) {
            throw new RuntimeException("库存扣减失败，商品可能已下架或库存不足");
        }

        // 更新销量
        productMapper.incrementSales(productId, quantity);

        // 记录库存操作日志
        StockOperationLog log = new StockOperationLog();
        log.setMerchantId(product.getMerchantId());
        log.setProductId(productId);
        log.setOrderId(orderId);
        log.setOperationType("DEDUCT");
        log.setQuantity(-quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(beforeStock - quantity);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationReason("付款成功扣减库存");
        stockOperationLogMapper.insert(log);
    }

    @Override
    public void unlockStock(Integer productId, Integer quantity, Integer orderId,
                           Integer operatorId, String operatorName) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new RuntimeException("参数错误：商品ID和数量不能为空，且数量必须大于0");
        }

        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        int beforeStock = product.getStock();

        // 回滚库存（取消订单，释放锁定的库存）
        int rows = productMapper.rollbackStock(productId, quantity);
        if (rows == 0) {
            throw new RuntimeException("库存回滚失败，商品可能不存在");
        }

        // 记录库存操作日志
        StockOperationLog log = new StockOperationLog();
        log.setMerchantId(product.getMerchantId());
        log.setProductId(productId);
        log.setOrderId(orderId);
        log.setOperationType("UNLOCK");
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(beforeStock + quantity);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationReason("取消订单回滚库存");
        stockOperationLogMapper.insert(log);
    }

    @Override
    public void refundRestoreStock(Integer productId, Integer quantity, Integer orderId,
                                 Integer operatorId, String operatorName) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new RuntimeException("参数错误：商品ID和数量不能为空，且数量必须大于0");
        }

        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        int beforeStock = product.getStock();

        // 回滚库存（退款退货，返还库存）
        int rows = productMapper.rollbackStock(productId, quantity);
        if (rows == 0) {
            throw new RuntimeException("库存回滚失败，商品可能不存在");
        }

        // 记录库存操作日志
        StockOperationLog log = new StockOperationLog();
        log.setMerchantId(product.getMerchantId());
        log.setProductId(productId);
        log.setOrderId(orderId);
        log.setOperationType("RESTORE");
        log.setQuantity(quantity);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(beforeStock + quantity);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationReason("退款退货回滚库存");
        stockOperationLogMapper.insert(log);
    }

    @Override
    public List<Map<String, Object>> getProductConversion(Integer merchantId) {
        List<Product> products = productMapper.selectAllByMerchantId(merchantId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("productName", product.getName());
            item.put("visits", product.getViews() != null ? product.getViews() : 0);
            item.put("browseCount", product.getViews() != null ? product.getViews() * 2 : 0);
            item.put("addCartCount", 0);
            item.put("orderCount", product.getSales() != null ? product.getSales() : 0);
            item.put("conversionRate", product.getViews() != null && product.getViews() > 0 
                    ? ((double)(product.getSales() != null ? product.getSales() : 0) / product.getViews()) * 100 : 0);
            item.put("cartRate", 0);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getOverstockWarning(Integer merchantId) {
        List<Product> products = productMapper.selectAllByMerchantId(merchantId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Product product : products) {
            int stock = product.getStock() != null ? product.getStock() : 0;
            int sales = product.getSales() != null ? product.getSales() : 0;
            int avgSales = Math.max(1, sales / 30); // 月均销量
            int warningDays = avgSales > 0 ? stock / avgSales : stock;
            
            int warningLevel = 3;
            String suggestion = "正常库存";
            if (warningDays > 60) {
                warningLevel = 1;
                suggestion = "建议清仓促销";
            } else if (warningDays > 30) {
                warningLevel = 2;
                suggestion = "建议加大推广";
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("productName", product.getName());
            item.put("category", "");
            item.put("brand", "");
            item.put("stock", stock);
            item.put("avgSales", avgSales);
            item.put("warningLevel", warningLevel);
            item.put("suggestion", suggestion);
            result.add(item);
        }
        
        return result;
    }

    /**
     * 简单的规格解析方式，处理常见的JSON格式
     */
    private String parseSpecsSimple(String specsJson, Map<Integer, String> typeIdToName, Map<Integer, String> valueIdToName) {
        try {
            // 简化处理：直接去掉所有引号和大括号
            specsJson = specsJson.replace("{", "").replace("}", "").replace("\"", "");
            String[] pairs = specsJson.split(",");
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i].trim();
                String[] kv = pair.split(":");
                if (kv.length == 2) {
                    try {
                        Integer typeId = Integer.parseInt(kv[0].trim());
                        Integer valueId = Integer.parseInt(kv[1].trim());
                        
                        String typeName = typeIdToName.get(typeId);
                        String valueName = valueIdToName.get(valueId);
                        
                        if (typeName == null) {
                            typeName = "规格" + typeId;
                        }
                        if (valueName == null) {
                            valueName = "值" + valueId;
                        }
                        
                        if (i > 0) {
                            result.append(", ");
                        }
                        result.append(typeName).append(":").append(valueName);
                    } catch (NumberFormatException e) {
                        // 如果解析失败，使用原始值
                        if (i > 0) {
                            result.append(", ");
                        }
                        result.append(pair);
                    }
                }
            }
            
            return result.length() > 0 ? result.toString() : specsJson;
        } catch (Exception e) {
            return specsJson;
        }
    }
}
