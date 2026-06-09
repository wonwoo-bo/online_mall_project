<template>
  <div class="inventory-warning">
    <el-card>
      <div class="filter-bar">
        <el-select v-model="warningLevel" class="filter-select" @change="loadData">
          <el-option label="全部预警" :value="0" />
          <el-option label="严重预警" :value="1" />
          <el-option label="一般预警" :value="2" />
          <el-option label="关注提醒" :value="3" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="showAll">展示全部</el-button>
        <el-button type="success" @click="saveChanges" :disabled="!hasChanges">保存修改</el-button>
      </div>

      <div class="warning-summary">
        <div class="summary-item danger">
          <div class="summary-icon">
            <el-icon><WarningFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-count">{{ warningStats.danger }}</div>
            <div class="summary-label">严重预警</div>
          </div>
        </div>
        <div class="summary-item warning">
          <div class="summary-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-count">{{ warningStats.warning }}</div>
            <div class="summary-label">一般预警</div>
          </div>
        </div>
        <div class="summary-item info">
          <div class="summary-icon">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-count">{{ warningStats.info }}</div>
            <div class="summary-label">关注提醒</div>
          </div>
        </div>
      </div>

      <div class="table-section">
        <h3>库存预警列表</h3>
        <div class="table-scroll">
          <el-table :data="warningList" border>
            <el-table-column prop="productName" label="商品名称" min-width="200" />
            <el-table-column prop="category" label="分类" />
            <el-table-column prop="brand" label="品牌" />
            <el-table-column prop="stock" label="库存数量" />
            <el-table-column prop="avgSales" label="日均销量" />
            <el-table-column prop="warningDays" label="预警天数">
              <template #default="{ row }">
                <el-tag :type="row.warningLevel === 1 ? 'danger' : row.warningLevel === 2 ? 'warning' : 'info'">
                  {{ calculateWarningDays(row) }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warningLevel" label="预警级别">
              <template #default="{ row }">
                <el-select 
                  v-model="row.warningLevel" 
                  class="level-select" 
                  @change="() => handleLevelChange(row)"
                >
                  <el-option label="严重预警" :value="1" />
                  <el-option label="一般预警" :value="2" />
                  <el-option label="关注提醒" :value="3" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="suggestion" label="处理建议">
              <template #default="{ row }">
                <el-input 
                  v-model="row.suggestion" 
                  class="suggestion-input" 
                  @change="markChanged(row)"
                  @dblclick="showDetail(row)"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <el-dialog 
      title="处理建议详情" 
      :visible.sync="detailVisible" 
      width="400px"
      draggable
    >
      <div v-if="selectedRow" class="detail-content">
        <div class="detail-item">
          <span class="detail-label">商品名称：</span>
          <span class="detail-value">{{ selectedRow.productName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">分类：</span>
          <span class="detail-value">{{ selectedRow.category }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">品牌：</span>
          <span class="detail-value">{{ selectedRow.brand }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">库存数量：</span>
          <span class="detail-value">{{ selectedRow.stock }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">日均销量：</span>
          <span class="detail-value">{{ selectedRow.avgSales }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">预警天数：</span>
          <span class="detail-value">{{ calculateWarningDays(selectedRow) }}天</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">预警级别：</span>
          <span class="detail-value">{{ levelText[selectedRow.warningLevel] }}</span>
        </div>
        <div class="detail-item suggestion-detail">
          <span class="detail-label">处理建议：</span>
          <textarea 
            v-model="selectedRow.suggestion" 
            class="detail-textarea" 
            @change="markChanged(selectedRow)"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { WarningFilled, Warning, InfoFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getStatisticsOverstock } from "@/api/merchant";

const warningLevel = ref(0);
const warningList = ref([]);
const warningStats = ref({ danger: 0, warning: 0, info: 0 });
const hasChanges = ref(false);
const changedItems = ref(new Set());
const detailVisible = ref(false);
const selectedRow = ref(null);
const loading = ref(false);

const levelText = {
  1: "严重预警",
  2: "一般预警",
  3: "关注提醒"
};

const calculateWarningDays = (row) => {
  if (!row.stock || !row.avgSales) return 0;
  if (row.avgSales === 0) return row.stock;
  return Math.floor(row.stock / row.avgSales);
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      warningLevel: warningLevel.value
    };

    const res = await getStatisticsOverstock(params);
    if (res.code === 200) {
      warningList.value = (res.data?.list || []).map(item => ({
        ...item,
        id: item.id || item.productId,
        stock: Number(item.stock) || 0,
        avgSales: Number(item.avgSales) || 0,
        warningLevel: Number(item.warningLevel) || 3,
        suggestion: item.suggestion || ""
      }));
    }
  } catch (error) {
    console.error("加载库存预警数据失败", error);
    ElMessage.error("加载库存预警数据失败");
  } finally {
    loading.value = false;
    updateStats();
  }
};

const updateStats = () => {
  warningStats.value = {
    danger: warningList.value.filter(item => item.warningLevel === 1).length,
    warning: warningList.value.filter(item => item.warningLevel === 2).length,
    info: warningList.value.filter(item => item.warningLevel === 3).length
  };
};

const showAll = () => {
  warningLevel.value = 0;
  warningList.value = JSON.parse(JSON.stringify(allWarningData));
  hasChanges.value = false;
  changedItems.value.clear();
  updateStats();
};

const showDetail = (row) => {
  selectedRow.value = row;
  detailVisible.value = true;
};

const markChanged = (row) => {
  hasChanges.value = true;
  changedItems.value.add(row.id);
  updateStats();
};

const handleLevelChange = (row) => {
  markChanged(row);
};

const saveChanges = () => {
  changedItems.value.forEach(id => {
    const item = warningList.value.find(i => i.id === id);
    if (item) {
      const original = allWarningData.find(i => i.id === id);
      if (original) {
        original.warningLevel = item.warningLevel;
        original.suggestion = item.suggestion;
      }
    }
  });
  
  ElMessage.success('修改已保存');
  hasChanges.value = false;
  changedItems.value.clear();
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.inventory-warning {
  padding: 20px;
}

.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.filter-select {
  width: 150px;
}

.warning-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.summary-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
}

.summary-item.danger {
  background: linear-gradient(135deg, #fff2f0 0%, #ffe0dc 100%);
  border-left: 4px solid #ff4d4f;
}

.summary-item.warning {
  background: linear-gradient(135deg, #fffbe6 0%, #fff5cc 100%);
  border-left: 4px solid #faad14;
}

.summary-item.info {
  background: linear-gradient(135deg, #e6f7ff 0%, #b3d9ff 100%);
  border-left: 4px solid #1890ff;
}

.summary-icon {
  font-size: 32px;
  margin-right: 16px;
}

.summary-item.danger .summary-icon {
  color: #ff4d4f;
}

.summary-item.warning .summary-icon {
  color: #faad14;
}

.summary-item.info .summary-icon {
  color: #1890ff;
}

.summary-count {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.summary-label {
  font-size: 14px;
  color: #666;
}

.table-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}

.table-scroll {
  overflow-x: auto;
  overflow-y: hidden;
}

.table-scroll ::v-deep .el-table {
  width: 100%;
  min-width: 100%;
}

.table-scroll ::v-deep .el-table__header-wrapper,
.table-scroll ::v-deep .el-table__body-wrapper {
  overflow-x: hidden;
}

.level-select {
  width: 100px;
}

.suggestion-input {
  min-width: 150px;
  max-width: calc(100% - 12px);
  width: 100%;
  word-break: break-all;
  white-space: normal;
  cursor: pointer;
  margin-right: 6px;
  padding-right: 6px;
}

.detail-content {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
}

.detail-label {
  width: 90px;
  font-weight: bold;
  color: #666;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #333;
}

.suggestion-detail {
  flex-direction: column;
}

.suggestion-detail .detail-label {
  margin-bottom: 8px;
}

.detail-textarea {
  width: 100%;
  height: 120px;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  resize: none;
}
</style>
