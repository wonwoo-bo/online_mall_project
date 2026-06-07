<template>
  <div class="product-ranking">
    <el-card>
      <div class="filter-bar">
        <el-select v-model="filterType" class="filter-select" @change="loadData">
          <el-option label="展示全部" :value="0" />
          <el-option label="按销量" :value="1" />
          <el-option label="按销售额" :value="2" />
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

      <div class="table-section">
        <h3>商品销量排行 Top20</h3>
        <el-table :data="productList" border v-loading="loading">
          <el-table-column type="index" label="排名" width="60">
            <template #default="{ $index }">
              <el-tag
                v-if="$index < 3"
                :type="indexType[$index]"
                size="small"
              >
                {{ $index + 1 }}
              </el-tag>
              <span v-else>{{ $index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" min-width="200" />
          <el-table-column prop="salesCount" label="销量(件)" />
          <el-table-column prop="salesAmount" label="销售额(元)">
            <template #default="{ row }">{{ row.salesAmount.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="avgPrice" label="均价(元)">
            <template #default="{ row }">{{ row.avgPrice.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="growth" label="环比增长">
            <template #default="{ row }">
              <el-tag :type="row.growth >= 0 ? 'success' : 'danger'">
                {{ row.growth >= 0 ? '+' : '' }}{{ row.growth.toFixed(1) }}%
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getProductRanking } from "@/api/merchant";

const filterType = ref(0);
const dateRange = ref([]);
const productList = ref([]);
const loading = ref(false);

const indexType = ["danger", "warning", "success"];

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      sortType: filterType.value,
    };

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0];
      params.endDate = dateRange.value[1].toISOString().split('T')[0];
    }

    const res = await getProductRanking(params);
    if (res.code === 200) {
      productList.value = (res.data?.list || []).map(item => ({
        ...item,
        salesCount: Number(item.salesCount) || 0,
        salesAmount: Number(item.salesAmount) || 0,
        avgPrice: Number(item.avgPrice) || 0,
        growth: Number(item.growth) || 0,
      }));
    }
  } catch (error) {
    console.error("加载数据失败", error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.product-ranking {
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

.table-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}
</style>
