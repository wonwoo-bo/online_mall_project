<template>
  <div class="my-reviews-page">
    <div class="page-header">
      <span class="back-btn" @click="$router.back()">&lt;</span>
      <h1 class="title">我的评价</h1>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="reviews.length === 0" class="empty">
      <el-empty description="暂无评价记录" />
    </div>

    <div v-else class="review-list">
      <div class="review-card" v-for="review in reviews" :key="review.id">
        <!-- 商品信息 -->
        <div class="review-product" @click="$router.push(`/product/${review.productId}`)">
          <img :src="review.productImage || defaultImage" class="product-img">
          <div class="product-info">
            <div class="product-name">{{ review.productName }}</div>
            <div class="review-time">{{ review.createTime }}</div>
          </div>
        </div>

        <!-- 评价内容 -->
        <div class="review-content">
          <el-rate :model-value="review.rating" disabled :colors="['#FF5000', '#FF5000', '#FF5000']" />
          <p class="content-text">{{ review.content }}</p>
          <!-- 评价图片 -->
          <div class="review-images" v-if="review.images && review.images.length > 0">
            <el-image
              v-for="(img, index) in review.images"
              :key="index"
              :src="img.url || img"
              fit="cover"
              class="review-img"
              :preview-src-list="review.images.map(i => i.url || i)"
              :initial-index="index"
              preview-teleported
            />
          </div>
        </div>

        <!-- 追评 -->
        <div class="review-append" v-if="review.append">
          <div class="append-header">
            <span class="append-label">追评</span>
            <span class="append-time">{{ review.append.createTime }}</span>
          </div>
          <p>{{ review.append.content }}</p>
        </div>

        <!-- 商家回复 -->
        <div class="review-reply" v-if="review.merchantReply">
          <div class="reply-header">
            <span class="reply-label">商家回复</span>
            <span class="reply-time" v-if="review.replyTime">{{ review.replyTime }}</span>
          </div>
          <p>{{ review.merchantReply }}</p>
        </div>

        <!-- 底部信息 -->
        <div class="review-footer">
          <span class="like-info">
            <el-icon><Star /></el-icon> {{ review.likeCount || 0 }} 人觉得有用
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyReviews } from '@/api'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(true)
const reviews = ref([])
const defaultImage = 'https://via.placeholder.com/80x80/FF5000/FFFFFF?text=商品'

const loadReviews = async () => {
  loading.value = true
  try {
    const userId = userStore.userId || 1
    const res = await getMyReviews(userId)
    if (res.code === 200) {
      reviews.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (e) {
    ElMessage.error('加载评价失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.my-reviews-page { padding-bottom: 40px; }
.page-header {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 10;
}
.back-btn {
  font-size: 22px;
  margin-right: 15px;
  cursor: pointer;
  color: #333;
}
.page-header .title {
  font-size: 18px;
  font-weight: 500;
}
.loading, .empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}
.review-list {
  padding: 10px 15px;
}
.review-card {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.review-product {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  cursor: pointer;
}
.product-img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}
.product-info {
  flex: 1;
}
.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.review-time {
  font-size: 12px;
  color: #999;
}
.review-content {
  margin-bottom: 10px;
}
.content-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-top: 8px;
  white-space: pre-wrap;
}
.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}
.review-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}
.review-append, .review-reply {
  background: #f5f5f5;
  padding: 10px 12px;
  border-radius: 6px;
  margin-top: 10px;
  font-size: 13px;
  line-height: 1.6;
}
.append-header, .reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.append-label {
  color: #FF5000;
  font-weight: bold;
  font-size: 12px;
}
.append-time, .reply-time {
  color: #999;
  font-size: 12px;
}
.reply-label {
  color: #409EFF;
  font-weight: bold;
  font-size: 12px;
}
.review-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}
.like-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}
</style>
