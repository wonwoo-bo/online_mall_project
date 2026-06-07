<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索商品"
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-content" v-loading="loading">
      <!-- 搜索提示 -->
      <div class="search-tip" v-if="keyword && !loading">
        <span>搜索 "<strong>{{ keyword }}</strong>" 共找到 <strong>{{ total }}</strong> 件商品</span>
      </div>

      <!-- 商品列表 -->
      <div v-if="products.length > 0" class="product-grid">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="goToDetail(product.id)"
        >
          <div class="product-image">
            <img :src="product.coverImg || defaultImage" :alt="product.name">
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span class="original-price" v-if="product.originalPrice && product.originalPrice > product.price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-meta">
              <span class="sales">已售 {{ product.sales || 0 }} 件</span>
              <span class="reviews">{{ product.reviewCount || 0 }} 条评价</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading && keyword" class="empty-state">
        <el-empty :description="`未找到与 '${keyword}' 相关的商品`">
          <template #image>
            <div style="font-size: 64px;">🔍</div>
          </template>
          <el-button type="primary" @click="keyword = ''">清空搜索</el-button>
        </el-empty>
      </div>

      <!-- 初始状态 -->
      <div v-else-if="!loading && !keyword" class="initial-state">
        <el-empty description="请输入关键词搜索商品">
          <template #image>
            <div style="font-size: 64px;">📦</div>
          </template>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="products.length > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadProducts"
        @current-change="loadProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '@/api'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const loading = ref(false)
const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)

const defaultImage = 'https://via.placeholder.com/200x200/FF5000/FFFFFF?text=商品'

const loadProducts = async () => {
  if (!keyword.value.trim()) {
    products.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const res = await searchProducts(keyword.value, page.value, size.value)
    if (res.code === 200) {
      products.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      products.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('搜索失败', e)
    ElMessage.error('搜索失败，请重试')
    products.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/products/${id}`)
}

// 监听路由参数变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    loadProducts()
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    loadProducts()
  }
})
</script>

<style scoped>
.search-page {
  padding-bottom: 40px;
}

.search-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-box {
  max-width: 600px;
  margin: 0 auto;
}

.search-content {
  min-height: 400px;
}

.search-tip {
  padding: 16px 0;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.search-tip strong {
  color: #FF5000;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: normal;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
}

.product-price {
  margin-bottom: 5px;
}

.product-price .price {
  color: #FF5000;
  font-size: 18px;
  font-weight: bold;
}

.product-price .original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}

.empty-state,
.initial-state {
  background: white;
  padding: 80px 20px;
  border-radius: 8px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
