<template>
  <div id="app">
    <!-- 商家后台页面 - 使用独立布局 -->
    <template v-if="isMerchantPage">
      <router-view />
    </template>

    <!-- 普通页面布局 -->
    <template v-else>
      <!-- 顶部导航栏 -->
      <header class="header" v-if="!hideHeader">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <el-icon :size="28"><ShoppingCart /></el-icon>
            <span>淘宝网</span>
          </div>
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品"
              @keyup.enter="handleSearch"
              clearable
            >
              <template #append>
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <nav class="nav-menu">
            <router-link v-if="!isAdminPage" to="/">首页</router-link>
            <router-link v-if="!isAdminPage" to="/products">商品列表</router-link>
            <router-link v-if="!isAdminPage" to="/category">分类</router-link>
            <router-link v-if="!isAdminPage" to="/cart">购物车</router-link>
            <router-link v-if="!isAdminPage" to="/orders">我的订单</router-link>
            <router-link v-if="!isAdminPage" to="/favorite">收藏</router-link>
            <router-link v-if="!isAdminPage" to="/history">浏览历史</router-link>
            <router-link v-if="!isAdminPage" to="/return">退换货</router-link>
          </nav>
          <!-- 用户登录状态 -->
          <div class="user-section">
            <template v-if="isCurrentLoggedIn">
              <el-dropdown @command="handleUserCommand">
                <span class="user-info">
                  <el-icon><User /></el-icon>
                  {{ currentUsername }}
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="!isAdminPage" command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item v-if="!isAdminPage" command="address">收货地址</el-dropdown-item>
                    <el-dropdown-item v-if="!isAdminPage" command="cart">购物车</el-dropdown-item>
                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-dropdown @command="handleIdentityCommand" class="identity-dropdown">
                <span class="identity-trigger">
                  <el-icon><User /></el-icon>
                  身份选择
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="user-login">
                      <el-icon><User /></el-icon>
                      用户登录
                    </el-dropdown-item>
                    <el-dropdown-item command="user-register">
                      <el-icon><Plus /></el-icon>
                      用户注册
                    </el-dropdown-item>
                    <el-dropdown-item divided command="merchant-login">
                      <el-icon><Setting /></el-icon>
                      商家入驻/登录
                    </el-dropdown-item>
                    <el-dropdown-item command="admin-login">
                      <el-icon><Setting /></el-icon>
                      管理员登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </div>
      </header>

      <!-- 主内容区 -->
      <main :class="['main-content', { 'full-width': isFullWidthPage }]">
        <router-view />
      </main>

      <!-- 底部 -->
      <footer class="footer" v-if="!hideHeader && !hideFooter">
        <p>© 2024 仿淘宝电商平台 - Spring Boot + Vue3 项目</p>
      </footer>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Search, User, ArrowDown, Setting, Plus, Phone } from '@element-plus/icons-vue'

import { 
  isLoggedIn, getUsername, logout,
  isAdminLoggedIn, getAdminUsername, adminLogout 
} from './utils/auth.js'

// 简单的商家状态管理
const isMerchantLoggedIn = () => {
  return !!localStorage.getItem('merchantToken')
}
const getMerchantUsername = () => {
  const info = localStorage.getItem('merchantInfo')
  if (info) {
    const merchantInfo = JSON.parse(info)
    return merchantInfo.name || merchantInfo.username
  }
  return null
}
const merchantLogout = () => {
  localStorage.removeItem('merchantToken')
  localStorage.removeItem('merchantInfo')
}

const router = useRouter()
const route = useRoute()
const searchKeyword = ref('')

const isAdminPage = computed(() => {
  return route.path.startsWith('/admin')
})

const isMerchantPage = computed(() => {
  return route.path.startsWith('/merchant')
})

const isFullWidthPage = computed(() => {
  return route.meta.fullWidth === true
})

const hideHeader = computed(() => {
  return route.meta.hideHeader === true
})

const hideFooter = computed(() => {
  return route.meta.hideFooter === true
})

const currentUsername = computed(() => {
  if (isAdminPage.value) {
    return getAdminUsername() || '管理员'
  }
  if (isMerchantPage.value) {
    return getMerchantUsername() || '商家用户'
  }
  return getUsername() || '用户'
})

const isCurrentLoggedIn = computed(() => {
  if (isAdminPage.value) {
    return isAdminLoggedIn()
  }
  if (isMerchantPage.value) {
    return isMerchantLoggedIn()
  }
  return isLoggedIn()
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'address':
      router.push('/address')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'logout':
      if (isAdminPage.value) {
        adminLogout()
        ElMessage.success('管理员已退出登录')
        router.push('/admin/login')
      } else if (isMerchantPage.value) {
        merchantLogout()
        ElMessage.success('商家已退出登录')
        router.push('/merchant-login')
      } else {
        logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      }
      break
  }
}

const handleIdentityCommand = (command) => {
  switch (command) {
    case 'user-login':
      router.push('/login')
      break
    case 'user-register':
      router.push('/register')
      break
    case 'merchant-login':
      router.push('/merchant-login')
      break
    case 'admin-login':
      router.push('/admin/login')
      break
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(to right, #ff5000, #ff1a1a);
  padding: 12px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.search-box :deep(.el-input__wrapper) {
  border-radius: 20px 0 0 20px;
}

.search-box :deep(.el-input-group__append) {
  border-radius: 0 20px 20px 0;
  background: #ff5000;
  border-color: #ff5000;
}

.search-box :deep(.el-input-group__append .el-button) {
  color: white;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.nav-menu a:hover,
.nav-menu a.router-link-active {
  background: rgba(255, 255, 255, 0.2);
}

.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
}

.login-btn,
.register-btn {
  color: white;
  text-decoration: none;
  font-size: 14px;
  padding: 6px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.login-btn {
  border: 1px solid white;
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.register-btn {
  background: white;
  color: #ff5000;
}

.register-btn:hover {
  background: #f0f0f0;
}

.admin-btn {
  color: #fff3cd;
  text-decoration: none;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #ffc107;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.admin-btn:hover {
  background: rgba(255, 193, 7, 0.2);
}

.merchant-btn {
  color: #d1edff;
  text-decoration: none;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #39c5bb;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.merchant-btn:hover {
  background: rgba(57, 197, 187, 0.2);
}

.identity-dropdown {
  position: relative;
}

.identity-trigger {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid white;
  transition: all 0.3s;
}

.identity-trigger:hover {
  background: rgba(255, 255, 255, 0.2);
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 25px;
  width: 100%;
}

.main-content.full-width {
  max-width: none;
  padding: 0;
}

.footer {
  background: #333;
  color: #999;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.footer p {
  font-size: 14px;
}
</style>
