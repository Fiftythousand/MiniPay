import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

api.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const createOrder = (data) => api.post('/orders', data)
export const getOrder = (orderId) => api.get(`/orders/${orderId}`)
export const updateOrderStatus = (orderId, status) => api.put(`/orders/${orderId}/status`, { status })
export const createPayment = (data) => api.post('/payments', data)
export const getPayment = (orderId) => api.get(`/payments/${orderId}`)