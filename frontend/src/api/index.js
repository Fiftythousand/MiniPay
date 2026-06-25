import axios from 'axios'

// 创建一个 axios 实例，设置基础 URL 为 /api 。之后所有请求都会自动加上这个前缀，比如 api.post('/orders') 实际请求的是 /api/orders
const api = axios.create({
  baseURL: '/api'
})
// 注册一个请求拦截器，每次发送请求前会自动执行这个函数。 config 是请求配置对象。
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token
  }
  return config
})

//注册响应拦截器。第一个参数处理成功响应，直接返回原样。
// 第二个参数处理错误响应：
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/'
    }
    return Promise.reject(error)
  }
)

export const createOrder = (data) => api.post('/orders', data)
export const getOrder = (orderId) => api.get(`/orders/${orderId}`)
export const updateOrderStatus = (orderId, status) => api.put(`/orders/${orderId}/status`, { status })
export const createPayment = (data) => api.post('/payments', data)
export const getPayment = (orderId) => api.get(`/payments/${orderId}`)
// 获取所有历史订单列表
export const getOrderList = () => api.get('/orders')