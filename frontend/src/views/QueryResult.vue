<template>
  <div class="container">
    <div class="card">
      <h2>订单与支付中心</h2>

      <!-- 1. 单笔订单查询区域 -->
      <form @submit.prevent="handleQuery">
        <div class="form-group">
          <label for="orderId">单笔订单查询</label>
          <input
            id="orderId"
            v-model="orderId"
            type="text"
            placeholder="请输入订单号"
            :disabled="loading"
          />
          <span v-if="errors.orderId" class="error">{{ errors.orderId }}</span>
        </div>
        <button type="submit" :disabled="loading" class="query-btn">
          <span v-if="loading" class="loading"></span>
          {{ loading ? '查询中...' : '查询订单' }}
        </button>
      </form>

      <!-- 2. 订单详情展示（查询到单笔订单时显示） -->
      <div v-if="orderData" class="result-section">
        <h3>订单详情</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">订单号</span>
            <span class="value">{{ orderData.orderId }}</span>
          </div>
          <div class="info-item">
            <span class="label">金额</span>
            <span class="value">¥{{ orderData.amount?.toFixed(2) || '0.00' }}</span>
          </div>
          <div class="info-item">
            <span class="label">商户ID</span>
            <span class="value">{{ orderData.memberId }}</span>
          </div>
          <div class="info-item">
            <span class="label">订单状态</span>
            <span :class="['value', 'status', orderData.status?.toLowerCase()]">
              {{ getOrderStatusText(orderData.status) }}
            </span>
          </div>
          <div class="info-item" v-if="orderData.description">
            <span class="label">描述</span>
            <span class="value">{{ orderData.description }}</span>
          </div>
          <div class="info-item">
            <span class="label">创建时间</span>
            <span class="value">{{ formatTime(orderData.createdAt) }}</span>
          </div>
        </div>

        <!-- 支付信息 -->
        <div v-if="paymentData" class="sub-section">
          <h4>支付信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">支付流水号</span>
              <span class="value">{{ paymentData.paymentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付状态</span>
              <span :class="['value', 'status', paymentData.status?.toLowerCase()]">
                {{ getPaymentStatusText(paymentData.status) }}
              </span>
            </div>
            <div class="info-item">
              <span class="label">支付金额</span>
              <span class="value">¥{{ paymentData.amount?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="info-item" v-if="paymentData.paidAt">
              <span class="label">支付时间</span>
              <span class="value">{{ formatTime(paymentData.paidAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 待支付：显示去支付按钮 -->
        <div v-if="orderData.status === 'PENDING'" class="action-section">
          <button @click="goToPay" class="pay-btn">去支付</button>
        </div>

        <!-- 已支付：显示成功提示 -->
        <div v-if="orderData.status === 'PAID'" class="success-tip">
          🎉 该订单已支付成功！
        </div>
      </div>

      <!-- 查询错误与空状态 -->
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="noResult" class="no-result"><p>未查询到相关订单</p></div>

      <!-- 3. 历史订单列表区域 -->
      <div class="history-section">
        <div class="history-header">
          <h3>历史订单记录</h3>
          <button @click="fetchHistory" class="refresh-btn" :disabled="listLoading">
            {{ listLoading ? '刷新中...' : '刷新列表' }}
          </button>
        </div>

        <div v-if="historyList.length === 0 && !listLoading" class="no-result">
          <p>暂无历史订单记录</p>
        </div>

        <div v-else class="table-wrapper">
          <table class="history-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>金额</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in historyList" :key="item.orderId">
                <td>{{ item.orderId }}</td>
                <td>¥{{ item.amount?.toFixed(2) || '0.00' }}</td>
                <td>
                  <span :class="['status-tag', item.status?.toLowerCase()]">
                    {{ getOrderStatusText(item.status) }}
                  </span>
                </td>
                <td>{{ formatTime(item.createdAt) }}</td>
                <td>
                  <button class="detail-btn" @click="viewDetail(item.orderId)">查看详情</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrder, getPayment, getOrderList } from '../api'

export default {
  name: 'QueryResult',
  setup() {
    const router = useRouter()

    // 单笔查询相关状态
    const orderId = ref('')
    const loading = ref(false)
    const errors = reactive({})
    const orderData = ref(null)
    const paymentData = ref(null)
    const errorMessage = ref('')
    const noResult = ref(false)
    let pollingTimer = null

    // 历史列表相关状态
    const historyList = ref([])
    const listLoading = ref(false)

    // --- 辅助函数 ---
    const getOrderStatusText = (status) => {
      const map = { 'PENDING': '待支付', 'PAID': '已支付', 'FAILED': '支付失败' }
      return map[status] || status || '-'
    }

    const getPaymentStatusText = (status) => {
      const map = { 'SUCCESS': '支付成功', 'FAILED': '支付失败', 'PENDING': '处理中' }
      return map[status] || status || '-'
    }

    const formatTime = (timestamp) => {
      if (!timestamp) return '-'
      return new Date(timestamp).toLocaleString('zh-CN')
    }

    // --- 历史列表逻辑 ---
    const fetchHistory = async () => {
      listLoading.value = true
      try {
        const res = await getOrderList()
        // 兼容 { code: 200, data: [] } 或直接返回数组
        historyList.value = res.data.data || res.data || []
      } catch (err) {
        console.error('获取历史订单失败:', err)
      } finally {
        listLoading.value = false
      }
    }

    // 点击表格中的“查看详情”
    const viewDetail = (id) => {
      orderId.value = id
      handleQuery()
      // 滚动到顶部查看结果
      window.scrollTo(0, 0)
    }

    // --- 单笔查询逻辑 ---
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
      stopPolling()

      if (!validate()) return

      loading.value = true

      try {
        const orderResponse = await getOrder(orderId.value.trim())

        if (orderResponse.data && orderResponse.data.code === 200) {
          orderData.value = orderResponse.data.data

          try {
            const paymentResponse = await getPayment(orderId.value.trim())
            if (paymentResponse.data && paymentResponse.data.code === 200) {
              paymentData.value = paymentResponse.data.data
            }
          } catch (e) { /* 暂无支付记录 */ }

          if (orderData.value.status === 'PENDING') {
            // startPolling() // 已关闭轮询
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

    // --- 轮询机制 ---
    const startPolling = () => { /* 已关闭轮询 */ return;
      if (pollingTimer) clearInterval(pollingTimer)
      pollingTimer = setInterval(async () => {
        try {
          const res = await getOrder(orderId.value.trim())
          if (res.data && res.data.code === 200) {
            orderData.value = res.data.data
            if (orderData.value.status !== 'PENDING') {
              stopPolling()
              // 状态变更后刷新历史列表和支付信息
              fetchHistory()
              try {
                const payRes = await getPayment(orderId.value.trim())
                if (payRes.data && payRes.data.code === 200) paymentData.value = payRes.data.data
              } catch (e) {}
            }
          }
        } catch (e) {}
      }, 3000)
    }

    const stopPolling = () => {
      if (pollingTimer) { clearInterval(pollingTimer); pollingTimer = null }
    }

    const goToPay = () => {
      if (orderData.value) router.push(`/pay/${orderData.value.orderId}`)
    }

    onMounted(() => {
      // 1. 加载历史列表
      fetchHistory()
      // 2. 处理 URL 参数带入的订单号
      const urlParams = new URLSearchParams(window.location.search)
      const paramOrderId = urlParams.get('orderId')
      if (paramOrderId) {
        orderId.value = paramOrderId
        handleQuery()
      }
    })

    onUnmounted(() => stopPolling())

    return {
      orderId, loading, errors, orderData, paymentData,
      errorMessage, noResult, historyList, listLoading,
      getOrderStatusText, getPaymentStatusText, formatTime,
      handleQuery, goToPay, fetchHistory, viewDetail
    }
  }
}
</script>

<style scoped>
.container { padding: 2rem; }
.card { background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
h2 { color: #333; margin-bottom: 1.5rem; font-size: 1.5rem; border-bottom: 2px solid #667eea; padding-bottom: 0.5rem; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-weight: 500; color: #555; }
.form-group input { width: 100%; padding: 0.75rem; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 1rem; transition: border-color 0.3s; }
.form-group input:focus { outline: none; border-color: #667eea; }
.form-group input:disabled { background: #f5f5f5; cursor: not-allowed; }
.error { display: block; color: #e74c3c; font-size: 0.875rem; margin-top: 0.25rem; }

.query-btn { width: 100%; padding: 1rem; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; font-size: 1rem; font-weight: 600; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 0.5rem; }
.query-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4); }
.query-btn:disabled { opacity: 0.7; cursor: not-allowed; }
.loading { width: 20px; height: 20px; border: 2px solid #fff; border-top-color: transparent; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.result-section { margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid #eee; }
.result-section h3 { color: #333; margin-bottom: 1rem; font-size: 1.2rem; }
.sub-section { margin-top: 1rem; padding: 1rem; background: #f9f9f9; border-radius: 8px; }
.sub-section h4 { margin-bottom: 0.5rem; color: #555; font-size: 1rem; }

.info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; }
.info-item { padding: 0.75rem; background: #f9f9f9; border-radius: 8px; }
.info-item .label { display: block; color: #666; font-size: 0.875rem; margin-bottom: 0.25rem; }
.info-item .value { display: block; color: #333; font-weight: 600; }

.status.pending { color: #f39c12; }
.status.paid, .status.success { color: #27ae60; }
.status.failed { color: #e74c3c; }

.action-section { margin-top: 1.5rem; text-align: center; }
.pay-btn { padding: 0.75rem 2rem; background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; border: none; border-radius: 8px; font-size: 1rem; font-weight: 600; cursor: pointer; transition: transform 0.2s; }
.pay-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4); }

.success-tip { margin-top: 1.5rem; padding: 1rem; text-align: center; background: #d4edda; border: 1px solid #c3e6cb; border-radius: 8px; color: #155724; font-weight: 600; font-size: 1.1rem; }
.error-message { margin-top: 1rem; padding: 1rem; background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 8px; color: #721c24; }
.no-result { margin-top: 2rem; padding: 2rem; text-align: center; background: #f9f9f9; border-radius: 8px; }
.no-result p { color: #666; margin: 0; }

/* 历史列表样式 */
.history-section { margin-top: 2.5rem; padding-top: 1.5rem; border-top: 2px dashed #eee; }
.history-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.history-header h3 { margin-bottom: 0; }
.refresh-btn { padding: 0.4rem 1rem; background: #f0f0f0; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; font-size: 0.85rem; transition: background 0.2s; }
.refresh-btn:hover:not(:disabled) { background: #e0e0e0; }
.refresh-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.table-wrapper { overflow-y: auto; overflow-x: hidden; max-height: 500px; scrollbar-width: thin; }
.history-table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
.history-table th, .history-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
.history-table th { background: #f9f9f9; color: #555; font-weight: 600; }
.history-table tr:hover { background: #fafafa; }

.status-tag { padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; font-weight: 600; white-space: nowrap; display: inline-block; }
.status-tag.pending { background: #fff3cd; color: #856404; }
.status-tag.paid { background: #d4edda; color: #155724; }
.status-tag.failed { background: #f8d7da; color: #721c24; }

.detail-btn { padding: 0.3rem 0.8rem; background: #667eea; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 0.8rem; transition: background 0.2s; }
.detail-btn:hover { background: #5a6fd6; }
</style>