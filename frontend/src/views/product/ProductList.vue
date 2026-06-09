<template>
  <div class="product-list-page">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="categoryName">{{ categoryName }}</el-breadcrumb-item>
          <el-breadcrumb-item v-else>全部商品</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="filter-right">
        <el-select v-model="sortBy" placeholder="排序方式" @change="loadProducts">
          <el-option label="综合排序" value="default" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="销量优先" value="sales" />
        </el-select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 商品列表 -->
    <div v-else-if="products.length > 0" class="product-grid">
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
    <div v-else class="empty-state">
      <el-empty description="暂无商品">
        <template #image>
          <div style="font-size: 64px;">📦</div>
        </template>
        <el-button type="primary" @click="$router.push('/')">去首页逛逛</el-button>
      </el-empty>
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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProducts } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const sortBy = ref('default')

const defaultImage = 'https://via.placeholder.com/200x200/FF5000/FFFFFF?text=商品'

const categoryId = computed(() => route.query.categoryId)
const categoryName = computed(() => route.query.categoryName || route.query.parentName)

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      sort: sortBy.value
    }
    if (categoryId.value) {
      params.categoryId = categoryId.value
    }
    if (route.query.keyword) {
      params.keyword = route.query.keyword
    }
    
    const res = await getProducts(params)
    if (res.code === 200) {
      products.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      products.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('加载商品失败', e)
    ElMessage.error('加载商品失败')
    products.value = []
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-list-page {
  padding: 20px 0;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
}

.loading-container {
  background: white;
  padding: 40px;
  border-radius: 8px;
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

.empty-state {
  background: white;
  padding: 60px 20px;
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
