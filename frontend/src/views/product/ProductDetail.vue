<template>
  <div class="product-detail-page" v-loading="loading">
    <template v-if="product">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 商品基本信息 -->
      <div class="product-main">
        <!-- 左侧图片 -->
        <div class="product-gallery">
          <div class="main-image">
            <img :src="currentImage" :alt="product.name">
            <div v-if="!currentImage" class="image-placeholder">
              <el-icon :size="64" color="#ccc"><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>
          <div class="thumbnail-list" v-if="images && images.length > 1">
            <div
              v-for="(img, index) in images"
              :key="index"
              class="thumbnail"
              :class="{ active: currentImage === img.imageUrl }"
              @click="currentImage = img.imageUrl"
            >
              <img :src="img.imageUrl" :alt="`图片${index + 1}`">
            </div>
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.description }}</p>

          <div class="price-box">
            <span class="label">价格</span>
            <span class="price">¥{{ currentPrice }}</span>
            <span class="original-price" v-if="product.originalPrice && product.originalPrice > currentPrice">¥{{ product.originalPrice }}</span>
          </div>

          <div class="stats-row">
            <span>销量: {{ product.sales || 0 }}</span>
            <span>浏览: {{ product.views || 0 }}</span>
            <span>库存: {{ currentStock }}</span>
          </div>

          <!-- 规格选择区域 -->
          <div class="sku-section" v-if="skus && skus.length > 0">
            <span class="label">规格</span>
            <!-- 如果有规格分组数据，显示分组选择（像淘宝那样） -->
            <div v-if="specGroups && specGroups.length > 0">
              <div v-for="group in specGroups" :key="group.id" class="spec-group">
                <span class="spec-group-label">{{ group.name }}</span>
                <div class="spec-values">
                  <span
                    v-for="value in group.values"
                    :key="value.id"
                    class="spec-value"
                    :class="{ 
                      active: selectedSpecs[group.id] === value.id,
                      disabled: !isSpecAvailable(group.id, value.id)
                    }"
                    @click="selectSpec(group.id, value.id)"
                  >
                    {{ value.name }}
                  </span>
                </div>
              </div>
            </div>
            <!-- 如果没有规格分组数据，显示原始的SKU选择方式（备用） -->
            <div v-else class="sku-list">
              <span
                v-for="sku in skus"
                :key="sku.id"
                class="sku-item"
                :class="{ active: selectedSku?.id === sku.id }"
                @click="selectedSku = sku"
              >
                {{ sku.specs || '默认' }}
                <span v-if="sku.price !== product.price">¥{{ sku.price }}</span>
              </span>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section">
            <span class="label">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="currentStock || 99" />
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button
              :type="isFavorite ? 'danger' : 'default'"
              :icon="isFavorite ? 'StarFilled' : 'Star'"
              size="large"
              @click="handleFavorite"
            >
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button type="warning" size="large" @click="handleAddCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button type="primary" size="large" class="buy-btn" @click="handleBuy">
              <el-icon><Lightning /></el-icon> 立即购买
            </el-button>
          </div>
        </div>
      </div>

      <!-- Tab 切换 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content">
              <p>{{ product.description || '暂无详细描述' }}</p>
            </div>
          </el-tab-pane>

          <el-tab-pane :label="`商品评价(${reviewStats.total || 0})`" name="review">
            <!-- 评价统计 -->
            <div class="review-stats">
              <div class="stats-left">
                <div class="good-rate">
                  <span class="rate">{{ reviewStats.goodRate || 100 }}%</span>
                  <span class="label">好评率</span>
                </div>
              </div>
              <div class="stats-right">
                <div class="star-row" v-for="i in 5" :key="i">
                  <span class="star-label">{{ 6 - i }}星</span>
                  <el-progress
                    :percentage="reviewStats[`star${6 - i}`] || 0"
                    :stroke-width="10"
                    :show-text="false"
                  />
                  <span class="star-count">{{ reviewStats[`star${6 - i}Count`] || 0 }}</span>
                </div>
              </div>
            </div>

            <!-- 评价列表 -->
            <div class="review-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <span class="user-name">{{ review.userName }}</span>
                  <el-rate v-model="review.rating" disabled />
                  <span class="review-time">{{ review.createTime }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <div class="review-images" v-if="review.images?.length">
                  <el-image
                    v-for="(img, idx) in review.images"
                    :key="idx"
                    :src="img"
                    :preview-src-list="review.images"
                    fit="cover"
                    class="review-image"
                  />
                </div>
                <div class="review-append" v-if="review.appendContent">
                  <span class="append-label">追评：</span>
                  {{ review.appendContent }}
                </div>
                <div class="review-reply" v-if="review.reply">
                  <span class="reply-label">商家回复：</span>
                  {{ review.reply }}
                </div>
                <div class="review-actions">
                  <el-button text @click="handleLike(review)">
                    <svg class="like-icon" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14z"/>
                      <path d="M7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/>
                    </svg> {{ review.likeCount || 0 }}
                  </el-button>
                </div>
              </div>
              <el-empty v-if="reviews.length === 0" description="暂无评价" />
            </div>

            <div class="view-all" v-if="reviewStats.total > reviews.length">
              <el-button @click="$router.push(`/review/${product.id}`)">
                查看全部 {{ reviewStats.total }} 条评价
              </el-button>
            </div>
          </el-tab-pane>

          <el-tab-pane label="商家信息" name="merchant">
            <div class="merchant-info" v-if="merchant">
              <div class="merchant-avatar">
                <el-icon :size="40"><Shop /></el-icon>
              </div>
              <div class="merchant-detail">
                <h4>{{ merchant.shopName || '默认店铺' }}</h4>
                <p><el-icon><Phone /></el-icon> {{ merchant.contactPhone || '暂无联系方式' }}</p>
                <p><el-icon><Document /></el-icon> {{ merchant.shopDesc || '暂无店铺介绍' }}</p>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>

    <el-empty v-else-if="!loading" description="商品不存在或已下架" />

    <!-- 加入购物车成功弹窗 -->
    <el-dialog v-model="cartSuccessVisible" width="340px" :show-close="true" center>
      <div class="cart-success-content">
        <div class="cart-success-icon">✅</div>
        <div class="cart-success-title">成功加入购物车</div>
      </div>
      <template #footer>
        <div class="cart-success-btns">
          <el-button type="primary" @click="goToCart">去购物车</el-button>
          <el-button @click="cartSuccessVisible = false">继续购物</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail, toggleFavorite, addCart, likeReview } from '@/api'
import { ElMessage } from 'element-plus'
import { getUserId } from '@/utils/auth'
import { Picture } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const product = ref(null)
const cartSuccessVisible = ref(false)
const images = ref([])
const skus = ref([])
const specGroups = ref([])
const merchant = ref(null)
const reviewStats = ref({})
const reviews = ref([])
const isFavorite = ref(false)

const currentImage = ref('')
const selectedSku = ref(null)
const selectedSpecs = ref({}) // 存储用户选择的规格 { typeId: valueId }
const quantity = ref(1)
const activeTab = ref('detail')

const defaultImage = 'https://via.placeholder.com/400x400/FF5000/FFFFFF?text=商品'

// 当前选中规格的价格和库存
const currentPrice = computed(() => {
  return selectedSku.value?.price || product.value?.price || 0
})

const currentStock = computed(() => {
  return selectedSku.value?.stock || product.value?.stock || 0
})

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const userId = getUserId()
    const res = await getProductDetail(id, userId)
    product.value = res.data?.product
    images.value = res.data?.images || []
    skus.value = res.data?.skus || []
    specGroups.value = res.data?.specGroups || []
    // 自动选中第一个SKU，并初始化规格选择
    if (skus.value.length > 0) {
      selectedSku.value = skus.value[0]
      // 解析第一个SKU的规格，初始化selectedSpecs
      if (selectedSku.value?.specsJson) {
        selectedSpecs.value = parseSpecsJson(selectedSku.value.specsJson)
      }
    }
    merchant.value = res.data?.merchant
    reviewStats.value = res.data?.reviewStats || {}
    isFavorite.value = res.data?.isFavorite || false

    // 设置默认图片
    if (images.value.length > 0) {
      currentImage.value = images.value[0].imageUrl
    } else {
      currentImage.value = product.value?.coverImg || defaultImage
    }

    // 加载评价
    if (res.data?.reviewStats?.total > 0) {
      const reviewRes = await fetch(`/api/reviews?productId=${id}&page=1&size=3`).then(r => r.json())
      if (reviewRes.code === 200) {
        reviews.value = reviewRes.data.list || []
      }
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
}

// 解析规格JSON字符串
const parseSpecsJson = (specsJson) => {
  try {
    const specs = {}
    specsJson = specsJson.replace('{', '').replace('}', '').replace(/"/g, '')
    const pairs = specsJson.split(',')
    pairs.forEach(pair => {
      const [key, value] = pair.split(':')
      if (key && value) {
        specs[parseInt(key.trim())] = parseInt(value.trim())
      }
    })
    return specs
  } catch (e) {
    return {}
  }
}

// 选择规格
const selectSpec = (typeId, valueId) => {
  selectedSpecs.value[typeId] = valueId
  // 尝试匹配对应的SKU
  matchSku()
}

// 根据选中的规格匹配对应的SKU
const matchSku = () => {
  const matched = skus.value.find(sku => {
    if (!sku.specsJson) return false
    const skuSpecs = parseSpecsJson(sku.specsJson)
    // 检查所有选中的规格是否都匹配
    for (const [typeId, valueId] of Object.entries(selectedSpecs.value)) {
      if (skuSpecs[typeId] !== valueId) {
        return false
      }
    }
    return true
  })
  if (matched) {
    selectedSku.value = matched
  }
}

// 检查某个规格值是否可选（即是否存在包含这个规格值的SKU）
const isSpecAvailable = (typeId, valueId) => {
  // 暂时简化：所有规格值都可选
  // TODO: 实际业务中需要检查库存等
  return true
}

const handleFavorite = async () => {
  try {
    const userId = getUserId()
    const action = isFavorite.value ? 'remove' : 'add'
    await toggleFavorite(userId, product.value.id, action)
    isFavorite.value = !isFavorite.value
    ElMessage.success(isFavorite.value ? '收藏成功' : '取消收藏成功')
  } catch (e) {
    console.error(e)
  }
}

const handleAddCart = async () => {
  try {
    await addCart({
      productId: product.value.id,
      skuId: selectedSku.value?.id,
      specs: selectedSku.value?.specs || '默认规格',
      quantity: quantity.value
    })
    cartSuccessVisible.value = true
  } catch (e) {
    console.error(e)
    ElMessage.error('加入购物车失败')
  }
}

const goToCart = () => {
  cartSuccessVisible.value = false
  router.push('/cart')
}

const handleLike = async (review) => {
  try {
    const res = await likeReview(review.id, userStore.userId)
    if (res.code !== 200) {
      ElMessage.warning(res.message || '操作失败')
      return
    }
    review.likeCount = (review.likeCount || 0) + 1
    ElMessage.success('点赞成功')
  } catch (e) {}
}

const handleBuy = () => {
  router.push({
    path: '/checkout',
    query: {
      productId: product.value.id,
      skuId: selectedSku.value?.id,
      specs: selectedSku.value?.specs || '默认规格',
      quantity: quantity.value,
      price: currentPrice.value
    }
  })
}

watch(() => route.params.id, loadDetail, { immediate: true })
</script>

<style scoped>
.product-detail-page {
  padding-bottom: 40px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.product-main {
  display: flex;
  gap: 40px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.product-gallery {
  width: 400px;
}

.main-image {
  width: 400px;
  height: 400px;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
  position: relative;
  background: #fafafa;
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #999;
  font-size: 14px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 60px;
  height: 60px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.thumbnail.active {
  border-color: #FF5000;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 20px;
  font-weight: normal;
  margin-bottom: 10px;
}

.product-subtitle {
  color: #666;
  margin-bottom: 20px;
}

.price-box {
  background: #FFF5F0;
  padding: 15px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.price-box .label {
  color: #999;
  margin-right: 20px;
}

.price-box .price {
  color: #FF5000;
  font-size: 28px;
  font-weight: bold;
}

.price-box .original-price {
  color: #999;
  text-decoration: line-through;
  margin-left: 15px;
}

.stats-row {
  display: flex;
  gap: 30px;
  color: #999;
  margin-bottom: 20px;
}

.sku-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 20px;
}

.sku-section .label {
  width: auto;
  color: #999;
  margin-bottom: 5px;
}

.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.quantity-section .label {
  width: 60px;
  color: #999;
}

/* 规格分组样式 */
.spec-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px;
}

.spec-group-label {
  color: #666;
  font-size: 14px;
  font-weight: normal;
}

.spec-values {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.spec-value {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  background: white;
}

.spec-value:hover:not(.disabled) {
  border-color: #FF5000;
  color: #FF5000;
  background: #FFF5F0;
}

.spec-value.active {
  border-color: #FF5000;
  color: #FF5000;
  background: #FFF5F0;
}

.spec-value.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f5f5f5;
}

/* 已选规格展示 */
.selected-specs {
  margin-top: 10px;
  padding: 10px 15px;
  background: #F9F9F9;
  border-radius: 4px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.selected-label {
  color: #666;
  font-size: 14px;
}

.selected-text {
  color: #333;
  font-size: 14px;
}

/* 备用SKU列表样式 */
.sku-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sku-item {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.sku-item:hover,
.sku-item.active {
  border-color: #FF5000;
  color: #FF5000;
  background: #FFF5F0;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.buy-btn {
  background: #FF5000;
  border-color: #FF5000;
}

.buy-btn:hover {
  background: #e64500;
  border-color: #e64500;
}

.detail-tabs {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.detail-content {
  padding: 20px;
  color: #666;
  line-height: 1.8;
}

.review-stats {
  display: flex;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 20px;
}

.stats-left {
  width: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-right: 1px solid #eee;
  margin-right: 30px;
}

.good-rate .rate {
  font-size: 36px;
  font-weight: bold;
  color: #FF5000;
}

.good-rate .label {
  color: #999;
}

.stats-right {
  flex: 1;
}

.star-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.star-label {
  width: 40px;
  color: #999;
}

.star-row .el-progress {
  flex: 1;
}

.star-count {
  width: 40px;
  text-align: right;
  color: #999;
}

.review-list {
  padding: 20px 0;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.user-name {
  font-weight: bold;
}

.review-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}

.review-content {
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.review-append,
.review-reply {
  background: #f5f5f5;
  padding: 10px 15px;
  border-radius: 4px;
  margin-top: 10px;
  font-size: 14px;
}

.append-label,
.reply-label {
  color: #FF5000;
  font-weight: bold;
}

.review-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.like-icon {
  vertical-align: middle;
  margin-right: 4px;
}

.view-all {
  text-align: center;
  padding: 20px;
}

.merchant-info {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.merchant-avatar {
  width: 80px;
  height: 80px;
  background: #FF5000;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.merchant-detail h4 {
  margin-bottom: 10px;
}

.merchant-detail p {
  color: #666;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.cart-success-content {
  text-align: center;
  padding: 20px 0;
}

.cart-success-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.cart-success-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.cart-success-btns {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
