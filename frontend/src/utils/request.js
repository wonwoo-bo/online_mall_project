import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.PROD ? '/api' : 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器 - 添加Token
request.interceptors.request.use(config => {
  let token = localStorage.getItem('token')
  if (!token) {
    token = localStorage.getItem('adminToken')
  }
  if (!token) {
    token = localStorage.getItem('merchantToken')
  }
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      // Token失效，清除登录状态并跳转登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return res
  },
  error => {
    console.error('请求失败:', error)
    // 把错误响应的数据传递下去，让组件处理
    if (error.response && error.response.data) {
      return Promise.reject(error)
    }
    return Promise.reject(error)
  }
)

export default request
