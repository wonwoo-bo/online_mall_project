package com.mall.module.product.controller;

import com.mall.common.PageResult;
import com.mall.common.Result;
import com.mall.module.product.entity.*;
import com.mall.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/products")
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表（支持多条件筛选）
     * GET /api/merchant/products
     */
    @GetMapping
    public Result<Map<String, Object>> getProductList(
            @RequestAttribute("userId") Integer merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        PageResult<Product> pageResult = productService.getMerchantProductList(
                merchantId, page, pageSize, keyword, categoryId, brandId, status, minPrice, maxPrice);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getList());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getPageNum());
        result.put("pageSize", pageResult.getPageSize());

        return Result.success(result);
    }

    /**
     * 获取商品详情
     * GET /api/merchant/products/{id}
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getProductDetail(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        // 校验商品归属
        Product product = productService.getProductByIdAndMerchant(id, merchantId);
        if (product == null) {
            return Result.error("商品不存在或无权操作");
        }
        Map<String, Object> detail = productService.getProductDetail(id);
        return Result.success(detail);
    }

    /**
     * 创建商品
     * POST /api/merchant/products
     */
    @PostMapping
    public Result<Product> createProduct(
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody ProductRequest request) {
        // 前端预校验已在API层处理，后端二次校验
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return Result.error("商品名称不能为空");
        }
        if (request.getPrice() == null) {
            return Result.error("商品价格不能为空");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("商品价格必须大于0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            return Result.error("库存不能为负数");
        }
        // 校验富文本描述不能为空
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return Result.error("商品详情不能为空");
        }
        // 校验主图
        if (request.getCoverImg() == null || request.getCoverImg().trim().isEmpty()) {
            return Result.error("商品主图不能为空");
        }

        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCostPrice(request.getCostPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        List<SkuRequest> skuRequests = request.getSkus();
        if (skuRequests != null && !skuRequests.isEmpty()) {
            List<ProductSku> skus = new java.util.ArrayList<>();
            for (SkuRequest skuRequest : skuRequests) {
                ProductSku sku = new ProductSku();
                sku.setSkuCode(skuRequest.getSkuCode());
                sku.setSpecs(skuRequest.getSpecs());
                sku.setPrice(skuRequest.getPrice() != null ? skuRequest.getPrice() : request.getPrice());
                sku.setStock(skuRequest.getStock() != null ? skuRequest.getStock() : request.getStock());
                skus.add(sku);
            }
            productService.createProductWithImagesAndSkus(product, request.getCoverImg(),
                    request.getMainImages(), request.getDetailImages(), skus);
        } else {
            productService.createProductWithImages(product, request.getCoverImg(),
                    request.getMainImages(), request.getDetailImages());
        }
        return Result.success("创建成功", product);
    }

    /**
     * 更新商品
     * PUT /api/merchant/products/{id}
     */
    @PutMapping("/{id}")
    public Result<Product> updateProduct(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId,
            @RequestBody ProductRequest request) {
        // 校验商品归属
        Product existProduct = productService.getProductByIdAndMerchant(id, merchantId);
        if (existProduct == null) {
            return Result.error("商品不存在或无权操作");
        }

        // 后端二次校验
        if (request.getName() != null && request.getName().trim().isEmpty()) {
            return Result.error("商品名称不能为空");
        }
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.error("售价不能为负数");
        }
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            return Result.error("售价不能为0");
        }
        if (request.getStock() != null && request.getStock() < 0) {
            return Result.error("库存不能为负数");
        }

        Product product = new Product();
        product.setId(id);
        product.setMerchantId(merchantId);
        if (request.getName() != null) product.setName(request.getName().trim());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) product.setOriginalPrice(request.getOriginalPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getBrandId() != null) product.setBrandId(request.getBrandId());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        productService.updateProductWithImages(product, request.getCoverImg(),
                request.getMainImages(), request.getDetailImages());
        return Result.success("更新成功", product);
    }

    /**
     * 删除商品（放入回收站）
     * DELETE /api/merchant/products/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        productService.deleteProductWithAuth(id, merchantId);
        return Result.success("删除成功");
    }

    /**
     * 更新商品状态（上下架）
     * PUT /api/merchant/products/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestAttribute("userId") Integer merchantId) {
        // 状态幂等校验：已在Service层处理
        productService.updateProductStatusWithAuth(id, merchantId, status);
        return Result.success(status == 1 ? "上架成功" : "下架成功");
    }

    /**
     * 批量更新商品状态
     * PUT /api/merchant/products/batch/status
     */
    @PutMapping("/batch/status")
    public Result<String> batchUpdateStatus(
            @RequestBody Map<String, Object> request,
            @RequestAttribute("userId") Integer merchantId) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        Integer status = (Integer) request.get("status");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要操作的商品");
        }
        productService.batchUpdateStatus(ids, merchantId, status);
        return Result.success("批量操作成功");
    }

    /**
     * 更新商品价格
     * PUT /api/merchant/products/{id}/price
     */
    @PutMapping("/{id}/price")
    public Result<String> updatePrice(
            @PathVariable Integer id,
            @RequestBody ProductRequest request,
            @RequestAttribute("userId") Integer merchantId) {
        // 校验价格参数
        if (request.getPrice() == null) {
            return Result.error("售价不能为空");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.error("售价不能为负数");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            return Result.error("售价不能为0");
        }
        if (request.getOriginalPrice() != null && request.getOriginalPrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.error("原价不能为负数");
        }
        if (request.getCostPrice() != null && request.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            return Result.error("成本价不能为负数");
        }

        productService.updateProductPrice(id, merchantId, request.getPrice(),
                request.getOriginalPrice(), request.getCostPrice());
        return Result.success("价格更新成功");
    }

    /**
     * 调整商品库存
     * PUT /api/merchant/products/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public Result<String> adjustStock(
            @PathVariable Integer id,
            @RequestBody StockAdjustRequest request,
            @RequestAttribute("userId") Integer merchantId) {
        Integer quantity = request.getQuantity();
        String reason = request.getReason();

        if (quantity == null) {
            return Result.error("调整数量不能为空");
        }

        if (quantity == 0) {
            return Result.error("调整数量不能为0");
        }

        productService.adjustStock(id, merchantId, quantity, merchantId, "商家", reason);
        return Result.success("库存调整成功");
    }

    /**
     * 恢复商品
     * PUT /api/merchant/products/{id}/restore
     */
    @PutMapping("/{id}/restore")
    public Result<String> restoreProduct(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        productService.restoreProductWithAuth(id, merchantId);
        return Result.success("恢复成功");
    }

    /**
     * 彻底删除商品
     * DELETE /api/merchant/products/{id}/forever
     */
    @DeleteMapping("/{id}/forever")
    public Result<String> forceDeleteProduct(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        // 检查是否有交易记录
        if (productService.checkProductHasTransaction(id)) {
            return Result.error("该商品有交易记录（订单/售后），禁止彻底删除");
        }
        productService.forceDeleteProductWithAuth(id, merchantId);
        return Result.success("彻底删除成功");
    }

    /**
     * 获取商品操作日志
     * GET /api/merchant/products/{id}/logs
     */
    @GetMapping("/{id}/logs")
    public Result<List<Map<String, Object>>> getProductLogs(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        List<Map<String, Object>> logs = productService.getProductOperationLogs(id, merchantId);
        return Result.success(logs);
    }

    /**
     * 获取库存操作日志
     * GET /api/merchant/products/{id}/stock-logs
     */
    @GetMapping("/{id}/stock-logs")
    public Result<List<Map<String, Object>>> getStockLogs(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer merchantId) {
        List<Map<String, Object>> logs = productService.getStockOperationLogs(id, merchantId);
        return Result.success(logs);
    }

    /**
     * 获取商品统计数量
     * GET /api/merchant/products/count
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getProductCount(
            @RequestAttribute("userId") Integer merchantId) {
        int totalCount = productService.countByMerchant(merchantId);
        int onShelfCount = productService.countByMerchantAndStatus(merchantId, 1);
        int offShelfCount = productService.countByMerchantAndStatus(merchantId, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("total", totalCount);
        result.put("onShelf", onShelfCount);
        result.put("offShelf", offShelfCount);
        return Result.success(result);
    }
}
