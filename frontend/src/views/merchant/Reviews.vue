<template>
  <div class="review-management">
    <!-- 进阶统计卡片 -->
    <el-card class="stats-card">
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-value">{{ advancedStats.totalReviews || 0 }}</div>
            <div class="stat-label">总评价数</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item good">
            <div class="stat-value">{{ advancedStats.goodReviews || 0 }}</div>
            <div class="stat-label">好评数</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item medium">
            <div class="stat-value">{{ advancedStats.mediumReviews || 0 }}</div>
            <div class="stat-label">中评数</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item bad">
            <div class="stat-value">{{ advancedStats.badReviews || 0 }}</div>
            <div class="stat-label">差评数</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-value">{{ advancedStats.goodRate || 0 }}%</div>
            <div class="stat-label">好评率</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item image">
            <div class="stat-value">{{ advancedStats.imageReviews || 0 }}</div>
            <div class="stat-label">带图评价</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="评价类型">
          <el-select v-model="queryParams.rating" placeholder="全部" clearable @change="loadReviews">
            <el-option label="全部" :value="null" />
            <el-option label="好评(4-5星)" :value="1" />
            <el-option label="中评(3星)" :value="2" />
            <el-option label="差评(1-2星)" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复状态">
          <el-select v-model="queryParams.hasReply" placeholder="全部" clearable @change="loadReviews">
            <el-option label="全部" :value="null" />
            <el-option label="已回复" :value="1" />
            <el-option label="未回复" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadReviews">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 趋势图 -->
    <el-card class="trend-card" v-if="trendData.length > 0">
      <div class="trend-title">近7天评价趋势</div>
      <div class="trend-chart">
        <div v-for="(item, index) in trendData" :key="index" class="trend-item">
          <div class="trend-date">{{ item.date }}</div>
          <div class="trend-bars">
            <div class="bar good" :style="{ height: (item.goodCount / maxTrendCount * 80) + 'px' }">
              <span class="bar-value">{{ item.goodCount }}</span>
            </div>
            <div class="bar medium" :style="{ height: (item.mediumCount / maxTrendCount * 80) + 'px' }">
              <span class="bar-value">{{ item.mediumCount }}</span>
            </div>
            <div class="bar bad" :style="{ height: (item.badCount / maxTrendCount * 80) + 'px' }">
              <span class="bar-value">{{ item.badCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="review-list-card">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="reviewList.length === 0" class="empty-container">
        <el-empty description="暂无评价数据" />
      </div>

      <div v-else class="review-list">
        <div v-for="review in reviewList" :key="review.id" class="review-item" :class="{ 'top-review': review.isTop }">
          <div class="review-header">
            <div class="product-info">
              <el-image :src="review.productImage" fit="cover" class="product-img">
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-name">
                <el-tag v-if="review.isTop" type="danger" size="small" style="margin-right: 8px;">置顶</el-tag>
                {{ review.productName }}
              </div>
            </div>
            <div class="review-meta">
              <el-rate v-model="review.rating" disabled text-color="#ff9900" />
              <span class="review-time">{{ formatTime(review.createTime) }}</span>
            </div>
          </div>

          <div class="review-content">
            <p>{{ review.content }}</p>
            <div v-if="review.images && review.images.length > 0" class="review-images">
              <el-image
                v-for="(img, index) in review.images"
                :key="index"
                :src="img.imageUrl"
                :preview-src-list="review.images.map(i => i.imageUrl)"
                :initial-index="index"
                fit="cover"
                class="review-img"
              />
            </div>
          </div>

          <div v-if="review.append" class="review-append">
            <div class="append-label">追评：</div>
            <div class="append-content">{{ review.append.content }}</div>
            <div v-if="review.appendReply" class="append-reply">
              <div class="reply-label">
                <el-icon><ChatDotRound /></el-icon>
                商家回复
              </div>
              <div class="reply-content">{{ review.appendReply.content }}</div>
              <div class="reply-time">{{ formatTime(review.appendReply.createTime) }}</div>
            </div>
            <div v-else class="append-actions">
              <el-button
                type="primary"
                link
                size="small"
                @click="openAppendReplyDialog(review)"
              >
                回复追评
              </el-button>
            </div>
          </div>

          <div v-if="review.replyId" class="merchant-reply">
            <div class="reply-label">
              <el-icon><ChatDotRound /></el-icon>
              商家回复
            </div>
            <div class="reply-content">{{ review.replyContent }}</div>
            <div class="reply-time">{{ formatTime(review.replyTime) }}</div>
          </div>

          <div v-if="review.explanation" class="review-explanation">
            <div class="explanation-label">
              <el-icon><Document /></el-icon>
              公开解释
            </div>
            <div class="explanation-content">{{ review.explanation.content }}</div>
            <div class="explanation-meta">
              <span>编辑{{ review.explanation.editCount || 0 }}次</span>
              <span>{{ formatTime(review.explanation.lastEditTime || review.explanation.createTime) }}</span>
            </div>
          </div>

          <div class="review-actions">
            <el-button
              v-if="!review.replyId"
              type="primary"
              link
              @click="openReplyDialog(review)"
            >
              <el-icon><ChatLineRound /></el-icon>
              回复评价
            </el-button>
            <el-button v-else type="info" link disabled>
              已回复
            </el-button>

            <el-button
              v-if="review.rating <= 3 && !review.explanation"
              type="warning"
              link
              @click="openExplanationDialog(review)"
            >
              <el-icon><Document /></el-icon>
              公开解释
            </el-button>
            <el-button
              v-if="review.rating <= 3 && review.explanation"
              type="warning"
              link
              @click="openExplanationDialog(review)"
            >
              <el-icon><Edit /></el-icon>
              编辑解释
            </el-button>

            <el-button
              v-if="review.rating === 5 && review.images && review.images.length > 0 && !review.isTop"
              type="success"
              link
              @click="topReview(review)"
            >
              <el-icon><Top /></el-icon>
              置顶评价
            </el-button>
            <el-button
              v-if="review.isTop"
              type="danger"
              link
              @click="cancelTopReview(review)"
            >
              <el-icon><Bottom /></el-icon>
              取消置顶
            </el-button>

            <el-button
              v-if="!review.hasReport"
              type="danger"
              link
              @click="openReportDialog(review)"
            >
              <el-icon><Warning /></el-icon>
              举报违规
            </el-button>
            <el-button v-else type="info" link disabled>
              已举报
            </el-button>

            <el-button
              v-if="review.rating <= 3 && !review.hasAppeal"
              type="danger"
              link
              @click="openAppealDialog(review)"
            >
              <el-icon><CircleClose /></el-icon>
              恶意差评申诉
            </el-button>
            <el-button
              v-if="review.hasAppeal"
              type="info"
              link
              disabled
            >
              已申诉
            </el-button>
          </div>
        </div>
      </div>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 回复评价弹窗 -->
    <el-dialog
      v-model="replyVisible"
      title="回复评价"
      width="600px"
      draggable
      destroy-on-close
    >
      <div class="review-preview">
        <div class="preview-label">评价内容：</div>
        <div class="preview-content">{{ currentReview?.content }}</div>
      </div>
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容（不能为空）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">
          提交回复
        </el-button>
      </template>
    </el-dialog>

    <!-- 回复追评弹窗 -->
    <el-dialog
      v-model="appendReplyVisible"
      title="回复追评"
      width="600px"
      draggable
      destroy-on-close
    >
      <div class="review-preview">
        <div class="preview-label">追评内容：</div>
        <div class="preview-content">{{ currentReview?.append?.content }}</div>
      </div>
      <el-form ref="appendReplyFormRef" :model="appendReplyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="appendReplyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容（不能为空）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appendReplyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppendReply" :loading="submitting">
          提交回复
        </el-button>
      </template>
    </el-dialog>

    <!-- 差评解释弹窗 -->
    <el-dialog
      v-model="explanationVisible"
      :title="currentReview?.explanation ? '编辑公开解释' : '添加公开解释'"
      width="600px"
      draggable
      destroy-on-close
    >
      <div class="review-preview">
        <div class="preview-label">评价内容：</div>
        <div class="preview-content">{{ currentReview?.content }}</div>
      </div>
      <el-form ref="explanationFormRef" :model="explanationForm" :rules="explanationRules" label-width="80px">
        <el-form-item label="解释内容" prop="content">
          <el-input
            v-model="explanationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入公开解释内容（不能为空）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="explanationVisible = false">取消</el-button>
        <el-button type="primary" @click="submitExplanation" :loading="submitting">
          {{ currentReview?.explanation ? '保存修改' : '提交解释' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 举报弹窗 -->
    <el-dialog
      v-model="reportVisible"
      title="举报违规评价"
      width="500px"
      draggable
      destroy-on-close
    >
      <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="80px">
        <el-form-item label="举报原因" prop="reason">
          <el-select v-model="reportForm.reason" placeholder="请选择举报原因">
            <el-option label="恶意差评" value="恶意差评" />
            <el-option label="虚假评价" value="虚假评价" />
            <el-option label="广告信息" value="广告信息" />
            <el-option label="不当内容" value="不当内容" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="reportForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport" :loading="reporting">
          提交举报
        </el-button>
      </template>
    </el-dialog>

    <!-- 恶意差评申诉弹窗 -->
    <el-dialog
      v-model="appealVisible"
      title="恶意差评申诉"
      width="600px"
      draggable
      destroy-on-close
    >
      <div class="review-preview">
        <div class="preview-label">评价内容：</div>
        <div class="preview-content">{{ currentReview?.content }}</div>
      </div>
      <el-form ref="appealFormRef" :model="appealForm" :rules="appealRules" label-width="80px">
        <el-form-item label="申诉原因" prop="reason">
          <el-select v-model="appealForm.reason" placeholder="请选择申诉原因">
            <el-option label="恶意差评" value="恶意差评" />
            <el-option label="同行竞争" value="同行竞争" />
            <el-option label="敲诈勒索" value="敲诈勒索" />
            <el-option label="虚假评价" value="虚假评价" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="appealForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="凭证URL" prop="evidenceUrls">
          <el-input
            v-model="appealForm.evidenceUrls"
            type="textarea"
            :rows="2"
            placeholder="请上传凭证图片，多个URL用逗号分隔（选填）"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="appealVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal" :loading="appealing">
          提交申诉
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, ChatDotRound, ChatLineRound, Warning, Document, Edit, Top, Bottom, CircleClose } from '@element-plus/icons-vue'
import request from '@/utils/merchantRequest'

const loading = ref(false)
const reviewList = ref([])
const total = ref(0)
const advancedStats = ref({})
const trendData = ref([])
const dateRange = ref([])

const replyVisible = ref(false)
const appendReplyVisible = ref(false)
const explanationVisible = ref(false)
const reportVisible = ref(false)
const appealVisible = ref(false)

const submitting = ref(false)
const reporting = ref(false)
const appealing = ref(false)

const currentReview = ref(null)
const replyFormRef = ref(null)
const appendReplyFormRef = ref(null)
const explanationFormRef = ref(null)
const reportFormRef = ref(null)
const appealFormRef = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  rating: null,
  hasReply: null,
  startTime: '',
  endTime: ''
})

const replyForm = reactive({
  content: ''
})

const appendReplyForm = reactive({
  content: ''
})

const explanationForm = reactive({
  content: ''
})

const reportForm = reactive({
  reason: '',
  description: ''
})

const appealForm = reactive({
  reason: '',
  description: '',
  evidenceUrls: ''
})

const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 1, max: 500, message: '回复内容在1-500个字符', trigger: 'blur' }
  ]
}

const explanationRules = {
  content: [
    { required: true, message: '请输入解释内容', trigger: 'blur' },
    { min: 1, max: 500, message: '解释内容在1-500个字符', trigger: 'blur' }
  ]
}

const reportRules = {
  reason: [{ required: true, message: '请选择举报原因', trigger: 'change' }]
}

const appealRules = {
  reason: [{ required: true, message: '请选择申诉原因', trigger: 'change' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

const maxTrendCount = computed(() => {
  if (trendData.value.length === 0) return 1
  return Math.max(...trendData.value.map(item => item.count)) || 1
})

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(5)
}

const loadAdvancedStats = async () => {
  try {
    const res = await request.get('/reviews/stats/advanced')
    if (res.code === 200) {
      advancedStats.value = res.data || {}
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadTrendData = async () => {
  try {
    const res = await request.get('/reviews/trend', {
      params: { days: 7 }
    })
    if (res.code === 200) {
      const data = res.data || []
      trendData.value = data.map(item => ({
        ...item,
        date: formatDate(item.date)
      }))
    }
  } catch (error) {
    console.error('加载趋势数据失败', error)
  }
}

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await request.get('/reviews', {
      params: {
        ...queryParams
      }
    })
    if (res.code === 200) {
      const data = res.data || {}
      reviewList.value = data.list || []
      total.value = data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载评价列表失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
}

const resetQuery = () => {
  queryParams.page = 1
  queryParams.pageSize = 10
  queryParams.rating = null
  queryParams.hasReply = null
  queryParams.startTime = ''
  queryParams.endTime = ''
  dateRange.value = []
  loadReviews()
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.page = 1
  loadReviews()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  loadReviews()
}

const openReplyDialog = (review) => {
  currentReview.value = review
  replyForm.content = ''
  replyVisible.value = true
}

const submitReply = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await request.post(
        `/reviews/${currentReview.value.id}/reply`,
        { content: replyForm.content }
      )
      if (res.code === 200) {
        ElMessage.success('回复成功')
        replyVisible.value = false
        loadReviews()
      } else {
        ElMessage.error(res.message || '回复失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '回复失败'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  })
}

const openAppendReplyDialog = (review) => {
  currentReview.value = review
  appendReplyForm.content = ''
  appendReplyVisible.value = true
}

const submitAppendReply = async () => {
  if (!appendReplyFormRef.value) return
  await appendReplyFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await request.post(
        `/reviews/append/${currentReview.value.append.id}/reply`,
        {
          reviewId: currentReview.value.id,
          content: appendReplyForm.content
        }
      )
      if (res.code === 200) {
        ElMessage.success('回复成功')
        appendReplyVisible.value = false
        loadReviews()
      } else {
        ElMessage.error(res.message || '回复失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '回复失败'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  })
}

const openExplanationDialog = (review) => {
  currentReview.value = review
  explanationForm.content = review.explanation?.content || ''
  explanationVisible.value = true
}

const submitExplanation = async () => {
  if (!explanationFormRef.value) return
  await explanationFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await request.post(
        `/reviews/${currentReview.value.id}/explanation`,
        { content: explanationForm.content }
      )
      if (res.code === 200) {
        ElMessage.success('保存成功')
        explanationVisible.value = false
        loadReviews()
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '保存失败'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  })
}

const topReview = async (review) => {
  try {
    const res = await request.post(
      `/reviews/${review.id}/top`,
      { productId: review.productId }
    )
    if (res.code === 200) {
      ElMessage.success('置顶成功')
      loadReviews()
      loadAdvancedStats()
    } else {
      ElMessage.error(res.message || '置顶失败')
    }
  } catch (error) {
    const msg = error.response?.data?.message || error.message || '置顶失败'
    ElMessage.error(msg)
  }
}

const cancelTopReview = async (review) => {
  try {
    const res = await request.post(
      `/reviews/${review.id}/cancel-top`,
      { productId: review.productId }
    )
    if (res.code === 200) {
      ElMessage.success('取消置顶成功')
      loadReviews()
      loadAdvancedStats()
    } else {
      ElMessage.error(res.message || '取消置顶失败')
    }
  } catch (error) {
    const msg = error.response?.data?.message || error.message || '取消置顶失败'
    ElMessage.error(msg)
  }
}

const openReportDialog = (review) => {
  currentReview.value = review
  reportForm.reason = ''
  reportForm.description = ''
  reportVisible.value = true
}

const submitReport = async () => {
  if (!reportFormRef.value) return
  await reportFormRef.value.validate(async (valid) => {
    if (!valid) return

    reporting.value = true
    try {
      const res = await request.post(
        `/reviews/${currentReview.value.id}/report`,
        {
          reason: reportForm.reason,
          description: reportForm.description
        }
      )
      if (res.code === 200) {
        ElMessage.success('举报提交成功')
        reportVisible.value = false
        loadReviews()
      } else {
        ElMessage.error(res.message || '举报提交失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '举报提交失败'
      ElMessage.error(msg)
    } finally {
      reporting.value = false
    }
  })
}

const openAppealDialog = (review) => {
  currentReview.value = review
  appealForm.reason = ''
  appealForm.description = ''
  appealForm.evidenceUrls = ''
  appealVisible.value = true
}

const submitAppeal = async () => {
  if (!appealFormRef.value) return
  await appealFormRef.value.validate(async (valid) => {
    if (!valid) return

    appealing.value = true
    try {
      const res = await request.post(
        `/reviews/${currentReview.value.id}/appeal`,
        {
          reason: appealForm.reason,
          description: appealForm.description,
          evidenceUrls: appealForm.evidenceUrls
        }
      )
      if (res.code === 200) {
        ElMessage.success('申诉提交成功')
        appealVisible.value = false
        loadReviews()
      } else {
        ElMessage.error(res.message || '申诉提交失败')
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.message || '申诉提交失败'
      ElMessage.error(msg)
    } finally {
      appealing.value = false
    }
  })
}

onMounted(() => {
  loadAdvancedStats()
  loadTrendData()
  loadReviews()
})
</script>

<style scoped>
.review-management {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-item.good .stat-value {
  color: #67c23a;
}

.stat-item.medium .stat-value {
  color: #e6a23c;
}

.stat-item.bad .stat-value {
  color: #f56c6c;
}

.stat-item.image .stat-value {
  color: #409eff;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.trend-card {
  margin-bottom: 20px;
}

.trend-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.trend-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 120px;
  padding: 0 20px;
}

.trend-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.trend-date {
  font-size: 12px;
  color: #909399;
}

.trend-bars {
  display: flex;
  gap: 4px;
  align-items: flex-end;
  height: 80px;
}

.bar {
  width: 16px;
  border-radius: 4px 4px 0 0;
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  min-height: 4px;
}

.bar.good {
  background: #67c23a;
}

.bar.medium {
  background: #e6a23c;
}

.bar.bad {
  background: #f56c6c;
}

.bar-value {
  position: absolute;
  top: -20px;
  font-size: 12px;
  color: #606266;
}

.filter-card {
  margin-bottom: 20px;
}

.review-list-card {
  margin-bottom: 20px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 60px 0;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  transition: box-shadow 0.3s;
}

.review-item.top-review {
  border: 2px solid #f56c6c;
}

.review-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-img {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  background: #f5f7fa;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 24px;
}

.product-name {
  font-size: 14px;
  color: #303133;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  margin-bottom: 16px;
}

.review-content p {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.review-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.review-append {
  background: #fdf6ec;
  border-left: 3px solid #e6a23c;
  padding: 12px 16px;
  margin-bottom: 16px;
  border-radius: 0 4px 4px 0;
}

.append-label {
  font-size: 12px;
  color: #e6a23c;
  font-weight: 500;
  margin-bottom: 4px;
}

.append-content {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.append-actions {
  margin-top: 8px;
}

.append-reply {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
  padding: 12px 16px;
  margin-top: 12px;
  border-radius: 0 4px 4px 0;
}

.merchant-reply {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
  padding: 12px 16px;
  margin-bottom: 16px;
  border-radius: 0 4px 4px 0;
}

.reply-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 8px;
}

.reply-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 4px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.review-explanation {
  background: #fef0f0;
  border-left: 3px solid #f56c6c;
  padding: 12px 16px;
  margin-bottom: 16px;
  border-radius: 0 4px 4px 0;
}

.explanation-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #f56c6c;
  font-weight: 500;
  margin-bottom: 8px;
}

.explanation-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 8px;
}

.explanation-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 16px;
}

.review-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  flex-wrap: wrap;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}

.review-preview {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}

.preview-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.preview-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}
</style>
