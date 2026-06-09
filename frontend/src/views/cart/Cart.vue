<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="title">购物车</h1>
      <el-button text @click="toggleManage">{{ manageMode ? '完成' : '管理' }}</el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><Loading /></el-icon> 加载中...
    </div>

    <div v-else-if="cartList.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="goHome">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <div class="merchant-group" v-for="group in groupedCart" :key="group.merchantName">
        <div class="merchant-header">
          <el-checkbox
            v-model="group.checked"
            @change="toggleMerchantSelect(group)"
          >{{ group.merchantName }}</el-checkbox>
        </div>
        <div class="merchant-items">
          <div class="cart-item" v-for="item in group.items" :key="item.id">
            <el-checkbox v-model="item.checked" @change="updateTotal" />
            <el-image :src="item.productImg" fit="cover" class="item-img">
              <template #error>
                <div class="img-placeholder">暂无图片</div>
              </template>
            </el-image>
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-spec">{{ item.specs || '默认规格' }}</div>
              <div class="item-bottom">
                <span class="item-price">¥{{ item.productPrice }}</span>
                <div class="item-bottom-right">
                  <el-input-number
                    v-model="item.quantity"
                    :min="1"
                    :max="99"
                    size="small"
                    @change="updateQuantity(item)"
                  />
                  <el-button
                    v-if="manageMode"
                    type="danger"
                    text
                    @click="deleteItem(item.id)"
                  >删除</el-button>
                  <el-icon v-else class="delete-icon" @click.stop="deleteItem(item.id)"><Delete /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="cart-footer" v-if="cartList.length > 0">
      <div class="footer-inner">
        <div class="footer-left">
          <el-checkbox v-model="selectAll" @change="toggleSelectAll">全选</el-checkbox>
        </div>
        <div class="footer-right">
          <div class="total-info" v-if="!manageMode">
            合计：<span class="price">¥{{ totalPrice }}</span>
          </div>
          <el-button v-if="manageMode" type="danger" @click="deleteSelected">删除</el-button>
          <el-button v-else class="checkout-btn" @click="goCheckout" :disabled="selectedCount === 0">
            结算({{ selectedCount }})
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCart, deleteCart } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(true)
const manageMode = ref(false)
const cartList = ref([])
const selectAll = ref(false)
const groupedCart = ref([])

const updateGroupedCart = () => {
  const groups = {}
  cartList.value.forEach(item => {
    const name = item.merchantName || '商家店铺'
    if (!groups[name]) {
      groups[name] = { merchantName: name, checked: false, items: [] }
    }
    groups[name].items.push(item)
  })
  const newGroups = Object.values(groups)
  newGroups.forEach(g => {
    const oldGroup = groupedCart.value.find(og => og.merchantName === g.merchantName)
    if (oldGroup) {
      g.checked = oldGroup.checked
    }
  })
  groupedCart.value = newGroups
}

const totalPrice = computed(() => {
  let total = 0
  cartList.value.forEach(item => {
    if (item.checked) {
      total += item.productPrice * item.quantity
    }
  })
  return total.toFixed(2)
})

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.checked).length
})

const goHome = () => {
  router.push('/')
}

const toggleManage = () => {
  manageMode.value = !manageMode.value
  cartList.value.forEach(item => item.checked = false)
  selectAll.value = false
  groupedCart.value.forEach(g => g.checked = false)
}

const toggleSelectAll = (val) => {
  cartList.value.forEach(item => item.checked = val)
  groupedCart.value.forEach(g => g.checked = val)
}

const toggleMerchantSelect = (group) => {
  group.items.forEach(item => item.checked = group.checked)
  selectAll.value = cartList.value.every(item => item.checked)
}

const updateTotal = () => {
  selectAll.value = cartList.value.every(item => item.checked)
  groupedCart.value.forEach(g => {
    g.checked = g.items.every(item => item.checked)
  })
  groupedCart.value = [...groupedCart.value]
}

const updateQuantity = async (item) => {
  try {
    await updateCart({ id: item.id, quantity: item.quantity })
  } catch (e) {
    ElMessage.error('修改数量失败')
  }
}

const deleteItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
    await deleteCart(id)
    cartList.value = cartList.value.filter(item => item.id !== id)
    updateGroupedCart()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const deleteSelected = async () => {
  const selected = cartList.value.filter(item => item.checked)
  if (selected.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除选中的${selected.length}件商品？`, '提示', { type: 'warning' })
    for (const item of selected) {
      await deleteCart(item.id)
    }
    cartList.value = cartList.value.filter(item => !item.checked)
    updateGroupedCart()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const goCheckout = () => {
  const selected = cartList.value.filter(item => item.checked)
  if (selected.length === 0) {
    ElMessage.warning('请选择商品')
    return
  }
  sessionStorage.setItem('checkoutCartIds', JSON.stringify(selected.map(item => item.id)))
  router.push('/checkout')
}

onMounted(async () => {
  try {
    const res = await getCartList()
    cartList.value = (res.data || []).map(item => ({ ...item, checked: false }))
    updateGroupedCart()
  } catch (e) {
    ElMessage.error('加载购物车失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
}

.title {
  flex: 1;
  font-size: 22px;
  font-weight: bold;
  color: #ff6700;
}

.cart-content {
  padding: 16px;
  padding-bottom: 80px;
}

.merchant-group {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.merchant-header {
  padding: 12px 16px;
  background: #fafafa;
  font-weight: bold;
}

.cart-item {
  display: flex;
  align-items: stretch;
  padding: 16px;
}

.cart-item :deep(.el-checkbox) {
  align-self: center;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-img {
  width: 80px;
  height: 80px;
  margin: 0 16px;
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

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.item-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-bottom-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-price {
  color: #ff6700;
  font-weight: bold;
}

.delete-icon {
  font-size: 18px;
  color: #999;
  cursor: pointer;
}

.delete-icon:hover {
  color: #ff4757;
}

.cart-footer {
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

.footer-inner {
  width: 100%;
  max-width: 1200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 25px;
}

.footer-inner :deep(.el-checkbox__label) {
  font-size: 16px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-info {
  font-size: 18px;
  color: #333;
  font-weight: bold;
}

.total-info .price {
  font-size: 22px;
  color: #ff6700;
}

.checkout-btn {
  background: #ff6700 !important;
  border-color: #ff6700 !important;
  color: #fff !important;
  font-size: 16px;
}

.checkout-btn:hover {
  background: #ff8533 !important;
  border-color: #ff8533 !important;
}

.checkout-btn:disabled {
  background: #ccc !important;
  border-color: #ccc !important;
  color: #fff !important;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-cart {
  padding: 60px 20px;
}
</style>
