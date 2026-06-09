<template>
  <div class="favorite-page">
    <h2 class="page-title">我的收藏</h2>
    <div class="favorite-list" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无收藏商品">
          <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
        </el-empty>
      </div>
      <div v-else class="product-grid">
        <div v-for="item in list" :key="item.id" class="product-card">
          <div class="product-image" @click="$router.push(`/products/${item.productId}`)">
            <img :src="item.coverImg || defaultImage" :alt="item.productName">
          </div>
          <div class="product-info">
            <h4 class="product-name" @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h4>
            <div class="product-price">
              <span class="price">¥{{ item.price }}</span>
            </div>
            <div class="product-actions">
              <el-button type="primary" size="small" @click="handleAddCart(item)">加入购物车</el-button>
              <el-button type="danger" size="small" text @click="handleRemove(item)">
                <el-icon><Delete /></el-icon> 取消收藏
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavorites, deleteFavorite, addCart } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserId } from '@/utils/auth'

const loading = ref(false)
const list = ref([])
const defaultImage = 'https://via.placeholder.com/200x200/FF5000/FFFFFF?text=商品'

const loadList = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    const res = await getFavorites(userId)
    list.value = res.data?.list || []
  } finally {
    loading.value = false
  }
}

const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定取消收藏该商品吗？', '提示')
    const userId = getUserId()
    await deleteFavorite(item.id, userId)
    list.value = list.value.filter(i => i.id !== item.id)
    ElMessage.success('取消收藏成功')
  } catch (e) {}
}

const handleAddCart = async (item) => {
  try {
    await addCart({
      productId: item.productId,
      quantity: 1
    })
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('加入购物车失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.favorite-page { padding-bottom: 40px; }
.page-title { margin-bottom: 20px; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card { background: white; border-radius: 8px; overflow: hidden; }
.product-image { width: 100%; height: 180px; cursor: pointer; }
.product-image img { width: 100%; height: 100%; object-fit: cover; }
.product-info { padding: 15px; }
.product-name { font-size: 14px; cursor: pointer; margin-bottom: 10px; }
.product-name:hover { color: #FF5000; }
.product-price { margin-bottom: 10px; }
.product-price .price { color: #FF5000; font-size: 18px; font-weight: bold; }
.product-actions { display: flex; justify-content: space-between; }
</style>
