package com.mall.module.product.service.impl;

import com.mall.module.product.entity.Promotion;
import com.mall.module.product.entity.PromotionProduct;
import com.mall.module.product.mapper.PromotionMapper;
import com.mall.module.product.mapper.PromotionProductMapper;
import com.mall.module.product.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private PromotionProductMapper promotionProductMapper;

    @Override
    public Promotion addPromotion(Integer merchantId, Promotion promotion) {
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            throw new RuntimeException("活动名称不能为空");
        }

        promotion.setMerchantId(merchantId);
        if (promotion.getStatus() == null) {
            promotion.setStatus(0);
        }
        if (promotion.getIsRecommend() == null) {
            promotion.setIsRecommend(0);
        }

        promotionMapper.insert(promotion);
        return promotionMapper.selectById(promotion.getId(), merchantId);
    }

    @Override
    public Promotion updatePromotion(Integer merchantId, Integer promotionId, Promotion promotion) {
        Promotion existing = promotionMapper.selectById(promotionId, merchantId);
        if (existing == null) {
            throw new RuntimeException("促销活动不存在");
        }

        promotion.setId(promotionId);
        promotion.setMerchantId(merchantId);
        promotionMapper.updateById(promotion);
        return promotionMapper.selectById(promotionId, merchantId);
    }

    @Override
    public boolean deletePromotion(Integer merchantId, Integer promotionId) {
        Promotion promotion = promotionMapper.selectById(promotionId, merchantId);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        promotionProductMapper.deleteByPromotionId(promotionId);
        return promotionMapper.deleteById(promotionId, merchantId) > 0;
    }

    @Override
    public Promotion getPromotionById(Integer merchantId, Integer promotionId) {
        return promotionMapper.selectById(promotionId, merchantId);
    }

    @Override
    public List<Promotion> getPromotionList(Integer merchantId) {
        return promotionMapper.selectByMerchantId(merchantId);
    }

    @Override
    public List<Promotion> getActivePromotions(Integer merchantId) {
        return promotionMapper.selectActivePromotions(merchantId);
    }

    @Override
    public boolean updatePromotionStatus(Integer merchantId, Integer promotionId, Integer status) {
        Promotion promotion = promotionMapper.selectById(promotionId, merchantId);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }
        return promotionMapper.updateStatus(promotionId, merchantId, status) > 0;
    }

    @Override
    public PromotionProduct addPromotionProduct(PromotionProduct promotionProduct) {
        if (promotionProduct.getProductId() == null) {
            throw new RuntimeException("商品ID不能为空");
        }

        if (promotionProduct.getStatus() == null) {
            promotionProduct.setStatus(1);
        }
        if (promotionProduct.getSortOrder() == null) {
            promotionProduct.setSortOrder(0);
        }

        promotionProductMapper.insert(promotionProduct);
        return promotionProductMapper.selectById(promotionProduct.getId());
    }

    @Override
    public PromotionProduct updatePromotionProduct(Integer id, PromotionProduct promotionProduct) {
        PromotionProduct existing = promotionProductMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("活动商品不存在");
        }

        promotionProduct.setId(id);
        promotionProductMapper.updateById(promotionProduct);
        return promotionProductMapper.selectById(id);
    }

    @Override
    public boolean deletePromotionProduct(Integer id) {
        PromotionProduct existing = promotionProductMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("活动商品不存在");
        }
        return promotionProductMapper.deleteById(id) > 0;
    }

    @Override
    public PromotionProduct getPromotionProductById(Integer id) {
        return promotionProductMapper.selectById(id);
    }

    @Override
    public List<PromotionProduct> getPromotionProductList(Integer promotionId) {
        return promotionProductMapper.selectByPromotionId(promotionId);
    }
}