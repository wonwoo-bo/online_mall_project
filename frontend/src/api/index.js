import request from '@/utils/request'

// ==================== 用户相关 ====================

// 用户注册
export function register(data) {
  return request.post('/user/register', data)
}

// 用户登录
export function login(data) {
  return request.post('/user/login', data)
}

// 获取用户信息
export function getUserInfo() {
  return request.get('/user/info')
}

// 更新用户信息
export function updateUserInfo(data) {
  return request.put('/user/info', data)
}

// 修改密码
export function updatePassword(data) {
  return request.put('/user/password', data)
}

// 获取收货地址列表
export function getAddressList() {
  return request.get('/user/address')
}

// 添加收货地址
export function addAddress(data) {
  return request.post('/user/address', data)
}

// 更新收货地址
export function updateAddress(id, data) {
  return request.put(`/user/address/${id}`, data)
}

// 删除收货地址
export function deleteAddress(id) {
  return request.delete(`/user/address/${id}`)
}

// 设置默认地址
export function setDefaultAddress(id) {
  return request.put(`/user/address/${id}/default`)
}

// ==================== 会员积分相关 ====================

// 获取会员信息
export function getMemberInfo() {
  return request.get('/user/member')
}

// 获取所有会员类型
export function getMemberTypes() {
  return request.get('/user/member/types')
}

// 购买会员
export function purchaseMember(levelCode) {
  return request.post('/user/member/purchase', { levelCode })
}

// 添加积分
export function addPoints(points) {
  return request.post('/user/member/points', { points })
}

// 获取积分历史
export function getPointsHistory() {
  return request.get('/user/member/points/history')
}

// ==================== 商品相关 ====================

// 获取商品列表
export function getProducts(params) {
  return request.get('/products', { params })
}

// 获取商品详情
export function getProductDetail(id, userId = 1) {
  return request.get(`/products/${id}`, { params: { userId } })
}

// 获取推荐商品
export function getRecommended(limit = 10) {
  return request.get('/products/recommended', { params: { limit } })
}

// 搜索商品
export function searchProducts(keyword, page = 1, size = 20) {
  return request.get('/products/search', { params: { keyword, page, size } })
}

// ==================== 分类相关 ====================

// 获取分类树
export function getCategories() {
  return request.get('/categories')
}

// 获取分类下的商品
export function getCategoryProducts(categoryId, page = 1, size = 20) {
  return request.get(`/categories/${categoryId}/products`, { params: { page, size } })
}

// ==================== 评价相关 ====================

// 获取评价列表
export function getReviews(productId, params = {}) {
  return request.get('/reviews', { params: { productId, ...params } })
}

// 发布评价
export function submitReview(data) {
  return request.post('/reviews', data)
}

// 追加评价
export function appendReview(id, content) {
  return request.post(`/reviews/${id}/append`, { content })
}

// 点赞评价
export function likeReview(id, userId) {
  return request.post(`/reviews/${id}/like`, { userId })
}

// 我的评价列表
export function getMyReviews(userId) {
  return request.get('/my/reviews', { params: { userId } })
}

// 扣减积分
export function deductPoints(userId, points, description) {
  return request.post('/user/points/deduct', { userId, points, description })
}

// ==================== 收藏相关 ====================

// 收藏/取消收藏
export function toggleFavorite(userId, productId, action) {
  return request.post('/favorites/toggle', { userId, productId, action })
}

// 检查收藏状态
export function checkFavorite(userId, productId) {
  return request.get('/favorites/check', { params: { userId, productId } })
}

// 获取收藏列表
export function getFavorites(userId, page = 1, size = 20) {
  return request.get('/favorites', { params: { userId, page, size } })
}

// 删除收藏
export function deleteFavorite(id, userId = 1) {
  return request.delete(`/favorites/${id}`, { params: { userId } })
}

// ==================== 浏览历史 ====================

// 获取浏览历史
export function getHistory(userId, page = 1, size = 20) {
  return request.get('/history', { params: { userId, page, size } })
}

// 删除浏览记录
export function deleteHistory(id) {
  return request.delete(`/history/${id}`)
}

// 清空浏览历史
export function clearHistory(userId = 1) {
  return request.delete('/history', { params: { userId } })
}

// ==================== 购物车相关 ====================

export const getCartList = () => request.get('/cart/list')

export const addCart = (data) => request.post('/cart/add', data)

export const updateCart = (data) => request.put('/cart/update', data)

export const deleteCart = (id) => request.delete(`/cart/delete/${id}`)

export const clearCart = () => request.delete('/cart/clear')

// ==================== 订单相关 ====================

export const createOrder = (data) => request.post('/order/create', data)

export const getOrderDetail = (id) => request.get(`/order/detail/${id}`)

export const getOrderList = (params) => request.get('/order/list', { params })

export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)

export const confirmReceive = (id) => request.put(`/order/${id}/confirm`)

export const updateOrderStatus = (id, status) => request.put(`/order/${id}/status`, { status })

// ==================== 支付相关 ====================

export const createPayment = (data) => request.post('/pay/create', data)

export const getPaymentDetail = (orderId) => request.get(`/pay/detail/${orderId}`)

// ==================== 退换货 ====================

// 获取退换货列表
export function getReturns(userId, status) {
  return request.get('/returns', { params: { userId, status } })
}

// 获取退换货详情
export function getReturnDetail(id) {
  return request.get(`/returns/${id}`)
}

// 提交退换货申请
export function submitReturn(data) {
  return request.post('/returns', data)
}

// 填写物流信息
export function submitShipping(id, logisticsCompany, logisticsNo) {
  return request.post(`/returns/${id}/shipping`, { logisticsCompany, logisticsNo })
}

// 取消退换货
export function cancelReturn(id) {
  return request.put(`/returns/${id}/cancel`)
}

// ==================== 管理员相关 ====================

// 管理员登录
export function adminLogin(data) {
  return request.post('/admin/login', data)
}

// 获取管理员信息
export function getAdminInfo() {
  return request.get('/admin/info')
}

// 获取管理员列表
export function getAdminList() {
  return request.get('/admin/list')
}

// 添加管理员
export function addAdmin(data) {
  return request.post('/admin/add', data)
}

// 删除管理员
export function deleteAdmin(id) {
  return request.delete(`/admin/${id}`)
}

// 更新管理员信息
export function updateAdminInfo(data) {
  return request.put('/admin/info', data)
}

// 更新管理员密码
export function updateAdminPassword(data) {
  return request.put('/admin/password', data)
}

// 获取商家列表
export function getMerchantList(status) {
  return request.get('/admin/merchants', { params: { status } })
}

// 审核商家
export function approveMerchant(id, status) {
  return request.put(`/admin/merchants/${id}/status`, { status })
}

// 获取系统配置列表
export function getConfigList(category) {
  return request.get('/admin/configs', { params: { category } })
}

// 获取单个配置
export function getConfigByKey(key) {
  return request.get(`/admin/configs/${key}`)
}

// 更新配置
export function updateConfig(id, value) {
  return request.put(`/admin/configs/${id}`, { configValue: value })
}

// 添加配置
export function addConfig(data) {
  return request.post('/admin/configs', data)
}

// 删除配置
export function deleteConfig(id) {
  return request.delete(`/admin/configs/${id}`)
}

// 首页Banner管理
export function getActiveBanners() {
  return request.get('/admin/banners/active')
}
