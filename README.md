# 在线商城系统 - 合并版

## 项目概述

本项目是一个功能完整的**前后端分离架构**在线商城系统，采用 **Spring Boot 2.7.18** + **Vue 3.5.34** 技术栈开发。系统包含完整的电商购物流程、商家后台管理、会员积分体系、促销营销功能和数据统计分析等核心模块。

### 主要特点

- **前后端分离**：后端提供RESTful API，前端采用Vue 3单页应用
- **多角色权限**：支持普通用户、商家、管理员三种角色
- **完整业务流程**：商品浏览、购物车、订单管理、支付、评价、退换货
- **会员积分系统**：多种会员等级（普通、银卡、金卡、钻石、铂金）、积分获取与抵扣
- **促销营销功能**：满减活动、折扣活动、优惠券发放与管理
- **数据统计分析**：销售趋势、商品排行、转化分析、库存预警、财务报表
- **商品管理**：完整的商品发布、SKU配置、上下架管理、回收站功能

---

# 技术架构

### 后端技术栈

| 技术                 | 版本   | 说明                            |
| -------------------- | ------ | ------------------------------- |
| Spring Boot          | 2.7.18 | 后端核心框架                    |
| Java                 | 1.8    | 开发语言                        |
| MyBatis              | 2.3.1  | ORM持久层框架                   |
| MySQL                | 8.0+   | 关系型数据库                    |
| JWT (JJWT)           | 0.12.5 | JSON Web Token身份认证          |
| Lombok               | 1.18.x | 简化Java代码（getter/setter等） |
| Spring AOP           | 2.7.x  | 面向切面编程（日志、权限）      |
| Apache Commons Codec | 1.15   | 密码加密工具                    |
| Spring Validation    | -      | 参数校验                        |
| Jackson              | -      | JSON序列化/反序列化             |
| Commons IO           | 2.11.0 | IO工具类（文件上传）            |

### 前端技术栈

| 技术                    | 版本   | 说明               |
| ----------------------- | ------ | ------------------ |
| Vue                     | 3.5.34 | 前端渐进式框架     |
| Vue Router              | 4.4.0  | 单页应用路由管理   |
| Pinia                   | 2.2.0  | 状态管理库         |
| Element Plus            | 2.9.0  | 基于Vue 3的组件库  |
| @element-plus/icons-vue | 2.3.1  | Element Plus图标库 |
| Axios                   | 1.7.0  | HTTP请求库         |
| Vite                    | 8.0.12 | 新一代前端构建工具 |

### 核心依赖说明

**后端**：

- `spring-boot-starter-web`：Web应用支持
- `mybatis-spring-boot-starter`：MyBatis集成
- `mysql-connector-j`：MySQL数据库驱动
- `spring-boot-starter-validation`：参数校验
- `jjwt-api/jjwt-impl/jjwt-jackson`：JWT认证

**前端**：

- `vue`：Vue 3核心框架
- `vue-router`：路由管理
- `pinia`：状态管理
- `element-plus`：UI组件库
- `axios`：HTTP客户端

### 数据库设计

| 表名                      | 说明                 | 所属模块   |
| ------------------------- | -------------------- | ---------- |
| `user`                    | 用户表               | 用户模块   |
| `user_address`            | 收货地址表           | 用户模块   |
| `member_info`             | 会员信息表           | 用户模块   |
| `member_type`             | 会员类型表           | 用户模块   |
| `points_record`           | 积分记录表           | 用户模块   |
| `cart`                    | 购物车表             | 订单模块   |
| `order`                   | 订单表               | 订单模块   |
| `order_item`              | 订单明细表           | 订单模块   |
| `order_operation_log`     | 订单操作日志表       | 订单模块   |
| `order_item_ship`         | 订单物流信息表       | 订单模块   |
| `order_tag`               | 订单标签表           | 订单模块   |
| `order_invoice`           | 电子发票表           | 订单模块   |
| `tracking_log`            | 物流轨迹表           | 订单模块   |
| `order_price_change`      | 订单价格修改记录表   | 订单模块   |
| `payment`                 | 支付记录表           | 订单模块   |
| `product`                 | 商品表               | 商品模块   |
| `product_image`           | 商品图片表           | 商品模块   |
| `product_sku`             | 商品SKU规格表        | 商品模块   |
| `category`                | 商品分类表           | 商品模块   |
| `merchant_category`       | 商家分类表           | 商家模块   |
| `brand`                   | 品牌表               | 商品模块   |
| `merchant_brand`          | 商家品牌表           | 商家模块   |
| `spec_type`               | 规格类型表           | 商品模块   |
| `spec_value`              | 规格值表             | 商品模块   |
| `shipping_template`       | 运费模板表           | 商家模块   |
| `sensitive_word`          | 敏感词表             | 系统模块   |
| `review`                  | 商品评价表           | 评价模块   |
| `review_image`            | 评价图片表           | 评价模块   |
| `review_reply`            | 评价回复表           | 评价模块   |
| `review_append`           | 追评表               | 评价模块   |
| `review_append_reply`     | 追评回复表           | 评价模块   |
| `review_explanation`      | 差评解释表           | 评价模块   |
| `review_top`              | 评价置顶表           | 评价模块   |
| `review_appeal`           | 恶意差评申诉表       | 评价模块   |
| `review_report`           | 评价举报表           | 评价模块   |
| `return_request`          | 退换货申请表         | 退换货模块 |
| `return_request_image`    | 退换货凭证表         | 退换货模块 |
| `merchant_return_address` | 商家退货地址表       | 售后模块   |
| `refund_operation_log`    | 售后操作日志表       | 售后模块   |
| `favorite`                | 商品收藏表           | 收藏模块   |
| `browse_history`          | 浏览历史表           | 浏览模块   |
| `merchant`                | 商家表               | 商家模块   |
| `promotion`               | 促销活动表           | 促销模块   |
| `promotion_product`       | 促销商品关联表       | 促销模块   |
| `coupon`                  | 优惠券表             | 促销模块   |
| `user_coupon`             | 用户优惠券领取记录表 | 促销模块   |
| `admin`                   | 管理员表             | 管理员模块 |
| `system_config`           | 系统配置表           | 管理员模块 |
| `operation_log`           | 操作日志表           | 系统模块   |

---

## 项目结构

```
merged-project/
│
├── backend/                        # 后端项目（Spring Boot）
│   ├── pom.xml                    # Maven依赖配置文件
│   ├── target/                    # Maven编译输出目录
│   └── src/main/
│       ├── java/com/mall/
│       │   ├── MallApplication.java # Spring Boot启动类
│       │   ├── common/             # 公共工具模块
│       │   │   ├── Result.java     # 统一响应结果封装类
│       │   │   ├── PageResult.java # 分页结果封装类
│       │   │   ├── JwtUtil.java    # JWT令牌生成与解析工具
│       │   │   └── PasswordUtil.java # 密码加密工具类
│       │   ├── config/             # 配置类模块
│       │   │   ├── WebConfig.java  # Web配置（CORS跨域、拦截器注册）
│       │   │   ├── JwtInterceptor.java # JWT认证拦截器
│       │   │   ├── CorsFilter.java # 跨域请求过滤器
│       │   │   ├── GlobalExceptionHandler.java # 全局统一异常处理器
│       │   │   └── DataInitializer.java # 初始化数据加载器
│       │   └── module/             # 业务模块包
│       │       ├── user/           # 用户模块（注册、登录、会员、积分）
│       │       │   ├── controller/  # UserController
│       │       │   ├── dto/         # LoginDTO, RegisterDTO, AddressDTO等
│       │       │   ├── entity/      # User, MemberInfo, PointsRecord等
│       │       │   ├── mapper/      # UserMapper, MemberInfoMapper等
│       │       │   └── service/     # UserService及实现类
│       │       ├── order/          # 订单模块（购物车、订单、支付）
│       │       │   ├── controller/  # CartController, OrderController, PaymentController
│       │       │   ├── entity/      # Cart, Order, OrderItem, Payment
│       │       │   ├── mapper/      # CartMapper, OrderMapper等
│       │       │   └── service/     # CartService, OrderService, PaymentService
│       │       ├── product/        # 商品模块（商品、分类、评价、退换货）
│       │       │   ├── controller/  # ApiController, MerchantProductController等
│       │       │   ├── dto/         # MerchantRegisterDTO等
│       │       │   ├── entity/      # Product, Category, Review, ReturnRequest等
│       │       │   ├── mapper/      # ProductMapper, ReviewMapper等
│       │       │   └── service/     # ProductService, ReviewService, ReturnRequestService等
│       │       └── admin/          # 管理员模块（商家审核、系统配置）
│       │           ├── controller/  # AdminController
│       │           ├── entity/      # Admin, SystemConfig
│       │           ├── mapper/      # AdminMapper, SystemConfigMapper
│       │           └── service/     # AdminService
│       └── resources/
│           ├── application.yml       # Spring Boot应用配置文件
│           ├── schema-add-columns.sql # 补充字段SQL脚本
│           ├── schema-additional.sql  # 额外数据库结构SQL脚本
│           └── mapper/              # MyBatis XML映射文件目录
│
├── frontend/                       # 前端项目（Vue 3）
│   ├── package.json                # NPM依赖配置
│   ├── pnpm-lock.yaml             # PNPM锁文件
│   ├── pnpm-workspace.yaml         # PNPM工作区配置
│   ├── index.html                  # HTML入口文件
│   ├── vite.config.js             # Vite构建配置
│   ├── dist/                       # 构建产物目录
│   └── src/
│       ├── main.js                 # Vue应用入口文件
│       ├── App.vue                 # 根组件
│       ├── api/                    # API接口封装目录
│       │   ├── index.js            # API统一导出
│       │   ├── user.js             # 用户API
│       │   ├── product.js          # 商品API
│       │   ├── cart.js             # 购物车API
│       │   ├── order.js            # 订单API
│       │   ├── payment.js          # 支付API
│       │   └── merchant.js         # 商家API
│       ├── router/                 # 路由配置
│       │   └── index.js            # Vue Router路由配置
│       ├── stores/                 # Pinia状态管理
│       │   └── user.js             # 用户状态管理
│       ├── utils/                  # 工具函数
│       │   ├── request.js          # Axios请求封装
│       │   ├── auth.js             # 认证工具函数
│       │   └── merchantRequest.js  # 商家专用请求封装
│       ├── layout/                 # 布局组件
│       │   └── MerchantLayout.vue  # 商家后台布局
│       └── views/                  # 页面组件
│           ├── home/               # 首页模块
│           ├── user/               # 用户模块（Login, Register, Profile）
│           ├── product/            # 商品模块（ProductList, ProductDetail, Search, Category）
│           ├── cart/               # 购物车订单模块（Cart, Checkout, OrderList, OrderDetail, Pay, Address）
│           ├── review/             # 评价模块（List, Write）
│           ├── favorite/           # 收藏模块（List）
│           ├── history/            # 浏览历史模块（List）
│           ├── return/             # 退换货模块（List, Apply, Detail）
│           ├── admin/              # 管理员后台（Login, Dashboard, MerchantPending, Config, AdminList）
│           └── merchant/           # 商家后台（Login, Register, Dashboard, Products, Orders, Reviews, Returns, Promotions, Coupons, Statistics, etc.）
│
└── database/                       # 数据库脚本目录
    └── sql语句.sql                  # 完整数据库初始化脚本（建表语句、测试数据）
```

### 目录说明

| 目录                                     | 说明            | 重要文件                            |
| ---------------------------------------- | --------------- | ----------------------------------- |
| `backend/src/main/java/com/mall/common/` | 公共工具类      | Result.java, JwtUtil.java           |
| `backend/src/main/java/com/mall/config/` | 配置类          | WebConfig.java, JwtInterceptor.java |
| `backend/src/main/java/com/mall/module/` | 业务模块        | user/, order/, product/, admin/     |
| `backend/src/main/resources/mapper/`     | MyBatis映射文件 | \*Mapper.xml                        |
| `frontend/src/api/`                      | API接口封装     | user.js, product.js, order.js       |
| `frontend/src/router/`                   | 路由配置        | index.js                            |
| `frontend/src/views/`                    | 页面组件        | 各模块页面                          |
| `frontend/src/stores/`                   | Pinia状态管理   | user.js                             |
| `frontend/src/utils/`                    | 工具函数        | request.js, auth.js                 |

---

## 核心功能模块详解

### 1. 用户模块

#### 1.1 认证功能

| 功能      | 说明                                 |
| --------- | ------------------------------------ |
| 用户注册  | 支持用户名、密码注册，自动生成用户ID |
| 用户登录  | JWT令牌认证，登录成功后返回Token     |
| Token验证 | 请求拦截器自动验证Token有效性        |

#### 1.2 个人信息管理

| 功能     | 说明                           |
| -------- | ------------------------------ |
| 个人信息 | 查看和修改昵称、手机号、地址等 |
| 修改密码 | 验证原密码后修改新密码         |
| 收货地址 | 多地址管理、设置默认地址       |

#### 1.3 会员系统

| 功能     | 说明                                             |
| -------- | ------------------------------------------------ |
| 会员等级 | 普通会员、银卡会员、金卡会员、钻石会员、铂金会员 |
| 购买会员 | 选择会员类型、支付购买                           |
| 积分管理 | 购物获得积分、积分抵扣、积分记录查询             |
| 角色权限 | 用户角色查询与更新                               |

### 2. 购物车订单模块

#### 2.1 购物车功能

| 功能       | 说明                          |
| ---------- | ----------------------------- |
| 添加购物车 | 添加商品到购物车，支持选择SKU |
| 修改数量   | 修改商品购买数量              |
| 删除商品   | 从购物车移除商品              |
| 清空购物车 | 一键清空所有商品              |
| 商品选中   | 支持部分商品结算              |

#### 2.2 订单功能

| 功能     | 说明                                      |
| -------- | ----------------------------------------- |
| 创建订单 | 从购物车结算或直接购买                    |
| 订单状态 | 待付款→已付款→已发货→已收货→已完成/已取消 |
| 订单列表 | 按状态筛选、查看订单详情                  |
| 取消订单 | 用户主动取消订单                          |
| 确认收货 | 收到货物后确认收货                        |

#### 2.3 支付功能

| 功能     | 说明                         |
| -------- | ---------------------------- |
| 模拟支付 | 选择支付方式（模拟）完成支付 |
| 支付记录 | 记录支付信息、支付时间       |
| 退款处理 | 退换货时的退款记录           |

### 3. 商品评价模块

#### 3.1 商品浏览

| 功能     | 说明                            |
| -------- | ------------------------------- |
| 商品列表 | 分页展示、支持分类筛选、排序    |
| 商品搜索 | 关键词搜索、价格区间筛选        |
| 商品详情 | 展示商品信息、SKU选择、评价统计 |
| 推荐商品 | 首页推荐、猜你喜欢              |

#### 3.2 评价功能

| 功能     | 说明                                |
| -------- | ----------------------------------- |
| 发表评价 | 订单完成后可对商品进行评价（1-5星） |
| 评价图片 | 支持上传评价晒图                    |
| 追评功能 | 购买后可追加评价                    |
| 评价点赞 | 对评价进行点赞                      |

### 4. 退换货模块

| 功能       | 说明                               |
| ---------- | ---------------------------------- |
| 申请退换货 | 选择订单商品、填写原因、上传凭证   |
| 退换货类型 | 退款、退货退款、换货               |
| 状态追踪   | 待审核→已同意→待收货→已完成/已拒绝 |
| 物流填写   | 填写退货物流信息                   |

### 5. 收藏与历史

| 功能     | 说明                       |
| -------- | -------------------------- |
| 收藏商品 | 收藏/取消收藏商品          |
| 收藏列表 | 查看收藏商品、快速购买     |
| 浏览历史 | 自动记录浏览过的商品       |
| 历史管理 | 删除单条记录、清空全部历史 |

### 6. 商家后台

#### 6.1 商家入驻

| 功能     | 说明                                 |
| -------- | ------------------------------------ |
| 商家注册 | 提交入驻申请（店铺名称、联系方式等） |
| 商家登录 | JWT令牌认证                          |
| 店铺设置 | 修改店铺信息                         |

#### 6.2 商品管理

| 功能       | 说明                     |
| ---------- | ------------------------ |
| 商品发布   | 发布新商品、设置价格库存 |
| 商品编辑   | 修改商品信息、价格、库存 |
| 商品上下架 | 上架/下架商品            |
| SKU管理    | 配置商品规格、价格、库存 |
| 商品回收站 | 软删除、恢复、永久删除   |

#### 6.3 订单管理

| 功能     | 说明                   |
| -------- | ---------------------- |
| 订单查询 | 按状态、时间筛选订单   |
| 订单发货 | 填写物流公司、物流单号 |
| 订单统计 | 订单数量、金额统计     |

#### 6.4 评价管理

| 功能     | 说明                 |
| -------- | -------------------- |
| 查看评价 | 查看用户对商品的评价 |
| 回复评价 | 商家回复用户评价     |
| 申诉处理 | 处理用户评价申诉     |

#### 6.5 促销活动

| 功能     | 说明               |
| -------- | ------------------ |
| 满减活动 | 设置满X减Y规则     |
| 折扣活动 | 商品折扣、限时折扣 |
| 优惠券   | 创建、发放优惠券   |
| 促销活动 | 管理促销活动商品   |

#### 6.6 数据分析

| 功能     | 说明                   |
| -------- | ---------------------- |
| 经营概览 | 销售额、订单数、访客数 |
| 销售趋势 | 日/周/月销售趋势图表   |
| 商品排行 | 商品销量TOP排行        |
| 转化分析 | 浏览-下单转化率分析    |
| 库存预警 | 库存积压预警           |
| 财务报表 | 收入支出对账           |

### 7. 管理员后台

| 功能       | 说明                          |
| ---------- | ----------------------------- |
| 管理员登录 | 独立的管理后台入口            |
| 商家审核   | 审核商家入驻申请（通过/拒绝） |
| 系统配置   | 网站名称、Logo等系统参数      |
| 管理员管理 | 添加/禁用管理员账号           |

---

## 认证机制详解

### JWT Token认证流程

```
┌─────────┐                           ┌─────────┐                           ┌─────────┐
│  Client │                           │ Server  │                           │   DB    │
└────┬────┘                           └────┬────┘                           └────┬────┘
     │                                      │                                      │
     │  1. POST /api/user/login            │                                      │
     │  {username, password}                │                                      │
     │ ─────────────────────────────────►  │                                      │
     │                                      │                                      │
     │                                      │  2. 查询用户信息                      │
     │                                      │ ─────────────────────────────────►  │
     │                                      │                                      │
     │                                      │  3. 验证密码                         │
     │                                      │ ◄─────────────────────────────────  │
     │                                      │                                      │
     │                                      │  4. 生成JWT Token                    │
     │                                      │  (包含userId, username, role)        │
     │                                      │                                      │
     │  5. 返回 Token                       │
     │ ◄─────────────────────────────────  │
     │                                      │
     │  6. 后续请求携带Token                │
     │  Authorization: Bearer {token}       │
     │ ─────────────────────────────────►  │
     │                                      │
     │                                      │  7. JWT拦截器验证Token               │
     │                                      │                                      │
     │                                      │  8. 解析Token获取userId              │
     │                                      │                                      │
     │  9. 返回业务数据                      │
     │ ◄─────────────────────────────────  │
```

### Token类型与存储

| Token类型   | 存储位置                   | 用途               |
| ----------- | -------------------------- | ------------------ |
| 用户Token   | localStorage.token         | 普通用户身份认证   |
| 管理员Token | localStorage.adminToken    | 管理员后台身份认证 |
| 商家Token   | localStorage.merchantToken | 商家后台身份认证   |

### 拦截器配置

```java
// 需要认证的接口
/api/user/info, /api/user/info/**
/api/user/password, /api/user/address/**
/api/cart/**, /api/order/**, /api/pay/**
/api/favorites/**, /api/history/**
/api/reviews, /api/returns/**
/api/admin/**（除登录外）
/api/merchant/**（除登录注册外）

// 无需认证的公开接口
/api/user/register, /api/user/login
/api/products/**, /api/categories/**
/api/reviews/**（部分）
/api/admin/login
/api/merchant/register, /api/merchant/login
```

---

## API接口文档

### 基础配置

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: `Authorization: Bearer {token}`

### 用户模块接口

| 方法   | 路径                        | 说明             | 认证 |
| ------ | --------------------------- | ---------------- | ---- |
| POST   | /user/register              | 用户注册         | 否   |
| POST   | /user/login                 | 用户登录         | 否   |
| GET    | /user/info                  | 获取用户信息     | 是   |
| PUT    | /user/info                  | 更新用户信息     | 是   |
| PUT    | /user/password              | 修改密码         | 是   |
| GET    | /user/address               | 获取收货地址列表 | 是   |
| POST   | /user/address               | 添加收货地址     | 是   |
| PUT    | /user/address/{id}          | 更新收货地址     | 是   |
| DELETE | /user/address/{id}          | 删除收货地址     | 是   |
| PUT    | /user/address/{id}/default  | 设置默认地址     | 是   |
| GET    | /user/member                | 获取会员信息     | 是   |
| GET    | /user/member/types          | 获取会员类型列表 | 否   |
| POST   | /user/member/purchase       | 购买会员         | 是   |
| POST   | /user/member/points         | 增加积分         | 是   |
| GET    | /user/member/points/history | 获取积分历史     | 是   |

### 商品模块接口

| 方法   | 路径                      | 说明                       | 认证 |
| ------ | ------------------------- | -------------------------- | ---- |
| GET    | /products                 | 获取商品列表（分页、筛选） | 否   |
| GET    | /products/{id}            | 获取商品详情               | 否   |
| GET    | /products/search          | 搜索商品                   | 否   |
| GET    | /products/recommended     | 获取推荐商品               | 否   |
| GET    | /categories               | 获取分类树                 | 否   |
| GET    | /categories/{id}/products | 获取分类商品               | 否   |
| GET    | /reviews                  | 获取评价列表               | 否   |
| POST   | /reviews                  | 提交评价                   | 是   |
| POST   | /reviews/{id}/append      | 追加评价                   | 是   |
| POST   | /reviews/{id}/like        | 点赞评价                   | 是   |
| POST   | /favorites/toggle         | 收藏/取消收藏              | 是   |
| GET    | /favorites                | 获取收藏列表               | 是   |
| DELETE | /favorites/{id}           | 删除收藏                   | 是   |
| GET    | /history                  | 获取浏览历史               | 是   |
| DELETE | /history/{id}             | 删除浏览记录               | 是   |
| DELETE | /history                  | 清空浏览历史               | 是   |

### 购物车订单接口

| 方法   | 路径                  | 说明           | 认证 |
| ------ | --------------------- | -------------- | ---- |
| GET    | /cart/list            | 获取购物车列表 | 是   |
| POST   | /cart/add             | 添加购物车     | 是   |
| PUT    | /cart/update          | 更新购物车     | 是   |
| DELETE | /cart/delete/{id}     | 删除购物车商品 | 是   |
| DELETE | /cart/clear           | 清空购物车     | 是   |
| POST   | /order/create         | 创建订单       | 是   |
| GET    | /order/list           | 获取订单列表   | 是   |
| GET    | /order/detail/{id}    | 获取订单详情   | 是   |
| PUT    | /order/{id}/cancel    | 取消订单       | 是   |
| PUT    | /order/{id}/confirm   | 确认收货       | 是   |
| POST   | /pay/create           | 创建支付       | 是   |
| GET    | /pay/detail/{orderId} | 获取支付详情   | 是   |

### 退换货接口

| 方法 | 路径                   | 说明           | 认证 |
| ---- | ---------------------- | -------------- | ---- |
| GET  | /returns               | 获取退换货列表 | 是   |
| GET  | /returns/{id}          | 获取退换货详情 | 是   |
| POST | /returns               | 提交退换货申请 | 是   |
| POST | /returns/{id}/shipping | 填写物流信息   | 是   |

### 商家模块接口

| 方法   | 路径                           | 说明         | 认证 |
| ------ | ------------------------------ | ------------ | ---- |
| POST   | /merchant/register             | 商家注册     | 否   |
| POST   | /merchant/login                | 商家登录     | 否   |
| GET    | /merchant/info                 | 获取商家信息 | 是   |
| PUT    | /merchant/info                 | 更新商家信息 | 是   |
| GET    | /merchant/dashboard            | 仪表盘数据   | 是   |
| GET    | /merchant/products             | 获取商品列表 | 是   |
| POST   | /merchant/products             | 添加商品     | 是   |
| PUT    | /merchant/products/{id}        | 更新商品     | 是   |
| DELETE | /merchant/products/{id}        | 删除商品     | 是   |
| GET    | /merchant/orders               | 获取订单列表 | 是   |
| PUT    | /merchant/orders/{id}/ship     | 发货         | 是   |
| GET    | /merchant/reviews              | 获取评价列表 | 是   |
| POST   | /merchant/reviews/{id}/reply   | 回复评价     | 是   |
| GET    | /merchant/promotions           | 获取促销活动 | 是   |
| POST   | /merchant/promotions           | 创建促销活动 | 是   |
| GET    | /merchant/coupons              | 获取优惠券   | 是   |
| POST   | /merchant/coupons              | 创建优惠券   | 是   |
| GET    | /merchant/statistics           | 获取统计数据 | 是   |
| GET    | /merchant/returns              | 获取退换货   | 是   |
| PUT    | /merchant/returns/{id}/approve | 同意退换货   | 是   |
| PUT    | /merchant/returns/{id}/reject  | 拒绝退换货   | 是   |

### 管理员模块接口

| 方法   | 路径                         | 说明           | 认证 |
| ------ | ---------------------------- | -------------- | ---- |
| POST   | /admin/login                 | 管理员登录     | 否   |
| GET    | /admin/info                  | 获取管理员信息 | 是   |
| GET    | /admin/list                  | 获取管理员列表 | 是   |
| POST   | /admin/add                   | 添加管理员     | 是   |
| DELETE | /admin/{id}                  | 删除管理员     | 是   |
| GET    | /admin/merchants             | 获取商家列表   | 是   |
| PUT    | /admin/merchants/{id}/status | 审核商家       | 是   |
| GET    | /admin/configs               | 获取系统配置   | 是   |
| PUT    | /admin/configs/{id}          | 更新系统配置   | 是   |
| POST   | /admin/configs               | 添加系统配置   | 是   |
| DELETE | /admin/configs/{id}          | 删除系统配置   | 是   |

---

## 项目启动指南

### 环境要求

| 软件     | 版本要求 | 说明         |
| -------- | -------- | ------------ |
| JDK      | 1.8+     | 后端运行环境 |
| Maven    | 3.6+     | 后端构建工具 |
| MySQL    | 8.0+     | 数据库       |
| Node.js  | 18+      | 前端运行环境 |
| npm/pnpm | 最新版   | 前端包管理器 |

---

### 详细启动步骤

#### 第一步：数据库配置

##### 方式一：使用MySQL Workbench导入

1. 打开MySQL Workbench，连接到本地MySQL服务器
2. 执行以下SQL创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS online_mall DEFAULT CHARACTER SET utf8mb4;
```

1. 在MySQL Workbench中打开 [database/create_database.sql](database/create_database.sql) 文件
2. 选择 `online_mall` 数据库，执行整个SQL脚本

##### 方式二：使用命令行导入

```bash
# 1. 进入数据库脚本目录
cd database

# 2. 登录MySQL并执行脚本
mysql -u root -p
# 输入密码后，执行：
source database/create_database.sql;

# 或直接从命令行导入：
mysql -u root -p online_mall < create_database.sql
```

##### 配置数据库连接

修改后端配置文件 [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_mall?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456 # 修改为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

#### 第二步：启动后端服务

##### 方式一：使用Maven命令启动（推荐）

```bash
# 1. 进入后端项目根目录
cd backend

# 2. 清理并编译项目（首次运行或代码修改后执行）
mvn clean install -DskipTests

# 3. 启动Spring Boot应用
mvn spring-boot:run
```

##### 方式二：直接运行JAR包

```bash
# 1. 打包项目
mvn clean package -DskipTests

# 2. 运行jar包
java -jar target/online-mall-merged-1.0.0.jar
```

##### 方式三：使用IDE启动（IntelliJ IDEA）

1. 使用IDEA打开 `backend` 目录
2. 等待Maven依赖下载完成
3. 找到启动类 [MallApplication.java](backend/src/main/java/com/mall/MallApplication.java)
4. 右键点击 `main` 方法，选择 "Run 'MallApplication'"

**后端启动成功标志：**

```
========================================
  在线商城系统已启动！
  访问地址: http://localhost:8080
========================================
```

---

#### 第三步：启动前端服务

##### 方式一：使用npm启动

```bash
# 1. 进入前端项目目录
cd frontend

# 2. 安装依赖（首次运行）
npm install

# 3. 启动开发服务器
npm run dev
```

##### 方式二：使用pnpm启动

```bash
# 1. 安装pnpm（如未安装）
npm install -g pnpm

# 2. 安装依赖
pnpm install

# 3. 启动开发服务器
pnpm run dev
```

##### 方式三：使用VS Code启动

1. 使用VS Code打开 `frontend` 目录
2. 打开终端（Terminal）
3. 执行 `npm install` 安装依赖
4. 执行 `npm run dev` 启动服务

**前端启动成功标志：**

```
  VITE v8.0.12  ready in xxx ms

  ➜  Local:   http://localhost:5173/
```

---

#### 第四步：访问系统

| 入口       | 地址                                   | 说明           |
| ---------- | -------------------------------------- | -------------- |
| 前台首页   | <http://localhost:5173>                | 顾客购物入口   |
| 管理员后台 | <http://localhost:5173/admin/login>    | 系统管理员入口 |
| 商家后台   | <http://localhost:5173/merchant-login> | 商家管理入口   |

---

### 启动注意事项

#### 1. 数据库相关

- **MySQL服务必须启动**：确保本地MySQL服务正在运行
- **数据库字符集**：必须使用 `utf8mb4` 字符集，支持emoji表情
- **密码配置**：务必检查 [application.yml](backend/src/main/resources/application.yml) 中的数据库密码是否正确
- **数据导入顺序**：确保先创建数据库，再导入SQL脚本

#### 2. 后端相关

- **端口占用**：确保8080端口未被其他程序占用（如Nginx、Tomcat等）
- **依赖下载**：首次运行Maven需要下载依赖，请确保网络连接正常
- **Tomcat临时目录**：项目已配置 `server.tomcat.basedir`，首次启动会自动创建临时目录
- **日志输出**：启动过程中如遇错误，请查看控制台日志输出

#### 3. 前端相关

- **端口占用**：确保5173端口未被占用，如需修改端口，请编辑 [vite.config.js](frontend/vite.config.js)
- **依赖安装**：国内网络环境下，建议配置npm淘宝镜像源

```bash
# 配置淘宝镜像
npm config set registry https://registry.npmmirror.com
```

- **浏览器兼容性**：建议使用Chrome、Firefox、Edge等现代浏览器

#### 4. 常见问题解决

| 问题              | 可能原因              | 解决方案                                                                                 |
| ----------------- | --------------------- | ---------------------------------------------------------------------------------------- |
| 数据库连接失败    | 密码错误或MySQL未启动 | 检查MySQL服务状态、验证密码正确性                                                        |
| 8080端口被占用    | 其他程序占用端口      | 修改 [application.yml](backend/src/main/resources/application.yml) 中的端口号            |
| Maven依赖下载失败 | 网络问题或仓库源配置  | 配置Maven阿里云镜像源                                                                    |
| 前端页面空白      | 依赖未安装或启动失败  | 重新执行 `npm install` 和 `npm run dev`                                                  |
| 跨域错误          | CORS配置问题          | 检查 [WebConfig.java](backend/src/main/java/com/mall/config/WebConfig.java) 中的跨域配置 |

---

## 测试账号

### 普通用户账号

| 用户名 | 密码   | 昵称     |
| ------ | ------ | -------- |
| user1  | 123456 | 小明同学 |
| user2  | 123456 | 小红同学 |
| user3  | 123456 | 小张同学 |

### 商家账号

| 用户名         | 密码   | 店铺名称   |
| -------------- | ------ | ---------- |
| shuma_shop     | 123456 | 数码旗舰店 |
| fushi_shop     | 123456 | 服饰专营店 |
| meizhuang_shop | 123456 | 美妆官方店 |
| jiaju_shop     | 123456 | 家居生活馆 |
| muying_shop    | 123456 | 母婴用品店 |

### 管理员账号

| 用户名     | 密码   | 角色       |
| ---------- | ------ | ---------- |
| 超级管理员 | 123456 | 系统管理员 |

---

## 项目亮点

### 1. 前后端分离架构

- 后端提供RESTful API接口
- 前端Vue 3单页应用
- 便于前后端独立开发和部署

### 2. 完善的权限控制

- JWT Token无状态认证
- 多角色权限管理（用户、商家、管理员）
- 接口级别权限拦截

### 3. 丰富的业务功能

- 完整的电商购物流程
- 商家后台管理系统
- 会员积分体系
- 促销营销功能
- 数据统计分析

### 4. 规范的代码结构

- 模块化分层架构
- 统一响应格式
- 全局异常处理
- 完整的注释文档

### 5. 优化的用户体验

- 页面路由懒加载
- 响应式设计
- 友好的错误提示
- 流畅的交互体验

---

## 开发规范

### 后端规范

**包结构规范**：

```
com.mall
├── common/         # 公共组件（工具类、响应封装）
├── config/         # 配置类（跨域、拦截器、异常处理）
└── module/         # 业务模块
    ├── user/       # 用户模块（注册、登录、会员、积分）
    ├── order/      # 订单模块（购物车、订单、支付）
    ├── product/    # 商品模块（商品、分类、评价、退换货）
    └── admin/      # 管理员模块（商家审核、系统配置）
```

**命名规范**：

- 包名：小写字母，如 `com.mall.module.user`
- 类名：大驼峰，如 `UserController`、`UserService`
- 方法名：小驼峰，如 `getUserInfo()`、`createOrder()`
- 常量：大写下划线，如 `MAX_PAGE_SIZE`、`JWT_EXPIRATION`
- 数据库表/字段：下划线命名，如 `user_address`、`order_item`
- DTO类：以DTO结尾，如 `LoginDTO`、`RegisterDTO`

**响应格式**：

后端统一使用 `Result` 类封装响应：

```java
{
    "code": 200,          // 状态码，200表示成功
    "message": "success", // 提示消息
    "data": { }           // 响应数据
}
```

**异常处理**：

- 使用 `GlobalExceptionHandler` 统一处理异常
- 自定义业务异常继承 `RuntimeException`
- 返回统一的错误响应格式

### 前端规范

**文件命名**：

- Vue组件：大驼峰，如 `UserLogin.vue`、`ProductList.vue`
- JS/CSS文件：短横线分隔，如 `user-request.js`、`global.css`
- 工具函数：小驼峰，如 `request.js`、`auth.js`

**目录结构**：

```
src/
├── api/            # API接口封装（按模块划分）
├── assets/         # 静态资源（图片、样式）
├── components/     # 公共组件（可复用组件）
├── router/         # 路由配置（index.js）
├── stores/         # Pinia状态管理
├── utils/          # 工具函数（请求封装、认证工具）
├── layout/         # 布局组件（商家后台布局等）
└── views/          # 页面组件（按模块划分）
```

**代码风格**：

- 使用ES6+语法
- 组件命名使用 PascalCase
- 函数和变量命名使用 camelCase
- 使用 `const`/`let` 替代 `var`
- 异步操作使用 `async/await`

---

## 部署注意事项

### 开发环境注意事项

1. **数据库配置**：首次使用请确认 [application.yml](backend/src/main/resources/application.yml) 中的数据库密码配置正确（默认密码：`123456`）
2. **JWT密钥**：生产环境请修改配置文件中的 `jwt.secret` 并妥善保管
3. **跨域配置**：当前配置允许所有来源，生产环境请在 [WebConfig.java](backend/src/main/java/com/mall/config/WebConfig.java) 中限制具体域名
4. **文件上传**：默认限制最大10MB文件上传，如需调整请修改 `application.yml` 中的 `spring.servlet.multipart` 配置

### 生产环境部署建议

1. **数据库优化**：
   - 为频繁查询的字段创建索引
   - 配置数据库连接池参数
   - 定期备份数据库

2. **后端部署**：
   - 使用 `mvn clean package -DskipTests` 打包
   - 使用 `java -jar` 或 Docker 容器部署
   - 配置JVM参数优化性能

3. **前端部署**：
   - 使用 `pnpm run build` 构建生产版本
   - 使用Nginx或CDN托管静态资源
   - 配置gzip压缩和缓存策略

4. **安全配置**：
   - 使用HTTPS协议
   - 配置防火墙规则
   - 定期更新依赖版本
   - 端口占用：后端默认8080端口，前端默认5173端口，如需修改请同步更新相关配置
   - 测试数据：数据库脚本包含完整的测试数据，可直接登录体验

### 生产环境部署建议

| 优化项     | 说明                                    |
| ---------- | --------------------------------------- |
| 数据库优化 | 添加索引、配置读写分离、定期备份        |
| 安全加固   | 配置HTTPS、修改JWT密钥、限制API访问频率 |
| 性能优化   | 集成Redis缓存、配置CDN、启用Gzip压缩    |
| 日志监控   | 配置日志收集、性能监控和告警机制        |
| 容器化部署 | 使用Docker+K8s进行容器化部署            |

### 安全注意事项

1. **不要提交敏感信息**：不要将包含真实密码、密钥的配置文件提交到代码仓库
2. **定期更新依赖**：定期更新Maven和npm依赖，修复安全漏洞
3. **权限控制**：生产环境使用专用的数据库账号，遵循最小权限原则
4. **HTTPS**：生产环境必须使用HTTPS协议，防止数据被窃取或篡改

---

## 后续优化建议

| 优化项     | 说明                                                    | 优先级 |
| ---------- | ------------------------------------------------------- | ------ |
| Redis缓存  | 使用Redis缓存热点数据（商品列表、分类等），提高访问速度 | 高     |
| 消息队列   | 使用RabbitMQ处理异步任务（如订单超时取消、积分发放）    | 高     |
| 对象存储   | 集成阿里云OSS或七牛云存储商品图片，减轻服务器压力       | 高     |
| 搜索引擎   | 集成ElasticSearch实现全文搜索，支持模糊搜索和排序       | 中     |
| 日志系统   | 集成ELK日志分析平台，实现日志收集和分析                 | 中     |
| 监控告警   | 集成Prometheus + Grafana监控，实时监控系统状态          | 中     |
| Docker部署 | 提供Dockerfile和docker-compose.yml，支持一键部署        | 中     |
| 微服务拆分 | 拆分为用户服务、商品服务、订单服务等独立微服务          | 低     |
| 分布式锁   | 使用Redis实现分布式锁，保证数据一致性                   | 中     |
| 接口限流   | 集成Sentinel或Resilience4j实现接口限流熔断              | 中     |

### 优化说明

1. **Redis缓存**：缓存商品列表、分类树、热门搜索等高频访问数据
2. **消息队列**：解耦订单超时取消、积分发放、邮件通知等异步操作
3. **对象存储**：将商品图片、评价图片存储到云存储服务
4. **搜索引擎**：提供更精准的商品搜索体验
5. **监控告警**：实时监控系统性能指标，及时发现问题

---

## 许可证

MIT License

---

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 GitHub Issue
- 发送邮件至项目维护者

---

**© 2024 在线商城系统 - Spring Boot + Vue3 项目**
