<template>
  <div class="page-container">
    <div class="page-header">
      <span class="back-btn" @click="goBack">&lt;</span>
      <h1 class="title">确认支付</h1>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else class="pay-content">
      <div class="pay-icon">💰</div>
      <div class="pay-amount">¥{{ amount }}</div>
      <div class="pay-info">共{{ orderCount }}个订单：{{ orderNos }}</div>

      <div class="selected-method">
        <span class="method-label">支付方式：</span>
        <span class="method-value">
          <span class="method-icon">{{ payMethodIcon }}</span>
          {{ payMethodName }}
        </span>
      </div>

      <el-button
        type="warning"
        size="large"
        class="pay-btn"
        @click="doPay"
        :loading="paying"
      >
        {{ paying ? '支付中...' : '确认支付' }}
      </el-button>
    </div>

    <el-dialog v-model="cancelVisible" title="放弃本次支付？" width="400px">
      <p>放弃后订单将保留在待付款中，您可以稍后继续支付</p>
      <template #footer>
        <el-button @click="cancelVisible = false">继续支付</el-button>
        <el-button @click="confirmCancel">放弃支付</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="successVisible" width="400px">
      <div class="success-content">
        <div class="success-icon">✅</div>
        <h3>支付成功！</h3>
        <p>您的订单已支付成功，商家将尽快为您发货</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="goToOrderDetail">查看订单</el-button>
        <el-button @click="goHome">继续购物</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail, createPayment } from '@/api'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const cancelVisible = ref(false)
const successVisible = ref(false)

const orderIds = computed(() => route.query.orderId ? route.query.orderId.split(',') : [])
const amount = computed(() => route.query.amount || '0.00')
const orderCount = computed(() => orderIds.value.length)
const orderNos = ref('')
const selectedPayMethod = computed(() => route.query.payMethod || '虚拟支付宝')
const payMethodIcon = computed(() => selectedPayMethod.value === '虚拟微信' ? '💚' : '💙')
const payMethodName = computed(() => selectedPayMethod.value === '虚拟微信' ? '微信支付' : '支付宝')

const goBack = () => {
  cancelVisible.value = true
}

const confirmCancel = () => {
  cancelVisible.value = false
  router.push(`/orders/${orderIds.value[0]}`)
}

const paying = ref(false)

const doPay = async () => {
  if (orderIds.value.length === 0) {
    ElMessage.error('订单ID不存在')
    return
  }

  paying.value = true
  try {
    // 模拟支付等待
    await new Promise(resolve => setTimeout(resolve, 2000))
    for (const id of orderIds.value) {
      await createPayment({ orderId: parseInt(id), payMethod: selectedPayMethod.value })
    }
    successVisible.value = true
  } catch (e) {
    ElMessage.error('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

const goToOrderDetail = () => {
  successVisible.value = false
  router.push(`/orders/${orderIds.value[0]}`)
}

const goHome = () => {
  successVisible.value = false
  router.push('/')
}

onMounted(async () => {
  try {
    const promises = orderIds.value.map(id => getOrderDetail(id))
    const results = await Promise.all(promises)
    const orders = results.filter(r => r.code === 200).map(r => r.data)
    orderNos.value = orders.map(o => o.orderNo).join('、')
  } catch (e) {
    orderNos.value = orderIds.value.join('、')
  } finally {
    loading.value = false
  }
})
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

.pay-content {
  text-align: center;
  padding: 40px 20px;
}

.pay-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.pay-amount {
  font-size: 36px;
  font-weight: bold;
  color: #ff6700;
  margin-bottom: 10px;
}

.pay-info {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.selected-method {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #fff;
  padding: 10px 24px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  margin-bottom: 24px;
  font-size: 15px;
  width: 240px;
  margin-left: auto;
  margin-right: auto;
}

.method-label {
  color: #999;
}

.method-value {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #333;
  font-weight: 500;
}

.method-icon {
  font-size: 20px;
}

.pay-btn {
  width: 200px;
  height: 40px;
}

.success-content {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.success-content h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.success-content p {
  color: #666;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
