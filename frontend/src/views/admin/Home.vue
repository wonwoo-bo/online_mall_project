<template>
  <div class="admin-container">
    <aside class="sidebar">
      <div class="logo">
        <h2>商城管理</h2>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" router>
        <el-menu-item index="/admin">
          <el-icon><HomeFilled /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu index="merchant">
          <template #title>
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </template>
          <el-menu-item index="/admin/merchant/pending">待审核商家</el-menu-item>
          <el-menu-item index="/admin/merchant/approved">已审核商家</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/config">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
        <el-menu-item index="/admin/banners">
          <el-icon><Picture /></el-icon>
          <span>首页管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/admins">
          <el-icon><Users /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
      </el-menu>
      <div class="logout" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
      </div>
    </aside>
    
    <main class="main-content">
      <header class="header">
        <div class="header-left" @click="goBack">
          <el-icon class="back-icon"><ArrowLeft /></el-icon>
          <span class="logo-text">返回首页</span>
        </div>
        <div class="header-right">
          <span>欢迎, {{ username }}</span>
        </div>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'

import { getAdminUsername, adminLogout } from '../../utils/auth.js'

const router = useRouter()
const route = useRoute()

const username = ref(getAdminUsername())

const activeMenu = computed(() => {
  return route.path
})

const handleLogout = () => {
  adminLogout()
  ElMessage.success('退出成功')
  router.push('/admin/login')
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-container {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.sidebar {
  width: 200px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: #bdc3c7;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background: #34495e;
}

.sidebar-menu .el-menu-item.is-active {
  background: #3498db;
  color: white;
}

.logout {
  padding: 15px 20px;
  border-top: 1px solid #34495e;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logout:hover {
  background: #34495e;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  padding: 15px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #3498db;
  font-size: 14px;
}

.header-left:hover {
  color: #2980b9;
}

.back-icon {
  font-size: 16px;
}

.logo-text {
  font-weight: 500;
}

.header-right {
  font-size: 14px;
  color: #666;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
}
</style>