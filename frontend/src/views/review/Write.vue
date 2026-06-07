<template>
  <div class="review-write-page">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ isAppendMode ? '追加评价' : '发表评价' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="write-card">
      <h2>{{ isAppendMode ? '追加评价' : '发表评价' }}</h2>

      <!-- 商品信息 -->
      <div class="product-info" v-if="productName">
        <img :src="coverImg || defaultImage" class="product-image">
        <span>{{ productName }}</span>
      </div>

      <el-form :model="form" label-width="100px" class="review-form">
        <!-- 评分（追评模式不显示） -->
        <el-form-item v-if="!isAppendMode" label="评分" required>
          <el-rate v-model="form.rating" :texts="['很差', '差', '一般', '好', '非常好']" show-text />
        </el-form-item>

        <!-- 评价内容 -->
        <el-form-item :label="isAppendMode ? '追评内容' : '评价内容'" required>
          <el-input v-model="form.content" type="textarea" :rows="5" :placeholder="isAppendMode ? '请输入您的追评内容...' : '请输入您的评价...'" maxlength="500" show-word-limit />
        </el-form-item>

        <!-- 匿名（追评模式不显示） -->
        <el-form-item v-if="!isAppendMode" label="匿名评价">
          <el-switch v-model="form.isAnonymous" active-value="1" inactive-value="0" />
        </el-form-item>

        <!-- 图片上传 -->
        <el-form-item label="上传图片">
          <el-upload
            action="#"
            :auto-upload="false"
            list-type="picture-card"
            :limit="5"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传5张图片</div>
        </el-form-item>

        <!-- 提交 -->
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">{{ isAppendMode ? '提交追评' : '提交评价' }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { submitReview, appendReview } from '@/api'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const submitting = ref(false)
const defaultImage = 'https://via.placeholder.com/80x80/FF5000/FFFFFF?text=商品'

const isAppendMode = computed(() => route.query.mode === 'append')

const productName = ref(route.query.productName || '')
const coverImg = ref(route.query.coverImg || '')
const imageUrls = ref([])

const form = reactive({
  content: '',
  rating: 0,
  isAnonymous: '0'
})

const handleImageChange = (file) => {
  imageUrls.value.push(file.url || URL.createObjectURL(file.raw))
}

const handleImageRemove = (file) => {
  const url = file.url || URL.createObjectURL(file.raw)
  imageUrls.value = imageUrls.value.filter(u => u !== url)
}

const handleSubmit = async () => {
  if (!form.content.trim()) {
    ElMessage.warning(isAppendMode.value ? '请输入追评内容' : '请输入评价内容')
    return
  }

  submitting.value = true
  try {
    if (isAppendMode.value) {
      // 追评模式
      const reviewId = parseInt(route.query.reviewId)
      if (!reviewId) {
        ElMessage.error('缺少评价ID')
        return
      }
      const res = await appendReview(reviewId, form.content)
      if (res.code !== 200) {
        ElMessage.error(res.message || '追评提交失败')
        return
      }
      ElMessage.success('追评提交成功！')
    } else {
      // 初始评价模式
      if (form.rating === 0) {
        ElMessage.warning('请选择评分')
        return
      }

      console.log('=== 评价参数 ===')
      console.log('orderItemId:', route.query.orderItemId)
      console.log('productId:', route.query.productId)
      console.log('merchantId:', route.query.merchantId)
      console.log('userId:', userStore.userId)

      if (!route.query.orderItemId) {
        ElMessage.error('缺少订单明细ID')
        return
      }
      if (!route.query.productId) {
        ElMessage.error('缺少商品ID')
        return
      }
      if (!route.query.merchantId) {
        ElMessage.error('缺少商家ID')
        return
      }

      const userId = userStore.userId || 1
      const requestData = {
        orderItemId: parseInt(route.query.orderItemId),
        productId: parseInt(route.query.productId),
        userId: userId,
        merchantId: parseInt(route.query.merchantId),
        content: form.content,
        rating: form.rating,
        isAnonymous: parseInt(form.isAnonymous),
        imageUrls: imageUrls.value.join(',')
      }
      console.log('发送评价请求:', requestData)
      const res = await submitReview(requestData)
      if (res.code !== 200) {
        ElMessage.error(res.message || '评价提交失败')
        return
      }
      ElMessage.success('评价提交成功！')
    }

    // 返回上一页
    if (route.query.orderId) {
      router.push(`/orders/${route.query.orderId}`)
    } else {
      router.back()
    }
  } catch (e) {
    console.error('提交失败:', e)
    ElMessage.error(e.response?.data?.message || e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-write-page { padding-bottom: 40px; }
.breadcrumb { margin-bottom: 20px; }
.write-card { background: white; padding: 30px; border-radius: 8px; }
.write-card h2 { margin-bottom: 20px; }
.product-info { display: flex; align-items: center; gap: 15px; padding: 15px; background: #f9f9f9; border-radius: 8px; margin-bottom: 20px; }
.product-image { width: 60px; height: 60px; border-radius: 4px; object-fit: cover; }
.review-form { max-width: 600px; }
.upload-tip { color: #999; font-size: 12px; margin-top: 5px; }
</style>
