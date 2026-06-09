import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn, isAdminLoggedIn } from '../utils/auth.js'

const routes = [
  // 首页
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Home.vue')
  },

  // 用户模块
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/user/Register.vue') },
  { path: '/profile', name: 'Profile', component: () => import('@/views/user/Profile.vue'), meta: { requiresAuth: true } },

  // 商品模块
  { path: '/products', name: 'ProductList', component: () => import('@/views/product/ProductList.vue') },
  { path: '/products/:id', name: 'ProductDetail', component: () => import('@/views/product/ProductDetail.vue'), meta: { hideFooter: true } },
  { path: '/search', name: 'Search', component: () => import('@/views/product/Search.vue') },
  { path: '/category', name: 'Category', component: () => import('@/views/product/Category.vue'), meta: { hideFooter: true } },

  // 购物车订单模块
  { path: '/cart', name: 'Cart', component: () => import('@/views/cart/Cart.vue'), meta: { requiresAuth: true, hideFooter: true } },
  { path: '/checkout', name: 'Checkout', component: () => import('@/views/cart/Checkout.vue'), meta: { requiresAuth: true, hideHeader: true } },
  { path: '/orders', name: 'OrderList', component: () => import('@/views/cart/OrderList.vue'), meta: { requiresAuth: true, fullWidth: true } },
  { path: '/orders/:id', name: 'OrderDetail', component: () => import('@/views/cart/OrderDetail.vue'), meta: { requiresAuth: true, hideHeader: true } },
  { path: '/pay', name: 'Pay', component: () => import('@/views/cart/Pay.vue'), meta: { requiresAuth: true, hideHeader: true } },
  { path: '/address', name: 'Address', component: () => import('@/views/cart/Address.vue'), meta: { requiresAuth: true, hideHeader: true } },

  // 评价模块
  { path: '/review/:productId', name: 'ReviewList', component: () => import('@/views/review/List.vue') },
  { path: '/review/write', name: 'ReviewWrite', component: () => import('@/views/review/Write.vue'), meta: { requiresAuth: true } },
  { path: '/my/reviews', name: 'MyReviews', component: () => import('@/views/review/MyReviews.vue'), meta: { requiresAuth: true } },

  // 收藏模块
  { path: '/favorite', name: 'Favorite', component: () => import('@/views/favorite/List.vue'), meta: { requiresAuth: true } },

  // 浏览历史模块
  { path: '/history', name: 'History', component: () => import('@/views/history/List.vue'), meta: { requiresAuth: true } },

  // 退换货模块
  { path: '/return', name: 'ReturnList', component: () => import('@/views/return/List.vue'), meta: { requiresAuth: true } },
  { path: '/return/apply', name: 'ReturnApply', component: () => import('@/views/return/Apply.vue'), meta: { requiresAuth: true } },
  { path: '/return/:id', name: 'ReturnDetail', component: () => import('@/views/return/Detail.vue'), meta: { requiresAuth: true } },

  // 管理员登录
  { path: '/admin/login', name: 'AdminLogin', component: () => import('@/views/admin/Login.vue') },

  // 商家登录
  { path: '/merchant-login', name: 'MerchantLogin', component: () => import('@/views/merchant/Login.vue') },
  { path: '/merchant-register', name: 'MerchantRegister', component: () => import('@/views/merchant/Register.vue') },

  // 商家后台
  {
    path: '/merchant',
    name: 'MerchantHome',
    component: () => import('@/layout/MerchantLayout.vue'),
    meta: { requiresMerchant: true },
    redirect: '/merchant/dashboard',
    children: [
      { path: 'dashboard', name: 'MerchantDashboard', component: () => import('@/views/merchant/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'products', name: 'MerchantProducts', component: () => import('@/views/merchant/Products.vue'), meta: { title: '商品管理' } },
      { path: 'products/add', name: 'MerchantAddProduct', component: () => import('@/views/merchant/AddProduct.vue'), meta: { title: '添加商品' } },
      { path: 'products/edit/:id', name: 'MerchantEditProduct', component: () => import('@/views/merchant/EditProduct.vue'), meta: { title: '编辑商品' } },
      { path: 'brands', name: 'MerchantBrands', component: () => import('@/views/merchant/Brand.vue'), meta: { title: '品牌管理' } },
      { path: 'specs', name: 'MerchantSpecs', component: () => import('@/views/merchant/Spec.vue'), meta: { title: '规格管理' } },
      { path: 'orders', name: 'MerchantOrders', component: () => import('@/views/merchant/Orders.vue'), meta: { title: '订单中心管理' } },
      { path: 'reviews', name: 'MerchantReviews', component: () => import('@/views/merchant/Reviews.vue'), meta: { title: '评价互动管理' } },
      { path: 'refunds', name: 'MerchantRefunds', component: () => import('@/views/merchant/Refunds.vue'), meta: { title: '售后退款管理' } },
      { path: 'settings', name: 'MerchantSettings', component: () => import('@/views/merchant/Settings.vue'), meta: { title: '商家账号与店铺管理' } },
      { path: 'promotions', name: 'MerchantPromotions', component: () => import('@/views/merchant/Promotions.vue'), meta: { title: '促销活动' } },
      { path: 'coupons', name: 'MerchantCoupons', component: () => import('@/views/merchant/Coupons.vue'), meta: { title: '优惠券管理' } },
      { path: 'product-ranking', name: 'ProductRanking', component: () => import('@/views/merchant/ProductRanking.vue'), meta: { title: '商品销量排行' } },
      { path: 'product-conversion', name: 'ProductConversion', component: () => import('@/views/merchant/ProductConversion.vue'), meta: { title: '商品转化数据分析' } },
      { path: 'inventory-warning', name: 'InventoryWarning', component: () => import('@/views/merchant/InventoryWarning.vue'), meta: { title: '库存积压预警统计' } },
      { path: 'financial-report', name: 'FinancialReport', component: () => import('@/views/merchant/FinancialReport.vue'), meta: { title: '财务对账报表' } },
      { path: 'recycle', name: 'MerchantRecycle', component: () => import('@/views/merchant/Recycle.vue'), meta: { title: '回收站' } }
    ]
  },

  // 管理员后台
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('@/views/admin/Home.vue'),
    meta: { requiresAdmin: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'merchant/pending', name: 'MerchantPending', component: () => import('@/views/admin/MerchantPending.vue') },
      { path: 'merchant/approved', name: 'MerchantApproved', component: () => import('@/views/admin/MerchantApproved.vue') },
      { path: 'config', name: 'Config', component: () => import('@/views/admin/Config.vue') },
      { path: 'admins', name: 'AdminList', component: () => import('@/views/admin/AdminList.vue') },
      { path: 'banners', name: 'BannerManage', component: () => import('@/views/admin/BannerManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 排除管理员登录、商家登录和商家注册页面的守卫检查
  if (to.path === '/admin/login' || to.path === '/merchant-login' || to.path === '/merchant-register') {
    next()
    return
  }

  if (to.meta.requiresAuth && !isLoggedIn()) {
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdminLoggedIn()) {
    next('/admin/login')
  } else if (to.meta.requiresMerchant && !localStorage.getItem('merchantToken')) {
    next('/merchant-login')
  } else {
    next()
  }
})

export default router
