package com.mall.module.product.service;

import com.mall.module.product.entity.Promotion;
import com.mall.module.product.entity.PromotionProduct;

import java.util.List;

public interface PromotionService {
    Promotion addPromotion(Integer merchantId, Promotion promotion);
    Promotion updatePromotion(Integer merchantId, Integer promotionId, Promotion promotion);
    boolean deletePromotion(Integer merchantId, Integer promotionId);
    Promotion getPromotionById(Integer merchantId, Integer promotionId);
    List<Promotion> getPromotionList(Integer merchantId);
    List<Promotion> getActivePromotions(Integer merchantId);
    boolean updatePromotionStatus(Integer merchantId, Integer promotionId, Integer status);

    PromotionProduct addPromotionProduct(PromotionProduct promotionProduct);
    PromotionProduct updatePromotionProduct(Integer id, PromotionProduct promotionProduct);
    boolean deletePromotionProduct(Integer id);
    PromotionProduct getPromotionProductById(Integer id);
    List<PromotionProduct> getPromotionProductList(Integer promotionId);
}