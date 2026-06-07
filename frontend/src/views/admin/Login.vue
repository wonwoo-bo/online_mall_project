<template>
  <div class="login-container">
    <div class="login-box">
      <h2>管理员登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <el-divider content-position="center">其他入口</el-divider>
          <div class="other-entries">
            <router-link to="/" class="entry-link">
              <el-icon><HomeFilled /></el-icon>
              返回首页
            </router-link>
            <router-link to="/merchant-login" class="entry-link merchant">
              <el-icon><Setting /></el-icon>
              商家入口
            </router-link>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, HomeFilled, Setting } from '@element-plus/icons-vue'

import { adminLogin } from '@/api'
import { setAdminToken, setAdminId, setAdminUsername } from '../../utils/auth.js'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const res = await adminLogin(form)
    if (res.code === 200) {
      setAdminToken(res.data.token)
      setAdminId(res.data.adminId)
      setAdminUsername(res.data.username)
      ElMessage.success('登录成功')
      router.push('/admin')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-footer {
  margin-top: 20px;
}

.other-entries {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.entry-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.entry-link:hover {
  color: #409eff;
}

.entry-link.merchant {
  color: #409eff;
  font-weight: 500;
}

.entry-link.merchant:hover {
  color: #66b1ff;
}
</style>
