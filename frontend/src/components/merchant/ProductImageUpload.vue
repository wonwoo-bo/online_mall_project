<template>
  <div class="product-image-upload">
    <!-- 图片列表 -->
    <div class="image-list">
      <div
        v-for="(image, index) in modelValue"
        :key="image.id || index"
        class="image-item"
        :class="{ 'is-main': image.isMain || index === 0 }"
      >
        <el-image
          :src="image.imageUrl || image.url"
          fit="cover"
          class="image-preview"
          :preview-src-list="[image.imageUrl || image.url]"
          preview-teleported
        />
        <div class="image-actions">
          <el-button
            v-if="!image.isMain && index !== 0"
            size="small"
            type="primary"
            link
            @click="setMainImage(index)"
          >
            设为主图
          </el-button>
          <el-tag v-if="image.isMain || index === 0" type="success" size="small">
            主图
          </el-tag>
          <el-button
            size="small"
            type="danger"
            link
            @click="removeImage(index)"
          >
            删除
          </el-button>
        </div>
        <div class="image-order">
          <span v-if="index > 0">
            <el-button size="small" link @click="moveImage(index, -1)">上移</el-button>
          </span>
          <span v-else style="color: #999; font-size: 12px">置顶</span>
        </div>
      </div>

      <!-- 上传按钮 -->
      <div class="upload-item" v-if="modelValue.length < maxCount">
        <el-upload
          ref="uploadRef"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :before-upload="handleBeforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :disabled="uploading"
          accept="image/jpeg,image/png,image/gif,image/webp"
        >
          <div class="upload-content">
            <el-icon v-if="uploading" class="el-icon-loading">
              <Loading />
            </el-icon>
            <el-icon v-else><Plus /></el-icon>
            <div class="upload-text">{{ uploading ? '上传中...' : '上传图片' }}</div>
          </div>
        </el-upload>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="upload-tips">
      <div class="tip-item">
        <el-icon><InfoFilled /></el-icon>
        <span>支持 jpg、png、gif、webp 格式</span>
      </div>
      <div class="tip-item">
        <el-icon><InfoFilled /></el-icon>
        <span>单张图片大小不超过 5MB</span>
      </div>
      <div class="tip-item">
        <el-icon><InfoFilled /></el-icon>
        <span>最多上传 {{ maxCount }} 张图片，第一张为主图</span>
      </div>
    </div>

    <!-- 图片裁剪对话框 -->
    <el-dialog
      v-model="cropperDialogVisible"
      title="图片裁剪"
      width="600px"
      draggable
    >
      <div class="cropper-container">
        <vue-cropper
          v-if="cropperDialogVisible"
          ref="cropperRef"
          :src="cropperImageUrl"
          :aspect-ratio="1"
          :view-mode="1"
          :drag-mode="'move'"
          :auto-crop-area="0.9"
          @ready="onCropperReady"
        />
      </div>
      <template #footer>
        <el-button @click="cropperDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCrop">确认裁剪</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Loading, InfoFilled } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxCount: {
    type: Number,
    default: 10
  },
  uploadUrl: {
    type: String,
    default: '/api/upload'
  },
  maxSize: {
    type: Number,
    default: 5 // MB
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'upload-success', 'upload-error'])

const uploadRef = ref(null)
const cropperRef = ref(null)
const uploading = ref(false)
const cropperDialogVisible = ref(false)
const cropperImageUrl = ref('')
const currentFile = ref(null)

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('merchant_token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

// 允许的图片类型
const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']

// 上传前校验
const handleBeforeUpload = (file) => {
  // 检查文件类型
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('不支持的图片格式，请上传 jpg、png、gif、webp 格式的图片')
    return false
  }

  // 检查文件大小
  const maxSizeBytes = props.maxSize * 1024 * 1024
  if (file.size > maxSizeBytes) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return false
  }

  // 检查数量限制
  if (props.modelValue.length >= props.maxCount) {
    ElMessage.error(`最多只能上传 ${props.maxCount} 张图片`)
    return false
  }

  uploading.value = true
  currentFile.value = file
  return true
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  uploading.value = false

  if (response.code === 200 || response.success) {
    const imageUrl = response.url || response.data?.url || response.data

    const newImage = {
      id: Date.now(),
      imageUrl: imageUrl,
      url: imageUrl,
      isMain: props.modelValue.length === 0,
      sortOrder: props.modelValue.length
    }

    const newList = [...props.modelValue, newImage]
    emit('update:modelValue', newList)
    emit('change', newList)
    emit('upload-success', newImage)

    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
    emit('upload-error', { file, error: response.message })
  }
}

// 上传失败
const handleUploadError = (error, file) => {
  uploading.value = false
  ElMessage.error('图片上传失败，请重试')
  emit('upload-error', { file, error })
}

// 设置主图
const setMainImage = (index) => {
  const newList = [...props.modelValue]
  const image = newList.splice(index, 1)[0]
  image.isMain = true
  newList.unshift(image)

  // 更新其他图片的主图状态
  newList.forEach((img, idx) => {
    img.isMain = idx === 0
    img.sortOrder = idx
  })

  emit('update:modelValue', newList)
  emit('change', newList)
}

// 删除图片
const removeImage = (index) => {
  const newList = [...props.modelValue]
  const removedImage = newList.splice(index, 1)[0]

  // 如果删除的是主图，将第一张设为主图
  if (removedImage.isMain && newList.length > 0) {
    newList[0].isMain = true
  }

  // 重新排序
  newList.forEach((img, idx) => {
    img.sortOrder = idx
  })

  emit('update:modelValue', newList)
  emit('change', newList)
}

// 移动图片
const moveImage = (index, direction) => {
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= props.modelValue.length) return

  const newList = [...props.modelValue]
  const temp = newList[index]
  newList[index] = newList[newIndex]
  newList[newIndex] = temp

  // 更新排序
  newList.forEach((img, idx) => {
    img.sortOrder = idx
  })

  emit('update:modelValue', newList)
  emit('change', newList)
}

// 裁剪相关方法
const onCropperReady = () => {
  console.log('Cropper ready')
}

const confirmCrop = () => {
  if (cropperRef.value) {
    cropperRef.value.getCropBlob((blob) => {
      // 创建新的文件对象
      const file = new File([blob], currentFile.value.name, {
        type: blob.type
      })

      // 替换上传
      const uploadInstance = uploadRef.value
      if (uploadInstance) {
        uploadInstance.clearFiles()
        uploadInstance.handleStart(file)
      }

      cropperDialogVisible.value = false
    })
  }
}

// 暴露方法给父组件
defineExpose({
  uploadRef,
  setMainImage,
  removeImage,
  moveImage
})
</script>

<style scoped>
.product-image-upload {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.image-item {
  width: 120px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  background: #fff;
}

.image-item.is-main {
  border-color: #409eff;
}

.image-preview {
  width: 100%;
  height: 120px;
  display: block;
}

.image-actions {
  padding: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  background: #f5f7fa;
  border-top: 1px solid #dcdfe6;
}

.image-order {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 12px;
}

.upload-item {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-item:hover {
  border-color: #409eff;
}

.upload-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  font-size: 12px;
  margin-top: 5px;
}

.upload-tips {
  display: flex;
  flex-direction: column;
  gap: 5px;
  color: #909399;
  font-size: 12px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.cropper-container {
  width: 100%;
  height: 400px;
}
</style>
