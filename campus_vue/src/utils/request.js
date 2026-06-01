// axios 请求封装模块，统一处理请求拦截、响应拦截、错误处理
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建 axios 实例，baseURL为后端API地址，timeout为请求超时时间（15秒）
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000
})

// 请求拦截器：在发送请求前自动添加 Authorization token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理响应错误（401登录过期、403权限不足、500服务器异常）
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        localStorage.clear()
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else if (status === 500) {
        ElMessage.error('服务器异常')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
