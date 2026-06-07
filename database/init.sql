-- ============================================
-- 在线商城数据库完整初始化脚本
-- 数据库名称: online_mall
-- 包含: 56张表 + 外键 + 测试数据 + 2个视图 + 存储过程
-- 使用方式: 直接在MySQL中运行此脚本即可
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 一、创建并使用数据库
-- ============================================
DROP DATABASE IF EXISTS `online_mall`;
CREATE DATABASE IF NOT EXISTS `online_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `online_mall`;

-- ============================================
-- 二、创建表结构（共56张表）
-- ============================================

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `salt` VARCHAR(16) NULL COMMENT '加密盐',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `phone` VARCHAR(20) COMMENT '手机号',
  `address` VARCHAR(200) COMMENT '地址',
  `avatar` VARCHAR(200) COMMENT '头像',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户角色表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `role_code` VARCHAR(20) NOT NULL COMMENT '角色代码',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `permissions` TEXT COMMENT '权限列表',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_id` (`user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- 3. 商家表
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `shop_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `phone` VARCHAR(20) COMMENT '绑定手机号',
  `shop_desc` TEXT COMMENT '店铺描述',
  `shop_logo` VARCHAR(255) COMMENT '店铺头像',
  `avatar` VARCHAR(255) COMMENT '商家头像',
  `bank_account` VARCHAR(100) COMMENT '收款账户',
  `business_hours` VARCHAR(100) COMMENT '营业时间',
  `dsr_score` DECIMAL(2,1) DEFAULT 5.0 COMMENT 'DSR评分',
  `violation_score` INT DEFAULT 0 COMMENT '违规扣分',
  `deposit` DECIMAL(10,2) DEFAULT 0.00 COMMENT '保证金',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
  `intro` TEXT COMMENT '商家简介',
  `main_category` VARCHAR(100) COMMENT '主营类目',
  `shop_notice` TEXT COMMENT '店铺公告',
  `business_status` TINYINT DEFAULT 1 COMMENT '营业状态：1-正常营业，0-暂停营业',
  `customer_service_phone` VARCHAR(20) COMMENT '客服电话',
  `customer_service_online` VARCHAR(255) COMMENT '在线客服入口',
  `shop_address` VARCHAR(500) COMMENT '店铺详细地址',
  `audit_status` TINYINT DEFAULT 1 COMMENT '入驻审核状态：0-待审核，1-已通过，2-已驳回',
  `return_name` VARCHAR(50) COMMENT '退货收货人姓名',
  `return_phone` VARCHAR(20) COMMENT '退货联系电话',
  `return_address` VARCHAR(500) COMMENT '退货地址',
  `return_province` VARCHAR(50) COMMENT '退货省份',
  `return_city` VARCHAR(50) COMMENT '退货城市',
  `return_district` VARCHAR(50) COMMENT '退货区县',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 4. 管理员表
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 5. 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` INT DEFAULT 0 COMMENT '父分类ID',
  `level` INT DEFAULT 1 COMMENT '层级',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `icon` VARCHAR(200) COMMENT '图标',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（回收站标识）',
  INDEX `idx_parent` (`parent_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 6. 品牌表
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
  `logo` VARCHAR(255) COMMENT '品牌logo',
  `description` TEXT COMMENT '品牌描述',
  `first_letter` VARCHAR(10) COMMENT '首字母',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `product_count` INT DEFAULT 0 COMMENT '商品数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（回收站标识）',
  INDEX `idx_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 8. 商家品牌表
DROP TABLE IF EXISTS `merchant_brand`;
CREATE TABLE `merchant_brand` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
  `logo` VARCHAR(255) COMMENT '品牌logo',
  `description` TEXT COMMENT '品牌描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家品牌表';

-- 9. 规格类型表
DROP TABLE IF EXISTS `spec_type`;
CREATE TABLE `spec_type` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(50) NOT NULL COMMENT '规格类型名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（回收站标识）',
  INDEX `idx_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规格类型表';

-- 10. 规格值表
DROP TABLE IF EXISTS `spec_value`;
CREATE TABLE `spec_value` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `type_id` INT NOT NULL COMMENT '规格类型ID',
  `value` VARCHAR(100) NOT NULL COMMENT '规格值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（回收站标识）',
  INDEX `idx_spec_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规格值表';

-- 11. 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `category_id` INT COMMENT '分类ID',
  `brand_id` INT COMMENT '品牌ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `category` VARCHAR(50) COMMENT '分类名称',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) COMMENT '原价',
  `cost_price` DECIMAL(10,2) COMMENT '成本价',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `views` INT DEFAULT 0 COMMENT '浏览量',
  `cover_img` VARCHAR(200) COMMENT '封面图片',
  `main_image` VARCHAR(255) COMMENT '商品主图',
  `images` TEXT COMMENT '商品图片列表JSON',
  `description` TEXT COMMENT '商品描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-上架 0-下架 -1-回收站',
  `is_recommended` TINYINT DEFAULT 0 COMMENT '是否推荐',
  `is_new` TINYINT DEFAULT 0 COMMENT '是否新品',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间（回收站标识）',
  INDEX `idx_category` (`category_id`),
  INDEX `idx_merchant` (`merchant_id`),
  INDEX `idx_brand` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 14. 商品图片表
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL COMMENT '商品ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_main` TINYINT DEFAULT 0 COMMENT '是否主图',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 15. 商品SKU表
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL COMMENT '商品ID',
  `sku_code` VARCHAR(50) NOT NULL UNIQUE COMMENT 'SKU编码',
  `specs` VARCHAR(500) COMMENT '规格JSON',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- 16. 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消 -1-已关闭',
  `shipping_address` VARCHAR(200) COMMENT '收货地址',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `remark` VARCHAR(255) COMMENT '用户订单备注',
  `merchant_remark` VARCHAR(500) COMMENT '商家内部备注',
  `express_company` VARCHAR(100) COMMENT '物流公司',
  `tracking_no` VARCHAR(100) COMMENT '运单号',
  `group_order_no` VARCHAR(50) COMMENT '拼团订单号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` DATETIME COMMENT '支付时间',
  `ship_time` DATETIME COMMENT '发货时间',
  `receive_time` DATETIME COMMENT '收货时间',
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 17. 订单明细表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `product_image` VARCHAR(255) COMMENT '商品图片',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '商品小计',
  `specs` VARCHAR(500) COMMENT '商品规格JSON',
  `brand_id` INT COMMENT '品牌ID',
  `brand_name` VARCHAR(100) COMMENT '品牌名称（冗余）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_order` (`order_id`),
  INDEX `idx_product` (`product_id`),
  INDEX `idx_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 18. 订单操作日志表
DROP TABLE IF EXISTS `order_operation_log`;
CREATE TABLE `order_operation_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `operator_id` INT COMMENT '操作人ID',
  `operator_name` VARCHAR(100) COMMENT '操作人名称',
  `operation_type` VARCHAR(32) COMMENT '操作类型',
  `operation_desc` VARCHAR(500) COMMENT '操作描述',
  `before_status` INT COMMENT '操作前状态',
  `after_status` INT COMMENT '操作后状态',
  `remark` VARCHAR(500) COMMENT '操作备注',
  `extend_data` TEXT COMMENT '扩展数据(JSON)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- 19. 订单物流信息表
DROP TABLE IF EXISTS `order_item_ship`;
CREATE TABLE `order_item_ship` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL UNIQUE COMMENT '订单ID',
  `express_company` VARCHAR(100) COMMENT '物流公司',
  `tracking_no` VARCHAR(100) COMMENT '运单号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流信息表';

-- 20. 订单标签表
DROP TABLE IF EXISTS `order_tag`;
CREATE TABLE `order_tag` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `tag_color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  UNIQUE KEY `uk_order_tag` (`order_id`, `tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单标签表';

-- 21. 电子发票表
DROP TABLE IF EXISTS `order_invoice`;
CREATE TABLE `order_invoice` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL UNIQUE COMMENT '订单ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `invoice_no` VARCHAR(100) NOT NULL UNIQUE COMMENT '发票编号',
  `invoice_type` TINYINT DEFAULT 1 COMMENT '发票类型：1-电子普通发票 2-增值税专用发票',
  `title` VARCHAR(200) NOT NULL COMMENT '发票抬头',
  `tax_no` VARCHAR(50) COMMENT '税号',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '发票金额',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效 2-已作废',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `cancel_time` DATETIME COMMENT '作废时间',
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子发票表';

-- 22. 订单价格修改记录表
DROP TABLE IF EXISTS `order_price_change`;
CREATE TABLE `order_price_change` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `old_amount` DECIMAL(10,2) NOT NULL COMMENT '修改前金额',
  `new_amount` DECIMAL(10,2) NOT NULL COMMENT '修改后金额',
  `change_reason` VARCHAR(200) COMMENT '修改原因',
  `operator_id` INT COMMENT '操作人ID',
  `operator_name` VARCHAR(100) COMMENT '操作人名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单价格修改记录表';

-- 23. 物流轨迹表
DROP TABLE IF EXISTS `tracking_log`;
CREATE TABLE `tracking_log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `tracking_no` VARCHAR(100) NOT NULL COMMENT '运单号',
  `location` VARCHAR(200) COMMENT '物流地点',
  `status` VARCHAR(100) COMMENT '物流状态',
  `description` VARCHAR(500) COMMENT '物流描述',
  `create_time` DATETIME COMMENT '物流时间',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- 24. 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `sku_id` INT COMMENT 'SKU ID',
  `specs` TEXT COMMENT '规格信息',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `selected` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_product_sku` (`user_id`, `product_id`, `sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 25. 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 26. 浏览历史表
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE `browse_history` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `browse_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- 27. 评价表
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_item_id` INT NOT NULL COMMENT '订单明细ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `content` TEXT COMMENT '评价内容',
  `rating` TINYINT COMMENT '评分 1-5星',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `has_append` TINYINT DEFAULT 0 COMMENT '是否有追评',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  `top_time` DATETIME COMMENT '置顶时间',
  `merchant_reply` TEXT COMMENT '商家回复',
  `reply_time` DATETIME COMMENT '回复时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_product` (`product_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 28. 评价图片表
DROP TABLE IF EXISTS `review_image`;
CREATE TABLE `review_image` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价图片表';

-- 29. 评价回复表
DROP TABLE IF EXISTS `review_reply`;
CREATE TABLE `review_reply` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review` (`review_id`),
  INDEX `idx_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价回复表';

-- 30. 追评表
DROP TABLE IF EXISTS `review_append`;
CREATE TABLE `review_append` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `content` TEXT COMMENT '追评内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='追评表';

-- 31. 追评回复表
DROP TABLE IF EXISTS `review_append_reply`;
CREATE TABLE `review_append_reply` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `append_id` INT NOT NULL COMMENT '追评ID',
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_append_id` (`append_id`),
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='追评回复表';

-- 32. 差评解释表
DROP TABLE IF EXISTS `review_explanation`;
CREATE TABLE `review_explanation` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `content` TEXT NOT NULL COMMENT '解释内容',
  `edit_count` INT DEFAULT 0 COMMENT '编辑次数',
  `last_edit_time` DATETIME COMMENT '最后编辑时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  UNIQUE KEY `uk_review_id` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='差评解释表';

-- 33. 评价置顶表
DROP TABLE IF EXISTS `review_top`;
CREATE TABLE `review_top` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `top_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '置顶时间',
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_product_id` (`product_id`),
  UNIQUE KEY `uk_review_product` (`review_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价置顶表';

-- 34. 恶意差评申诉表
DROP TABLE IF EXISTS `review_appeal`;
CREATE TABLE `review_appeal` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `reason` VARCHAR(200) NOT NULL COMMENT '申诉理由',
  `description` TEXT COMMENT '详细描述',
  `evidence_urls` TEXT COMMENT '凭证图片URL',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已驳回',
  `handle_time` DATETIME COMMENT '处理时间',
  `handle_remark` TEXT COMMENT '处理备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='恶意差评申诉表';

-- 35. 评价举报表
DROP TABLE IF EXISTS `review_report`;
CREATE TABLE `review_report` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `reason` VARCHAR(100) NOT NULL COMMENT '举报原因',
  `description` TEXT COMMENT '详细描述',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已处理',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价举报表';

-- 36. 评价点赞记录表
DROP TABLE IF EXISTS `review_like`;
CREATE TABLE `review_like` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `review_id` INT NOT NULL COMMENT '评价ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  UNIQUE KEY `uk_review_user` (`review_id`, `user_id`),
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价点赞记录表';

-- 37. 退换货申请表
DROP TABLE IF EXISTS `return_request`;
CREATE TABLE `return_request` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL COMMENT '订单ID',
  `order_item_id` INT COMMENT '订单项ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `reason` TEXT COMMENT '退换货原因',
  `type` TINYINT DEFAULT 1 COMMENT '类型 1-退款 2-退货退款 3-换货',
  `reason_type` VARCHAR(50) COMMENT '原因类型',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已同意 2-已拒绝 3-待收货 4-已完成',
  `merchant_remark` TEXT COMMENT '商家备注',
  `reject_reason` VARCHAR(500) COMMENT '商家拒绝理由',
  `user_evidence_urls` TEXT COMMENT '用户凭证图片',
  `return_address_id` INT COMMENT '退货地址ID',
  `return_receiver_name` VARCHAR(50) COMMENT '退货收货人',
  `return_receiver_phone` VARCHAR(20) COMMENT '退货收货人电话',
  `return_address` VARCHAR(500) COMMENT '退货地址',
  `refund_amount` DECIMAL(10,2) COMMENT '退款金额',
  `actual_refund_amount` DECIMAL(10,2) COMMENT '实际退款金额',
  `audit_time` DATETIME COMMENT '审核时间',
  `receive_time` DATETIME COMMENT '确认收货时间',
  `refund_time` DATETIME COMMENT '退款时间',
  `logistics_no` VARCHAR(50) COMMENT '物流单号',
  `logistics_company` VARCHAR(50) COMMENT '物流公司',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`),
  INDEX `idx_order` (`order_id`),
  INDEX `idx_order_item_id` (`order_item_id`),
  INDEX `idx_return_address_id` (`return_address_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退换货申请表';

-- 38. 退换货凭证图片表
DROP TABLE IF EXISTS `return_request_image`;
CREATE TABLE `return_request_image` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `return_request_id` INT NOT NULL COMMENT '退换货申请ID',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_return_request` (`return_request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退换货凭证图片表';

-- 39. 纠纷申请表
DROP TABLE IF EXISTS `dispute_application`;
CREATE TABLE `dispute_application` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `return_id` INT NOT NULL COMMENT '退换货申请ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `reason` TEXT COMMENT '纠纷原因',
  `evidence` TEXT COMMENT '凭证图片',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待处理 1-处理中 2-已处理',
  `platform_result` TEXT COMMENT '平台处理结果',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_return_id` (`return_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纠纷申请表';

-- 40. 支付记录表
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL UNIQUE COMMENT '关联订单ID',
  `pay_method` VARCHAR(20) NOT NULL COMMENT '支付方式',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待支付 1-支付成功 2-支付失败 3-已退款',
  `pay_time` DATETIME COMMENT '支付时间',
  `refund_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` DATETIME COMMENT '退款时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 41. 用户地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区/县',
  `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 42. 会员信息表
DROP TABLE IF EXISTS `member_info`;
CREATE TABLE `member_info` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL UNIQUE COMMENT '用户ID',
  `member_level` VARCHAR(20) DEFAULT '非会员' COMMENT '会员等级',
  `points` INT DEFAULT 0 COMMENT '积分',
  `expire_time` DATETIME COMMENT '会员有效期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';

-- 43. 会员类型表
DROP TABLE IF EXISTS `member_type`;
CREATE TABLE `member_type` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `level_name` VARCHAR(50) NOT NULL COMMENT '会员等级名称',
  `level_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '会员等级代码',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `duration_days` INT NOT NULL COMMENT '有效期天数',
  `points_bonus` INT DEFAULT 0 COMMENT '赠送积分',
  `privileges` TEXT COMMENT '特权描述',
  `description` VARCHAR(200) COMMENT '简短描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员类型表';

-- 44. 积分记录表
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL COMMENT '用户ID',
  `points` INT NOT NULL COMMENT '积分变化',
  `type` VARCHAR(20) COMMENT '类型',
  `description` VARCHAR(200) COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 45. 促销活动表
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(100) NOT NULL COMMENT '活动名称',
  `type` TINYINT NOT NULL COMMENT '活动类型：1-满减 2-折扣 3-包邮 4-赠品',
  `description` TEXT COMMENT '活动描述',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `min_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '满减最低金额',
  `reduce_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '满减金额',
  `discount_rate` DECIMAL(5,2) DEFAULT 1.00 COMMENT '折扣率',
  `free_shipping_threshold` DECIMAL(10,2) DEFAULT 0 COMMENT '包邮门槛',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐展示',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant` (`merchant_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销活动表';

-- 46. 活动商品关联表
DROP TABLE IF EXISTS `promotion_product`;
CREATE TABLE `promotion_product` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `promotion_id` INT NOT NULL COMMENT '活动ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称（冗余）',
  `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品原价（冗余）',
  `promotion_price` DECIMAL(10,2) COMMENT '活动价格',
  `stock` INT DEFAULT 0 COMMENT '活动库存',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_promotion` (`promotion_id`),
  INDEX `idx_product` (`product_id`),
  UNIQUE KEY `uk_promotion_product` (`promotion_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动商品关联表';

-- 47. 优惠券表
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` TINYINT NOT NULL COMMENT '类型：1-满减券 2-折扣券 3-无门槛券',
  `face_value` DECIMAL(10,2) NOT NULL COMMENT '面值/折扣率',
  `min_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
  `total_count` INT NOT NULL COMMENT '发放总量',
  `received_count` INT DEFAULT 0 COMMENT '已领取数量',
  `used_count` INT DEFAULT 0 COMMENT '已使用数量',
  `per_user_limit` INT DEFAULT 1 COMMENT '每人限领数量',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant` (`merchant_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 48. 用户优惠券领取记录表
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `coupon_id` INT NOT NULL COMMENT '优惠券ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `order_id` INT DEFAULT NULL COMMENT '使用订单ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已使用 1-未使用 2-已过期',
  `receive_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` DATETIME COMMENT '使用时间',
  INDEX `idx_coupon` (`coupon_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券领取记录表';

-- 49. 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) COMMENT '操作用户名',
  `operation` VARCHAR(200) COMMENT '操作描述',
  `method` VARCHAR(500) COMMENT '请求方法',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `params` TEXT COMMENT '请求参数',
  `result` TEXT COMMENT '返回结果',
  `status` INT DEFAULT 1 COMMENT '状态 1-成功 0-失败',
  `error_msg` TEXT COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `duration` BIGINT DEFAULT 0 COMMENT '执行时长(毫秒)',
  INDEX `idx_username` (`username`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 50. 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_name` VARCHAR(100) DEFAULT NULL COMMENT '配置名称',
  `description` VARCHAR(200) COMMENT '描述',
  `category` VARCHAR(50) DEFAULT 'basic' COMMENT '分类',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 51. 首页轮播图表
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
  `link_url` VARCHAR(500) DEFAULT NULL COMMENT '点击跳转链接',
  `color` VARCHAR(20) DEFAULT '#FF5000' COMMENT '背景色',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- 52. 商家退货地址表
DROP TABLE IF EXISTS `merchant_return_address`;
CREATE TABLE `merchant_return_address` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家退货地址表';

-- 53. 售后操作日志表
DROP TABLE IF EXISTS `refund_operation_log`;
CREATE TABLE `refund_operation_log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `refund_id` INT NOT NULL COMMENT '售后ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `operator_type` VARCHAR(20) NOT NULL COMMENT '操作人类型',
  `operator_id` INT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人姓名',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_detail` TEXT COMMENT '操作详情',
  `remark` VARCHAR(500) COMMENT '备注',
  `old_status` INT COMMENT '变更前状态',
  `new_status` INT COMMENT '变更后状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_refund_id` (`refund_id`),
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后操作日志表';

-- 54. 商家收款账户表
DROP TABLE IF EXISTS `merchant_payment_account`;
CREATE TABLE `merchant_payment_account` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `account_type` VARCHAR(20) NOT NULL COMMENT '账户类型',
  `account_name` VARCHAR(100) NOT NULL COMMENT '账户名称',
  `account_number` VARCHAR(100) NOT NULL COMMENT '账户号码',
  `bank_name` VARCHAR(100) COMMENT '开户银行',
  `bank_branch` VARCHAR(100) COMMENT '开户支行',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家收款账户表';

-- 55. 商家消息表
DROP TABLE IF EXISTS `merchant_message`;
CREATE TABLE `merchant_message` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `message_type` VARCHAR(20) NOT NULL COMMENT '消息类型',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `related_id` INT COMMENT '关联业务ID',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
  `read_time` DATETIME COMMENT '阅读时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家消息表';

-- 56. 商家子账号表
DROP TABLE IF EXISTS `merchant_sub_account`;
CREATE TABLE `merchant_sub_account` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '主商家ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '子账号登录名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家子账号表';

-- 57. 商家子账号权限表
DROP TABLE IF EXISTS `merchant_sub_account_permission`;
CREATE TABLE `merchant_sub_account_permission` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `sub_account_id` INT NOT NULL COMMENT '子账号ID',
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `permission_code` VARCHAR(50) NOT NULL COMMENT '权限代码',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_sub_account_id` (`sub_account_id`),
  INDEX `idx_merchant_id` (`merchant_id`),
  UNIQUE KEY `uk_sub_permission` (`sub_account_id`, `permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家子账号权限表';

-- 58. 商家操作日志表
DROP TABLE IF EXISTS `merchant_operation_log`;
CREATE TABLE `merchant_operation_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `operator_id` INT COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人名称',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_desc` VARCHAR(500) NOT NULL COMMENT '操作描述',
  `request_method` VARCHAR(10) COMMENT '请求方法',
  `request_url` VARCHAR(255) COMMENT '请求URL',
  `request_params` TEXT COMMENT '请求参数',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `error_msg` TEXT COMMENT '错误信息',
  `execute_time` BIGINT COMMENT '执行时长',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家操作日志表';

-- 59. 商品操作日志表
DROP TABLE IF EXISTS `product_operation_log`;
CREATE TABLE `product_operation_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `merchant_id` INT NOT NULL COMMENT '商家ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `operator_id` INT COMMENT '操作人ID',
  `operator_name` VARCHAR(50) COMMENT '操作人名称',
  `operation_type` VARCHAR(50) COMMENT '操作类型',
  `operation_desc` VARCHAR(500) COMMENT '操作描述',
  `before_value` TEXT COMMENT '修改前的值',
  `after_value` TEXT COMMENT '修改后的值',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_merchant_id` (`merchant_id`),
  INDEX `idx_product_id` (`product_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品操作日志表';

-- ============================================
-- 三、添加外键约束
-- ============================================

ALTER TABLE `product` ADD CONSTRAINT `fk_product_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `product` ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL;
ALTER TABLE `product` ADD CONSTRAINT `fk_product_brand` FOREIGN KEY (`brand_id`) REFERENCES `brand`(`id`) ON DELETE SET NULL;
ALTER TABLE `product_image` ADD CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `product_sku` ADD CONSTRAINT `fk_product_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `order` ADD CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_item` ADD CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_item` ADD CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_item` ADD CONSTRAINT `fk_order_item_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `favorite` ADD CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `favorite` ADD CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `browse_history` ADD CONSTRAINT `fk_browse_history_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `browse_history` ADD CONSTRAINT `fk_browse_history_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `review` ADD CONSTRAINT `fk_review_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item`(`id`) ON DELETE CASCADE;
ALTER TABLE `review` ADD CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `review` ADD CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `review` ADD CONSTRAINT `fk_review_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_image` ADD CONSTRAINT `fk_review_image_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_reply` ADD CONSTRAINT `fk_review_reply_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_reply` ADD CONSTRAINT `fk_review_reply_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_append` ADD CONSTRAINT `fk_review_append_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_append` ADD CONSTRAINT `fk_review_append_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_append_reply` ADD CONSTRAINT `fk_review_append_reply_append` FOREIGN KEY (`append_id`) REFERENCES `review_append`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_append_reply` ADD CONSTRAINT `fk_review_append_reply_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_append_reply` ADD CONSTRAINT `fk_review_append_reply_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_explanation` ADD CONSTRAINT `fk_review_explanation_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_explanation` ADD CONSTRAINT `fk_review_explanation_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_top` ADD CONSTRAINT `fk_review_top_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_top` ADD CONSTRAINT `fk_review_top_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_top` ADD CONSTRAINT `fk_review_top_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_appeal` ADD CONSTRAINT `fk_review_appeal_review` FOREIGN KEY (`review_id`) REFERENCES `review`(`id`) ON DELETE CASCADE;
ALTER TABLE `review_appeal` ADD CONSTRAINT `fk_review_appeal_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request_image` ADD CONSTRAINT `fk_return_request_image` FOREIGN KEY (`return_request_id`) REFERENCES `return_request`(`id`) ON DELETE CASCADE;
ALTER TABLE `payment` ADD CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_address` ADD CONSTRAINT `fk_user_address_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `member_info` ADD CONSTRAINT `fk_member_info_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `points_record` ADD CONSTRAINT `fk_points_record_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_role` ADD CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `brand` ADD CONSTRAINT `fk_brand_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `merchant_brand` ADD CONSTRAINT `fk_merchant_brand_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `spec_type` ADD CONSTRAINT `fk_spec_type_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `spec_value` ADD CONSTRAINT `fk_spec_value_spec_type` FOREIGN KEY (`type_id`) REFERENCES `spec_type`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_operation_log` ADD CONSTRAINT `fk_order_operation_log_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_operation_log` ADD CONSTRAINT `fk_order_operation_log_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `order_item_ship` ADD CONSTRAINT `fk_order_item_ship_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE;
ALTER TABLE `promotion` ADD CONSTRAINT `fk_promotion_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `promotion_product` ADD CONSTRAINT `fk_promotion_product_promotion` FOREIGN KEY (`promotion_id`) REFERENCES `promotion`(`id`) ON DELETE CASCADE;
ALTER TABLE `promotion_product` ADD CONSTRAINT `fk_promotion_product_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE;
ALTER TABLE `coupon` ADD CONSTRAINT `fk_coupon_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_coupon` ADD CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_coupon` ADD CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_coupon` ADD CONSTRAINT `fk_user_coupon_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `user_coupon` ADD CONSTRAINT `fk_user_coupon_order` FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE SET NULL;
ALTER TABLE `merchant_return_address` ADD CONSTRAINT `fk_merchant_return_address_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `refund_operation_log` ADD CONSTRAINT `fk_refund_operation_log_refund` FOREIGN KEY (`refund_id`) REFERENCES `return_request`(`id`) ON DELETE CASCADE;
ALTER TABLE `refund_operation_log` ADD CONSTRAINT `fk_refund_operation_log_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_request_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item`(`id`) ON DELETE CASCADE;
ALTER TABLE `return_request` ADD CONSTRAINT `fk_return_request_return_address` FOREIGN KEY (`return_address_id`) REFERENCES `merchant_return_address`(`id`) ON DELETE SET NULL;

-- ============================================
-- 四、插入分类数据（12个平台级大类 + 子分类）
-- ============================================

INSERT INTO `category` (`name`, `parent_id`, `level`, `sort_order`, `icon`, `status`) VALUES
('服饰鞋包', 0, 1, 1, 'ShoppingBag', 1),
('美妆个护', 0, 1, 2, 'MagicStick', 1),
('数码家电', 0, 1, 3, 'Monitor', 1),
('家居生活', 0, 1, 4, 'House', 1),
('母婴玩具', 0, 1, 5, 'Present', 1),
('食品生鲜', 0, 1, 6, 'Food', 1),
('运动户外', 0, 1, 7, 'Trophy', 1),
('图书办公', 0, 1, 8, 'Reading', 1),
('汽车用品', 0, 1, 9, 'Van', 1),
('珠宝配饰', 0, 1, 10, 'Watch', 1),
('医药健康', 0, 1, 11, 'FirstAidKit', 1),
('虚拟服务', 0, 1, 12, 'Ticket', 1),
('女装', 1, 2, 1, NULL, 1),
('男装', 1, 2, 2, NULL, 1),
('童装', 1, 2, 3, NULL, 1),
('内衣', 1, 2, 4, NULL, 1),
('鞋靴', 1, 2, 5, NULL, 1),
('箱包皮具', 1, 2, 6, NULL, 1),
('护肤', 2, 2, 1, NULL, 1),
('彩妆', 2, 2, 2, NULL, 1),
('香水', 2, 2, 3, NULL, 1),
('美发护发', 2, 2, 4, NULL, 1),
('身体护理', 2, 2, 5, NULL, 1),
('口腔护理', 2, 2, 6, NULL, 1),
('美容仪', 2, 2, 7, NULL, 1),
('手机', 3, 2, 1, NULL, 1),
('电脑', 3, 2, 2, NULL, 1),
('平板', 3, 2, 3, NULL, 1),
('智能设备', 3, 2, 4, NULL, 1),
('摄影摄像', 3, 2, 5, NULL, 1),
('家用电器', 3, 2, 6, NULL, 1),
('家纺', 4, 2, 1, NULL, 1),
('厨具', 4, 2, 2, NULL, 1),
('收纳整理', 4, 2, 3, NULL, 1),
('清洁用品', 4, 2, 4, NULL, 1),
('灯具照明', 4, 2, 5, NULL, 1),
('益智玩具', 5, 2, 1, NULL, 1),
('积木拼图', 5, 2, 2, NULL, 1),
('娃娃公仔', 5, 2, 3, NULL, 1),
('模型手办', 5, 2, 4, NULL, 1),
('奶粉辅食', 5, 2, 5, NULL, 1),
('纸尿裤', 5, 2, 6, NULL, 1),
('休闲零食', 6, 2, 1, NULL, 1),
('生鲜水果', 6, 2, 2, NULL, 1),
('茶饮冲调', 6, 2, 3, NULL, 1),
('粮油调味', 6, 2, 4, NULL, 1),
('运动鞋服', 7, 2, 1, NULL, 1),
('健身器材', 7, 2, 2, NULL, 1),
('户外装备', 7, 2, 3, NULL, 1),
('骑行运动', 7, 2, 4, NULL, 1),
('教材教辅', 8, 2, 1, NULL, 1),
('办公文具', 8, 2, 2, NULL, 1),
('办公设备', 8, 2, 3, NULL, 1),
('车饰', 9, 2, 1, NULL, 1),
('车载电器', 9, 2, 2, NULL, 1),
('养护用品', 9, 2, 3, NULL, 1),
('黄金珠宝', 10, 2, 1, NULL, 1),
('时尚饰品', 10, 2, 2, NULL, 1),
('眼镜', 10, 2, 3, NULL, 1),
('中西药品', 11, 2, 1, NULL, 1),
('保健品', 11, 2, 2, NULL, 1),
('医疗器械', 11, 2, 3, NULL, 1),
('充值缴费', 12, 2, 1, NULL, 1),
('生活服务', 12, 2, 2, NULL, 1);

-- ============================================
-- 五、插入测试数据
-- ============================================

-- 商家数据
INSERT INTO `merchant` (`shop_name`, `username`, `password`, `contact_phone`, `shop_desc`, `status`, `return_name`, `return_phone`, `return_address`, `return_province`, `return_city`, `return_district`) VALUES
('数码旗舰店', 'shuma_shop', '123456', '400-888-1001', '专注数码科技，正品保障', 1, '张经理', '13800111111', '北京市朝阳区仓库路1号', '北京市', '北京市', '朝阳区'),
('服饰专营店', 'fushi_shop', '123456', '400-888-1002', '时尚服饰，品质之选', 1, '李经理', '13800222222', '上海市浦东新区科技路2号', '上海市', '上海市', '浦东新区'),
('美妆官方店', 'meizhuang_shop', '123456', '400-888-1003', '美妆护肤，正品保障', 1, '王经理', '13800333333', '广州市天河区美妆街3号', '广东省', '广州市', '天河区'),
('家居生活馆', 'jiaju_shop', '123456', '400-888-1004', '品质家居，美好生活', 1, '陈经理', '13800444444', '成都市武侯区家居大道4号', '四川省', '成都市', '武侯区'),
('母婴用品店', 'muying_shop', '123456', '400-888-1005', '呵护宝宝，健康成长', 1, '赵经理', '13800555555', '杭州市西湖区母婴路5号', '浙江省', '杭州市', '西湖区');

-- 管理员数据
INSERT INTO `admin` (`username`, `password`, `role`, `status`) VALUES
('超级管理员', '123456', 'admin', 1);

-- 会员类型数据
INSERT INTO `member_type` (`level_name`, `level_code`, `price`, `duration_days`, `points_bonus`, `privileges`, `description`, `sort_order`) VALUES
('银卡会员', 'SILVER', 29.90, 30, 100, '95折优惠,双倍积分', '适合偶尔购物的用户', 1),
('金卡会员', 'GOLD', 89.90, 90, 500, '9折优惠,双倍积分,生日礼包', '最受欢迎的会员选择', 2),
('钻石会员', 'DIAMOND', 199.00, 180, 1500, '85折优惠,三倍积分,生日礼包,优先发货', '尊享钻石特权', 3),
('铂金会员', 'PLATINUM', 399.00, 365, 5000, '8折优惠,四倍积分,专属客服,优先发货,专属活动', '顶级会员尊享', 4);

-- 商品数据
INSERT INTO `product` (`merchant_id`, `category_id`, `brand_id`, `name`, `category`, `price`, `original_price`, `stock`, `sales`, `views`, `cover_img`, `description`, `status`, `is_recommended`, `is_new`) VALUES
(1, 26, NULL, 'iPhone 15 Pro 256GB 深空黑', '手机', 7999.00, 8999.00, 200, 1500, 5000, 'https://via.placeholder.com/400x400?text=iPhone15', '最新款iPhone，A17 Pro芯片，钛金属设计。', 1, 1, 1),
(1, 26, NULL, '华为Mate60 Pro 旗舰手机', '手机', 6999.00, 7999.00, 300, 2000, 8000, 'https://via.placeholder.com/400x400?text=Mate60', '华为旗舰，麒麟芯片，卫星通话。', 1, 1, 1),
(1, 27, NULL, 'MacBook Pro 14英寸 M3芯片', '电脑', 12999.00, 14999.00, 80, 800, 3000, 'https://via.placeholder.com/400x400?text=MacBook', 'M3芯片，14英寸Liquid Retina XDR显示屏。', 1, 1, 1),
(1, 27, NULL, 'ThinkPad X1 Carbon 轻薄商务本', '电脑', 8999.00, 9999.00, 150, 600, 2500, 'https://via.placeholder.com/400x400?text=ThinkPad', '商务首选，轻薄便携，性能强劲。', 1, 1, 0),
(2, 13, NULL, '法式复古碎花连衣裙', '女装', 299.00, 599.00, 500, 3200, 8000, 'https://via.placeholder.com/400x400?text=连衣裙', '法式复古风格，碎花印花，A字版型。', 1, 1, 1),
(2, 13, NULL, '韩版简约纯色T恤', '女装', 89.00, 129.00, 1000, 5000, 10000, 'https://via.placeholder.com/400x400?text=T恤', '韩版简约设计，纯棉面料，百搭款式。', 1, 1, 0),
(2, 14, NULL, '男士商务休闲夹克', '男装', 399.00, 599.00, 300, 1200, 3000, 'https://via.placeholder.com/400x400?text=夹克', '商务休闲两不误，品质面料。', 1, 1, 0),
(2, 17, NULL, '运动休闲跑步鞋', '鞋靴', 299.00, 399.00, 600, 1800, 4500, 'https://via.placeholder.com/400x400?text=运动鞋', '轻便透气，缓震舒适，运动首选。', 1, 1, 1),
(3, 19, NULL, 'SK-II护肤精华套装', '护肤', 1599.00, 1999.00, 100, 800, 2500, 'https://via.placeholder.com/400x400?text=SKII', '神仙水精华露，补水保湿，焕活肌肤。', 1, 1, 0),
(3, 20, NULL, '迪奥烈艳蓝金唇膏', '彩妆', 299.00, 350.00, 200, 1500, 4000, 'https://via.placeholder.com/400x400?text=口红', '经典色号，丝绒质地，持久显色。', 1, 1, 0),
(4, 32, NULL, '全棉四件套简约套件', '家纺', 299.00, 399.00, 400, 900, 2000, 'https://via.placeholder.com/400x400?text=四件套', '全棉面料，柔软舒适，简约风格。', 1, 1, 1),
(4, 33, NULL, '不锈钢炒锅不粘锅', '厨具', 199.00, 299.00, 500, 1200, 3000, 'https://via.placeholder.com/400x400?text=炒锅', '不粘易清洗，省油少油烟。', 1, 0, 0),
(5, 37, NULL, '乐高城市系列积木玩具', '益智玩具', 299.00, 399.00, 200, 800, 2000, 'https://via.placeholder.com/400x400?text=乐高', '开发智力，培养动手能力，安全无毒。', 1, 1, 0);

-- 商品图片数据
INSERT INTO `product_image` (`product_id`, `image_url`, `sort_order`, `is_main`) VALUES
(1, 'https://via.placeholder.com/400x400?text=iPhone15', 0, 1),
(2, 'https://via.placeholder.com/400x400?text=Mate60', 0, 1),
(3, 'https://via.placeholder.com/400x400?text=MacBook', 0, 1),
(4, 'https://via.placeholder.com/400x400?text=ThinkPad', 0, 1),
(5, 'https://via.placeholder.com/400x400?text=连衣裙', 0, 1),
(6, 'https://via.placeholder.com/400x400?text=T恤', 0, 1),
(7, 'https://via.placeholder.com/400x400?text=夹克', 0, 1),
(8, 'https://via.placeholder.com/400x400?text=运动鞋', 0, 1),
(9, 'https://via.placeholder.com/400x400?text=SKII', 0, 1),
(10, 'https://via.placeholder.com/400x400?text=口红', 0, 1),
(11, 'https://via.placeholder.com/400x400?text=四件套', 0, 1),
(12, 'https://via.placeholder.com/400x400?text=炒锅', 0, 1),
(13, 'https://via.placeholder.com/400x400?text=乐高', 0, 1);

-- 商品SKU数据
INSERT INTO `product_sku` (`product_id`, `sku_code`, `specs`, `price`, `stock`, `sales`, `status`) VALUES
(1, 'IPHONE15-256-BLACK', '{"存储":"256GB","颜色":"黑色"}', 7999.00, 100, 800, 1),
(1, 'IPHONE15-256-WHITE', '{"存储":"256GB","颜色":"白色"}', 7999.00, 100, 700, 1),
(5, 'DRESS-S-BLUE', '{"尺码":"S","颜色":"蓝色"}', 299.00, 150, 1000, 1),
(5, 'DRESS-M-BLUE', '{"尺码":"M","颜色":"蓝色"}', 299.00, 200, 1200, 1),
(7, 'JACKET-M-BLACK', '{"尺码":"M","颜色":"黑色"}', 399.00, 100, 400, 1),
(7, 'JACKET-L-BLACK', '{"尺码":"L","颜色":"黑色"}', 399.00, 100, 400, 1);

-- 轮播图数据
INSERT INTO `banner` (`title`, `description`, `color`, `sort_order`, `status`) VALUES
('618年中大促', '全场低至5折起', '#FF5000', 1, 1),
('新品首发', '最新数码产品抢先体验', '#FF1A1A', 2, 1),
('品质生活', '精选好物，品质保证', '#FF7F00', 3, 1);

-- 促销活动数据
INSERT INTO `promotion` (`merchant_id`, `name`, `type`, `description`, `start_time`, `end_time`, `min_amount`, `reduce_amount`, `discount_rate`, `status`, `is_recommend`) VALUES
(1, '新年数码特惠', 1, '满减优惠，数码产品全场特惠', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 3000, 300, 1.00, 1, 1),
(2, '服饰换季大促', 2, '全场8折起，限时抢购', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 0, 0, 0.80, 1, 1),
(3, '美妆节', 3, '满99包邮', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 99, 0, 1.00, 1, 1);

-- 促销商品关联数据
INSERT INTO `promotion_product` (`promotion_id`, `product_id`, `product_name`, `product_price`, `promotion_price`, `stock`, `status`) VALUES
(1, 1, 'iPhone 15 Pro 256GB 深空黑', 7999.00, 7699.00, 200, 1),
(1, 2, '华为Mate60 Pro 旗舰手机', 6999.00, 6699.00, 300, 1),
(2, 5, '法式复古碎花连衣裙', 299.00, 239.20, 500, 1),
(2, 7, '男士商务休闲夹克', 399.00, 319.20, 300, 1);

-- 优惠券数据
INSERT INTO `coupon` (`merchant_id`, `name`, `type`, `face_value`, `min_amount`, `total_count`, `received_count`, `used_count`, `per_user_limit`, `start_time`, `end_time`, `status`) VALUES
(1, '满2000减200', 1, 200.00, 2000.00, 100, 30, 15, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(1, '满5000减500', 1, 500.00, 5000.00, 50, 20, 10, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(2, '新人专享券', 3, 50.00, 0.00, 1000, 500, 200, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
(3, '满199减30', 1, 30.00, 199.00, 200, 80, 40, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1);

-- 系统配置数据
INSERT INTO `system_config` (`config_key`, `config_value`, `config_name`, `description`, `category`, `sort_order`) VALUES
('site_name', '仿淘宝电商平台', '网站名称', '网站显示的名称', 'basic', 1),
('site_description', 'Spring Boot + Vue3 全栈电商项目', '网站描述', '网站简介描述', 'basic', 2),
('site_icp', '京ICP备2024XXXXX号', '备案号', '网站ICP备案号', 'basic', 3),
('customer_service_phone', '400-800-1234', '客服电话', '平台客服热线', 'basic', 4),
('default_page_size', '10', '默认分页数', '列表页默认每页显示条数', 'basic', 5),
('merchant_auto_approve', 'false', '自动审核商家', '新商家注册是否自动通过审核', 'merchant', 1),
('merchant_commission_rate', '0.05', '平台佣金比例', '平台从每笔交易中抽取的佣金比例', 'merchant', 2),
('max_products_per_merchant', '100', '商家商品上限', '每个商家最多可发布的商品数量', 'merchant', 3),
('merchant_settlement_cycle', '7', '结算周期(天)', '商家货款结算周期', 'merchant', 4),
('order_auto_cancel_minutes', '30', '自动取消时间(分钟)', '未支付订单自动取消的超时时间', 'order', 1),
('order_auto_confirm_days', '15', '自动确认收货(天)', '发货后超时未确认则自动确认收货', 'order', 2),
('order_return_deadline_days', '7', '退换货期限(天)', '确认收货后可申请退换货的天数', 'order', 3),
('order_shipping_fee', '0', '默认运费', '默认运费金额，0表示包邮', 'order', 4),
('free_shipping_threshold', '99', '包邮门槛', '订单金额达到此值后免运费', 'order', 5),
('points_per_yuan', '1', '每元积分数', '每消费1元获得的积分数', 'member', 1),
('register_bonus_points', '100', '注册赠送积分', '新用户注册赠送的积分', 'member', 2),
('member_level_silver', '银卡会员', '银卡会员名称', '银卡会员等级名称', 'member', 3),
('member_level_gold', '金卡会员', '金卡会员名称', '金卡会员等级名称', 'member', 4),
('member_level_diamond', '钻石会员', '钻石会员名称', '钻石会员等级名称', 'member', 5),
('upload_max_size_mb', '2', '上传大小限制(MB)', '单个文件最大上传大小', 'upload', 1),
('upload_allowed_types', 'jpg,jpeg,png,gif,webp', '允许上传类型', '允许上传的图片文件扩展名', 'upload', 2),
('upload_path', '/uploads/', '上传存储路径', '文件上传后存储的目录路径', 'upload', 3);

-- ============================================
-- 六、创建视图
-- ============================================

DROP VIEW IF EXISTS v_product_detail;
CREATE VIEW v_product_detail AS
SELECT
    p.*,
    c.name AS category_name,
    m.shop_name AS store_name,
    m.contact_phone AS store_phone,
    (SELECT COUNT(*) FROM review WHERE product_id = p.id) AS review_count,
    (SELECT IFNULL(AVG(rating), 0) FROM review WHERE product_id = p.id) AS avg_rating
FROM product p
LEFT JOIN category c ON p.category_id = c.id
LEFT JOIN merchant m ON p.merchant_id = m.id;

DROP VIEW IF EXISTS v_review_detail;
CREATE VIEW v_review_detail AS
SELECT
    r.*,
    u.nickname AS user_nickname,
    p.name AS product_name,
    m.shop_name AS merchant_name
FROM review r
LEFT JOIN `user` u ON r.user_id = u.id
LEFT JOIN product p ON r.product_id = p.id
LEFT JOIN merchant m ON r.merchant_id = m.id;

-- ============================================
-- 七、创建存储过程
-- ============================================

DROP PROCEDURE IF EXISTS sp_increment_product_view;
DROP PROCEDURE IF EXISTS sp_increment_product_sales;

DELIMITER //
CREATE PROCEDURE sp_increment_product_view(IN p_id INT)
BEGIN
    UPDATE product SET views = views + 1 WHERE id = p_id;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_increment_product_sales(IN p_id INT, IN p_quantity INT)
BEGIN
    UPDATE product SET sales = sales + p_quantity WHERE id = p_id;
END//
DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 执行完成
-- ============================================
SELECT 'online_mall数据库初始化完成！共60张表' AS message;
