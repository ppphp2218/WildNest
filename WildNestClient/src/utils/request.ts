import axios from 'axios'
import { showToast } from 'vant'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response
    
    // 统一处理响应数据
    if (data.code === 200) {
      return data.data
    } else {
      showToast(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    // 处理HTTP错误
    if (error.response) {
      const { status } = error.response
      switch (status) {
        case 401:
          showToast('未授权，请重新登录')
          // 可以在这里处理登录跳转
          break
        case 403:
          showToast('权限不足')
          break
        case 404:
          showToast('请求的资源不存在')
          break
        case 500:
          showToast('服务器内部错误')
          break
        default:
          showToast('网络错误')
      }
    } else {
      showToast('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request