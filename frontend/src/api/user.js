import request from '../utils/request.js'

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
