<template>
  <div class="product-form">
    <el-card>
      <template #header>
        <span>添加商品</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            type="textarea"
            v-model="form.description"
            placeholder="请输入商品描述"
            rows="4"
          />
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" placeholder="请输入商品价格" style="width: 200px;" />
        </el-form-item>

        <el-form-item label="商品原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" placeholder="请输入商品原价（可选）" style="width: 200px;" />
        </el-form-item>

        <el-form-item label="成本价" prop="costPrice">
          <el-input-number v-model="form.costPrice" :min="0" :precision="2" placeholder="请输入成本价（可选）" style="width: 200px;" />
        </el-form-item>

        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :precision="0" placeholder="请输入库存数量" style="width: 200px;" />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-cascader
            v-model="categoryPath"
            :options="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择商品分类"
            clearable
            @change="handleCategoryChange"
          />
        </el-form-item>

        <el-form-item label="商品品牌" prop="brandId">
          <el-select
            v-model="form.brandId"
            filterable
            remote
            :remote-method="searchBrands"
            placeholder="请搜索选择商品品牌"
            :loading="brandLoading"
          >
            <el-option
              v-for="item in brandOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="商品规格">
          <div class="spec-section">
            <div class="spec-header">
              <span>已选规格</span>
              <el-button type="primary" size="small" @click="openSpecDialog">
                <el-icon><Plus /></el-icon>
                添加规格
              </el-button>
            </div>
            
            <div v-if="selectedSpecs.length === 0" class="empty-tip">
              暂无规格，请点击上方按钮添加
            </div>
            
            <el-table v-else :data="selectedSpecs" border>
              <el-table-column prop="typeName" label="规格类型" />
              <el-table-column prop="value" label="规格值" />
              <el-table-column label="操作" width="100">
                <template #default="{ row, $index }">
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeSpec($index)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- SKU预览列表 -->
            <div v-if="form.skus.length > 0" class="sku-preview">
              <div class="sku-preview-header">
                <span>SKU预览</span>
              </div>
              <el-table :data="form.skus" border size="small">
                <el-table-column prop="specs" label="规格组合" />
                <el-table-column label="价格">
                  <template #default="{ row }">
                    <el-input-number
                      v-model="row.price"
                      :min="0"
                      size="small"
                      style="width: 120px;"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="库存">
                  <template #default="{ row }">
                    <el-input-number
                      v-model="row.stock"
                      :min="0"
                      size="small"
                      style="width: 120px;"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="商品状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择商品状态">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图">
          <div class="image-upload-wrapper">
            <div v-if="coverImages.length > 0" class="image-preview-item">
              <img :src="getFullImageUrl(coverImages[0].url)" class="preview-image" @click="previewImage(coverPreviewList, 0)" />
              <div class="preview-overlay">
                <div class="overlay-icons">
                  <el-icon class="preview-icon" @click.stop="previewImage(coverPreviewList, 0)"><ZoomIn /></el-icon>
                  <el-icon class="delete-icon" @click.stop="handleCoverRemove()"><Delete /></el-icon>
                </div>
              </div>
            </div>
            <el-upload
              class="upload-demo"
              :action="''"
              list-type="picture-card"
              :show-file-list="coverImages.length === 0"
              :file-list="coverImages"
              :limit="1"
              :http-request="customUpload"
              :before-upload="beforeUpload"
              :on-remove="handleCoverRemove"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
          <div class="upload-tip">请上传商品封面图，支持 jpg、png 格式</div>
        </el-form-item>

        <el-form-item label="商品主图">
          <div class="image-upload-wrapper">
            <div v-for="(img, index) in mainImages" :key="img.uid" class="image-preview-item">
              <img :src="getFullImageUrl(img.url)" class="preview-image" @click="previewImage(mainPreviewList, index)" />
              <div class="preview-overlay">
                <div class="overlay-icons">
                  <el-icon class="preview-icon" @click.stop="previewImage(mainPreviewList, index)"><ZoomIn /></el-icon>
                  <el-icon class="delete-icon" @click.stop="handleMainImageRemove(img.uid)"><Delete /></el-icon>
                </div>
              </div>
            </div>
            <el-upload
              class="upload-demo"
              action="/api/upload"
              list-type="picture-card"
              :show-file-list="mainImages.length === 0"
              :file-list="mainImages"
              :limit="5"
              :on-success="handleMainUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :on-remove="handleMainRemove"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
          <div class="upload-tip">
            请上传商品主图，最多上传5张，支持 jpg、png 格式
          </div>
        </el-form-item>

        <el-form-item label="商品详情图">
          <div class="image-upload-wrapper">
            <div v-for="(img, index) in detailImages" :key="img.uid" class="image-preview-item">
              <img :src="getFullImageUrl(img.url)" class="preview-image" @click="previewImage(detailPreviewList, index)" />
              <div class="preview-overlay">
                <div class="overlay-icons">
                  <el-icon class="preview-icon" @click.stop="previewImage(detailPreviewList, index)"><ZoomIn /></el-icon>
                  <el-icon class="delete-icon" @click.stop="handleDetailImageRemove(img.uid)"><Delete /></el-icon>
                </div>
              </div>
            </div>
            <el-upload
              class="upload-demo"
              action="/api/upload"
              list-type="picture-card"
              :show-file-list="detailImages.length === 0"
              :file-list="detailImages"
              :limit="10"
              :on-success="handleDetailUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :on-remove="handleDetailRemove"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </div>
          <div class="upload-tip">
            请上传商品详情图，最多上传10张，支持 jpg、png 格式
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      v-model="specDialogVisible"
      title="添加规格"
      width="450px"
      draggable
    >
      <el-form ref="specFormRef" :model="specForm" label-width="100px">
        <el-form-item label="规格" prop="specPath">
          <el-cascader
            v-model="specForm.specPath"
            :options="specTree"
            :props="{ label: 'name', value: 'id', children: 'values' }"
            placeholder="请选择规格类型和规格值"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="specDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addSpec">确定</el-button>
      </template>
    </el-dialog>

    <el-image-viewer 
      v-if="showImageViewer" 
      :url-list="imageViewerUrls" 
      :initial-index="imageViewerIndex"
      @close="showImageViewer = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ZoomIn, Delete, Plus } from "@element-plus/icons-vue";
import { 
  createProduct,
  getFirstLevelCategories,
  getSecondLevelCategories,
  getBrandList,
  getSpecTypeTree
} from "@/api/merchant";
import axios from "axios";

const router = useRouter();
const formRef = ref(null);
const specFormRef = ref(null);

const form = reactive({
  name: "",
  description: "",
  price: null,
  originalPrice: null,
  costPrice: null,
  stock: 0,
  categoryId: null,
  brandId: null,
  status: 1,
  coverImg: "",
  mainImages: [],
  detailImages: [],
  skus: []
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: "请输入商品名称", trigger: "blur" },
    { min: 2, max: 100, message: "商品名称长度为2-100个字符", trigger: "blur" }
  ],
  description: [
    { required: true, message: "请输入商品描述", trigger: "blur" },
    { min: 10, message: "商品描述至少10个字符", trigger: "blur" }
  ],
  price: [
    { required: true, message: "请输入商品价格", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === "") {
          callback(new Error("请输入商品价格"));
        } else if (value <= 0) {
          callback(new Error("商品价格必须大于0"));
        } else {
          callback();
        }
      },
      trigger: "change"
    }
  ],
  stock: [
    { required: true, message: "请输入库存数量", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === "") {
          callback(new Error("请输入库存数量"));
        } else if (value < 0) {
          callback(new Error("库存数量不能为负数"));
        } else {
          callback();
        }
      },
      trigger: "change"
    }
  ],
  categoryId: [
    { required: true, message: "请选择商品分类", trigger: "change" }
  ],
  coverImg: [
    { required: true, message: "请上传商品封面图", trigger: "change" }
  ]
};

const coverImages = ref([]);
const mainImages = ref([]);
const detailImages = ref([]);

const coverPreviewList = ref([]);
const mainPreviewList = ref([]);
const detailPreviewList = ref([]);

const showImageViewer = ref(false);
const imageViewerUrls = ref([]);
const imageViewerIndex = ref(0);

const categoryTree = ref([]);
const categoryPath = ref([]);
const brandOptions = ref([]);
const brandLoading = ref(false);

const specTree = ref([]);
const selectedSpecs = ref([]);
const specDialogVisible = ref(false);
const specForm = reactive({
  specPath: []
});

const previewImage = (urls, index) => {
  if (urls && urls.length > 0 && index >= 0 && index < urls.length) {
    imageViewerUrls.value = urls;
    imageViewerIndex.value = index;
    showImageViewer.value = true;
  }
};

const getFullImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  if (url.startsWith('/')) {
    return import.meta.env.PROD ? url : 'http://localhost:8080' + url;
  }
  return url;
};

const loadCategories = async () => {
  try {
    const res = await getFirstLevelCategories();
    if (res.code === 200 && res.data) {
      const tree = [];
      for (const cat of res.data) {
        const node = {
          id: cat.id,
          name: cat.name,
          children: []
        };
        const secondRes = await getSecondLevelCategories(cat.id);
        if (secondRes.code === 200 && secondRes.data) {
          node.children = secondRes.data.map(sub => ({
            id: sub.id,
            name: sub.name
          }));
        }
        tree.push(node);
      }
      categoryTree.value = tree;
    }
  } catch (error) {
    console.error("加载分类失败:", error);
  }
};

const handleCategoryChange = (value) => {
  if (value && value.length > 0) {
    form.categoryId = value[value.length - 1];
    categoryPath.value = value;
  } else {
    form.categoryId = null;
    categoryPath.value = [];
  }
};

const loadBrands = async () => {
  try {
    const res = await getBrandList();
    if (res.code === 200 && res.data) {
      brandOptions.value = res.data.map(item => ({ id: item.id, name: item.name }));
    }
  } catch (error) {
    console.error("加载品牌失败:", error);
  }
};

const searchBrands = async (query) => {
  if (!query) {
    await loadBrands();
    return;
  }
  brandLoading.value = true;
  try {
    await loadBrands();
    brandOptions.value = brandOptions.value.filter(item => 
      item.name.toLowerCase().includes(query.toLowerCase())
    );
  } catch (error) {
    console.error("搜索品牌失败:", error);
  } finally {
    brandLoading.value = false;
  }
};

const loadSpecTypes = async () => {
  try {
    const res = await getSpecTypeTree();
    if (res.code === 200 && res.data) {
      specTree.value = res.data.map(type => ({
        id: type.id,
        name: type.name,
        values: type.values ? type.values.map(v => ({
          id: v.id,
          name: v.value
        })) : []
      }));
    }
  } catch (error) {
    console.error("加载规格类型失败:", error);
  }
};

const openSpecDialog = async () => {
  specForm.specPath = [];
  await loadSpecTypes();
  specDialogVisible.value = true;
};

const addSpec = () => {
  if (!specForm.specPath || specForm.specPath.length < 2) {
    ElMessage.warning("请选择规格类型和规格值");
    return;
  }
  
  const typeId = specForm.specPath[0];
  const valueId = specForm.specPath[1];
  
  const type = specTree.value.find(t => t.id === typeId);
  const value = type?.values?.find(v => v.id === valueId);
  
  if (type && value) {
    selectedSpecs.value.push({
      typeId: typeId,
      typeName: type.name,
      valueId: valueId,
      value: value.name
    });
    // 生成规格组合的笛卡尔积
    generateSkus();
    specDialogVisible.value = false;
    ElMessage.success("规格添加成功");
  }
};

// 生成规格组合的笛卡尔积
const generateSkus = () => {
  if (selectedSpecs.value.length === 0) {
    form.skus = [];
    return;
  }
  
  // 按规格类型分组
  const specGroups = {};
  for (const spec of selectedSpecs.value) {
    if (!specGroups[spec.typeId]) {
      specGroups[spec.typeId] = [];
    }
    specGroups[spec.typeId].push({
      typeId: spec.typeId,
      typeName: spec.typeName,
      valueId: spec.valueId,
      value: spec.value
    });
  }
  
  // 生成笛卡尔积
  const typeIds = Object.keys(specGroups);
  const combinations = cartesianProduct(typeIds.map(id => specGroups[id]));
  
  // 生成SKU列表
  form.skus = combinations.map((comb, idx) => {
    const specsObj = {};
    const specsArr = [];
    for (const item of comb) {
      specsObj[item.typeId] = item.valueId;
      specsArr.push(`${item.typeName}:${item.value}`);
    }
    return {
      skuCode: `SKU${Date.now()}${idx}`,
      specs: specsArr.join(","),
      specsJson: JSON.stringify(specsObj),
      price: form.price,
      stock: form.stock
    };
  });
};

// 笛卡尔积计算函数
const cartesianProduct = (arrays) => {
  return arrays.reduce((a, b) => {
    const result = [];
    for (const x of a) {
      for (const y of b) {
        result.push([...(Array.isArray(x) ? x : [x]), y]);
      }
    }
    return result.length > 0 ? result : arrays;
  });
};

const removeSpec = (index) => {
  selectedSpecs.value.splice(index, 1);
  // 重新生成SKU
  generateSkus();
};

const submitForm = async () => {
  if (!formRef.value) return;

  // 表单校验
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    ElMessage.warning("请完善商品信息后再提交");
    return;
  }

  // 封面图校验
  if (!form.coverImg) {
    ElMessage.warning("请上传商品封面图");
    return;
  }

  try {
    const res = await createProduct(form);
    if (res && (res.code === 200 || res.success)) {
      ElMessage.success("添加成功");
      router.push("/merchant/products");
    } else {
      ElMessage.error(res?.message || "添加失败");
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || "添加失败");
  }
};

const goBack = () => {
  router.push("/merchant/products");
};

const beforeUpload = (file) => {
  const isImage =
    file.type === "image/jpeg" ||
    file.type === "image/png" ||
    file.type === "image/jpg";
  if (!isImage) {
    ElMessage.error("只能上传 JPG/PNG 格式的图片");
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB");
    return false;
  }
  return true;
};

const customUpload = async (options) => {
  const { file, onSuccess, onError } = options;
  
  const formData = new FormData();
  formData.append("file", file);
  
  try {
    const response = await axios.post("/api/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    
    if (response.data && response.data.code === 200 && response.data.data) {
      const url = response.data.data;
      const fullUrl = getFullImageUrl(url);
      form.coverImg = url;
      coverImages.value = [{
        uid: String(Date.now()),
        name: file.name,
        url: url,
        status: 'success'
      }];
      coverPreviewList.value = [fullUrl];
      onSuccess(response.data, file);
      ElMessage.success("封面图上传成功");
    } else {
      onError(new Error(response.data?.message || "上传失败"));
      ElMessage.error(response.data?.message || "封面图上传失败");
    }
  } catch (error) {
    onError(error);
    ElMessage.error("封面图上传失败，请稍后重试");
  }
};

const handleCoverUploadSuccess = (response, file, fileList) => {
  const url = response.data || response.url || "";
  if (url) {
    form.coverImg = url;
    coverImages.value = fileList;
    coverPreviewList.value = [getFullImageUrl(url)];
  }
};

const handleMainUploadSuccess = (response, file, fileList) => {
  mainImages.value = fileList;
  const urls = fileList.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.mainImages = urls.filter((u) => u);
  mainPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
  ElMessage.success("主图上传成功");
};

const handleDetailUploadSuccess = (response, file, fileList) => {
  detailImages.value = fileList;
  const urls = fileList.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.detailImages = urls.filter((u) => u);
  detailPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
  ElMessage.success("详情图上传成功");
};

const handleCoverRemove = () => {
  form.coverImg = "";
  coverImages.value = [];
  coverPreviewList.value = [];
};

const handleMainRemove = (file, fileList) => {
  mainImages.value = fileList;
  const urls = fileList.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.mainImages = urls.filter((u) => u);
  mainPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
};

const handleDetailRemove = (file, fileList) => {
  detailImages.value = fileList;
  const urls = fileList.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.detailImages = urls.filter((u) => u);
  detailPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
};

const handleMainImageRemove = (uid) => {
  mainImages.value = mainImages.value.filter(img => img.uid !== uid);
  const urls = mainImages.value.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.mainImages = urls.filter((u) => u);
  mainPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
  ElMessage.success("删除成功");
};

const handleDetailImageRemove = (uid) => {
  detailImages.value = detailImages.value.filter(img => img.uid !== uid);
  const urls = detailImages.value.map(
    (item) => item.response?.data || item.response?.url || item.url || "",
  );
  form.detailImages = urls.filter((u) => u);
  detailPreviewList.value = urls.filter((u) => u).map(url => getFullImageUrl(url));
  ElMessage.success("删除成功");
};

const handleUploadError = () => {
  ElMessage.error("图片上传失败，请稍后重试");
};

// 当基础价格变化时，更新SKU的默认价格
watch(
  () => form.price,
  (newVal) => {
    if (form.skus && form.skus.length > 0 && newVal > 0) {
      form.skus.forEach(sku => {
        if (!sku.price || sku.price <= 0) {
          sku.price = newVal;
        }
      });
    }
  },
);

// 当基础库存变化时，更新SKU的默认库存
watch(
  () => form.stock,
  (newVal) => {
    if (form.skus && form.skus.length > 0 && newVal >= 0) {
      form.skus.forEach(sku => {
        if (!sku.stock || sku.stock < 0) {
          sku.stock = newVal;
        }
      });
    }
  },
);

onMounted(async () => {
  await loadCategories();
  loadBrands();
  loadSpecTypes();
});
</script>

<style scoped>
.product-form {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.product-form :deep(.el-form) {
  width: 100%;
}

.product-form :deep(.el-form-item__content) {
  max-width: 500px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.upload-demo {
  display: inline-block;
}

.product-form :deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

.product-form :deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.product-form :deep(.el-upload-list__item) {
  width: 120px;
  height: 120px;
}

.image-upload-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: flex-start;
}

.image-preview-item {
  position: relative;
  width: 120px;
  height: 120px;
  cursor: pointer;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
  z-index: 1;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0.3s ease;
  z-index: 10;
}

.image-preview-item:hover .preview-overlay {
  opacity: 1;
  visibility: visible;
}

.overlay-icons {
  display: flex;
  gap: 30px;
}

.preview-icon,
.delete-icon {
  font-size: 32px;
  color: #ffffff;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.preview-icon:hover,
.delete-icon:hover {
  transform: scale(1.2);
}

.spec-section {
  margin-top: 10px;
}

.spec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.empty-tip {
  padding: 30px;
  text-align: center;
  color: #999;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
}

.product-form :deep(.el-table) {
  margin-top: 10px;
}

.sku-preview {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
}

.sku-preview-header {
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}
</style>