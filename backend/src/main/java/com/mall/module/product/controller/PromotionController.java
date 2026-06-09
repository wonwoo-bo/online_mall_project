package com.mall.module.product.controller;

import com.mall.module.product.entity.Promotion;
import com.mall.module.product.entity.PromotionProduct;
import com.mall.module.product.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addPromotion(@RequestBody Promotion promotion) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Promotion saved = promotionService.addPromotion(merchantId, promotion);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", saved);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePromotion(@PathVariable Integer id, @RequestBody Promotion promotion) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Promotion updated = promotionService.updatePromotion(merchantId, id, promotion);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePromotion(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean deleted = promotionService.deletePromotion(merchantId, id);
            result.put("code", 200);
            result.put("message", deleted ? "删除成功" : "删除失败");
            result.put("data", deleted);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPromotionById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            Promotion promotion = promotionService.getPromotionById(merchantId, id);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", promotion);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPromotionList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            List<Promotion> list = promotionService.getPromotionList(merchantId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActivePromotions() {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            List<Promotion> list = promotionService.getActivePromotions(merchantId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePromotionStatus(@PathVariable Integer id, @RequestParam Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean updated = promotionService.updatePromotionStatus(merchantId, id, status);
            result.put("code", 200);
            result.put("message", updated ? "状态更新成功" : "状态更新失败");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishPromotion(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer merchantId = 1;
            boolean updated = promotionService.updatePromotionStatus(merchantId, id, 1);
            result.put("code", 200);
            result.put("message", updated ? "发布成功" : "发布失败");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Map<String, Object>> addProductToPromotion(@PathVariable Integer id, @RequestBody Map<String, Integer> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromotionProduct promotionProduct = new PromotionProduct();
            promotionProduct.setPromotionId(id);
            promotionProduct.setProductId(params.get("productId"));
            PromotionProduct saved = promotionService.addPromotionProduct(promotionProduct);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", saved);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<Map<String, Object>> removeProductFromPromotion(@PathVariable Integer id, @PathVariable Integer productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromotionProduct> products = promotionService.getPromotionProductList(id);
            for (PromotionProduct p : products) {
                if (p.getProductId().equals(productId)) {
                    promotionService.deletePromotionProduct(p.getId());
                    break;
                }
            }
            result.put("code", 200);
            result.put("message", "删除成功");
            result.put("data", true);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Map<String, Object>> getPromotionProducts(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromotionProduct> list = promotionService.getPromotionProductList(id);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/product")
    public ResponseEntity<Map<String, Object>> addPromotionProduct(@RequestBody PromotionProduct promotionProduct) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromotionProduct saved = promotionService.addPromotionProduct(promotionProduct);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", saved);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> updatePromotionProduct(@PathVariable Integer id, @RequestBody PromotionProduct promotionProduct) {
        Map<String, Object> result = new HashMap<>();
        try {
            PromotionProduct updated = promotionService.updatePromotionProduct(id, promotionProduct);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", updated);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> deletePromotionProduct(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = promotionService.deletePromotionProduct(id);
            result.put("code", 200);
            result.put("message", deleted ? "删除成功" : "删除失败");
            result.put("data", deleted);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/{promotionId}")
    public ResponseEntity<Map<String, Object>> getPromotionProductList(@PathVariable Integer promotionId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PromotionProduct> list = promotionService.getPromotionProductList(promotionId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
