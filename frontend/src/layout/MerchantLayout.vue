<template>
  <el-container
    class="layout-container"
    :class="{ 'sidebar-mini': isSidebarMini }"
  >
    <el-aside :width="isSidebarMini ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon v-if="!isSidebarMini" class="logo-icon"
          ><ShoppingCart
        /></el-icon>
        <h2 v-if="!isSidebarMini">商家后台</h2>
        <el-icon v-else class="logo-icon-mini"><ShoppingCart /></el-icon>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#ff4400"
        text-color="#fff"
        active-text-color="#ffdd00"
        unique-opened
        class="sidebar-menu"
      >
        <el-menu-item index="/merchant/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>{{ isSidebarMini ? "" : "仪表盘" }}</span>
        </el-menu-item>
        <el-sub-menu index="statistics-submenu">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>{{ isSidebarMini ? "" : "数据统计中心" }}</span>
          </template>
          <el-menu-item index="/merchant/product-ranking">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ isSidebarMini ? "" : "商品销量排行" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/product-conversion">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ isSidebarMini ? "" : "商品转化数据分析" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/inventory-warning">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ isSidebarMini ? "" : "库存积压预警统计" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/financial-report">
            <el-icon><Document /></el-icon>
            <span>{{ isSidebarMini ? "" : "财务对账报表" }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="product-center-submenu">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>{{ isSidebarMini ? "" : "商品中心管理" }}</span>
          </template>
          <el-menu-item index="/merchant/products">
            <el-icon><Goods /></el-icon>
            <span>{{ isSidebarMini ? "" : "商品管理" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/brands">
            <el-icon><Present /></el-icon>
            <span>{{ isSidebarMini ? "" : "品牌管理" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/specs">
            <el-icon><ChatSquare /></el-icon>
            <span>{{ isSidebarMini ? "" : "规格管理" }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/merchant/orders">
          <el-icon><Tickets /></el-icon>
          <span>{{ isSidebarMini ? "" : "订单中心管理" }}</span>
        </el-menu-item>
        <el-menu-item index="/merchant/reviews">
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ isSidebarMini ? "" : "评价互动管理" }}</span>
        </el-menu-item>
        <el-menu-item index="/merchant/refunds">
          <el-icon><Service /></el-icon>
          <span>{{ isSidebarMini ? "" : "售后退款管理" }}</span>
        </el-menu-item>
        <el-sub-menu index="marketing-submenu">
          <template #title>
            <el-icon><Present /></el-icon>
            <span>{{ isSidebarMini ? "" : "营销活动管理" }}</span>
          </template>
          <el-menu-item index="/merchant/promotions">
            <el-icon><Present /></el-icon>
            <span>{{ isSidebarMini ? "" : "促销活动" }}</span>
          </el-menu-item>
          <el-menu-item index="/merchant/coupons">
            <el-icon><Wallet /></el-icon>
            <span>{{ isSidebarMini ? "" : "优惠券管理" }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/merchant/settings">
          <el-icon><Setting /></el-icon>
          <span>{{ isSidebarMini ? "" : "商家账号与店铺管理" }}</span>
        </el-menu-item>
        <el-menu-item index="/merchant/recycle">
          <el-icon><Delete /></el-icon>
          <span>{{ isSidebarMini ? "" : "回收站" }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-button @click="toggleSidebar" class="sidebar-toggle">
            <el-icon><Menu /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/merchant/dashboard' }"
              >首页</el-breadcrumb-item
            >
            <el-breadcrumb-item
              v-for="(item, index) in breadcrumbItems"
              :key="index"
            >
              {{ item }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleMessageCommand" trigger="click">
            <span class="message-icon">
              <el-icon><Bell /></el-icon>
              <span v-if="unreadMessageCount > 0" class="message-badge">
                {{ unreadMessageCount }}
              </span>
            </span>
            <template #dropdown>
              <div class="message-dropdown">
                <div class="message-header">
                  <span>系统消息</span>
                  <span class="mark-all" @click.stop="markAllRead"
                    >全部已读</span
                  >
                </div>
                <div class="message-list">
                  <div
                    v-for="msg in messages.slice(0, 5)"
                    :key="msg.id"
                    class="message-item"
                    :class="{ 'message-unread': msg.isRead === 0 }"
                    @click="markMessageRead(msg.id)"
                  >
                    <el-icon class="msg-icon" :class="msg.type">
                      <component :is="getMessageIcon(msg.type)" />
                    </el-icon>
                    <div class="msg-content">
                      <div class="msg-title">{{ msg.title }}</div>
                      <div class="msg-time">{{ msg.time }}</div>
                    </div>
                  </div>
                  <div v-if="messages.length === 0" class="no-message">
                    暂无消息
                  </div>
                </div>
              </div>
            </template>
          </el-dropdown>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ merchantInfo?.shopName || "商家" }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="settings">店铺设置</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ShoppingCart,
  DataAnalysis,
  Goods,
  Tickets,
  ChatDotRound,
  RefreshLeft,
  Present,
  Wallet,
  Setting,
  User,
  ArrowDown,
  Menu,
  TrendCharts,
  Bell,
  ChatSquare,
  Box,
  Document,
  Service,
  Delete,
} from "@element-plus/icons-vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { getMerchantInfo, getMessageListFull, readMessage, batchMarkAsRead } from "@/api/merchant";

const route = useRoute();
const router = useRouter();
const isSidebarMini = ref(false);
const merchantInfo = ref({});
const messages = ref([]);
const unreadMessageCount = ref(0);

const activeMenu = computed(() => route.path);

const breadcrumbItems = computed(() => {
  const items = [];
  const matched = route.matched.slice(1);
  matched.forEach((item) => {
    if (item.meta.title) {
      items.push(item.meta.title);
    }
  });
  return items;
});

const toggleSidebar = () => {
  isSidebarMini.value = !isSidebarMini.value;
};

const handleCommand = (command) => {
  if (command === "logout") {
    ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
      confirmButtonClass: "el-button--danger",
    })
      .then(() => {
        localStorage.removeItem("merchantToken");
        localStorage.removeItem("merchantId");
        localStorage.removeItem("merchantName");
        ElMessage.success("退出成功");
        router.push("/merchant-login");
      })
      .catch(() => {});
  } else if (command === "settings") {
    router.push("/merchant/settings");
  }
};

const handleMessageCommand = () => {
  router.push("/merchant/settings");
};

const getMessageIcon = (type) => {
  const icons = {
    order: Box,
    review: ChatSquare,
    refund: Service,
    announcement: Document,
  };
  return icons[type] || Document;
};

const markMessageRead = async (id) => {
  try {
    await readMessage(id);
    const msg = messages.value.find((m) => m.id === id);
    if (msg) {
      msg.isRead = 1;
      unreadMessageCount.value = Math.max(0, unreadMessageCount.value - 1);
    }
  } catch (error) {
    console.error("标记消息已读失败", error);
  }
};

const markAllRead = async () => {
  try {
    const unreadIds = messages.value.filter((m) => m.isRead === 0).map((m) => m.id);
    if (unreadIds.length > 0) {
      await batchMarkAsRead(unreadIds);
      messages.value.forEach((m) => (m.isRead = 1));
      unreadMessageCount.value = 0;
      ElMessage.success("已全部标记为已读");
    }
  } catch (error) {
    console.error("批量标记已读失败", error);
    ElMessage.error("操作失败");
  }
};

const loadMessages = async () => {
  try {
    const res = await getMessageListFull({ isRead: 0 });
    if (res.code === 200) {
      const msgList = res.data?.messages || [];
      messages.value = msgList.map((m) => ({
        id: m.id,
        title: m.title,
        type: m.messageType,
        time: m.createTime,
        isRead: m.isRead,
      }));
      unreadMessageCount.value = res.data?.unreadCount || messages.value.length;
    }
  } catch (error) {
    console.error("加载消息失败", error);
  }
};

const loadMerchantInfo = async () => {
  try {
    const res = await getMerchantInfo();
    if (res.code === 200) {
      merchantInfo.value = res.data;
      if (res.data.shopName) {
        localStorage.setItem("merchantName", res.data.shopName);
      }
    } else {
      const shopName = localStorage.getItem("merchantName");
      if (shopName) {
        merchantInfo.value = { shopName };
      }
    }
  } catch (error) {
    console.error("加载商家信息失败", error);
    const shopName = localStorage.getItem("merchantName");
    if (shopName) {
      merchantInfo.value = { shopName };
    }
  }
};

onMounted(() => {
  loadMessages();
  loadMerchantInfo();
});
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
  min-height: 100vh;
  transition: all 0.3s ease;
  display: flex;
}

.layout-container.sidebar-mini .aside {
  width: 64px !important;
}

.aside {
  background-color: #ff4400;
  overflow: hidden;
  transition: width 0.3s ease;
  height: 100%;
  min-height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e63900;
  gap: 10px;
}

.logo h2 {
  color: #fff;
  font-size: 16px;
  margin: 0;
  font-weight: 600;
}

.logo-icon,
.logo-icon-mini {
  font-size: 24px;
  color: #ffdd00;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu > :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  margin: 4px 8px;
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.15);
  font-weight: 500;
  font-size: 15px;
}

.sidebar-menu > :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.25);
}

.sidebar-menu :deep(.el-sub-menu) .el-menu {
  background-color: transparent;
}

.sidebar-menu :deep(.el-sub-menu) .el-menu-item {
  margin: 3px 16px;
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.sidebar-menu :deep(.el-sub-menu) .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #ffdd00 !important;
  color: #ff4400 !important;
}

:deep(.el-container) {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-header) {
  flex-shrink: 0;
}

:deep(.el-main) {
  flex: 1;
  overflow: hidden;
}

.header {
  background: linear-gradient(135deg, #fff 0%, #fff8f5 100%);
  border-bottom: 2px solid #ff4400;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(255, 68, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sidebar-toggle {
  width: 36px;
  height: 36px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: rgba(255, 68, 0, 0.1);
  border: 1px solid rgba(255, 68, 0, 0.2);
  color: #ff4400;
}

.breadcrumb :deep(.el-breadcrumb__item) {
  font-size: 14px;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #ff4400;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.message-icon {
  cursor: pointer;
  position: relative;
  padding: 8px;
  color: #ff4400;
  font-size: 20px;
}

.message-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  font-size: 12px;
  color: #fff;
  background-color: #ef4444;
  border-radius: 9px;
  padding: 0 5px;
}

.message-dropdown {
  width: 320px;
  padding: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
}

.mark-all {
  font-size: 12px;
  color: #ff4400;
  cursor: pointer;
}

.mark-all:hover {
  text-decoration: underline;
}

.message-list {
  max-height: 300px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.message-item:hover {
  background-color: #fafafa;
}

.message-unread {
  background-color: #fff8f5;
}

.msg-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.msg-icon.order {
  background-color: #e3f2fd;
  color: #1976d2;
}

.msg-icon.review {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.msg-icon.system {
  background-color: #e8f5e9;
  color: #388e3c;
}

.msg-content {
  flex: 1;
  min-width: 0;
}

.msg-title {
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.msg-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.no-message {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.header-right .user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: rgba(255, 68, 0, 0.1);
  border-radius: 20px;
  color: #ff4400;
  font-weight: 500;
}

.main {
  background: linear-gradient(135deg, #fff8f5 0%, #fef5f0 100%);
  padding: 20px;
  height: calc(100% - 60px);
  overflow-y: auto;
  flex: 1;
}

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

@media screen and (max-width: 768px) {
  .aside {
    position: fixed;
    z-index: 100;
    left: -220px;
    top: 0;
    height: 100%;
  }

  .layout-container.sidebar-mini .aside {
    left: 0;
    width: 64px !important;
  }

  .header-left {
    flex: 1;
  }

  .breadcrumb {
    display: none;
  }
}
</style>
