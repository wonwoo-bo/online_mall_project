<template>
  <div class="return-detail-page" v-loading="loading">
    <template v-if="detail">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/return' }">退换货</el-breadcrumb-item>
        <el-breadcrumb-item>详情</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 状态卡片 -->
      <div class="status-card" :class="'status-' + detail.status">
        <div class="status-bg-icon">
          <el-icon :size="120"><component :is="getStatusIcon(detail.status)" /></el-icon>
        </div>
        <div class="status-content">
          <div class="status-icon">
            <el-icon :size="40"><component :is="getStatusIcon(detail.status)" /></el-icon>
          </div>
          <div class="status-info">
            <h2>{{ getStatusText(detail.status) }}</h2>
            <p>{{ getStatusDesc(detail.status) }}</p>
          </div>
          <div class="status-actions">
            <el-button v-if="detail.status === 'rejected'" type="primary" size="large" round @click="handleReapply">
              <el-icon><RefreshRight /></el-icon> 重新申请
            </el-button>
            <el-button v-if="detail.status === 'approved'" type="warning" size="large" round @click="showShippingForm = true">
              <el-icon><EditPen /></el-icon> 填写物流信息
            </el-button>
            <el-button v-if="detail.status === 'completed'" type="success" size="large" plain round>
              <el-icon><CircleCheck /></el-icon> 查看退款
            </el-button>
          </div>
        </div>
      </div>

      <div class="detail-content">
        <!-- 左侧详情 -->
        <div class="detail-left">
          <!-- 申请信息 -->
          <div class="info-card">
            <h3>
              <span class="card-icon"><el-icon><Document /></el-icon></span>
              申请信息
            </h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="服务类型">
                <el-tag :type="getTypeColor(detail.type)" effect="dark" round>{{ getTypeText(detail.type) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="getStatusColor(detail.status)" effect="dark" round>{{ getStatusText(detail.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="退款金额">
                <span class="price">¥{{ detail.refundAmount || '0.00' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="订单编号">
                <span class="order-id">{{ detail.orderId }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="申请时间">
                <span class="time-text">
                  <el-icon><Calendar /></el-icon>
                  {{ detail.createTime }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="申请原因">
                <el-tag type="info" effect="plain">{{ detail.reasonType || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="问题描述" :span="2">
                <div class="reason-text">{{ detail.reason }}</div>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 商家退货地址（审核通过后显示） -->
          <div class="info-card merchant-address-card" v-if="detail.status === 'approved'">
            <h3>
              <span class="card-icon primary"><el-icon><Location /></el-icon></span>
              商家退货地址
            </h3>
            <div class="merchant-address">
              <div class="address-header">
                <el-icon :size="20" color="#FF5000"><Shop /></el-icon>
                <span class="merchant-name">{{ detail.merchantName || '商家店铺' }}</span>
              </div>
              <div class="address-content">
                <p class="address-text">{{ detail.merchantAddress || '浙江省杭州市余杭区文一西路969号阿里巴巴西溪园区' }}</p>
                <p class="contact-info">
                  <span class="contact-name">{{ detail.merchantContact || '退货处理中心' }}</span>
                  <span class="contact-phone">{{ detail.merchantPhone || '400-800-8888' }}</span>
                </p>
              </div>
              <el-alert
                title="请将此地址复制到快递单上，不要到付！"
                type="warning"
                :closable="false"
                show-icon
                style="margin-top: 12px;"
              />
            </div>
          </div>

          <!-- 物流信息填写（审核通过后显示） -->
          <div class="info-card shipping-form-card" v-if="detail.status === 'approved' && showShippingForm">
            <h3>
              <span class="card-icon warning"><el-icon><Van /></el-icon></span>
              填写物流信息
            </h3>
            <el-alert
              title="请将商品寄回并填写物流信息，商家确认收货后将为您处理退款。"
              type="warning"
              :closable="false"
              show-icon
              style="margin-bottom: 20px;"
            />
            <el-form :model="logisticsForm" label-width="100px" style="max-width: 500px;">
              <el-form-item label="物流公司" required>
                <el-select v-model="logisticsForm.company" placeholder="请选择物流公司" style="width: 100%" size="large">
                  <el-option label="顺丰速运" value="顺丰速运" />
                  <el-option label="中通快递" value="中通快递" />
                  <el-option label="圆通速递" value="圆通速递" />
                  <el-option label="韵达快递" value="韵达快递" />
                  <el-option label="申通快递" value="申通快递" />
                  <el-option label="极兔速递" value="极兔速递" />
                  <el-option label="邮政EMS" value="邮政EMS" />
                  <el-option label="京东物流" value="京东物流" />
                </el-select>
              </el-form-item>
              <el-form-item label="物流单号" required>
                <el-input v-model="logisticsForm.no" placeholder="请输入物流单号" size="large" clearable />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" round @click="submitLogistics" :loading="submitting">
                  <el-icon><Check /></el-icon> 提交物流信息
                </el-button>
                <el-button size="large" round @click="showShippingForm = false">取消</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 已填写的物流信息 -->
          <div class="info-card" v-if="detail.logisticsNo">
            <h3>
              <span class="card-icon success"><el-icon><Location /></el-icon></span>
              物流信息
            </h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="物流公司">
                <span class="logistics-company">
                  <el-icon><Van /></el-icon>
                  {{ detail.logisticsCompany }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="物流单号">
                <span class="logistics-no">{{ detail.logisticsNo }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 商家备注 -->
          <div class="info-card" v-if="detail.merchantRemark">
            <h3>
              <span class="card-icon success"><el-icon><ChatDotRound /></el-icon></span>
              商家备注
            </h3>
            <div class="merchant-remark">
              <div class="merchant-avatar">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <div class="merchant-content">
                <p class="remark-label">商家回复</p>
                <p class="remark-content">{{ detail.merchantRemark }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧 -->
        <div class="detail-right">
          <!-- 商品信息 -->
          <div class="info-card">
            <h3>
              <span class="card-icon"><el-icon><Goods /></el-icon></span>
              商品信息
            </h3>
            <div class="product-info">
              <img :src="detail.coverImg || defaultImage" class="product-image">
              <div class="product-detail">
                <h4>{{ detail.productName }}</h4>
                <span class="price">¥{{ detail.price }}</span>
              </div>
            </div>
          </div>

          <!-- 处理进度 -->
          <div class="info-card">
            <h3>
              <span class="card-icon"><el-icon><Clock /></el-icon></span>
              处理进度
            </h3>
            <el-timeline>
              <el-timeline-item type="primary" :hollow="false" size="large">
                <div class="timeline-title">
                  <el-icon color="#409eff"><EditPen /></el-icon>
                  提交申请
                </div>
                <div class="timeline-desc">{{ detail.createTime }}</div>
              </el-timeline-item>
              <el-timeline-item
                v-if="detail.status === 'cancelled'"
                type="info"
                :hollow="false"
                size="large"
              >
                <div class="timeline-title">
                  <el-icon color="#909399"><CircleClose /></el-icon>
                  卖家已取消
                </div>
                <div class="timeline-desc">申请已取消</div>
              </el-timeline-item>
              <el-timeline-item
                v-else-if="detail.status !== 'pending'"
                :type="detail.status === 'rejected' ? 'danger' : 'primary'"
                :hollow="false"
                size="large"
              >
                <div class="timeline-title">
                  <el-icon :color="detail.status === 'rejected' ? '#f56c6c' : '#409eff'">
                    <CircleCheck />
                  </el-icon>
                  商家审核
                </div>
                <div class="timeline-desc">
                  {{ detail.status === 'rejected' ? '审核未通过' : '审核通过' }}
                </div>
              </el-timeline-item>
              <el-timeline-item
                v-if="detail.logisticsNo"
                type="primary"
                :hollow="false"
                size="large"
              >
                <div class="timeline-title">
                  <el-icon color="#409eff"><Van /></el-icon>
                  买家寄回
                </div>
                <div class="timeline-desc">{{ detail.logisticsCompany }} {{ detail.logisticsNo }}</div>
              </el-timeline-item>
              <el-timeline-item
                v-if="detail.status === 'completed'"
                type="success"
                :hollow="false"
                size="large"
              >
                <div class="timeline-title">
                  <el-icon color="#67c23a"><SuccessFilled /></el-icon>
                  处理完成
                </div>
                <div class="timeline-desc">退款已完成</div>
              </el-timeline-item>
            </el-timeline>
          </div>

          <!-- 温馨提示 -->
          <div class="info-card tips-card">
            <h3>
              <span class="card-icon warning"><el-icon><InfoFilled /></el-icon></span>
              温馨提示
            </h3>
            <ul>
              <li>
                <el-icon><Clock /></el-icon>
                退换货申请提交后，商家将在 <strong>48小时</strong> 内审核
              </li>
              <li>
                <el-icon><Box /></el-icon>
                审核通过后，请在 <strong>7天</strong> 内寄回商品并填写物流信息
              </li>
              <li>
                <el-icon><Wallet /></el-icon>
                商家确认收货后，退款将在 <strong>1-3个工作日</strong> 内原路退回
              </li>
              <li>
                <el-icon><Service /></el-icon>
                如有疑问，请联系商家客服
              </li>
            </ul>
          </div>
        </div>
      </div>
    </template>
    <el-empty v-else-if="!loading" description="退换货申请不存在">
      <el-button type="primary" round @click="$router.push('/return')">返回列表</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getReturnDetail, submitShipping } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)
const showShippingForm = ref(false)
const submitting = ref(false)
const defaultImage = 'https://via.placeholder.com/80x80/FF5000/FFFFFF?text=商品'

const logisticsForm = reactive({ company: '', no: '' })

const getStatusIcon = (s) => ({ pending: 'Clock', approved: 'CircleCheck', rejected: 'CircleClose', shipping: 'Van', completed: 'SuccessFilled', cancelled: 'CircleClose' }[s] || 'Clock')
const getStatusText = (s) => ({ pending: '待审核', approved: '已同意', rejected: '已拒绝', shipping: '退货中', completed: '已完成', cancelled: '已取消' }[s] || '未知')
const getStatusDesc = (s) => ({
  pending: '您的退换货申请已提交，请耐心等待商家审核',
  approved: '商家已同意您的申请，请尽快寄回商品并填写物流信息',
  rejected: '很抱歉，商家拒绝了您的退换货申请，您可以重新申请',
  shipping: '商品已寄回，等待商家确认收货',
  completed: '退换货已完成，感谢您的使用',
  cancelled: '您已取消此次退换货申请'
}[s] || '')
const getStatusColor = (s) => ({ pending: 'warning', approved: 'success', rejected: 'danger', shipping: '', completed: 'success', cancelled: 'info' }[s] || 'info')
const getTypeColor = (t) => ({ refund: 'danger', return: 'warning', exchange: 'success' }[t] || 'info')
const getTypeText = (t) => ({ refund: '仅退款', return: '退货退款', exchange: '换货' }[t] || t)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getReturnDetail(route.params.id)
    detail.value = res.data || {}
  } finally {
    loading.value = false
  }
}

const submitLogistics = async () => {
  if (!logisticsForm.company || !logisticsForm.no) {
    ElMessage.warning('请填写完整的物流信息')
    return
  }
  submitting.value = true
  try {
    await submitShipping(route.params.id, logisticsForm.company, logisticsForm.no)
    ElMessage.success('物流信息提交成功')
    showShippingForm.value = false
    loadDetail()
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const handleReapply = () => {
  if (!detail.value) return
  router.push({
    path: '/return/apply',
    query: {
      orderId: detail.value.orderId,
      productId: detail.value.productId,
      productName: detail.value.productName || '',
      price: detail.value.price || detail.value.refundAmount || '0',
      coverImg: detail.value.coverImg || '',
      orderStatus: 3 // 已完成状态，允许所有退款类型
    }
  })
}

onMounted(loadDetail)
</script>

<style scoped>
.return-detail-page {
  padding-bottom: 40px;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 状态卡片 */
.status-card {
  position: relative;
  display: flex;
  align-items: center;
  padding: 32px;
  border-radius: 12px;
  margin-bottom: 24px;
  color: white;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.status-bg-icon {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.1;
}

.status-pending { background: linear-gradient(135deg, #f7ba2a, #e6a23c); }
.status-approved { background: linear-gradient(135deg, #67c23a, #5daf34); }
.status-rejected { background: linear-gradient(135deg, #f56c6c, #e64500); }
.status-shipping { background: linear-gradient(135deg, #409eff, #337ecc); }
.status-completed { background: linear-gradient(135deg, #909399, #73767a); }
.status-cancelled { background: linear-gradient(135deg, #909399, #606266); }

.status-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
  width: 100%;
}

.status-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  backdrop-filter: blur(10px);
}

.status-info {
  flex: 1;
}

.status-info h2 {
  font-size: 24px;
  margin-bottom: 6px;
  font-weight: 600;
}

.status-info p {
  opacity: 0.9;
  font-size: 14px;
}

.status-actions {
  flex-shrink: 0;
}

.status-actions .el-button {
  font-weight: 500;
}

/* 详情内容 */
.detail-content {
  display: flex;
  gap: 20px;
}

.detail-left {
  flex: 1;
  min-width: 0;
}

.detail-right {
  width: 380px;
  flex-shrink: 0;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s;
}

.info-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.info-card h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding-bottom: 14px;
  border-bottom: 2px solid #f5f5f5;
}

.card-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  border-radius: 8px;
  color: #409eff;
}

.card-icon.warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.card-icon.success {
  background: #f0f9eb;
  color: #67c23a;
}

.card-icon.primary {
  background: #f0f5ff;
  color: #409eff;
}

.price {
  color: #FF5000;
  font-size: 20px;
  font-weight: 700;
}

.order-id {
  font-family: 'Courier New', monospace;
  color: #666;
  font-size: 13px;
}

.time-text {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 13px;
}

.reason-text {
  background: #f9f9f9;
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.8;
  color: #555;
  font-size: 14px;
}

/* 物流信息 */
.logistics-company {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #333;
}

.logistics-no {
  font-family: 'Courier New', monospace;
  color: #409eff;
  font-weight: 500;
}

/* 商品信息 */
.product-info {
  display: flex;
  gap: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.product-image {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #f0f0f0;
}

.product-detail h4 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 商家退货地址 */
.merchant-address-card {
  background: linear-gradient(135deg, #fff8f0, #fff0e6);
  border-color: #ffd4b3;
}

.merchant-address {
  padding: 4px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #ffd4b3;
}

.merchant-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.address-content {
  padding: 8px 0;
}

.address-text {
  font-size: 15px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
  font-weight: 500;
}

.contact-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.contact-name {
  font-weight: 500;
}

.contact-phone {
  color: #FF5000;
  font-weight: 600;
}

/* 商家备注 */
.merchant-remark {
  display: flex;
  gap: 14px;
  padding: 16px;
  background: linear-gradient(135deg, #f0f9eb, #e8f5e9);
  border-radius: 10px;
  border-left: 4px solid #67c23a;
}

.merchant-avatar {
  width: 44px;
  height: 44px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #67c23a;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.merchant-content {
  flex: 1;
}

.merchant-remark .remark-label {
  font-weight: 600;
  color: #67c23a;
  margin-bottom: 6px;
  font-size: 14px;
}

.merchant-remark .remark-content {
  color: #555;
  line-height: 1.7;
  font-size: 14px;
}

/* 时间线 */
.timeline-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  margin-bottom: 4px;
  font-size: 14px;
}

.timeline-desc {
  color: #999;
  font-size: 13px;
  padding-left: 22px;
}

/* 温馨提示 */
.tips-card {
  background: linear-gradient(135deg, #fffbf0, #fff8e6);
  border-color: #f5dab1;
}

.tips-card h3 {
  color: #e6a23c;
  border-bottom-color: #f5dab1;
}

.tips-card ul {
  padding-left: 0;
  list-style: none;
  color: #996600;
}

.tips-card li {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 13px;
  line-height: 1.5;
}

.tips-card li:last-child {
  margin-bottom: 0;
}

.tips-card li .el-icon {
  color: #e6a23c;
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 900px) {
  .detail-content {
    flex-direction: column;
  }

  .detail-right {
    width: 100%;
  }
}
</style>
