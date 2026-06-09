// 认证相关工具函数

// ===== 普通用户认证 =====
export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  localStorage.setItem('token', token)
}

export function removeToken() {
  localStorage.removeItem('token')
}

export function getUserId() {
  return localStorage.getItem('userId')
}

export function setUserId(userId) {
  localStorage.setItem('userId', userId)
}

export function getUsername() {
  return localStorage.getItem('username')
}

export function setUsername(username) {
  localStorage.setItem('username', username)
}

export function isLoggedIn() {
  return !!getToken()
}

export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
}

// ===== 管理员认证 =====
export function getAdminToken() {
  return localStorage.getItem('adminToken')
}

export function setAdminToken(token) {
  localStorage.setItem('adminToken', token)
}

export function removeAdminToken() {
  localStorage.removeItem('adminToken')
}

export function getAdminId() {
  return localStorage.getItem('adminId')
}

export function setAdminId(adminId) {
  localStorage.setItem('adminId', adminId)
}

export function getAdminUsername() {
  return localStorage.getItem('adminUsername')
}

export function setAdminUsername(username) {
  localStorage.setItem('adminUsername', username)
}

export function isAdminLoggedIn() {
  return !!getAdminToken()
}

export function adminLogout() {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminId')
  localStorage.removeItem('adminUsername')
}
