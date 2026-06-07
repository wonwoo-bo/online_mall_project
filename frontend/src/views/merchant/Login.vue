<template>
  <div class="merchant-login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>商家后台登录</h2>
        <p>欢迎使用商城商家管理系统</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <el-link type="primary" @click="goToHome">返回首页</el-link>
        <span class="divider">|</span>
        <el-link type="primary" @click="goToRegister">商家入驻</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/merchant'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const res = await login(loginForm)
    if (res.code === 200) {
      const token = res.data?.token || res.token
      const merchant = res.data?.merchant || res.data
      localStorage.setItem('merchantToken', token)
      localStorage.setItem('merchantId', merchant?.id?.toString() || '')
      localStorage.setItem('merchantName', merchant?.shopName || merchant?.username || '')
      localStorage.setItem('merchantInfo', JSON.stringify(merchant || {}))
      ElMessage.success('登录成功')
      setTimeout(() => {
        router.push('/merchant/dashboard').catch(() => {
          window.location.href = '/merchant/dashboard'
        })
      }, 300)
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goToHome = () => {
  router.push('/')
}

const goToRegister = () => {
  router.push('/merchant-register')
}
</script>

<style scoped>
.merchant-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.login-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.divider {
  margin: 0 10px;
  color: #ddd;
}
</style>
