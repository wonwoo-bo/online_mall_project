<template>
  <div class="brand-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>品牌管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openDialog('add')">
              <el-icon><Plus /></el-icon>
              添加品牌
            </el-button>

            <el-button @click="goToRecycle">
              <el-icon><Delete /></el-icon>
              查看回收站
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="brandList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="品牌名称" min-width="150" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'info'"
              style="cursor: pointer"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="openDialog('edit', row)"
            >
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? "禁用" : "启用" }}
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加品牌' : '编辑品牌'"
      width="500px"
      draggable
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入品牌名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入品牌描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Delete } from "@element-plus/icons-vue";
import {
  getBrandList,
  createBrand,
  updateBrand,
  deleteBrand,
  updateBrandStatus,
} from "@/api/merchant";

const router = useRouter();
const loading = ref(false);
const brandList = ref([]);
const dialogVisible = ref(false);
const dialogType = ref("add");
const submitting = ref(false);
const formRef = ref(null);

const form = reactive({
  id: null,
  name: "",
  description: "",
});

const rules = {
  name: [{ required: true, message: "请输入品牌名称", trigger: "blur" }],
};

const loadBrands = async () => {
  loading.value = true;
  try {
    const res = await getBrandList();
    if (res.code === 200) {
      brandList.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error("加载品牌列表失败");
  } finally {
    loading.value = false;
  }
};

const openDialog = (type, row = null) => {
  dialogType.value = type;
  if (type === "edit" && row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      description: row.description || "",
    });
  } else {
    Object.assign(form, {
      id: null,
      name: "",
      description: "",
    });
  }
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      const data = dialogType.value === "add" ? form : { ...form, id: form.id };
      const res = dialogType.value === "add" ? await createBrand(data) : await updateBrand(form.id, data);

      if (res.code === 200) {
        ElMessage.success(dialogType.value === "add" ? "添加成功" : "修改成功");
        dialogVisible.value = false;
        loadBrands();
      } else {
        ElMessage.error(res.message || "操作失败");
      }
    } catch (error) {
      ElMessage.error("操作失败");
    } finally {
      submitting.value = false;
    }
  });
};

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? "启用" : "禁用";

  try {
    const res = await updateBrandStatus(row.id, newStatus);
    if (res.code === 200) {
      row.status = newStatus;
      ElMessage.success(`品牌已${statusText}`);
    } else {
      ElMessage.error(res.message || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除品牌"${row.name}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });

  try {
    const res = await deleteBrand(row.id);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      loadBrands();
    } else {
      ElMessage.error(res.message || "删除失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const goToRecycle = () => {
  router.push("/merchant/recycle?type=brand");
};



onMounted(() => {
  loadBrands();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>
