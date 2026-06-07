<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <el-carousel height="400px">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ background: item.color }">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description || item.desc }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 推荐商品 -->
    <section class="section">
      <div class="section-header">
        <h3><el-icon><Goods /></el-icon> 推荐商品</h3>
        <router-link to="/products">查看更多 <el-icon><ArrowRight /></el-icon></router-link>
      </div>
      <div class="product-grid" v-loading="loading">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="$router.push(`/products/${product.id}`)"
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
            <div class="product-sales">已售 {{ product.sales || 0 }} 件</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="section">
      <div class="section-header">
        <h3><el-icon><List /></el-icon> 商品分类</h3>
      </div>
      <div class="category-grid" v-loading="categoryLoading">
        <div
          v-for="category in categories.slice(0, 8)"
          :key="category.id"
          class="category-card"
          @click="$router.push(`/category?categoryId=${category.id}`)"
        >
          <el-icon :size="32"><Goods /></el-icon>
          <span>{{ category.name }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProducts, getCategories, getActiveBanners } from '@/api'
import { Goods, ArrowRight, List } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const categoryLoading = ref(false)
const products = ref([])
const categories = ref([])

const defaultImage = 'https://via.placeholder.com/200x200/FF5000/FFFFFF?text=商品'

const banners = ref([])

onMounted(async () => {
  // 加载轮播图
  try {
    const bannerRes = await getActiveBanners()
    if (bannerRes.code === 200 && bannerRes.data?.length > 0) {
      banners.value = bannerRes.data
    } else {
      // 后端无数据时使用默认
      banners.value = [
        { id: 1, title: '618年中大促', description: '全场低至5折起', color: '#FF5000' },
        { id: 2, title: '新品首发', description: '最新数码产品抢先体验', color: '#FF1A1A' },
        { id: 3, title: '品质生活', description: '精选好物，品质保证', color: '#FF7F00' }
      ]
    }
  } catch (e) {
    banners.value = [
      { id: 1, title: '618年中大促', description: '全场低至5折起', color: '#FF5000' },
      { id: 2, title: '新品首发', description: '最新数码产品抢先体验', color: '#FF1A1A' },
      { id: 3, title: '品质生活', description: '精选好物，品质保证', color: '#FF7F00' }
    ]
  }

  loading.value = true
  try {
    const res = await getProducts({ page: 1, size: 8 })
    products.value = res.data?.list || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }

  categoryLoading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    categoryLoading.value = false
  }
})
</script>

<style scoped>
.home-page {
  padding-bottom: 40px;
}

.banner-section {
  margin-bottom: 30px;
}

.banner-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
}

.banner-item h2 {
  font-size: 36px;
  margin-bottom: 10px;
}

.banner-item p {
  font-size: 18px;
}

.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header a {
  color: #FF5000;
  text-decoration: none;
  display: flex;
  align-items: center;
  font-size: 14px;
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

.product-sales {
  color: #999;
  font-size: 12px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 15px;
}

.category-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover {
  background: #FFF5F0;
  color: #FF5000;
}

.category-card span {
  font-size: 14px;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .category-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>