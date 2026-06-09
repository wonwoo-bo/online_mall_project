<template>
  <div class="return-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon :size="24"><RefreshRight /></el-icon>
        退换货管理
      </h2>
      <p class="page-desc">查看和管理您的退换货申请记录</p>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="status" @change="loadList" size="default">
        <el-radio-button value="">
          <el-icon><Grid /></el-icon> 全部
        </el-radio-button>
        <el-radio-button value="pending">
          <el-icon><Clock /></el-icon> 待审核
        </el-radio-button>
        <el-radio-button value="approved">
          <el-icon><CircleCheck /></el-icon> 已同意
        </el-radio-button>
        <el-radio-button value="rejected">
          <el-icon><CircleClose /></el-icon> 已拒绝
        </el-radio-button>
        <el-radio-button value="shipping">
          <el-icon><Van /></el-icon> 退货中
        </el-radio-button>
        <el-radio-button value="completed">
          <el-icon><SuccessFilled /></el-icon> 已完成
        </el-radio-button>
        <el-radio-button value="cancelled">
          <el-icon><CircleClose /></el-icon> 已取消
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 列表 -->
    <div class="return-list" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无退换货记录" :image-size="120">
          <template #description>
            <p style="color: #999;">您还没有退换货记录</p>
          </template>
          <el-button type="primary" round @click="$router.push('/orders')">
            <el-icon><List /></el-icon> 查看我的订单
          </el-button>
        </el-empty>
      </div>

      <div v-else class="return-items">
        <div v-for="item in list" :key="item.id" class="return-card" @click="$router.push(`/return/${item.id}`)">
          <!-- 卡片头部 -->
          <div class="return-header">
            <div class="return-header-left">
              <span class="return-id">
                <el-icon><Ticket /></el-icon>
                申请编号：{{ item.id }}
              </span>
              <span class="return-time">
                <el-icon><Calendar /></el-icon>
                {{ item.createTime }}
              </span>
            </div>
            <div class="return-header-right">
              <el-tag :type="getStatusType(item.status)" effect="dark" round size="default">
                <el-icon v-if="item.status === 'pending'"><Clock /></el-icon>
                <el-icon v-else-if="item.status === 'approved'"><CircleCheck /></el-icon>
                <el-icon v-else-if="item.status === 'rejected'"><CircleClose /></el-icon>
                <el-icon v-else-if="item.status === 'shipping'"><Van /></el-icon>
                <el-icon v-else><SuccessFilled /></el-icon>
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>

          <!-- 卡片内容 -->
          <div class="return-body">
            <div class="product-info" @click.stop="$router.push(`/products/${item.productId}`)">
              <img :src="item.coverImg || defaultImage" class="product-image">
              <div class="product-detail">
                <h4>{{ item.productName }}</h4>
                <span class="price">¥{{ item.refundAmount || item.price || '0.00' }}</span>
              </div>
            </div>
            <div class="return-info">
              <div class="info-row">
                <span class="info-label">服务类型</span>
                <el-tag :type="getTypeTagColor(item.type)" effect="plain" size="small" round>
                  {{ getTypeText(item.type) }}
                </el-tag>
              </div>
              <div class="info-row">
                <span class="info-label">申请原因</span>
                <span class="info-value">{{ item.reason }}</span>
              </div>
              <div class="info-row" v-if="item.status === 'approved'">
                <span class="info-label">操作提示</span>
                <span class="info-action">请尽快填写物流信息</span>
              </div>
            </div>
          </div>

          <!-- 卡片底部 -->
          <div class="return-footer">
            <el-button type="primary" size="default" round @click.stop="$router.push(`/return/${item.id}`)">
              <el-icon><View /></el-icon> 查看详情
            </el-button>
            <el-button
              v-if="item.status === 'pending'"
              type="info"
              size="default"
              plain
              round
              @click.stop="handleCancel(item)"
            >
              <el-icon><CircleClose /></el-icon> 取消申请
            </el-button>
            <el-button
              v-if="item.status === 'approved'"
              type="warning"
              size="default"
              plain
              round
              @click.stop="$router.push(`/return/${item.id}`)"
            >
              <el-icon><EditPen /></el-icon> 填写物流
            </el-button>
            <el-button
              v-if="item.status === 'rejected'"
              type="danger"
              size="default"
              plain
              round
              @click.stop="handleReapply(item)"
            >
              <el-icon><RefreshRight /></el-icon> 重新申请
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReturns, cancelReturn } from '@/api'
import { getUserId } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const list = ref([])
const status = ref('')
const defaultImage = 'https://via.placeholder.com/80x80/FF5000/FFFFFF?text=商品'

const getStatusType = (s) => ({ pending: 'warning', approved: 'success', rejected: 'danger', shipping: 'info', completed: '', cancelled: 'info' }[s] || 'info')
const getStatusText = (s) => ({ pending: '待审核', approved: '已同意', rejected: '已拒绝', shipping: '退货中', completed: '已完成', cancelled: '已取消' }[s] || s)
const getTypeText = (t) => ({ refund: '仅退款', return: '退货退款', exchange: '换货' }[t] || t)
const getTypeTagColor = (t) => ({ refund: 'danger', return: 'warning', exchange: 'success' }[t] || 'info')

const loadList = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    const res = await getReturns(userId, status.value)
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleCancel = async (item) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个退换货申请吗？取消后无法恢复。',
      '取消确认',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning',
      }
    )
    await cancelReturn(item.id)
    ElMessage.success('已取消退换货申请')
    loadList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('取消失败，请重试')
    }
  }
}

const handleReapply = (item) => {
  router.push({
    path: '/return/apply',
    query: {
      orderId: item.orderId,
      productId: item.productId,
      productName: item.productName || '',
      price: item.price || item.refundAmount || '0',
      coverImg: item.coverImg || '',
      orderStatus: 3 // 已完成状态，允许所有退款类型
    }
  })
}

let routeListener = null

onMounted(() => {
  loadList()
  routeListener = route.fullPath
  const checkRouteChange = () => {
    if (route.fullPath !== routeListener) {
      routeListener = route.fullPath
      loadList()
    }
  }
  const timer = setInterval(checkRouteChange, 500)
  onUnmounted(() => clearInterval(timer))
})
</script>

<style scoped>
.return-page {
  padding-bottom: 40px;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-title .el-icon {
  color: #FF5000;
}

.page-desc {
  color: #999;
  font-size: 14px;
  margin: 0;
}

/* 筛选栏 */
.filter-bar {
  margin-bottom: 24px;
  background: white;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-bar :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 6px !important;
  border: none !important;
  padding: 8px 16px;
  font-size: 13px;
}

.filter-bar :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 6px !important;
}

.filter-bar :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 6px !important;
}

/* 退换货卡片 */
.return-card {
  background: white;
  border-radius: 12px;
  padding: 0;
  margin-bottom: 16px;
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;
  border: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.return-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

/* 卡片头部 */
.return-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: linear-gradient(135deg, #fafafa, #f5f5f5);
  border-bottom: 1px solid #f0f0f0;
}

.return-header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.return-id {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 13px;
}

.return-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 13px;
}

/* 卡片内容 */
.return-body {
  display: flex;
  gap: 24px;
  padding: 20px;
}

.product-info {
  display: flex;
  gap: 16px;
  flex-shrink: 0;
  cursor: pointer;
}

.product-image {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #f0f0f0;
  transition: transform 0.3s;
}

.product-info:hover .product-image {
  transform: scale(1.05);
}

.product-detail h4 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-detail .price {
  color: #FF5000;
  font-size: 20px;
  font-weight: 700;
}

.return-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  color: #999;
  font-size: 13px;
  min-width: 60px;
  flex-shrink: 0;
}

.info-value {
  color: #555;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.info-action {
  color: #e6a23c;
  font-size: 13px;
  font-weight: 500;
}

/* 卡片底部 */
.return-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.return-footer .el-button {
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 768px) {
  .return-body {
    flex-direction: column;
    gap: 16px;
  }

  .return-info {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .return-footer {
    flex-wrap: wrap;
  }
}
</style>
