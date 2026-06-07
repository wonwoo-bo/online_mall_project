<template>
  <div class="product-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="goToAdd">
              <el-icon><Plus /></el-icon>
              添加商品
            </el-button>
            <el-button @click="goToRecycle">
              <el-icon><Delete /></el-icon>
              回收站
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-form-item label="商品名称">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索商品名称"
              style="width: 200px"
              clearable
              @clear="loadProducts"
              @keyup.enter="loadProducts"
            >
              <template #append>
                <el-button :icon="Search" @click="loadProducts" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="商品分类">
            <el-select
              v-model="queryParams.categoryId"
              placeholder="请选择分类"
              style="width: 150px"
              clearable
              @change="loadProducts"
            >
              <el-option
                v-for="cat in categoryOptions"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="商品品牌">
            <el-select
              v-model="queryParams.brandId"
              placeholder="请选择品牌"
              style="width: 150px"
              clearable
              @change="loadProducts"
            >
              <el-option
                v-for="brand in brandOptions"
                :key="brand.id"
                :label="brand.name"
                :value="brand.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="商品状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              style="width: 120px"
              clearable
              @change="loadProducts"
            >
              <el-option label="全部" :value="null" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>

          <el-form-item label="价格区间">
            <el-input
              v-model.number="queryParams.minPrice"
              placeholder="最低价"
              type="number"
              style="width: 100px"
              clearable
              @change="loadProducts"
            />
            <span style="margin: 0 5px">-</span>
            <el-input
              v-model.number="queryParams.maxPrice"
              placeholder="最高价"
              type="number"
              style="width: 100px"
              clearable
              @change="loadProducts"
            />
          </el-form-item>

          <el-form-item>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 批量操作区域 -->
      <div class="batch-actions" v-if="selectedProducts.length > 0">
        <el-alert type="info" :closable="false" style="margin-bottom: 10px">
          已选择 {{ selectedProducts.length }} 项
          <el-button type="primary" link @click="selectedProducts = []">清空</el-button>
        </el-alert>
        <div class="action-buttons">
          <el-button size="small" @click="batchOnShelf">批量上架</el-button>
          <el-button size="small" @click="batchOffShelf">批量下架</el-button>
          <el-button size="small" @click="batchDelete">批量删除</el-button>
        </div>
      </div>

      <!-- 商品列表 -->
      <el-table
        ref="productTable"
        :data="productList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImg || row.mainImage"
              :src="row.coverImg || row.mainImage"
              style="width: 60px; height: 60px"
              fit="cover"
              :preview-src-list="[row.coverImg || row.mainImage]"
              preview-teleported
            />
            <div v-else class="no-image">无图</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            <span v-if="row.originalPrice" style="text-decoration: line-through; color: #999">¥{{ row.originalPrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.stock <= 10 ? '#f56c6c' : 'inherit' }">
              {{ row.stock }}
            </span>
            <el-tag v-if="row.stock <= 10" type="danger" size="small" style="margin-left: 5px">低库存</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="goToEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="primary" link size="small" @click="adjustPrice(row)">
              调价
            </el-button>
            <el-button type="primary" link size="small" @click="adjustStock(row)">
              调库存
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </el-card>

    <!-- 价格调整对话框 -->
    <el-dialog title="调整价格" v-model="priceDialogVisible" width="500px" draggable>
      <el-form :model="priceForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ priceForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前售价">
          <span>¥{{ priceForm.currentPrice }}</span>
        </el-form-item>
        <el-form-item label="新售价" required>
          <el-input-number
            v-model="priceForm.price"
            :precision="2"
            :min="0.01"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number
            v-model="priceForm.originalPrice"
            :precision="2"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="成本价">
          <el-input-number
            v-model="priceForm.costPrice"
            :precision="2"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="priceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPriceAdjust" :loading="priceLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 库存调整对话框 -->
    <el-dialog title="调整库存" v-model="stockDialogVisible" width="500px" draggable>
      <el-form :model="stockForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stockForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整数量" required>
          <el-input-number
            v-model="stockForm.quantity"
            :min="-stockForm.currentStock"
            style="width: 100%"
          />
          <div style="color: #999; font-size: 12px; margin-top: 5px">
            正数增加库存，负数减少库存
          </div>
        </el-form-item>
        <el-form-item label="调整后库存">
          <span>{{ stockForm.currentStock + stockForm.quantity }}</span>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input
            v-model="stockForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStockAdjust" :loading="stockLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Delete } from '@element-plus/icons-vue'
import {
  getProductList,
  updateProductStatus,
  deleteProduct,
  batchOnShelf as batchOnShelfApi,
  batchOffShelf as batchOffShelfApi,
  updateProductPrice,
  adjustProductStock,
  getCategoryTree,
  getBrandList
} from '@/api/merchant'

const router = useRouter()
const loading = ref(false)
const productList = ref([])
const selectedProducts = ref([])
const total = ref(0)
const productTable = ref(null)
const categoryOptions = ref([])
const brandOptions = ref([])

// 价格调整相关
const priceDialogVisible = ref(false)
const priceLoading = ref(false)
const priceForm = reactive({
  id: null,
  productName: '',
  currentPrice: 0,
  price: 0,
  originalPrice: 0,
  costPrice: 0
})

// 库存调整相关
const stockDialogVisible = ref(false)
const stockLoading = ref(false)
const stockForm = reactive({
  id: null,
  productName: '',
  currentStock: 0,
  quantity: 0,
  reason: ''
})

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  categoryId: null,
  brandId: null,
  status: null,
  minPrice: null,
  maxPrice: null
})

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = {}
    Object.keys(queryParams).forEach(key => {
      const value = queryParams[key]
      if (value !== null && value !== undefined && value !== '') {
        params[key] = value
      }
    })
    const res = await getProductList(params)
    if (res.code === 200) {
      productList.value = res.data?.list || res.data || []
      total.value = res.data?.total || productList.value.length
    }
  } catch (error) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    if (res.code === 200) {
      categoryOptions.value = flattenCategories(res.data || [])
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 加载品牌列表
const loadBrands = async () => {
  try {
    const res = await getBrandList()
    if (res.code === 200) {
      brandOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载品牌失败', error)
  }
}

// 扁平化分类树
const flattenCategories = (categories, result = []) => {
  categories.forEach(cat => {
    result.push({ id: cat.id, name: cat.name })
    if (cat.children && cat.children.length > 0) {
      flattenCategories(cat.children, result)
    }
  })
  return result
}

// 重置筛选
const resetFilters = () => {
  queryParams.keyword = ''
  queryParams.categoryId = null
  queryParams.brandId = null
  queryParams.status = null
  queryParams.minPrice = null
  queryParams.maxPrice = null
  loadProducts()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedProducts.value = selection
}

// 跳转到添加商品页面
const goToAdd = () => {
  router.push('/merchant/products/add')
}

// 跳转到编辑商品页面
const goToEdit = (row) => {
  router.push(`/merchant/products/edit/${row.id}`)
}

// 跳转到回收站
const goToRecycle = () => {
  router.push('/merchant/recycle?type=product')
}

// 切换上下架状态
const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = row.status === 1 ? '下架' : '上架'

  try {
    const res = await updateProductStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadProducts()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    ElMessage.error(`${action}失败`)
  }
}

// 删除商品
const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除商品"${row.name}"吗？删除后可在回收站恢复。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })

  try {
    const res = await deleteProduct(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadProducts()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量上架
const batchOnShelf = async () => {
  const ids = selectedProducts.value.map(p => p.id)
  if (ids.length === 0) {
    ElMessage.warning('请选择要上架的商品')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要上架选中的 ${ids.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    const res = await batchOnShelfApi(ids)
    if (res.code === 200) {
      ElMessage.success('批量上架成功')
      selectedProducts.value = []
      loadProducts()
    } else {
      ElMessage.error(res.message || '批量上架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量上架失败')
    }
  }
}

// 批量下架
const batchOffShelf = async () => {
  const ids = selectedProducts.value.map(p => p.id)
  if (ids.length === 0) {
    ElMessage.warning('请选择要下架的商品')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要下架选中的 ${ids.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    const res = await batchOffShelfApi(ids)
    if (res.code === 200) {
      ElMessage.success('批量下架成功')
      selectedProducts.value = []
      loadProducts()
    } else {
      ElMessage.error(res.message || '批量下架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
    }
  }
}

// 批量删除
const batchDelete = async () => {
  const ids = selectedProducts.value.map(p => p.id)
  if (ids.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 件商品吗？删除后可在回收站恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 逐个删除
    for (const id of ids) {
      await deleteProduct(id)
    }
    ElMessage.success('批量删除成功')
    selectedProducts.value = []
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 打开价格调整对话框
const adjustPrice = (row) => {
  priceForm.id = row.id
  priceForm.productName = row.name
  priceForm.currentPrice = row.price
  priceForm.price = row.price
  priceForm.originalPrice = row.originalPrice || 0
  priceForm.costPrice = row.costPrice || 0
  priceDialogVisible.value = true
}

// 提交价格调整
const submitPriceAdjust = async () => {
  if (priceForm.price <= 0) {
    ElMessage.warning('售价必须大于0')
    return
  }

  priceLoading.value = true
  try {
    const res = await updateProductPrice(priceForm.id, {
      price: priceForm.price,
      originalPrice: priceForm.originalPrice,
      costPrice: priceForm.costPrice
    })

    if (res.code === 200) {
      ElMessage.success('价格调整成功')
      priceDialogVisible.value = false
      loadProducts()
    } else {
      ElMessage.error(res.message || '价格调整失败')
    }
  } catch (error) {
    ElMessage.error('价格调整失败')
  } finally {
    priceLoading.value = false
  }
}

// 打开库存调整对话框
const adjustStock = (row) => {
  stockForm.id = row.id
  stockForm.productName = row.name
  stockForm.currentStock = row.stock
  stockForm.quantity = 0
  stockForm.reason = ''
  stockDialogVisible.value = true
}

// 提交库存调整
const submitStockAdjust = async () => {
  if (stockForm.quantity === 0) {
    ElMessage.warning('调整数量不能为0')
    return
  }

  if (stockForm.currentStock + stockForm.quantity < 0) {
    ElMessage.warning('库存不能为负数')
    return
  }

  stockLoading.value = true
  try {
    const res = await adjustProductStock(stockForm.id, {
      quantity: stockForm.quantity,
      reason: stockForm.reason
    })

    if (res.code === 200) {
      ElMessage.success('库存调整成功')
      stockDialogVisible.value = false
      loadProducts()
    } else {
      ElMessage.error(res.message || '库存调整失败')
    }
  } catch (error) {
    ElMessage.error('库存调整失败')
  } finally {
    stockLoading.value = false
  }
}

// 页面加载时初始化
onMounted(() => {
  loadProducts()
  loadCategories()
  loadBrands()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.filter-form {
  margin-bottom: 0;
}

.batch-actions {
  margin-bottom: 15px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.no-image {
  width: 60px;
  height: 60px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
