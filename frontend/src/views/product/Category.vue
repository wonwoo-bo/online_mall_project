<template>
  <div class="category-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div class="category-container" v-else>
      <!-- 左侧大类导航 -->
      <div class="category-sidebar">
        <div class="sidebar-header">
          <el-icon><Menu /></el-icon>
          <span>全部分类</span>
        </div>
        <div class="sidebar-list">
          <div
            v-for="category in categories"
            :key="category.id"
            class="sidebar-item"
            :class="{ active: activeCategory?.id === category.id }"
            @click="selectCategory(category)"
            @mouseenter="selectCategory(category)"
          >
            <div class="item-icon" :style="{ background: getCategoryColor(category.id) + '15' }">
              <el-icon :size="18" :color="getCategoryColor(category.id)">
                <component :is="getCategoryIcon(category.id)" />
              </el-icon>
            </div>
            <span class="item-name">{{ category.name }}</span>
            <el-icon class="item-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 右侧小类展示 -->
      <div class="category-content">
        <template v-if="activeCategory">
          <!-- 大类标题 -->
          <div class="content-header">
            <div class="header-icon" :style="{ background: getCategoryColor(activeCategory.id) }">
              <el-icon :size="24" color="white">
                <component :is="getCategoryIcon(activeCategory.id)" />
              </el-icon>
            </div>
            <div class="header-info">
              <h2>{{ activeCategory.name }}</h2>
              <p>共 {{ activeCategory.children?.length || 0 }} 个分类</p>
            </div>
          </div>

          <!-- 小类网格 -->
          <div class="subcategory-grid" v-if="activeCategory.children?.length">
            <div
              v-for="sub in activeCategory.children"
              :key="sub.id"
              class="subcategory-card"
              @click="goToProductList(sub, activeCategory)"
            >
              <div class="card-icon" :style="{ background: getCategoryColor(activeCategory.id) + '10' }">
                <el-icon :size="32" :color="getCategoryColor(activeCategory.id)">
                  <Goods />
                </el-icon>
              </div>
              <div class="card-name">{{ sub.name }}</div>
              <div class="card-arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="empty-state" v-else>
            <el-empty description="暂无子分类">
              <el-button type="primary" round @click="$router.push('/products')">
                查看全部商品
              </el-button>
            </el-empty>
          </div>

          <!-- 热门推荐 -->
          <div class="hot-section" v-if="activeCategory.children?.length">
            <div class="section-title">
              <el-icon><TrendCharts /></el-icon>
              <span>热门推荐</span>
            </div>
            <div class="hot-products" v-if="hotProducts.length">
              <div
                v-for="product in hotProducts"
                :key="product.id"
                class="hot-product-card"
                @click="goToProduct(product)"
              >
                <el-image :src="product.coverImg || product.image" fit="cover" class="hot-product-img">
                  <template #error>
                    <div class="img-placeholder">暂无图片</div>
                  </template>
                </el-image>
                <div class="hot-product-info">
                  <div class="hot-product-name">{{ product.name }}</div>
                  <div class="hot-product-price">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="sales" v-if="product.sales">已售{{ product.sales }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="hot-tags" v-else-if="!hotLoading">
              <el-tag
                v-for="sub in activeCategory.children?.slice(0, 6)"
                :key="'hot-' + sub.id"
                :color="getCategoryColor(activeCategory.id)"
                effect="dark"
                round
                class="hot-tag"
                @click="goToProductList(sub, activeCategory)"
              >
                {{ sub.name }}
              </el-tag>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCategories, getProducts } from '@/api'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const categories = ref([])
const activeCategory = ref(null)
const hotProducts = ref([])
const hotLoading = ref(false)

// 分类颜色映射
const categoryColors = {
  1: '#FF4757',   // 服饰鞋包 - 红色
  2: '#FF6B9D',   // 美妆个护 - 粉色
  3: '#3742FA',   // 数码家电 - 蓝色
  4: '#2ED573',   // 家居生活 - 绿色
  5: '#FFA502',   // 母婴玩具 - 橙色
  6: '#1DD1A1',   // 食品生鲜 - 青色
  7: '#5352ED',   // 运动户外 - 紫蓝
  8: '#A55EEA',   // 图书办公 - 紫色
  9: '#57606F',   // 汽车用品 - 灰色
  10: '#F9CA24',  // 珠宝配饰 - 金色
  11: '#26DE81',  // 医药健康 - 翠绿
  12: '#9B59B6'   // 虚拟服务 - 淡紫
}

// 分类图标映射
const categoryIcons = {
  1: 'ShoppingBag',
  2: 'MagicStick',
  3: 'Monitor',
  4: 'House',
  5: 'Present',
  6: 'Food',
  7: 'Trophy',
  8: 'Reading',
  9: 'Van',
  10: 'Watch',
  11: 'FirstAidKit',
  12: 'Ticket'
}

const getCategoryColor = (id) => categoryColors[id] || '#409EFF'
const getCategoryIcon = (id) => categoryIcons[id] || 'Grid'

// 加载分类数据
const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []

    // 从 URL 参数中获取 categoryId
    const categoryId = parseInt(route.query.categoryId)
    if (categoryId) {
      // 选中 URL 中指定的分类
      const targetCategory = categories.value.find(c => c.id === categoryId)
      if (targetCategory) {
        activeCategory.value = targetCategory
      } else if (categories.value.length > 0) {
        activeCategory.value = categories.value[0]
      }
    } else if (categories.value.length > 0) {
      // 默认选中第一个分类
      activeCategory.value = categories.value[0]
    }
    // 初始加载热门推荐
    if (activeCategory.value) {
      loadHotProducts(activeCategory.value)
    }
  } catch (e) {
    console.error('加载分类失败', e)
  } finally {
    loading.value = false
  }
}

// 监听分类变化，自动加载热门推荐
watch(() => activeCategory.value, (newCat) => {
  if (newCat) loadHotProducts(newCat)
})

// 选择分类
const selectCategory = (category) => {
  activeCategory.value = category
}

// 加载热门推荐商品
const loadHotProducts = async (category) => {
  if (!category) return
  hotLoading.value = true
  hotProducts.value = []
  try {
    // 用父分类ID查询商品
    const res = await getProducts({ categoryId: category.id, size: 6, sort: 'sales' })
    hotProducts.value = res.data?.list || []
  } catch (e) {
    console.error('加载热门推荐失败', e)
  } finally {
    hotLoading.value = false
  }
}

// 跳转到商品详情
const goToProduct = (product) => {
  router.push(`/products/${product.id}`)
}

// 跳转到商品列表页
const goToProductList = (subCategory, parentCategory) => {
  router.push({
    path: '/products',
    query: {
      categoryId: subCategory.id,
      categoryName: subCategory.name,
      parentName: parentCategory.name
    }
  })
}

onMounted(loadCategories)
</script>

<style scoped>
.category-page {
  min-height: calc(100vh - 180px);
}

/* 加载状态 */
.loading-container {
  background: white;
  padding: 40px;
  border-radius: 12px;
}

/* 主容器 - 左右分栏 */
.category-container {
  display: flex;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  min-height: 600px;
}

/* 左侧导航栏 */
.category-sidebar {
  width: 220px;
  background: linear-gradient(180deg, #fafafa 0%, #f5f5f5 100%);
  border-right: 1px solid #eee;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #eee;
  background: white;
}

.sidebar-list {
  padding: 8px 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
  margin: 2px 0;
}

.sidebar-item:hover {
  background: white;
}

.sidebar-item.active {
  background: white;
  border-left-color: var(--category-color, #FF5000);
}

.sidebar-item .item-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sidebar-item .item-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.sidebar-item.active .item-name {
  color: var(--category-color, #FF5000);
}

.sidebar-item .item-arrow {
  color: #ccc;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}

.sidebar-item:hover .item-arrow,
.sidebar-item.active .item-arrow {
  opacity: 1;
  color: var(--category-color, #FF5000);
}

/* 右侧内容区 */
.category-content {
  flex: 1;
  padding: 24px;
  background: white;
}

/* 大类标题 */
.content-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.header-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-info h2 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.header-info p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 小类网格 */
.subcategory-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.subcategory-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 12px;
  background: #fafafa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  border: 2px solid transparent;
}

.subcategory-card:hover {
  background: white;
  border-color: var(--category-color, #FF5000);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.subcategory-card .card-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: transform 0.3s;
}

.subcategory-card:hover .card-icon {
  transform: scale(1.1);
}

.subcategory-card .card-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  text-align: center;
}

.subcategory-card:hover .card-name {
  color: var(--category-color, #FF5000);
}

.subcategory-card .card-arrow {
  position: absolute;
  right: 8px;
  top: 8px;
  color: #ccc;
  opacity: 0;
  transition: all 0.3s;
}

.subcategory-card:hover .card-arrow {
  opacity: 1;
  color: var(--category-color, #FF5000);
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
}

/* 热门推荐 */
.hot-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.section-title .el-icon {
  color: #FF5000;
}

.hot-products {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.hot-product-card {
  background: #fafafa;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.hot-product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #eee;
}

.hot-product-img {
  width: 100%;
  height: 140px;
  display: block;
}

.img-placeholder {
  width: 100%;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  color: #999;
  font-size: 12px;
}

.hot-product-info {
  padding: 10px 12px;
}

.hot-product-name {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}

.hot-product-price {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.hot-product-price .price {
  font-size: 16px;
  font-weight: bold;
  color: #FF5000;
}

.hot-product-price .sales {
  font-size: 11px;
  color: #999;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  cursor: pointer;
  font-size: 13px;
  padding: 8px 16px;
  height: auto;
  transition: all 0.3s;
}

.hot-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 响应式 */
@media (max-width: 768px) {
  .category-container {
    flex-direction: column;
  }

  .category-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #eee;
  }

  .sidebar-list {
    display: flex;
    overflow-x: auto;
    padding: 8px;
    gap: 4px;
  }

  .sidebar-item {
    flex-shrink: 0;
    padding: 10px 14px;
    border-left: none;
    border-bottom: 2px solid transparent;
    border-radius: 8px;
  }

  .sidebar-item.active {
    border-bottom-color: var(--category-color, #FF5000);
  }

  .sidebar-item .item-arrow {
    display: none;
  }

  .subcategory-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .subcategory-card {
    padding: 16px 8px;
  }

  .subcategory-card .card-icon {
    width: 48px;
    height: 48px;
  }
}
</style>
