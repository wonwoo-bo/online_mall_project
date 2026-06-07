-- ============================================
-- 清理测试数据脚本
-- 删除硬编码插入的测试数据，保留真实业务数据
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `online_mall`;

-- ============================================
-- 一、删除旧测试订单及其关联数据（2024年1月的测试数据）
-- ============================================

-- 1. 删除订单操作日志（关联旧订单）
DELETE FROM `order_operation_log` 
WHERE `order_no` LIKE 'DD2024%';

-- 2. 删除订单标签（关联旧订单）
DELETE FROM `order_tag` 
WHERE `order_id` IN (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%');

-- 3. 删除订单物流信息（关联旧订单）
DELETE FROM `order_item_ship` 
WHERE `order_id` IN (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%');

-- 4. 删除物流轨迹（关联旧订单）
DELETE FROM `tracking_log` 
WHERE `order_id` IN (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%');

-- 5. 删除评价数据（关联旧订单）
DELETE FROM `review_appeal` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review_explanation` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review_append_reply` 
WHERE `append_id` IN (SELECT id FROM `review_append` WHERE `review_id` IN 
    (SELECT id FROM `review` WHERE `order_item_id` IN 
        (SELECT id FROM `order_item` WHERE `order_id` IN 
            (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%'))));

DELETE FROM `review_append` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review_reply` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review_image` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review_top` 
WHERE `review_id` IN (SELECT id FROM `review` WHERE `order_item_id` IN 
    (SELECT id FROM `order_item` WHERE `order_id` IN 
        (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%')));

DELETE FROM `review` 
WHERE `order_item_id` IN (SELECT id FROM `order_item` WHERE `order_id` IN 
    (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%'));

-- 6. 删除售后退款数据（关联旧订单）
DELETE FROM `refund_operation_log` 
WHERE `refund_id` IN (SELECT id FROM `return_request` WHERE `order_id` IN 
    (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%'));

DELETE FROM `dispute_application` 
WHERE `return_id` IN (SELECT id FROM `return_request` WHERE `order_id` IN 
    (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%'));

DELETE FROM `return_request` 
WHERE `order_id` IN (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%');

-- 7. 删除订单明细（关联旧订单）
DELETE FROM `order_item` 
WHERE `order_id` IN (SELECT id FROM `order` WHERE `order_no` LIKE 'DD2024%');

-- 8. 删除订单（2024年的测试订单）
DELETE FROM `order` 
WHERE `order_no` LIKE 'DD2024%';

-- ============================================
-- 二、删除旧测试用户数据（测试账号）
-- ============================================

-- 删除测试用户的收藏和浏览记录
DELETE FROM `favorite` WHERE `user_id` IN (1, 2, 3);
DELETE FROM `browse_history` WHERE `user_id` IN (1, 2, 3);

-- 删除测试用户的优惠券领取记录
DELETE FROM `user_coupon` WHERE `user_id` IN (1, 2, 3);

-- 删除测试用户
DELETE FROM `user` WHERE `username` IN ('user1', 'user2', 'user3');

-- ============================================
-- 三、删除商家回收站测试数据
-- ============================================

-- 删除回收站商品（status=-1）
DELETE FROM `product` WHERE `status` = -1;

-- 删除回收站品牌（status=-1）
DELETE FROM `brand` WHERE `status` = -1;

-- 删除回收站分类（status=-1）
DELETE FROM `merchant_category` WHERE `status` = -1;

-- 删除回收站规格类型（status=-1）
DELETE FROM `spec_type` WHERE `status` = -1;

-- 删除关联的回收站规格值（属于已删除的规格类型）
DELETE FROM `spec_value` WHERE `type_id` IN (
    SELECT id FROM `spec_type` WHERE `status` = -1
);

-- ============================================
-- 四、重置自增ID（可选）
-- ============================================

-- ALTER TABLE `order` AUTO_INCREMENT = 1;
-- ALTER TABLE `order_item` AUTO_INCREMENT = 1;
-- ALTER TABLE `review` AUTO_INCREMENT = 1;
-- ALTER TABLE `return_request` AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '清理完成！' AS result;
