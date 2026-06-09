<template>
  <div class="return-apply-page">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/return' }">退换货</el-breadcrumb-item>
      <el-breadcrumb-item>申请退换货</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="apply-container">
      <!-- 左侧：申请表单 -->
      <div class="apply-card">
        <div class="apply-header">
          <h2>
            <el-icon><EditPen /></el-icon>
            申请退换货
          </h2>
          <p>请如实填写退换货信息，以便商家快速处理您的申请</p>
        </div>

        <!-- 商品信息 -->
        <div class="product-info" v-if="productName">
          <img :src="coverImg || defaultImage" class="product-image">
          <div class="product-detail">
            <h4>{{ productName }}</h4>
            <span class="price">¥{{ price || '0.00' }}</span>
          </div>
        </div>

        <el-form :model="form" label-width="120px" class="apply-form" label-position="top">
          <!-- 服务类型 -->
          <el-form-item label="服务类型" required>
            <div class="type-cards">
              <div
                v-for="t in typeOptions"
                :key="t.value"
                class="type-card"
                :class="{ active: form.type === t.value, [t.value]: form.type === t.value }"
                @click="form.type = t.value"
              >
                <el-icon :size="40"><component :is="t.icon" /></el-icon>
                <span class="type-label">{{ t.label }}</span>
                <span class="type-desc">{{ t.desc }}</span>
              </div>
            </div>
          </el-form-item>

          <!-- 原因类型 -->
          <el-form-item label="申请原因" required>
            <el-select v-model="form.reasonType" placeholder="请选择退换货原因" size="large" style="width: 100%">
              <el-option-group label="推荐">
                <el-option label="质量问题" value="质量问题" />
              </el-option-group>
              <el-option-group label="常见">
                <el-option label="尺码不合适" value="尺码问题" />
                <el-option label="颜色/款式与描述不符" value="描述不符" />
                <el-option label="不喜欢/不想要" value="不喜欢" />
                <el-option label="商品损坏/有瑕疵" value="商品损坏" />
                <el-option label="发错货/漏发" value="发错货" />
                <el-option label="七天无理由退换" value="七天无理由" />
                <el-option label="其他原因" value="其他" />
              </el-option-group>
            </el-select>
          </el-form-item>

          <!-- 问题描述 -->
          <el-form-item label="问题描述" required>
            <el-input
              type="textarea"
              v-model="form.reason"
              placeholder="请详细描述您遇到的问题或退换货原因"
              :rows="4"
              style="width: 100%"
            ></el-input>
          </el-form-item>

          <!-- 退款金额 -->
          <el-form-item label="退款金额">
            <div class="amount-input">
              <span class="amount-symbol">¥</span>
              <el-input
                v-model="form.refundAmount"
                type="number"
                placeholder="请输入退款金额"
                size="large"
                :min="0"
                @input="handleRefundAmountInput"
              ></el-input>
            </div>
          </el-form-item>

          <!-- 凭证图片 -->
          <el-form-item label="凭证图片">
            <el-upload
              class="uploader"
              action="#"
              :auto-upload="false"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :limit="5"
              :file-list="imageUrls.map(u => ({ url: u }))"
              list-type="picture-card"
            >
              <div class="upload-btn">
                <el-icon :size="24"><Plus /></el-icon>
                <span>上传图片</span>
              </div>
            </el-upload>
            <div class="upload-tip">
              <el-icon><InfoFilled /></el-icon>
              最多上传5张凭证图片，支持 JPG、PNG 格式，每张不超过 5MB
            </div>
          </el-form-item>

          <!-- 提交 -->
          <el-form-item class="form-actions">
            <el-button type="primary" size="large" round @click="handleSubmit" :loading="submitting">
              <el-icon><Check /></el-icon> 提交申请
            </el-button>
            <el-button size="large" round @click="$router.back()">
              <el-icon><Back /></el-icon> 取消
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 右侧：注意事项 -->
      <div class="side-tips">
        <div class="tip-card">
          <h3>
            <el-icon><InfoFilled /></el-icon>
            退换货须知
          </h3>
          <ul>
            <li>
              <el-icon color="#e6a23c"><Clock /></el-icon>
              <div>
                <strong>审核时效</strong>
                <p>提交申请后，商家将在48小时内完成审核</p>
              </div>
            </li>
            <li>
              <el-icon color="#409eff"><Box /></el-icon>
              <div>
                <strong>退货要求</strong>
                <p>商品需保持原包装完好，不影响二次销售</p>
              </div>
            </li>
            <li>
              <el-icon color="#67c23a"><Wallet /></el-icon>
              <div>
                <strong>退款方式</strong>
                <p>退款将原路返回到您的支付账户</p>
              </div>
            </li>
            <li>
              <el-icon color="#f56c6c"><Warning /></el-icon>
              <div>
                <strong>注意事项</strong>
                <p>请勿在审核通过前将商品寄回</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { submitReturn, updateOrderStatus } from '@/api'
import { ElMessage } from 'element-plus'
import { getUserId } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const submitting = ref(false)
const defaultImage = 'https://via.placeholder.com/80x80/FF5000/FFFFFF?text=商品'

const productName = ref(route.query.productName || '')
const coverImg = ref(route.query.coverImg || '')
const price = ref(route.query.price || '')
const imageUrls = ref([])

const allTypeOptions = [
  { value: 'refund', label: '仅退款', desc: '未收到货或无需退货', icon: 'Wallet' },
  { value: 'return', label: '退货退款', desc: '已收到货需要退回', icon: 'RefreshRight' },
  { value: 'exchange', label: '换货', desc: '更换同款或其他商品', icon: 'Sort' }
]

// 根据订单状态过滤可选类型：未发货(0/1)只能仅退款，已发货(2/3)全部可选
const orderStatus = parseInt(route.query.orderStatus)
const typeOptions = computed(() => {
  if (orderStatus === 0 || orderStatus === 1) {
    // 待付款/待发货 → 只能仅退款
    return allTypeOptions.filter(t => t.value === 'refund')
  }
  return allTypeOptions
})

const reasonOptions = [
  { value: '质量问题', label: '质量问题', tag: '推荐' },
  { value: '尺码问题', label: '尺码不合适', tag: '' },
  { value: '描述不符', label: '颜色/款式与描述不符', tag: '' },
  { value: '不喜欢', label: '不喜欢/不想要', tag: '' },
  { value: '商品损坏', label: '商品损坏/有瑕疵', tag: '' },
  { value: '发错货', label: '发错货/漏发', tag: '' },
  { value: '七天无理由', label: '七天无理由退换', tag: '常见' },
  { value: '其他', label: '其他原因', tag: '' }
]

const form = reactive({
  type: 'return',
  reasonType: '',
  reason: '',
  refundAmount: parseFloat(price.value) || 0
})

const handleImageChange = (file) => {
  imageUrls.value.push(file.url || URL.createObjectURL(file.raw))
}

const handleImageRemove = (file) => {
  const url = file.url || URL.createObjectURL(file.raw)
  imageUrls.value = imageUrls.value.filter(u => u !== url)
}

const handleRefundAmountInput = (value) => {
  const num = parseFloat(value)
  const maxAmount = parseFloat(price.value) || 0
  if (isNaN(num) || num < 0) {
    form.refundAmount = 0
  } else if (num > maxAmount) {
    form.refundAmount = maxAmount
  }
}

const handleSubmit = async () => {
  if (!form.reasonType) {
    ElMessage.warning('请选择申请原因')
    return
  }
  if (!form.reason.trim()) {
    ElMessage.warning('请填写问题描述')
    return
  }
  if (form.refundAmount < 0) {
    ElMessage.warning('退款金额不能为负数')
    return
  }
  const maxAmount = parseFloat(price.value) || 0
  if (form.refundAmount > maxAmount) {
    ElMessage.warning(`退款金额不能超过商品价格 ¥${maxAmount}`)
    return
  }

  submitting.value = true
  try {
    const orderId = parseInt(route.query.orderId) || 1
    const userId = getUserId()

    await submitReturn({
      orderId: orderId,
      userId: userId,
      productId: parseInt(route.query.productId) || 1,
      reason: form.reason,
      type: form.type,
      reasonType: form.reasonType,
      refundAmount: form.refundAmount,
      imageUrls: imageUrls.value.join(',')
    })

    await updateOrderStatus(orderId, 5)

    ElMessage.success('申请提交成功！')
    router.push('/return')
  } catch (e) {
    console.error(e)
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.return-apply-page {
  padding-bottom: 40px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.apply-container {
  display: flex;
  gap: 24px;
}

.apply-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.apply-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.apply-header h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.apply-header h2 .el-icon {
  color: #FF5000;
}

.apply-header p {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.product-info {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 24px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-detail h4 {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-detail .price {
  color: #FF5000;
  font-size: 18px;
  font-weight: 700;
}

.type-cards {
  display: flex;
  gap: 20px;
}

.type-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 24px;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.type-card:hover {
  border-color: #FF5000;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 80, 0, 0.15);
}

.type-card.active {
  border-color: #FF5000;
  background: linear-gradient(135deg, #fff7f0, #fff0e6);
  box-shadow: 0 4px 12px rgba(255, 80, 0, 0.2);
}

.type-card .el-icon {
  margin-bottom: 12px;
  color: #666;
  transition: all 0.3s;
}

.type-card:hover .el-icon,
.type-card.active .el-icon {
  color: #FF5000;
}

.type-card.refund .el-icon {
  color: #f56c6c;
}

.type-card.return .el-icon {
  color: #e6a23c;
}

.type-card.exchange .el-icon {
  color: #67c23a;
}

.type-card.active .el-icon {
  color: #FF5000;
}

.type-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  white-space: nowrap;
}

.type-desc {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
}

.amount-input {
  display: flex;
  align-items: center;
}

.amount-symbol {
  font-size: 18px;
  font-weight: 700;
  color: #FF5000;
  margin-right: 8px;
}

.uploader {
  margin-bottom: 8px;
}

.upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.upload-btn span {
  font-size: 12px;
  color: #999;
}

.upload-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #999;
  font-size: 13px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

.form-actions .el-button {
  min-width: 140px;
}

.side-tips {
  width: 320px;
  flex-shrink: 0;
}

.tip-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.tip-card h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.tip-card ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.tip-card li {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.tip-card li:last-child {
  border-bottom: none;
}

.tip-card li div strong {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.tip-card li div p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.option-tag {
  float: right;
  font-size: 12px;
  color: #FF5000;
  background: rgba(255, 80, 0, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

@media (max-width: 992px) {
  .apply-container {
    flex-direction: column;
  }

  .side-tips {
    width: 100%;
  }

  .type-cards {
    flex-wrap: wrap;
  }

  .type-card {
    flex: 0 0 calc(50% - 6px);
  }
}
</style>
