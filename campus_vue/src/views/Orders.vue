<template>
  <div class="my-orders-page">
    <div class="container">
      <div class="back-bar">
        <button class="back-btn" @click="goBack">← 返回</button>
        <h2>我的订单</h2>
      </div>

      <div class="order-tabs">
        <button
            class="tab-btn"
            :class="{ active: activeTab === 'buy' }"
            @click="switchTab('buy')"
        >
          我买的
        </button>
        <button
            class="tab-btn"
            :class="{ active: activeTab === 'sell' }"
            @click="switchTab('sell')"
        >
          我卖的
        </button>
      </div>

      <div class="filter-box">
        <span>订单状态：</span>
        <select v-model="statusFilter" class="status-select">
          <option value="">全部</option>
          <option value="0">待交易</option>
          <option value="1">已完成</option>
          <option value="2">已取消</option>
        </select>
      </div>

      <div class="order-list">
        <div class="order-card" v-for="item in filteredOrders" :key="item.oid">
          <div class="goods-img">
            <img
                :src="item.product?.imagePath || 'https://picsum.photos/120/120'"
                alt="商品图片"
            >
          </div>

          <div class="order-info">
            <div class="goods-name">{{ item.product?.title || '商品已下架' }}</div>
            <div class="price">¥{{ item.product?.price || '0.00' }}</div>

            <div class="info-row">
              <span>交易时间：{{ formatTime(item.tradeTime) }}</span>
              <span>交易地点：{{ item.tradePlace }}</span>
            </div>

            <div class="info-row">
              <span>创建时间：{{ formatTime(item.createTime) }}</span>
              <div class="right-box">
                <span class="status-tag" :class="tagClass(item.status)">
                  {{ getStatusText(item) }}
                </span>

                <button
                    v-if="item.status === 0"
                    class="cancel-btn"
                    @click="cancelOrder(item.oid)"
                >
                  取消订单
                </button>

                <button
                    v-if="item.status === 0"
                    class="finish-btn"
                    @click="confirmOrder(item)"
                >
                  完成
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="filteredOrders.length === 0" class="empty">暂无订单</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const orderList = ref([])
const activeTab = ref('buy')
const statusFilter = ref('')
const uid = localStorage.getItem('uid')

// 状态文字
const statusText = {
  0: '待交易',
  1: '已完成',
  2: '已取消',
}

// 时间格式化
const formatTime = (timeStr) => {
  if (!timeStr) return '未知'
  return timeStr.replace('T', ' ').split('.')[0]
}

// 状态标签颜色
const tagClass = (s) => {
  if (s === 0 || s === 3 || s === 4) return 'waiting'
  if (s === 1) return 'success'
  if (s === 2) return 'cancel'
  return ''
}

// 获取状态文本
const getStatusText = (item) => {
  return statusText[item.status] || '未知'
}

// 切换标签
const switchTab = (tab) => {
  activeTab.value = tab
  loadOrders()
}

// 加载订单
const loadOrders = async () => {
  if (!uid) return
  try {
    let res
    if (activeTab.value === 'buy') {
      res = await axios.get(`http://localhost:8080/order/buyer/${uid}`)
    } else {
      res = await axios.get(`http://localhost:8080/order/seller/${uid}`)
    }

    const orders = res.data || []
    for (const o of orders) {
      try {
        const p = await axios.get(`http://localhost:8080/product/detail/${o.pid}`)
        o.product = p.data
      } catch {
        o.product = null
      }
    }
    orderList.value = orders
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// 取消订单
const cancelOrder = async (oid) => {
  try {
    await ElMessageBox.confirm('确定取消订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.post(`http://localhost:8080/order/cancel/${oid}`)
    ElMessage.success('取消成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const confirmOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认交易已完成？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    // 判断是买家还是卖家
    if (order.buyerId == uid) {
    } else {
      await axios.post(`http://localhost:8080/order/sellerConfirm/${order.oid}`)
    }

    ElMessage.success('确认成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

// 筛选
const filteredOrders = computed(() => {
  let list = [...orderList.value]
  if (statusFilter.value) {
    list = list.filter(i => i.status == statusFilter.value)
  }
  return list
})

onMounted(() => {
  loadOrders()
})

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.my-orders-page {
  position: fixed;
  top: 60px;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: var(--bg);
  overflow-y: auto;
  padding: 30px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}
.container {
  width: 750px;
  margin: 0 auto;
}
.back-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}
.back-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
}
.order-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.tab-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
}
.tab-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
.filter-box {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  font-size: 14px;
}
.status-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.goods-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
}
.goods-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.order-info {
  flex: 1;
}
.goods-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 6px;
}
.price {
  font-size: 18px;
  color: #ff4d4f;
  font-weight: bold;
  margin-bottom: 10px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.right-box {
  display: flex;
  align-items: center;
  gap: 10px;
}
.status-tag {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}
.waiting {
  background: #fff7e6;
  color: #ff7d00;
}
.success {
  background: #e6fffb;
  color: #00b42a;
}
.cancel {
  background: #fff1f0;
  color: #ff4d4f;
}
.cancel-btn {
  padding: 4px 10px;
  border: none;
  background: #ff4d4f;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.finish-btn {
  padding: 4px 10px;
  border: none;
  background: #00b42a;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.empty {
  text-align: center;
  padding: 60px 0;
  color: #999;
}
</style>