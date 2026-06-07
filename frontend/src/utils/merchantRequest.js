import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.PROD ? '/api/merchant' : 'http://localhost:8080/api/merchant',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('merchantToken')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('merchantToken')
      localStorage.removeItem('merchantId')
      localStorage.removeItem('merchantName')
      window.location.href = '/merchant-login'
    }
    return res
  },
  error => {
    console.error('请求失败:', error)
    return Promise.reject(error)
  }
)

export default request
