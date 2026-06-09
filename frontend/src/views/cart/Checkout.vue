<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="title">确认订单</h1>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else>
      <div class="section" @click="openAddressSelector">
        <el-icon class="address-icon"><Location /></el-icon>
        <div class="address-content" v-if="address">
          <div class="address-name">{{ addressName }}</div>
          <div class="address-detail">{{ addressDetail }}</div>
        </div>
        <div class="address-content" v-else>
          <span class="text-muted">请选择收货地址</span>
        </div>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </div>

      <div class="merchant-group" v-for="group in groupedCart" :key="group.merchantName">
        <div class="merchant-header">{{ group.merchantName }}</div>
        <div class="merchant-items">
          <div class="product-item" v-for="item in group.items" :key="item.id">
            <el-image :src="item.productImage || item.productImg" fit="cover" class="product-img">
              <template #error>
                <div class="img-placeholder">暂无图片</div>
              </template>
            </el-image>
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-spec">默认规格 x{{ item.quantity }}</div>
            </div>
            <div class="product-price">¥{{ item.productPrice }}</div>
          </div>
        </div>
      </div>

      <div class="price-summary">
        <div class="price-row">
          <span>商品总额</span>
          <span>¥{{ totalAmount }}</span>
        </div>
        <div class="price-row">
          <span>运费</span>
          <span>¥0.00</span>
        </div>
        <div class="price-row total">
          <span>实付金额</span>
          <span class="price">¥{{ actualAmount }}</span>
        </div>
      </div>

      <div class="payment-section">
        <div class="pay-method-item" :class="{ active: payMethod === '虚拟支付宝' }" @click="payMethod = '虚拟支付宝'">
          <div class="radio-circle" :class="{ checked: payMethod === '虚拟支付宝' }">
            <div class="radio-dot" v-if="payMethod === '虚拟支付宝'"></div>
          </div>
          <span class="method-icon">💙</span>
          <span class="method-name">支付宝</span>
        </div>
        <div class="pay-method-item" :class="{ active: payMethod === '虚拟微信' }" @click="payMethod = '虚拟微信'">
          <div class="radio-circle" :class="{ checked: payMethod === '虚拟微信' }">
            <div class="radio-dot" v-if="payMethod === '虚拟微信'"></div>
          </div>
          <span class="method-icon">💚</span>
          <span class="method-name">微信支付</span>
        </div>
      </div>

      <div class="submit-bar">
        <div class="submit-inner">
          <el-button class="submit-btn" size="large" @click="submitOrder" :disabled="!address">
            提交订单  ¥{{ actualAmount }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCartList, createOrder, getProductDetail, getAddressList } from '@/api'
import { ElMessage } from 'element-plus'
import { Loading, Location, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const cartItems = ref([])
const address = ref('')
const payMethod = ref('虚拟支付宝')
const isDirectBuy = ref(false)

const loadData = async () => {
  loading.value = true
  
  const selectedAddress = sessionStorage.getItem('selectedAddress')
  if (selectedAddress) {
    address.value = selectedAddress
  } else {
    try {
      const addrRes = await getAddressList()
      const list = addrRes.data || []
      const defaultAddr = list.find(a => a.isDefault === 1) || list[0]
      if (defaultAddr) {
        address.value = `${defaultAddr.name} ${defaultAddr.phone} ${defaultAddr.province}${defaultAddr.city}${defaultAddr.district}${defaultAddr.detail}`
      }
    } catch (e) {
      console.error('获取地址失败', e)
    }
  }

  // 判断是直接购买还是购物车结算
  const productId = route.query.productId
  const buyQuantity = parseInt(route.query.quantity) || 1

  if (productId) {
    // 直接购买模式
    isDirectBuy.value = true
    const queryPrice = parseFloat(route.query.price)
    const querySpecs = route.query.specs || '默认规格'
    try {
      const res = await getProductDetail(productId)
      const p = res.data?.product
      if (p) {
        cartItems.value = [{
          productId: p.id,
          productName: p.name,
          productPrice: queryPrice || p.price,
          productImage: p.coverImg,
          productImg: p.coverImg,
          merchantId: p.merchantId,
          merchantName: res.data?.merchant?.shopName || '商家店铺',
          quantity: buyQuantity,
          specs: querySpecs
        }]
      }
    } catch (e) {
      ElMessage.error('加载商品信息失败')
    } finally {
      loading.value = false
    }
  } else {
    // 购物车结算模式
    let cartIds = []
    if (route.query.cartIds) {
      cartIds = route.query.cartIds.split(',').map(Number)
    } else {
      const stored = sessionStorage.getItem('checkoutCartIds')
      if (stored) cartIds = JSON.parse(stored)
    }

    try {
      const res = await getCartList()
      const allCart = res.data || []
      cartItems.value = cartIds.length > 0
        ? allCart.filter(item => cartIds.includes(item.id))
        : allCart
    } catch (e) {
      ElMessage.error('加载购物车失败')
    } finally {
      loading.value = false
    }
  }
}

onMounted(() => {
  loadData()
})

// 监听路由变化，当从地址页返回时重新加载
watch(() => route.query, () => {
  loadData()
}, { immediate: true })

const groupedCart = computed(() => {
  const groups = {}
  cartItems.value.forEach(item => {
    const name = item.merchantName || '商家店铺'
    if (!groups[name]) {
      groups[name] = { merchantName: name, items: [] }
    }
    groups[name].items.push(item)
  })
  return Object.values(groups)
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0).toFixed(2)
})

const actualAmount = computed(() => {
  return totalAmount.value
})

const totalCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const addressName = computed(() => {
  if (!address.value) return ''
  const parts = address.value.split(' ')
  return `${parts[0] || ''}  ${parts[1] || ''}`
})

const addressDetail = computed(() => {
  if (!address.value) return ''
  const parts = address.value.split(' ')
  return parts.slice(2).join(' ') || ''
})

const goBack = () => {
  router.push('/cart')
}

const openAddressSelector = () => {
  // 保留当前所有查询参数，确保返回时商品信息不丢失
  const currentQuery = { ...route.query, return: 'checkout' }
  router.push({ path: '/address', query: currentQuery })
}

const submitOrder = async () => {
  if (!address.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  try {
    let res
    if (isDirectBuy.value) {
      // 直接购买：传商品信息
      const item = cartItems.value[0]
      res = await createOrder({
        items: [{
          productId: item.productId,
          quantity: item.quantity,
          merchantId: item.merchantId,
          price: item.productPrice,
          specs: item.specs
        }],
        address: address.value
      })
    } else {
      // 购物车结算：传购物车ID
      const cartIds = cartItems.value.map(item => item.id)
      res = await createOrder({ cartIds, address: address.value })
    }

    if (res.code === 200) {
      const orders = res.data || []
      const orderIds = orders.map(o => o.id).join(',')
      router.push({
        path: '/pay',
        query: {
          orderId: orderIds,
          amount: actualAmount.value,
          payMethod: payMethod.value
        }
      })
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch (e) {
    ElMessage.error('下单失败')
  }
}


</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
}

.title {
  font-size: 22px;
  font-weight: bold;
  color: #ff6700;
}

.section {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.section:hover {
  border-color: #1890ff;
}

.address-icon {
  font-size: 24px;
  color: #ff6700;
  margin-right: 12px;
  flex-shrink: 0;
}

.address-content {
  flex: 1;
}

.address-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.address-detail {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
}

.arrow {
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}

.merchant-group {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.merchant-header {
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-weight: bold;
  font-size: 14px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px dashed #e8e8e8;
}

.product-item:last-child {
  border-bottom: none;
}

.product-img {
  width: 80px;
  height: 80px;
  margin-right: 16px;
  flex-shrink: 0;
}

.img-placeholder {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
  font-size: 12px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.product-spec {
  font-size: 12px;
  color: #999;
}

.product-price {
  color: #ff6700;
  font-weight: bold;
}

.price-summary {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.price-row.total {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
}

.payment-section {
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 80px;
}

.payment-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.pay-method-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 10px;
  border: 2px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}

.pay-method-item:last-child {
  margin-bottom: 0;
}

.pay-method-item.active {
  border-color: #ff6700;
  background: #fff8f0;
}

.radio-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.radio-circle.checked {
  border-color: #ff6700;
}

.radio-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ff6700;
}

.method-icon {
  font-size: 22px;
}

.method-name {
  font-size: 15px;
  color: #333;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  padding: 12px 0;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: #f5f5f5;
  z-index: 100;
}

.submit-inner {
  width: 100%;
  max-width: 1200px;
  display: flex;
  justify-content: flex-end;
  padding: 0 25px;
}

.submit-btn {
  height: 44px;
  padding: 0 56px;
  font-size: 16px;
  font-weight: bold;
  color: #fff !important;
  background: #ff6700 !important;
  border: none !important;
  border-radius: 22px;
}

.submit-btn:hover {
  background: #ff8533 !important;
}

.submit-btn:disabled {
  background: #ccc !important;
  color: #fff !important;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
