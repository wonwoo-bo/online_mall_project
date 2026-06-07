<template>
  <div class="product-conversion">
    <el-card>
      <div class="filter-bar">
        <el-select v-model="filterType" class="filter-select" @change="loadData">
          <el-option label="展示全部" :value="0" />
          <el-option label="按转化率" :value="1" />
          <el-option label="按访问量" :value="2" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadData"
        />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>

      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.overallConversion.toFixed(1) }}%</div>
          <div class="stat-label">整体转化率</div>
          <div class="stat-trend up">↑ 2.3%</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalVisits.toLocaleString() }}</div>
          <div class="stat-label">总访问量</div>
          <div class="stat-trend up">↑ 8.5%</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalOrders.toLocaleString() }}</div>
          <div class="stat-label">总订单数</div>
          <div class="stat-trend up">↑ 10.2%</div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.avgVisitDepth.toFixed(1) }}</div>
          <div class="stat-label">平均访问深度</div>
          <div class="stat-trend down">↓ 0.5</div>
        </el-card>
      </div>

      <div class="table-section">
        <h3>商品转化数据</h3>
        <el-table :data="conversionList" border>
          <el-table-column prop="productName" label="商品名称" min-width="200" />
          <el-table-column prop="visits" label="访问量" />
          <el-table-column prop="browseCount" label="浏览次数" />
          <el-table-column prop="addCartCount" label="加购数" />
          <el-table-column prop="orderCount" label="订单数" />
          <el-table-column prop="conversionRate" label="转化率">
            <template #default="{ row }">
              <el-tag :type="row.conversionRate >= 20 ? 'success' : row.conversionRate >= 10 ? 'warning' : 'danger'">
                {{ row.conversionRate.toFixed(1) }}%
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cartRate" label="加购率">
            <template #default="{ row }">{{ row.cartRate.toFixed(1) }}%</template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getStatisticsProductConversion } from "@/api/merchant";
import { ElMessage } from "element-plus";

const filterType = ref(0);
const dateRange = ref([]);
const conversionList = ref([]);
const loading = ref(false);

const stats = ref({
  overallConversion: 0,
  totalVisits: 0,
  totalOrders: 0,
  avgVisitDepth: 0
});

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      sortType: filterType.value
    };

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0];
      params.endDate = dateRange.value[1].toISOString().split('T')[0];
    }

    const res = await getStatisticsProductConversion(params);
    if (res.code === 200) {
      conversionList.value = (res.data?.list || []).map(item => ({
        ...item,
        visits: Number(item.visits) || 0,
        browseCount: Number(item.browseCount) || 0,
        addCartCount: Number(item.addCartCount) || 0,
        orderCount: Number(item.orderCount) || 0,
        conversionRate: Number(item.conversionRate) || 0,
        cartRate: Number(item.cartRate) || 0
      }));
      updateStats(conversionList.value);
    }
  } catch (error) {
    console.error("加载转化数据失败", error);
    ElMessage.error("加载转化数据失败");
  } finally {
    loading.value = false;
  }
};

const updateStats = (data) => {
  if (data.length === 0) {
    stats.value = {
      overallConversion: 0,
      totalVisits: 0,
      totalOrders: 0,
      avgVisitDepth: 0
    };
    return;
  }

  const totalVisits = data.reduce((sum, item) => sum + item.visits, 0);
  const totalOrders = data.reduce((sum, item) => sum + item.orderCount, 0);
  const totalBrowseCount = data.reduce((sum, item) => sum + item.browseCount, 0);
  
  const overallConversion = totalVisits > 0 ? (totalOrders / totalVisits) * 100 : 0;
  const avgVisitDepth = totalVisits > 0 ? totalBrowseCount / totalVisits : 0;

  stats.value = {
    overallConversion,
    totalVisits,
    totalOrders,
    avgVisitDepth
  };
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.product-conversion {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #ff4400;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.stat-trend {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.stat-trend.up {
  background: #f0f9eb;
  color: #52c41a;
}

.stat-trend.down {
  background: #fff2f0;
  color: #ff4d4f;
}

.table-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}
</style>
