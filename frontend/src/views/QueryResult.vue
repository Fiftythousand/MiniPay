<template>
  <div class="container">
    <div class="card">
      <h2>结果查询</h2>
      <form @submit.prevent="handleQuery">
        <div class="form-group">
          <label for="orderId">订单号</label>
          <input
            id="orderId"
            v-model="orderId"
            type="text"
            placeholder="请输入订单号"
            :disabled="loading.value"
          />
          <span v-if="errors.orderId" class="error">{{ errors.orderId }}</span>
        </div>
        <button type="submit" :disabled="loading.value" class="query-btn">
          <span v-if="loading.value" class="loading"></span>
          {{ loading.value ? '查询中...' : '查询' }}
        </button>
      </form>

      <div v-if="orderData.value" class="result-section">
        <h3>订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">订单号</span>
            <span class="value">{{ orderData.value.orderId }}</span>
          </div>
          <div class="info-item">
            <span class="label">金额</span>
            <span class="value">¥{{ orderData.value.amount?.toFixed(2) || '0.00' }}</span>
          </div>
          <div class="info-item">
            <span class="label">商户ID</span>
            <span class="value">{{ orderData.value.memberId }}</span>
          </div>
          <div class="info-item">
            <span class="label">订单状态</span>
            <span :class="['value', 'status', orderData.value.status?.toLowerCase()]">
              {{ getOrderStatusText(orderData.value.status) }}
            </span>
          </div>
          <div class="info-item" v-if="orderData.value.description">
            <span class="label">描述</span>
            <span class="value">{{ orderData.value.description }}</span>
          </div>
          <div class="info-item">
            <span class="label">创建时间</span>
            <span class="value">{{ formatTime(orderData.value.createdAt) }}</span>
          </div>
        </div>
      </div>

      <div v-if="paymentData.value" class="result-section">
        <h3>支付信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">支付流水号</span>
            <span class="value">{{ paymentData.value.paymentId }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付状态</span>
            <span :class="['value', 'status', paymentData.value.status?.toLowerCase()]">
              {{ getPaymentStatusText(paymentData.value.status) }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">支付金额</span>
            <span class="value">¥{{ paymentData.value.amount?.toFixed(2) || '0.00' }}</span>
          </div>
          <div class="info-item" v-if="paymentData.value.paidAt">
            <span class="label">支付时间</span>
            <span class="value">{{ formatTime(paymentData.value.paidAt) }}</span>
          </div>
          <div class="info-item" v-if="paymentData.value.message">
            <span class="label">备注</span>
            <span class="value">{{ paymentData.value.message }}</span>
          </div>
        </div>
      </div>

      <div v-if="orderData.value && orderData.value.status === 'PENDING'" class="action-section">
        <button @click="goToPay" class="pay-btn">去支付</button>
      </div>

      <div v-if="errorMessage.value" class="error-message">{{ errorMessage.value }}</div>

      <div v-if="noResult.value" class="no-result">
        <p>未查询到相关订单</p>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted } from 'vue'
import { getOrder, getPayment } from '../api'

export default {
  name: 'QueryResult',
  setup() {
    const orderId = ref('')
    const loading = reactive({ value: false })
    const errors = reactive({})
    const orderData = reactive({ value: null })
    const paymentData = reactive({ value: null })
    const errorMessage = reactive({ value: '' })
    const noResult = reactive({ value: false })

    const getOrderStatusText = (status) => {
      const map = {
        'PENDING': '待支付',
        'PAID': '已支付',
        'FAILED': '支付失败'
      }
      return map[status] || status || '-'
    }

    const getPaymentStatusText = (status) => {
      const map = {
        'SUCCESS': '支付成功',
        'FAILED': '支付失败',
        'PENDING': '处理中'
      }
      return map[status] || status || '-'
    }

    const formatTime = (timestamp) => {
      if (!timestamp) return '-'
      return new Date(timestamp).toLocaleString('zh-CN')
    }

    const validate = () => {
      errors.orderId = ''
      
      if (!orderId.value.trim()) {
        errors.orderId = '请输入订单号'
      }
      
      return !errors.orderId
    }

    const handleQuery = async () => {
      errorMessage.value = ''
      orderData.value = null
      paymentData.value = null
      noResult.value = false
      
      if (!validate()) return

      loading.value = true
      
      try {
        const orderResponse = await getOrder(orderId.value.trim())
        
        if (orderResponse.data && orderResponse.data.code === 200) {
          orderData.value = orderResponse.data.data
          
          const paymentResponse = await getPayment(orderId.value.trim())
          if (paymentResponse.data && paymentResponse.data.code === 200) {
            paymentData.value = paymentResponse.data.data
          }
        } else {
          noResult.value = true
        }
      } catch (err) {
        if (err.response?.status === 404) {
          noResult.value = true
        } else {
          errorMessage.value = err.response?.data?.message || '查询失败，请稍后重试'
        }
      } finally {
        loading.value = false
      }
    }

    const goToPay = () => {
      if (orderData.value) {
        window.location.href = `/pay/${orderData.value.orderId}`
      }
    }

    const getQueryParam = (name) => {
      const urlParams = new URLSearchParams(window.location.search)
      return urlParams.get(name)
    }

    onMounted(() => {
      const paramOrderId = getQueryParam('orderId')
      if (paramOrderId) {
        orderId.value = paramOrderId
        handleQuery()
      }
    })

    return {
      orderId,
      loading,
      errors,
      orderData,
      paymentData,
      errorMessage,
      noResult,
      getOrderStatusText,
      getPaymentStatusText,
      formatTime,
      handleQuery,
      goToPay
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

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.query-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.query-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.query-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading {
  width: 20px;
  height: 20px;
  border: 2px solid #fff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.result-section {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.result-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  padding: 0.75rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.info-item .label {
  display: block;
  color: #666;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.info-item .value {
  display: block;
  color: #333;
  font-weight: 600;
}

.status.pending {
  color: #f39c12;
}

.status.paid,
.status.success {
  color: #27ae60;
}

.status.failed {
  color: #e74c3c;
}

.action-section {
  margin-top: 1.5rem;
  text-align: center;
}

.pay-btn {
  padding: 0.75rem 2rem;
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4);
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 8px;
  color: #721c24;
}

.no-result {
  margin-top: 2rem;
  padding: 2rem;
  text-align: center;
  background: #f9f9f9;
  border-radius: 8px;
}

.no-result p {
  color: #666;
  margin: 0;
}
</style>