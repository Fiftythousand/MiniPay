<template>
  <div class="container">
    <div class="card" v-if="order">
      <h2>订单确认</h2>
      
      <div class="order-info">
        <div class="info-row">
          <span class="label">订单号</span>
          <span class="value">{{ order.orderId }}</span>
        </div>
        <div class="info-row">
          <span class="label">金额</span>
          <span class="value amount">¥{{ order.amount ? order.amount.toFixed(2) : '0.00' }}</span>
        </div>
        <div class="info-row">
          <span class="label">状态</span>
          <span :class="['value', 'status', order.status ? order.status.toLowerCase() : '']">{{ statusText }}</span>
        </div>
        <div class="info-row" v-if="order.description">
          <span class="label">描述</span>
          <span class="value">{{ order.description }}</span>
        </div>
        <div class="info-row">
          <span class="label">创建时间</span>
          <span class="value">{{ formatTime(order.createdAt) }}</span>
        </div>
      </div>

      <div v-if="showPayButton" class="pay-section">
        <button @click="handlePay" :disabled="paying.value" class="pay-btn">
          <span v-if="paying.value" class="loading"></span>
          {{ paying.value ? '支付中...' : '发起支付' }}
        </button>
      </div>

      <div v-if="paymentModal.value" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>模拟支付</h3>
            <button @click="closeModal" class="close-btn">×</button>
          </div>
          <div class="modal-body">
            <div class="payment-info">
              <p>订单号：{{ order.orderId }}</p>
              <p>支付金额：¥{{ order.amount ? order.amount.toFixed(2) : '0.00' }}</p>
            </div>
            <div class="payment-form">
              <div class="form-group">
                <label for="cardNo">卡号</label>
                <input id="cardNo" v-model="paymentForm.cardNo" type="text" placeholder="请输入银行卡号" />
              </div>
              <div class="form-group">
                <label for="expiry">有效期</label>
                <input id="expiry" v-model="paymentForm.expiry" type="text" placeholder="MM/YY" />
              </div>
              <div class="form-group">
                <label for="cvv">CVV</label>
                <input id="cvv" v-model="paymentForm.cvv" type="text" placeholder="安全码" />
              </div>
              <div class="form-group">
                <label for="password">支付密码</label>
                <input id="password" v-model="paymentForm.password" type="password" placeholder="请输入支付密码" />
              </div>
            </div>
            <button @click="confirmPayment" :disabled="processing.value" class="confirm-btn">
              <span v-if="processing.value" class="loading"></span>
              {{ processing.value ? '处理中...' : '确认支付' }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="paymentResult.value" class="result-section">
        <div :class="['result-card', paymentResult.value.status ? paymentResult.value.status.toLowerCase() : '']">
          <div class="result-icon">
            <span v-if="paymentResult.value.status === 'SUCCESS'">✓</span>
            <span v-else>✗</span>
          </div>
          <h3>{{ paymentResult.value.status === 'SUCCESS' ? '支付成功' : '支付失败' }}</h3>
          <p v-if="paymentResult.value.status === 'SUCCESS'">
            支付金额：¥{{ paymentResult.value.amount?.toFixed(2) || (order.amount ? order.amount.toFixed(2) : '0.00') }}
          </p>
          <p v-if="paymentResult.value.paymentId">支付流水号：{{ paymentResult.value.paymentId }}</p>
          <p v-if="paymentResult.value.message">{{ paymentResult.value.message }}</p>
          <div class="result-actions">
            <button @click="goBack" class="back-btn">返回首页</button>
            <button @click="queryStatus" class="query-btn">查看详情</button>
          </div>
        </div>
      </div>

      <div v-if="errorMessage.value" class="error-message">{{ errorMessage.value }}</div>
    </div>

    <div v-else-if="loading.value" class="loading-container">
      <div class="loading"></div>
      <p>加载订单信息...</p>
    </div>

    <div v-else class="empty-state">
      <p>订单不存在或已过期</p>
      <button @click="goBack" class="back-btn">返回首页</button>
    </div>
  </div>
</template>

<script>
import { reactive, computed, onMounted } from 'vue'
import { getOrder, createPayment } from '../api'

export default {
  name: 'PayOrder',
  setup() {
    const order = reactive({})
    const loading = reactive({ value: true })
    const paying = reactive({ value: false })
    const processing = reactive({ value: false })
    const paymentModal = reactive({ value: false })
    const paymentResult = reactive({ value: null })
    const errorMessage = reactive({ value: '' })
    
    const paymentForm = reactive({
      cardNo: '',
      expiry: '',
      cvv: '',
      password: ''
    })

    const statusText = computed(() => {
      const map = {
        'PENDING': '待支付',
        'PAID': '已支付',
        'FAILED': '支付失败'
      }
      return map[order.status] || order.status
    })

    const showPayButton = computed(() => {
      if (!order.status) return false
      if (order.status !== 'PENDING') return false
      if (paymentResult.value) return false
      return true
    })

    const formatTime = (timestamp) => {
      if (!timestamp) return '-'
      return new Date(timestamp).toLocaleString('zh-CN')
    }

    const fetchOrder = async () => {
      const orderId = window.location.pathname.split('/').pop()
      if (!orderId) {
        loading.value = false
        return
      }

      try {
        const response = await getOrder(orderId)
        if (response.data && response.data.code === 200) {
          Object.assign(order, response.data.data)
        } else {
          errorMessage.value = response.data?.message || '订单查询失败'
        }
      } catch (err) {
        errorMessage.value = err.response?.data?.message || '网络错误'
      } finally {
        loading.value = false
      }
    }

    const handlePay = () => {
      paymentModal.value = true
    }

    const closeModal = () => {
      paymentModal.value = false
    }

    const confirmPayment = async () => {
      processing.value = true
      
      try {
        const response = await createPayment({
          orderId: order.orderId,
          amount: order.amount
        })
        
        if (response.data && response.data.code === 200) {
          const data = response.data.data
          paymentResult.value = {
            status: data.status || 'SUCCESS',
            amount: data.amount,
            paymentId: data.paymentId,
            message: response.data.message
          }
        } else {
          paymentResult.value = {
            status: 'FAILED',
            amount: order.amount,
            paymentId: null,
            message: response.data?.message || '支付失败'
          }
        }
      } catch (err) {
        paymentResult.value = {
          status: 'FAILED',
          amount: order.amount,
          paymentId: null,
          message: err.response?.data?.message || '支付请求失败'
        }
      } finally {
        processing.value = false
        closeModal()
      }
    }

    const goBack = () => {
      window.location.href = '/create'
    }

    const queryStatus = () => {
      window.location.href = `/query?orderId=${order.orderId}`
    }

    onMounted(() => {
      fetchOrder()
    })

    return {
      order,
      loading,
      paying,
      processing,
      paymentModal,
      paymentResult,
      errorMessage,
      paymentForm,
      statusText,
      showPayButton,
      formatTime,
      handlePay,
      closeModal,
      confirmPayment,
      goBack,
      queryStatus
    }
  }
}
</script>

<style scoped>
.container {
  padding: 2rem;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

.order-info {
  margin-bottom: 1.5rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #333;
  font-weight: 600;
}

.value.amount {
  color: #e74c3c;
  font-size: 1.25rem;
}

.status.pending {
  color: #f39c12;
}

.status.paid {
  color: #27ae60;
}

.status.failed {
  color: #e74c3c;
}

.pay-section {
  margin-top: 1rem;
}

.pay-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.pay-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4);
}

.pay-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 0 0.5rem;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem;
}

.payment-info {
  background: #f9f9f9;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.payment-info p {
  margin: 0.5rem 0;
  color: #555;
}

.payment-form {
  margin-bottom: 1rem;
}

.payment-form .form-group {
  margin-bottom: 1rem;
}

.payment-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.payment-form input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
}

.payment-form input:focus {
  outline: none;
  border-color: #667eea;
}

.confirm-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.confirm-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.confirm-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.result-section {
  margin-top: 1.5rem;
}

.result-card {
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
}

.result-card.success {
  background: #d4edda;
  border: 1px solid #c3e6cb;
}

.result-card.failed {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
}

.result-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  font-size: 2rem;
}

.result-card.success .result-icon {
  background: #27ae60;
  color: white;
}

.result-card.failed .result-icon {
  background: #e74c3c;
  color: white;
}

.result-card h3 {
  margin-bottom: 1rem;
  color: #333;
}

.result-card p {
  margin: 0.5rem 0;
  color: #555;
}

.result-actions {
  margin-top: 1.5rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.back-btn,
.query-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.back-btn {
  background: #6c757d;
  color: white;
}

.back-btn:hover {
  background: #5a6268;
}

.query-btn {
  background: #667eea;
  color: white;
}

.query-btn:hover {
  background: #5a67d8;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 8px;
  color: #721c24;
}

.loading-container {
  text-align: center;
  padding: 3rem;
}

.loading-container .loading {
  width: 40px;
  height: 40px;
  border: 3px solid #667eea;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 12px;
}

.empty-state p {
  margin-bottom: 1rem;
  color: #666;
}
</style>