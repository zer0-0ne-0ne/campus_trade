<template>
  <div class="product-detail-page">
    <div class="container">
      <div class="back-bar">
        <button class="back-btn" @click="goBack">← 返回</button>
        <h2>商品详情</h2>
      </div>

      <div class="detail-card">
        <div class="product-img">
          <img :src="getProductImage(product.imagePath, product.pid)" alt="商品图片">
        </div>

        <div class="product-info">
          <h1 class="title">{{ product.title }}</h1>
          <p class="desc">{{ product.description || '暂无描述' }}</p>
          <div class="price">¥{{ product.price }}</div>
          <div class="meta">
            <span>分类：{{ categoryName || '未分类' }}</span>
            <span>发布时间：{{ (product.createTime || '').split('T')[0] || '未知' }}</span>
          </div>
        </div>

        <div class="action-bar" v-if="!isMyOwn">
          <button class="contact-btn" @click="toggleCollect">
            {{ isCollected ? '✅ 已收藏' : '🤍 收藏' }}
          </button>
          <button class="buy-btn" @click="openBuyDialog">立即购买</button>
        </div>

        <div class="action-bar" v-else>
          <button class="del-btn" @click="deleteGoods">删除商品</button>
        </div>
      </div>
    </div>

    <div class="buy-dialog-mask" v-if="showBuyDialog" @click.self="closeBuyDialog">
      <div class="buy-dialog">
        <h3>确认下单</h3>
        <div class="dialog-form">
          <div class="form-item">
            <label>交易时间</label>
            <input type="datetime-local" v-model="orderForm.trade_time" />
          </div>
          <div class="form-item">
            <label>交易地点</label>
            <input type="text" v-model="orderForm.trade_place" placeholder="请输入交易地点" />
          </div>
        </div>
        <div class="dialog-btns">
          <button class="btn-cancel" @click="closeBuyDialog">返回</button>
          <button class="btn-confirm" @click="confirmBuy">确认下单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../utils/request'
import { getProductImage } from '../utils/images'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const product = ref({})
const categoryList = ref([])
const categoryName = ref('')
const isMyOwn = ref(false)
const isCollected = ref(false)

const showBuyDialog = ref(false)
const orderForm = ref({
  trade_time: '',
  trade_place: ''
})

const openBuyDialog = () => {
  const uid = localStorage.getItem('uid')
  if (!uid) {
    ElMessage.warning('请先登录')
    return
  }
  showBuyDialog.value = true
}

const closeBuyDialog = () => {
  showBuyDialog.value = false
  orderForm.value = { trade_time: '', trade_place: '' }
}

const confirmBuy = async () => {
  const { trade_time, trade_place } = orderForm.value

  if (!trade_time || !trade_place) {
    ElMessage.warning('请填写完整交易信息')
    return
  }

  const uid = localStorage.getItem('uid')
  if (!uid) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const order = {
      pid: Number(product.value.pid),
      buyerId: Number(uid),
      sellerId: Number(product.value.uid),
      tradeTime: trade_time,
      tradePlace: trade_place,
      status: 0
    }

    const res = await request.post('/order/add', order)

    if (res.data === '创建订单成功') {
      ElMessage.success('下单成功！')
      closeBuyDialog()
    } else {
      ElMessage.error(res.data)
    }
  } catch (error) {
    console.error('错误', error)
    ElMessage.error('下单失败：服务器异常')
  }
}

const getCategoryNameById = (cid) => {
  const cat = categoryList.value.find(c => c.cid === cid)
  return cat ? cat.categoryName : '未分类'
}

onMounted(async () => {
  try {
    const catRes = await request.get('/category/list')
    categoryList.value = catRes.data
  } catch (e) {}

  const pid = route.params.pid
  if (!pid) return
  loadProductDetail(pid)
})

const loadProductDetail = async (pid) => {
  try {
    const res = await request.get('/product/detail/' + pid)
    product.value = res.data
    categoryName.value = getCategoryNameById(product.value.cid)
    const loginUid = localStorage.getItem('uid')
    isMyOwn.value = loginUid && product.value.uid == loginUid
    if (!isMyOwn.value && loginUid) checkCollectStatus(loginUid, pid)
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const checkCollectStatus = async (uid, pid) => {
  try {
    const res = await request.get('/collect/isCollect', { params: { uid, pid } })
    isCollected.value = res.data.isCollect
  } catch (e) {}
}

const toggleCollect = async () => {
  const uid = localStorage.getItem('uid')
  const pid = product.value.pid
  try {
    if (isCollected.value) {
      await request.post('/collect/delete', null, { params: { uid, pid } })
      isCollected.value = false
      ElMessage.success('取消收藏')
    } else {
      await request.post('/collect/add', null, { params: { uid, pid } })
      isCollected.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteGoods = async () => {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post('/product/delete/' + product.value.pid)
    ElMessage.success('删除成功')
    router.go(-1)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const goBack = () => router.go(-1)
</script>

<style scoped>
.product-detail-page {
  padding: 30px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.container {
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}
.back-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}
.back-bar h2 {
  color: var(--text);
  margin: 0;
  font-size: 18px;
}
.back-btn {
  padding: 6px 12px;
  border: 1px solid var(--line);
  border-radius: 6px;
  background: var(--card);
  color: var(--text);
  cursor: pointer;
}
.detail-card {
  background: var(--card);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 5px var(--shadow);
}
.product-img {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}
.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.product-info {
  margin-bottom: 20px;
}
.title {
  font-size: 22px;
  margin: 0 0 10px;
  color: var(--text);
}
.desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 15px;
  line-height: 1.6;
}
.price {
  font-size: 24px;
  color: var(--price);
  font-weight: bold;
  margin: 0 0 15px;
}
.meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--text-muted);
  flex-wrap: wrap;
}
.action-bar {
  display: flex;
  gap: 15px;
}
.contact-btn {
  flex: 1;
  padding: 12px;
  border: 1px solid var(--primary);
  background: var(--card);
  color: var(--primary);
  border-radius: 6px;
  cursor: pointer;
}
.buy-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: var(--primary);
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
}
.del-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: var(--danger);
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
}

.buy-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.buy-dialog {
  background: var(--card);
  width: 420px;
  max-width: 90vw;
  padding: 24px;
  border-radius: 12px;
}
.buy-dialog h3 {
  margin: 0 0 20px;
  text-align: center;
  color: var(--text);
}
.form-item {
  margin-bottom: 16px;
}
.form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: var(--text);
}
.form-item input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--input-border);
  border-radius: 6px;
  background: var(--card);
  color: var(--text);
}
.dialog-btns {
  display: flex;
  gap: 12px;
}
.btn-cancel {
  flex: 1;
  padding: 12px;
  border: 1px solid var(--line);
  background: var(--card);
  border-radius: 6px;
  color: var(--text);
  cursor: pointer;
}
.btn-confirm {
  flex: 1;
  padding: 12px;
  border: none;
  background: var(--primary);
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .product-img {
    height: 200px;
  }
  .title {
    font-size: 18px;
  }
  .price {
    font-size: 20px;
  }
}
</style>
