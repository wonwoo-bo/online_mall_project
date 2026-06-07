<template>
  <div class="page-container">
    <div class="page-header">
      <span class="back-btn" @click="goBack">&lt;</span>
      <h1 class="title">{{ statusMap[order?.status] || '订单详情' }}</h1>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else-if="order">

      <div class="section" :class="{ 'clickable': order.status === 0 }" @click="order.status === 0 ? changeAddress() : null">
        <div class="section-title">
          <el-icon><Location /></el-icon> 收货信息
        </div>
        <div class="address-row">
          <div class="address-content">{{ order.shippingAddress || order.address }}</div>
          <el-icon class="arrow" v-if="order.status === 0"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="section">
        <div class="section-title">商品信息</div>
        <div class="merchant-header" @click="goToShop(order.items?.[0]?.merchantId)">
          {{ order.items?.[0]?.merchantName || '商家店铺' }}
          <span class="merchant-arrow">›</span>
        </div>
        <div class="product-item" v-for="item in order.items" :key="item.id">
          <el-image :src="item.productImage || item.productImg" fit="cover" class="product-img">
            <template #error>
              <div class="img-placeholder">暂无图片</div>
            </template>
          </el-image>
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-spec">{{ item.specs || '默认规格' }} x{{ item.quantity }}</div>
          </div>
          <div class="product-price">¥{{ item.productPrice }}</div>
        </div>
        <!-- 评价按钮（只有已完成的订单才显示） -->
        <div class="review-section" v-if="order.status === 3">
          <div class="review-item" v-for="item in order.items" :key="item.id">
            <el-button
              v-if="!reviewedItems.has(item.id)"
              type="primary"
              size="small"
              @click="writeReview(item)"
            >
              评价商品
            </el-button>
            <el-button
              v-else-if="!appendedItems.has(item.id)"
              type="warning"
              size="small"
              @click="writeAppend(item)"
            >
              追评
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              disabled
            >
              已评价
            </el-button>
          </div>
        </div>

        <div class="price-summary">
          <div class="price-row">
            <span>商品总额</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="price-row">
            <span>运费</span>
            <span>¥0.00</span>
          </div>
          <div class="price-row total">
            <span>实付金额</span>
            <span class="price">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">订单信息</div>
        <div class="info-row">
          <span class="info-label">订单编号</span>
          <span class="info-value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ order.createTime }}</span>
        </div>
        <div class="info-row" v-if="order.payTime">
          <span class="info-label">支付时间</span>
          <span class="info-value">{{ order.payTime }}</span>
        </div>
        <div class="info-row" v-if="order.payMethod">
          <span class="info-label">支付方式</span>
          <span class="info-value">{{ order.payMethod }}</span>
        </div>
        <!-- 待付款时显示支付方式选择 -->
        <div class="pay-method-section" v-if="order.status === 0">
          <div class="section-title" style="margin-top:12px">选择支付方式</div>
          <div
            class="pay-method-item"
            :class="{ active: selectedPayMethod === '虚拟支付宝' }"
            @click="selectedPayMethod = '虚拟支付宝'"
          >
            <span class="method-icon">💙</span>
            <span class="method-name">支付宝</span>
            <span class="method-check">{{ selectedPayMethod === '虚拟支付宝' ? '✓' : '' }}</span>
          </div>
          <div
            class="pay-method-item"
            :class="{ active: selectedPayMethod === '虚拟微信' }"
            @click="selectedPayMethod = '虚拟微信'"
          >
            <span class="method-icon">💚</span>
            <span class="method-name">微信支付</span>
            <span class="method-check">{{ selectedPayMethod === '虚拟微信' ? '✓' : '' }}</span>
          </div>
        </div>
      </div>

      <div class="action-bar" @click.stop>
        <template v-if="order.status === 0">
          <el-button type="primary" :disabled="!selectedPayMethod" @click="payOrder">立即支付</el-button>
          <el-button type="danger" plain @click="cancelOrder">取消订单</el-button>
        </template>
        <template v-if="order.status === 1">
          <el-button plain @click="applyRefund">申请退款</el-button>
        </template>
        <template v-if="order.status === 2">
          <el-button plain @click="applyRefund">申请退款</el-button>
          <el-button plain @click="viewLogistics">查看物流</el-button>
          <el-button type="primary" @click="confirmReceive">确认收货</el-button>
        </template>
        <template v-if="order.status === 3">
          <el-button plain @click="viewLogistics">查看物流</el-button>
          <el-button type="primary" @click="reorder">再次拼单</el-button>
        </template>
        <template v-if="order.status === 4">
          <el-button type="primary" @click="reorder">再次拼单</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail, cancelOrder as apiCancel, confirmReceive as apiConfirm, getReviews, createPayment } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Location, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const order = ref(null)
const reviewedItems = ref(new Set()) // 已评价的订单项ID
const reviewedMap = ref({}) // orderItemId -> reviewId 映射
const appendedItems = ref(new Set()) // 已追评的订单项ID
const selectedPayMethod = ref('') // 选中的支付方式

const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消', 5: '退换货中' }

const goBack = () => {
  router.push('/orders')
}

const changeAddress = () => {
  router.push({
    path: '/address',
    query: { return: `orders/${order.value.id}` }
  })
}

const payOrder = () => {
  if (!selectedPayMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  router.push({
    path: '/pay',
    query: {
      orderId: order.value.id,
      amount: order.value.totalAmount,
      payMethod: selectedPayMethod.value
    }
  })
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await apiCancel(order.value.id)
    ElMessage.success('订单已取消')
    loadOrder()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const confirmReceive = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
    await apiConfirm(order.value.id)
    ElMessage.success('确认收货成功')
    loadOrder()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('确认失败')
  }
}

const applyRefund = () => {
  const items = order.value.items || []
  const item = items[0] || {}
  router.push({
    path: '/return/apply',
    query: {
      orderId: order.value.id,
      productId: item.productId,
      productName: item.productName,
      coverImg: item.productImage || item.productImg,
      price: order.value.totalAmount,
      orderStatus: order.value.status
    }
  })
}

const viewLogistics = () => ElMessage.info('查看物流功能待开发')
const reorder = () => {
  const items = order.value?.items || []
  if (items.length === 1) {
    router.push(`/products/${items[0].productId}`)
  } else if (items.length > 1) {
    router.push('/products')
  } else {
    ElMessage.warning('没有可重新购买的商品')
  }
}

const goToShop = (merchantId) => {
  if (merchantId) {
    router.push(`/merchant/dashboard?id=${merchantId}`)
  }
}

const writeReview = (item) => {
  router.push({
    path: '/review/write',
    query: {
      orderId: order.value.id,
      orderItemId: item.id,
      productId: item.productId,
      merchantId: item.merchantId,
      productName: item.productName,
      coverImg: item.productImage || item.productImg
    }
  })
}

const writeAppend = (item) => {
  const reviewId = reviewedMap.value[item.id]
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

const checkIfReviewed = async (orderItemId, productId) => {
  try {
    const res = await getReviews(productId, { page: 1, size: 100 })
    if (res.data && res.data.list) {
      const found = res.data.list.find(review => review.orderItemId === orderItemId)
      if (found) {
        reviewedItems.value.add(orderItemId)
        reviewedMap.value[orderItemId] = found.id
        // 检查是否已追评
        if (found.hasAppend === 1 || found.append) {
          appendedItems.value.add(orderItemId)
        }
      }
    }
  } catch (e) {
    console.error('检查评价状态失败', e)
  }
}

const loadOrder = async () => {
  const id = route.params.id
  try {
    const res = await getOrderDetail(id)
    order.value = res.data
    
    // 检查每个订单项是否已评价
    if (order.value && order.value.items) {
      for (const item of order.value.items) {
        if (item.productId) {
          await checkIfReviewed(item.id, item.productId)
        }
      }
    }
  } catch (e) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadOrder)
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f5f5;
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

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.section {
  background: #fff;
  margin-bottom: 12px;
  padding: 16px;
}

.section.clickable {
  cursor: pointer;
}

.section.clickable:hover {
  background: #fafafa;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.address-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-content {
  color: #333;
  line-height: 1.6;
  flex: 1;
}

.arrow {
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}

.merchant-header {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.merchant-header:hover {
  color: #ff6700;
}

.merchant-arrow {
  font-size: 16px;
}

.product-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
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
  font-size: 12px;
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

.price-summary {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.price-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #666;
  font-size: 14px;
}

.price-row.total {
  font-weight: bold;
  color: #333;
  font-size: 16px;
  padding-top: 12px;
  margin-top: 8px;
  border-top: 1px dashed #f0f0f0;
}

.price-row .price {
  color: #ff6700;
  font-size: 20px;
}

.info-row {
  display: flex;
  padding: 8px 0;
}

.info-label {
  width: 80px;
  color: #999;
  font-size: 14px;
}

.info-value {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.review-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.review-item {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px;
  background: #fff;
  position: sticky;
  bottom: 0;
}

.pay-method-section {
  margin-top: 8px;
}

.pay-method-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: #f9f9f9;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.pay-method-item.active {
  border-color: #ff6700;
  background: #fff8f0;
}

.method-icon {
  font-size: 22px;
}

.method-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.method-check {
  font-size: 16px;
  color: #ff6700;
  font-weight: bold;
}
</style>
