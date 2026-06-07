import request from '../utils/request.js'

// 获取商品列表
export function getProducts(params) {
  return request.get('/products', { params })
}

// 获取商品详情
export function getProductDetail(id, userId = 1) {
  return request.get(`/products/${id}`, { params: { userId } })
}

// 搜索商品
export function searchProducts(params) {
  return request.get('/products/search', { params })
}

// 获取推荐商品
export function getRecommended(limit = 10) {
  return request.get('/products/recommended', { params: { limit } })
}

// 获取分类列表
export function getCategories() {
  return request.get('/categories')
}

// 获取分类商品
export function getCategoryProducts(id, params) {
  return request.get(`/categories/${id}/products`, { params })
}

// 获取评价列表
export function getReviews(params) {
  return request.get('/reviews', { params })
}

// 提交评价
export function submitReview(data) {
  return request.post('/reviews', data)
}

// 收藏/取消收藏
export function toggleFavorite(data) {
  return request.post('/favorites/toggle', data)
}

// 检查收藏状态
export function checkFavorite(userId, productId) {
  return request.get('/favorites/check', { params: { userId, productId } })
}

// 获取收藏列表
export function getFavorites(params) {
  return request.get('/favorites', { params })
}

// 删除收藏
export function deleteFavorite(id, userId) {
  return request.delete(`/favorites/${id}`, { params: { userId } })
}

// 获取浏览历史
export function getHistory(params) {
  return request.get('/history', { params })
}

// 删除浏览记录
export function deleteHistory(id) {
  return request.delete(`/history/${id}`)
}

// 清空浏览历史
export function clearHistory(userId) {
  return request.delete('/history', { params: { userId } })
}
