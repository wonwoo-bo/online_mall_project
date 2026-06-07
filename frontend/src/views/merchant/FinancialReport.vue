<template>
  <div class="financial-report">
    <el-card>
      <div class="filter-bar">
        <el-select
          v-model="reportType"
          class="filter-select"
          @change="loadData"
        >
          <el-option label="展示全部" :value="0" />
          <el-option label="日报" :value="1" />
          <el-option label="周报" :value="2" />
          <el-option label="月报" :value="3" />
          <el-option label="年报" :value="4" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <el-select v-model="typeFilter" class="filter-select">
          <el-option label="全部" :value="0" />
          <el-option label="收入" :value="1" />
          <el-option label="支出" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="exportReport">导出报表</el-button>
      </div>

      <div class="stats-row">
        <el-card class="stat-card main">
          <div class="stat-header">
            <span class="stat-title">总收入</span>
            <el-tag type="success">+12.5%</el-tag>
          </div>
          <div class="stat-amount">¥ {{ formatMoney(totalIncome) }}</div>
          <div class="stat-detail">订单数: {{ orderCount }}单</div>
        </el-card>

        <div class="stat-group">
          <el-card class="stat-card">
            <div class="stat-label">商品销售</div>
            <div class="stat-value">¥ {{ formatMoney(productSales) }}</div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-label">运费收入</div>
            <div class="stat-value">¥ {{ formatMoney(shippingIncome) }}</div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-label">退款金额</div>
            <div class="stat-value refund">
              -¥ {{ formatMoney(refundAmount) }}
            </div>
          </el-card>
        </div>
      </div>

      <div class="report-section">
        <h3>收支明细</h3>
        <el-table :data="reportData" border>
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="type" label="类型" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="income" label="收入(元)">
            <template #default="{ row }">
              <span v-if="Number(row.income) > 0" class="income"
                >+{{ row.income }}</span
              >
            </template>
          </el-table-column>
          <el-table-column prop="expense" label="支出(元)">
            <template #default="{ row }">
              <span v-if="Number(row.expense) > 0" class="expense"
                >-{{ row.expense }}</span
              >
            </template>
          </el-table-column>
          <el-table-column prop="balance" label="余额(元)">
            <template #default="{ row }">{{ row.balance }}</template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getStatisticsFinancial } from "@/api/merchant";

const reportType = ref(0);
const dateRange = ref([]);
const typeFilter = ref(0);
const reportData = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(100);
const loading = ref(false);

const totalIncome = ref(0);
const orderCount = ref(0);
const productSales = ref(0);
const shippingIncome = ref(0);
const refundAmount = ref(0);

const formatMoney = (value) => {
  return value.toLocaleString("zh-CN", { minimumFractionDigits: 2 });
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      reportType: reportType.value,
      typeFilter: typeFilter.value,
      page: currentPage.value,
      size: pageSize.value
    };

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0];
      params.endDate = dateRange.value[1].toISOString().split('T')[0];
    }

    const res = await getStatisticsFinancial(params);
    if (res.code === 200) {
      reportData.value = res.data?.list || [];
      total.value = res.data?.total || 0;
      
      totalIncome.value = Number(res.data?.totalIncome || 0);
      orderCount.value = Number(res.data?.orderCount || 0);
      productSales.value = Number(res.data?.productSales || 0);
      shippingIncome.value = Number(res.data?.shippingIncome || 0);
      refundAmount.value = Number(res.data?.refundAmount || 0);
    }
  } catch (error) {
    console.error("加载财务数据失败", error);
    ElMessage.error("加载财务数据失败");
  } finally {
    loading.value = false;
  }
};

const updateStats = () => {
  if (reportData.value.length === 0) return;
  
  const productSalesItems = reportData.value.filter((item) =>
    item.description && item.description.includes("商品销售"),
  );
  const shippingIncomeItems = reportData.value.filter((item) =>
    item.description && item.description.includes("运费"),
  );
  const refundItems = reportData.value.filter((item) => 
    item.description && item.description.includes("退款")
  );

  productSales.value = productSalesItems.reduce(
    (sum, item) => sum + (Number(item.income) || 0),
    0,
  );
  shippingIncome.value = shippingIncomeItems.reduce(
    (sum, item) => sum + (Number(item.income) || 0),
    0,
  );
  refundAmount.value = refundItems.reduce((sum, item) => sum + (Number(item.expense) || 0), 0);

  const allIncome = reportData.value.filter((item) => item.type === "收入");
  totalIncome.value = allIncome.reduce((sum, item) => sum + (Number(item.income) || 0), 0);
};

const handleSizeChange = (val) => {
  pageSize.value = val;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

const exportReport = () => {
  if (reportData.value.length === 0) {
    ElMessage.warning("没有数据可导出");
    return;
  }

  let csvContent = "日期,类型,描述,收入(元),支出(元),余额(元)\n";

  reportData.value.forEach((item) => {
    csvContent += `${item.date},${item.type},${item.description},${item.income.toFixed(2)},${item.expense.toFixed(2)},${item.balance.toFixed(2)}\n`;
  });

  const blob = new Blob([`\uFEFF${csvContent}`], {
    type: "text/csv;charset=utf-8;",
  });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);

  link.setAttribute("href", url);
  link.setAttribute(
    "download",
    `财务报表_${new Date().toLocaleDateString("zh-CN").replace(/\//g, "-")}.csv`,
  );
  link.style.visibility = "hidden";

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  ElMessage.success("报表导出成功");
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.financial-report {
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

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card.main {
  flex: 2;
  padding: 24px;
  background: linear-gradient(135deg, #ff4400 0%, #ff6b35 100%);
  color: #fff;
  border: none;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-title {
  font-size: 14px;
  opacity: 0.9;
}

.stat-card.main .stat-amount {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-detail {
  font-size: 13px;
  opacity: 0.8;
}

.stat-group {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.stat-card {
  padding: 16px;
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-value.refund {
  color: #ff4d4f;
}

.report-section {
  margin-top: 20px;
}

.report-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}

.income {
  color: #52c41a;
}

.expense {
  color: #ff4d4f;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
