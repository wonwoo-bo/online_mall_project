<template>
  <div class="merchant-register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>商家入驻</h2>
        <p>加入我们，开启您的电商之旅</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="设置用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="设置密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="shopName">
          <el-input
            v-model="registerForm.shopName"
            placeholder="店铺名称"
            :prefix-icon="Setting"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="contactPhone">
          <el-input
            v-model="registerForm.contactPhone"
            placeholder="联系电话"
            :prefix-icon="Phone"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="shopDesc">
          <el-input
            v-model="registerForm.shopDesc"
            type="textarea"
            :rows="3"
            placeholder="店铺简介（可选）"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            立即入驻
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <el-link type="primary" @click="goToLogin">已有账号？去登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { register } from '@/api/merchant';
import { User, Lock, Setting, Phone } from '@element-plus/icons-vue';

const router = useRouter();
const registerFormRef = ref(null);
const loading = ref(false);

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  shopName: '',
  contactPhone: '',
  shopDesc: ''
});

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  shopName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      await register(registerForm);
      ElMessage.success('注册成功！请等待管理员审核');
      router.push('/merchant-login');
    } catch (error) {
      ElMessage.error(error.message || '注册失败，请稍后重试');
    } finally {
      loading.value = false;
    }
  });
};

const goToLogin = () => {
  router.push('/merchant-login');
};
</script>

<style scoped>
.merchant-register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 450px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.register-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.register-form {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
}

.register-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
