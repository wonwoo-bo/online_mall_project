<template>
  <div class="history-page">
    <div class="page-header">
      <h2 class="page-title">浏览历史</h2>
      <el-button type="danger" text @click="handleClear" v-if="list.length > 0">
        <el-icon><Delete /></el-icon> 清空历史
      </el-button>
    </div>
    <div class="history-list" v-loading="loading">
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无浏览记录" />
      </div>
      <div v-else class="history-grid">
        <div v-for="item in list" :key="item.id" class="history-card">
          <el-button class="delete-btn" type="danger" :icon="Delete" circle size="small" @click="handleDelete(item.id)" />
          <div class="product-image" @click="$router.push(`/products/${item.productId}`)">
            <img :src="item.coverImg || defaultImage" :alt="item.productName">
          </div>
          <div class="product-info">
            <h4 class="product-name" @click="$router.push(`/products/${item.productId}`)">{{ item.productName }}</h4>
            <div class="product-price"><span class="price">¥{{ item.price }}</span></div>
            <div class="view-time">{{ item.viewTime }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHistory, deleteHistory, clearHistory } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getUserId } from '@/utils/auth'

const loading = ref(false)
const list = ref([])
const defaultImage = 'https://via.placeholder.com/200x200/FF5000/FFFFFF?text=商品'

const loadList = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    const res = await getHistory(userId)
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteHistory(id)
    list.value = list.value.filter(i => i.id !== id)
    ElMessage.success('删除成功')
  } catch (e) {}
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空所有浏览记录吗？', '提示')
    const userId = getUserId()
    await clearHistory(userId)
    list.value = []
    ElMessage.success('清空成功')
  } catch (e) {}
}

onMounted(loadList)
</script>

<style scoped>
.history-page { padding-bottom: 40px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.history-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.history-card { background: white; border-radius: 8px; overflow: hidden; position: relative; }
.delete-btn { position: absolute; top: 10px; right: 10px; opacity: 0; transition: opacity 0.3s; z-index: 10; }
.history-card:hover .delete-btn { opacity: 1; }
.product-image { width: 100%; height: 180px; cursor: pointer; }
.product-image img { width: 100%; height: 100%; object-fit: cover; }
.product-info { padding: 15px; }
.product-name { font-size: 14px; cursor: pointer; margin-bottom: 10px; }
.product-name:hover { color: #FF5000; }
.product-price { margin-bottom: 5px; }
.product-price .price { color: #FF5000; font-size: 18px; font-weight: bold; }
.view-time { color: #999; font-size: 12px; }
</style>
