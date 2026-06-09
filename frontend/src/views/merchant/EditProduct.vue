<template>
  <div class="product-form">
    <el-card>
      <template #header>
        <span>编辑商品</span>
      </template>

      <el-form ref="formRef" :model="form" label-width="120px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            type="textarea"
            v-model="form.description"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input v-model.number="form.price" placeholder="请输入商品价格" />
        </el-form-item>

        <el-form-item label="库存数量" prop="stock">
          <el-input v-model.number="form.stock" placeholder="请输入库存数量" />
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
          <el-button type="primary" @click="submitForm">保存</el-button>
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
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { ZoomIn, Delete, Plus } from "@element-plus/icons-vue";
import { 
  getProductDetail, 
  updateProduct,
  getFirstLevelCategories,
  getSecondLevelCategories,
  getBrandList,
  getSpecTypeTree
} from "@/api/merchant";
import axios from "axios";

const router = useRouter();
const route = useRoute();
const formRef = ref(null);
const specFormRef = ref(null);

const form = reactive({
  id: null,
  name: "",
  description: "",
  price: 0,
  stock: 0,
  categoryId: null,
  brandId: null,
  status: 1,
  coverImg: "",
  mainImages: [],
  detailImages: [],
  specs: []
});

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

const findCategoryPath = (tree, targetId) => {
  for (const node of tree) {
    if (node.id === targetId) {
      return [node.id];
    }
    if (node.children) {
      const path = findCategoryPath(node.children, targetId);
      if (path) {
        return [node.id, ...path];
      }
    }
  }
  return null;
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
    form.specs = selectedSpecs.value.map(s => ({
      typeId: s.typeId,
      valueId: s.valueId
    }));
    specDialogVisible.value = false;
    ElMessage.success("规格添加成功");
  }
};

const removeSpec = (index) => {
  selectedSpecs.value.splice(index, 1);
  form.specs = selectedSpecs.value.map(s => ({
    typeId: s.typeId,
    valueId: s.valueId
  }));
};

const loadProduct = async () => {
  const id = route.params.id;
  if (!id) return;

  try {
    const res = await getProductDetail(id);
    if (res && res.data) {
      const productData = res.data.product || res.data;
      if (productData) {
        form.id = productData.id || productData.productId || id;
        form.name = productData.name || "";
        form.description = productData.description || productData.desc || "";
        form.price = productData.price || 0;
        form.stock = productData.stock || 0;
        form.categoryId = productData.categoryId || productData.category_id || null;
        form.brandId = productData.brandId || productData.brand_id || null;
        form.status = productData.status !== undefined ? productData.status : 1;
        form.coverImg = productData.coverImg || productData.cover_image || "";
        
        if (form.categoryId) {
          const path = findCategoryPath(categoryTree.value, form.categoryId);
          if (path) {
            categoryPath.value = path;
          }
        }
      }

      if (res.data.images && Array.isArray(res.data.images)) {
        res.data.images.forEach((img, index) => {
          const url = img.imageUrl || img.url || "";
          if (url) {
            const isMain = img.isMain === 1 || (img.is_main === 1) || index === 0;
            const fileName = url.substring(url.lastIndexOf('/') + 1);
            const fileObj = {
              uid: String(Date.now() + index),
              name: fileName,
              url: url,
              status: 'success'
            };
            
            const fullUrl = getFullImageUrl(url);
            if (!form.coverImg || isMain) {
              coverImages.value = [fileObj];
              coverPreviewList.value = [fullUrl];
              form.coverImg = url;
            } else {
              mainImages.value.push(fileObj);
              mainPreviewList.value.push(fullUrl);
              form.mainImages.push(url);
            }
          }
        });
      }
    }
  } catch (error) {
    console.error("加载商品信息失败:", error);
    ElMessage.error("加载商品信息失败");
  }
};

const submitForm = async () => {
  if (!formRef.value) return;

  try {
    const res = await updateProduct(form.id, form);
    if (res && (res.code === 200 || res.success)) {
      ElMessage.success("修改成功");
      router.push("/merchant/products");
    } else {
      ElMessage.error(res?.message || "修改失败");
    }
  } catch (error) {
    ElMessage.error("修改失败");
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

watch(
  () => form.price,
  (newVal) => {
    if (newVal < 0) {
      form.price = 0;
      ElMessage.warning("商品价格不能为负数");
    }
  },
);

watch(
  () => form.stock,
  (newVal) => {
    if (newVal < 0) {
      form.stock = 0;
      ElMessage.warning("库存数量不能为负数");
    }
  },
);

onMounted(async () => {
  await loadCategories();
  loadProduct();
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
</style>