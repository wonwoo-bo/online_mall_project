<template>
  <div class="page-container">
    <div class="page-header">
      <span class="back-btn" @click="goBack">&lt;</span>
      <h1 class="title">我的订单</h1>
      <router-link to="/search" class="search-link">
        <el-icon><Search /></el-icon>
      </router-link>
    </div>

    <el-tabs v-model="currentStatus" @tab-change="loadOrders" class="filter-tabs">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
      <el-tab-pane label="退换货" name="5" />
    </el-tabs>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else-if="filteredOrders.length === 0" class="empty-orders">
      <el-empty :description="`暂无${currentStatus === 'all' ? '' : statusText}订单`">
        <template #image>
          <div style="font-size: 64px;">📦</div>
        </template>
      </el-empty>
    </div>

    <div v-else>
      <div class="order-item" v-for="order in filteredOrders" :key="order.id" @click="viewDetail(order.id)">
        <div class="order-header">
          <span class="merchant-name">{{ order.items?.[0]?.merchantName || '商家店铺' }}</span>
          <el-tag :type="statusType(order.status)" size="small">{{ statusMap[order.status] }}</el-tag>
        </div>
        <div class="order-body" v-for="item in (order.items || []).slice(0, 1)" :key="item.id">
          <el-image :src="item.productImage || item.productImg" fit="cover" class="product-img">
            <template #error>
              <div class="img-placeholder">暂无图片</div>
            </template>
          </el-image>
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-spec">{{ item.specs || '默认规格' }} x{{ item.quantity }}</div>
          </div>
          <div class="product-price">¥{{ order.totalAmount }}</div>
        </div>
        <div class="order-footer" @click.stop>
          <template v-if="order.status === 0">
            <el-button type="primary" @click="payOrder(order)">立即支付</el-button>
            <el-button type="danger" plain @click="cancelOrder(order.id)">取消订单</el-button>
          </template>
          <template v-if="order.status === 1">
            <el-button plain @click="applyRefund(order)">申请退款</el-button>
            <el-button plain @click="viewLogistics">查看物流</el-button>
          </template>
          <template v-if="order.status === 2">
            <el-button plain @click="applyRefund(order)">申请退款</el-button>
            <el-button plain @click="viewLogistics">查看物流</el-button>
            <el-button type="primary" @click="confirmReceive(order.id)">确认收货</el-button>
          </template>
          <template v-if="order.status === 3">
            <el-button v-if="order.expressCompany || order.trackingNo" plain @click="viewLogistics">查看物流</el-button>
            <el-button
              v-if="!order.hasRefunded && !isOrderReviewed(order)"
              type="primary"
              @click="writeReview(order)"
            >评价商品</el-button>
            <el-button
              v-else-if="!order.hasRefunded && !isOrderAppended(order)"
              type="warning"
              @click="writeAppend(order)"
            >追评</el-button>
            <el-button
              v-else-if="!order.hasRefunded"
              type="success"
              disabled
            >已评价</el-button>
            <el-button
              v-if="!order.hasRefunded"
              type="success"
              plain
              @click="reorder(order)"
            >再次拼单</el-button>
          </template>
          <template v-if="order.status === 4">
            <el-button type="primary" @click="reorder(order)">再次拼单</el-button>
          </template>
          <template v-if="order.status === 5">
            <el-button type="primary" @click="$router.push('/return')">查看详情</el-button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder as apiCancel, confirmReceive as apiConfirm, getReviews } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Search } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(true)
const orders = ref([])
const currentStatus = ref('all')
const reviewedOrderItems = ref(new Set()) // 已评价的订单项ID
const reviewedOrderMap = ref({}) // orderItemId -> reviewId
const appendedOrderItems = ref(new Set()) // 已追评的订单项ID

const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退换货中' }
const statusText = computed(() => statusMap[currentStatus.value] || '')

const filteredOrders = computed(() => {
  if (currentStatus.value === 'all') return orders.value
  return orders.value.filter(o => o.status === parseInt(currentStatus.value))
})

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'success', 4: 'info', 5: 'danger' }
  return map[status] || 'info'
}

const isOrderReviewed = (order) => {
  return order.items?.some(item => reviewedOrderItems.value.has(item.id)) || false
}

const isOrderAppended = (order) => {
  return order.items?.some(item => appendedOrderItems.value.has(item.id)) || false
}

const goBack = () => {
  router.back()
}

const viewDetail = (id) => {
  router.push(`/orders/${id}`)
}

const payOrder = (order) => {
  router.push({
    path: '/pay',
    query: {
      orderId: order.id,
      amount: order.totalAmount
    }
  })
}

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await apiCancel(id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const confirmReceive = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
    await apiConfirm(id)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('确认失败')
  }
}

const applyRefund = (order) => {
  const item = order.items?.[0]
  router.push({
    path: '/return/apply',
    query: {
      orderId: order.id,
      productId: item?.productId,
      productName: item?.productName,
      coverImg: item?.productImage || item?.productImg,
      price: order.totalAmount,
      orderStatus: order.status
    }
  })
}

const viewLogistics = () => ElMessage.info('查看物流功能待开发')

const reorder = (order) => {
  const item = order.items?.[0]
  if (item) {
    router.push({
      path: '/checkout',
      query: {
        productId: item.productId,
        quantity: 1
      }
    })
  } else {
    ElMessage.info('商品信息不存在')
  }
}

const writeReview = (order) => {
  const item = order.items?.[0]
  if (item) {
    router.push({
      path: '/review/write',
      query: {
        orderId: order.id,
        orderItemId: item.id,
        productId: item.productId,
        merchantId: item.merchantId,
        productName: item.productName,
        coverImg: item.productImage || item.productImg
      }
    })
  } else {
    ElMessage.info('商品信息不存在')
  }
}

const writeAppend = (order) => {
  const item = order.items?.[0]
  if (item) {
    const reviewId = reviewedOrderMap.value[item.id]
    router.push({
      path: '/review/write',
      query: {
        mode: 'append',
        reviewId: reviewId,
        productId: item.productId,
        productName: item.productName,
        coverImg: item.productImage || item.productImg
      }
    })
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {}
    if (currentStatus.value !== 'all') {
      params.status = parseInt(currentStatus.value)
    }
    const res = await getOrderList(params)
    orders.value = res.data?.list || []
    // 检查已完成订单的评价状态
    await checkReviewedStatus()
  } catch (e) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const checkReviewedStatus = async () => {
  const completedOrders = orders.value.filter(o => o.status === 3 && o.items?.length > 0)
  for (const order of completedOrders) {
    for (const item of order.items) {
      if (!item.productId) continue
      try {
        const res = await getReviews(item.productId, { page: 1, size: 100 })
        if (res.data && res.data.list) {
          const found = res.data.list.find(r => r.orderItemId === item.id)
          if (found) {
            reviewedOrderItems.value.add(item.id)
            reviewedOrderMap.value[item.id] = found.id
            // 检查是否已追评
            if (found.hasAppend === 1 || found.append) {
              appendedOrderItems.value.add(item.id)
            }
          }
        }
      } catch (e) {
        // 静默失败
      }
    }
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #ff6700;
  color: #fff;
}

.back-btn {
  font-size: 24px;
  cursor: pointer;
}

.title {
  flex: 1;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
}

.search-link {
  color: #fff;
}

.filter-tabs {
  background: #fff;
  margin-bottom: 12px;
  padding-left: 12px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-orders {
  padding: 40px;
}

.order-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.merchant-name {
  font-weight: bold;
}

.order-body {
  display: flex;
  padding: 12px 16px;
  gap: 12px;
}

.product-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-spec {
  font-size: 12px;
  color: #999;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff6700;
  display: flex;
  align-items: center;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
