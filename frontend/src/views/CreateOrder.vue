<template>
  <div class="container">
    <div class="card">
      <h2>创建订单</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="amount">订单金额（元）</label>
          <input
            id="amount"
            v-model.number="form.amount"
            type="number"
            step="0.01"
            min="0.01"
            placeholder="请输入订单金额"
            :disabled="loading"
          />
          <span v-if="errors.amount" class="error">{{ errors.amount }}</span>
        </div>
        <div class="form-group">
          <label for="memberId">商户ID</label>
          <input
            id="memberId"
            v-model.number="form.memberId"
            type="number"
            min="1"
            placeholder="请输入商户ID"
            :disabled="loading"
          />
          <span v-if="errors.memberId" class="error">{{ errors.memberId }}</span>
        </div>
        <div class="form-group">
          <label for="description">订单描述</label>
          <input
            id="description"
            v-model="form.description"
            type="text"
            placeholder="请输入订单描述（可选）"
            :disabled="loading"
          />
        </div>
        <button type="submit" :disabled="loading" class="submit-btn">
          <span v-if="loading" class="loading"></span>
          {{ loading ? '创建中...' : '创建订单' }}
        </button>
      </form>
      <div v-if="successMessage" class="success-message">
        <p>{{ successMessage }}</p>
        <button @click="goToPay" class="pay-btn">立即支付</button>
      </div>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </div>
  </div>
</template>

<script>
import { reactive, ref } from 'vue'
import { createOrder } from '../api'

export default {
  name: 'CreateOrder',
  setup() {
    const form = reactive({
      amount: '',
      memberId: '',
      description: ''
    })
    
    const errors = reactive({})
    const loading = ref(false)
    const successMessage = ref('')
    const errorMessage = ref('')
    const createdOrderId = ref('')

    const validate = () => {
      errors.amount = ''
      errors.memberId = ''
      
      if (!form.amount || form.amount <= 0) {
        errors.amount = '请输入有效的金额'
      }
      if (!form.memberId || form.memberId <= 0) {
        errors.memberId = '请输入有效的商户ID'
      }
      
      return !errors.amount && !errors.memberId
    }

    const handleSubmit = async () => {
      errorMessage.value = ''
      successMessage.value = ''
      
      if (!validate()) return

      loading.value = true
      
      try {
        const response = await createOrder({
          amount: form.amount,
          memberId: form.memberId,
          description: form.description
        })
        
        if (response.data && response.data.code === 200) {
          createdOrderId.value = response.data.data.orderId
          successMessage.value = `订单创建成功！订单号：${createdOrderId.value}`
          form.amount = ''
          form.memberId = ''
          form.description = ''
        } else {
          errorMessage.value = response.data?.message || '订单创建失败'
        }
      } catch (err) {
        errorMessage.value = err.response?.data?.message || '网络错误，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    const goToPay = () => {
      if (createdOrderId.value) {
        window.location.href = `/pay/${createdOrderId.value}`
      }
    }

    return {
      form,
      errors,
      loading,
      successMessage,
      errorMessage,
      handleSubmit,
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

.submit-btn {
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

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
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

.success-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 8px;
  color: #155724;
}

.success-message p {
  margin-bottom: 0.75rem;
}

.pay-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.pay-btn:hover {
  background: #218838;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 8px;
  color: #721c24;
}
</style>