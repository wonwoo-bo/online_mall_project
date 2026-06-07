<template>
  <div class="spec-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>规格管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openSpecDialog('add')">
              <el-icon><Plus /></el-icon>
              添加规格
            </el-button>

            <el-button @click="goToRecycle">
              <el-icon><Delete /></el-icon>
              查看回收站
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="specTree"
        v-loading="loading"
        row-key="id"
        default-expand-all
      >
        <el-table-column prop="name" label="规格名称" min-width="200" />
        <el-table-column label="规格值" min-width="400">
          <template #default="{ row }">
            <el-tag
              v-for="value in row.values"
              :key="value.id"
              :type="value.status === 1 ? 'success' : ''"
              :class="value.status === 0 ? 'disabled-tag' : ''"
              style="margin-right: 8px; margin-bottom: 4px; cursor: pointer"
              @click="toggleSpecValueStatus(row, value)"
            >
              {{ value.value }}
            </el-tag>
            <span
              v-if="!row.values || row.values.length === 0"
              style="color: #999"
            >
              暂无规格值
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'info'"
              style="cursor: pointer"
              @click="toggleSpecStatus(row)"
            >
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="openSpecDialog('edit', row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="deleteSpecType(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="specDialogVisible"
      :title="specDialogType === 'add' ? '添加规格' : '编辑规格'"
      width="650px"
      draggable
    >
      <el-form
        ref="specFormRef"
        :model="specForm"
        :rules="specRules"
        label-width="100px"
      >
        <el-form-item label="规格名称" prop="name">
          <el-input v-model="specForm.name" placeholder="如：颜色、尺码" />
        </el-form-item>
        <el-form-item label="规格状态" prop="status">
          <el-select v-model="specForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格值">
          <div class="spec-values-list">
            <div
              v-for="(value, index) in specForm.values"
              :key="index"
              class="spec-value-item"
            >
              <el-input
                v-model="value.value"
                :placeholder="`规格值 ${index + 1}`"
                class="value-input"
              />
              <el-switch
                v-model="value.status"
                :active-value="1"
                :inactive-value="0"
                active-color="#10b981"
                inactive-color="#6b7280"
                @change="updateSpecValueStatus(value)"
              />
              <el-button
                v-if="specForm.values.length > 1"
                type="danger"
                size="small"
                @click="deleteSpecValueItem(index)"
                class="delete-btn"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="spec-values-footer">
            <el-button type="primary" size="small" @click="addSpecValue">
              <el-icon><Plus /></el-icon>
              添加规格值
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="specDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSpec" :loading="specSubmitting">
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
import { Plus, Close, Delete } from "@element-plus/icons-vue";
import {
  getSpecTypeTree,
  createSpecType,
  updateSpecType,
  deleteSpecType as deleteSpecTypeApi,
  deleteSpecValue,
  updateSpecValue,
} from "@/api/merchant";

const router = useRouter();
const loading = ref(false);
const specTree = ref([]);
const specDialogVisible = ref(false);
const specDialogType = ref("add");
const specSubmitting = ref(false);

const specFormRef = ref(null);

const specForm = reactive({
  id: null,
  name: "",
  status: 1,
  values: [{ value: "", status: 1 }],
});

const specRules = {
  name: [{ required: true, message: "请输入规格名称", trigger: "blur" }],
  status: [{ required: true, message: "请选择规格状态", trigger: "change" }],
};

const loadSpecs = async () => {
  loading.value = true;
  try {
    const res = await getSpecTypeTree();
    if (res.code === 200) {
      specTree.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error("加载规格失败");
  } finally {
    loading.value = false;
  }
};

const openSpecDialog = (type, row = null) => {
  specDialogType.value = type;
  if (type === "edit" && row) {
    specForm.id = row.id;
    specForm.name = row.name;
    specForm.status = row.status !== undefined ? row.status : 1;
    specForm.values = row.values
      ? row.values.map((v) => ({
          id: v.id,
          value: v.value,
          status: v.status !== undefined ? v.status : 1,
        }))
      : [{ value: "", status: 1 }];
  } else {
    specForm.id = null;
    specForm.name = "";
    specForm.status = 1;
    specForm.values = [{ value: "", status: 1 }];
  }
  specDialogVisible.value = true;
};

const addSpecValue = () => {
  specForm.values.push({ value: "", status: 1 });
};

const removeSpecValue = (index) => {
  specForm.values.splice(index, 1);
};

const deleteSpecValueItem = async (index) => {
  const value = specForm.values[index];

  await ElMessageBox.confirm(`确定要删除规格值"${value.value}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });

  try {
    if (value.id) {
      const res = await deleteSpecValue(value.id);
      if (res.code === 200) {
        specForm.values.splice(index, 1);
        ElMessage.success("删除成功");
        loadSpecs();
      } else {
        ElMessage.error(res.message || "删除失败");
      }
    } else {
      specForm.values.splice(index, 1);
      ElMessage.success("删除成功");
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const updateSpecValueStatus = async (value) => {
  if (!value.id) return;
  const oldStatus = value.status;
  try {
    const res = await updateSpecValue(value.id, {
      id: value.id,
      value: value.value,
      status: value.status,
    });
    if (res.code === 200) {
      const statusText = value.status === 1 ? "启用" : "禁用";
      ElMessage.success(`规格值已${statusText}`);
      await loadSpecs();
    } else {
      value.status = oldStatus;
      ElMessage.error(res.message || "状态更新失败");
    }
  } catch (error) {
    value.status = oldStatus;
    ElMessage.error("状态更新失败");
  }
};

const submitSpec = async () => {
  if (!specFormRef.value) return;

  const validValues = specForm.values.filter((v) => v.value && v.value.trim());
  if (validValues.length === 0) {
    ElMessage.warning("请至少输入一个规格值");
    return;
  }

  specSubmitting.value = true;
  try {
    const data = {
      id: specForm.id,
      name: specForm.name,
      status: specForm.status,
      values: validValues.map((v) => ({ value: v.value, status: v.status })),
    };

    const api =
      specDialogType.value === "add" ? createSpecType : updateSpecType;
    const res =
      specDialogType.value === "add"
        ? await api(data)
        : await api(specForm.id, data);

    if (res.code === 200) {
      ElMessage.success(
        specDialogType.value === "add" ? "添加成功" : "修改成功",
      );
      specDialogVisible.value = false;
      loadSpecs();
    } else {
      ElMessage.error(res.message || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
  } finally {
    specSubmitting.value = false;
  }
};

const toggleSpecStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? "启用" : "禁用";

  try {
    const res = await updateSpecType(row.id, {
      id: row.id,
      name: row.name,
      status: newStatus,
      values: row.values,
    });
    if (res.code === 200) {
      row.status = newStatus;
      ElMessage.success(`规格已${statusText}`);
    } else {
      ElMessage.error(res.message || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

const toggleSpecValueStatus = async (specRow, value) => {
  if (!value.id) return;
  const newStatus = value.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? "启用" : "禁用";

  try {
    const res = await updateSpecValue(value.id, {
      id: value.id,
      value: value.value,
      status: newStatus,
    });
    if (res.code === 200) {
      value.status = newStatus;
      ElMessage.success(`规格值已${statusText}`);
    } else {
      ElMessage.error(res.message || "操作失败");
    }
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

const deleteSpecType = async (row) => {
  await ElMessageBox.confirm(`确定要删除规格"${row.name}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  });

  try {
    const res = await deleteSpecTypeApi(row.id);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      loadSpecs();
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
  router.push("/merchant/recycle?type=spec");
};



onMounted(() => {
  loadSpecs();
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

.spec-values-footer {
  margin-top: 12px;
}

.spec-values-list {
  max-height: 250px;
  overflow-y: auto;
}

.spec-value-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.value-input {
  flex: 1;
}

.disabled-tag {
  background-color: #f5f5f5;
  border-color: #d9d9d9;
  color: #999;
}
</style>
