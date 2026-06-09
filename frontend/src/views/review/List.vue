<template>
  <div class="review-list-page">
    <h2 class="page-title">商品评价</h2>

    <!-- 评价统计 -->
    <div class="review-stats" v-loading="loading">
      <div class="stats-left">
        <div class="good-rate">
          <span class="rate">{{ stats.goodRate || 100 }}%</span>
          <span class="label">好评率</span>
        </div>
        <div class="total-count">共 {{ stats.totalCount || 0 }} 条评价</div>
      </div>
      <div class="stats-right">
        <div class="star-row" v-for="i in 5" :key="i">
          <span class="star-label">{{ 6 - i }}星</span>
          <el-progress :percentage="stats[`star${6 - i}`] || 0" :stroke-width="10" :show-text="false" />
          <span class="star-count">{{ stats[`star${6 - i}Count`] || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterRating" @change="loadReviews">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button :value="5">好评</el-radio-button>
        <el-radio-button :value="3">中评</el-radio-button>
        <el-radio-button :value="1">差评</el-radio-button>
        <el-radio-button value="hasImage">有图</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <el-avatar :size="40">{{ review.userName?.charAt(0) }}</el-avatar>
          <div class="user-info">
            <span class="user-name">{{ review.userName }}</span>
            <el-rate v-model="review.rating" disabled size="small" />
          </div>
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div class="review-images" v-if="review.images?.length">
          <el-image v-for="(img, idx) in review.images" :key="idx" :src="img" :preview-src-list="review.images" fit="cover" class="review-image" />
        </div>
        <div class="review-append" v-if="review.appendContent">
          <span class="append-label">追评：</span>{{ review.appendContent }}
        </div>
        <div class="review-reply" v-if="review.reply">
          <span class="reply-label">商家回复：</span>{{ review.reply }}
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
      <el-empty v-if="reviews.length === 0 && !loading" description="暂无评价" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination v-model:current-page="page" :total="total" :page-size="10" layout="prev, pager, next" @current-change="loadReviews" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getReviews, likeReview } from '@/api'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const stats = ref({})
const reviews = ref([])
const total = ref(0)
const page = ref(1)
const filterRating = ref('')

const loadReviews = async () => {
  loading.value = true
  try {
    const params = { productId: route.params.productId, page: page.value, size: 10 }
    if (filterRating.value === 'hasImage') {
      params.hasImage = 1
    } else if (filterRating.value && filterRating.value !== '') {
      params.rating = filterRating.value
    }
    const res = await getReviews(route.params.productId, params)
    stats.value = res.data?.stats || {}
    reviews.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
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

onMounted(loadReviews)
</script>

<style scoped>
.review-list-page { padding-bottom: 40px; }
.page-title { margin-bottom: 20px; }
.review-stats { display: flex; padding: 20px; background: white; border-radius: 8px; margin-bottom: 20px; }
.stats-left { width: 150px; text-align: center; border-right: 1px solid #eee; margin-right: 30px; }
.good-rate .rate { font-size: 36px; font-weight: bold; color: #FF5000; }
.total-count { color: #999; margin-top: 10px; }
.stats-right { flex: 1; }
.star-row { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.star-label { width: 40px; color: #999; }
.star-row .el-progress { flex: 1; }
.star-count { width: 40px; text-align: right; color: #999; }
.filter-bar { margin-bottom: 20px; }
.review-list { background: white; border-radius: 8px; }
.review-item { padding: 20px; border-bottom: 1px solid #eee; }
.review-header { display: flex; align-items: center; gap: 15px; margin-bottom: 10px; }
.user-info { display: flex; flex-direction: column; gap: 5px; }
.review-time { color: #999; font-size: 12px; margin-left: auto; }
.review-content { line-height: 1.6; margin-bottom: 10px; }
.review-images { display: flex; gap: 10px; margin-bottom: 10px; }
.review-image { width: 80px; height: 80px; border-radius: 4px; }
.review-append, .review-reply { background: #f5f5f5; padding: 10px 15px; border-radius: 4px; margin-top: 10px; }
.append-label, .reply-label { color: #FF5000; font-weight: bold; }
.review-actions { margin-top: 10px; display: flex; justify-content: flex-end; }
.like-icon { vertical-align: middle; margin-right: 4px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
