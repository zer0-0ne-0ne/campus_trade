<template>
  <div class="my-collect-page">
    <div class="container">
      <div class="back-bar">
        <button class="back-btn" @click="goBack">← 返回</button>
        <h2>我的收藏</h2>
      </div>

      <div class="goods-list">
        <div class="product-card" v-for="p in productList" :key="p.pid">
          <div class="product-img">
            <img :src="getProductImage(p.imagePath, p.pid)" alt="商品图片">
          </div>

          <div class="card-content">
            <h3 class="product-name">{{ p.title }}</h3>
            <p class="product-desc">{{ p.description }}</p>
            <div class="product-price">¥{{ p.price }}</div>
          </div>

          <div class="action-btns">
            <button class="view-btn" @click="goDetail(p.pid)">查看</button>
            <button class="del-btn" @click="cancelCollect(p.pid)">取消收藏</button>
          </div>
        </div>

        <div v-if="productList.length === 0" class="empty">暂无收藏商品</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'
import { getProductImage } from '../utils/images'
import { useRouter } from 'vue-router'

const router = useRouter()
const productList = ref([])

onMounted(() => {
  getMyCollectList()
})

const getMyCollectList = async () => {
  const uid = localStorage.getItem('uid')
  if (!uid) {
    ElMessage.error('请先登录')
    return
  }

  try {
    const resIds = await request.get('/collect/myIds', {
      params: { uid }
    })
    const ids = resIds.data.ids || []

    if (ids.length === 0) {
      productList.value = []
      return
    }

    const list = []
    const detailRequests = ids.map(pid =>
      request.get('/product/detail/' + pid)
        .then(res => { if (res.data) list.push(res.data) })
        .catch(() => {})
    )
    await Promise.all(detailRequests)
    productList.value = list
  } catch (e) {
    console.error(e)
    ElMessage.error('加载收藏失败')
  }
}

const goDetail = (pid) => {
  router.push('/product/' + pid)
}

const cancelCollect = async (pid) => {
  const uid = localStorage.getItem('uid')
  if (!uid) return

  try {
    await ElMessageBox.confirm('确定取消收藏该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.post('/collect/delete', null, {
      params: { uid, pid }
    })
    ElMessage.success('取消收藏成功')
    getMyCollectList()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消收藏失败')
  }
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.my-collect-page {
  padding: 30px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.container {
  max-width: 700px;
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
  cursor: pointer;
  color: var(--text);
}
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.product-card {
  background: var(--card);
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 5px var(--shadow);
}
.product-img {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}
.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-content {
  flex: 1;
  padding: 15px;
  min-width: 0;
}
.product-name {
  font-size: 16px;
  margin: 0 0 5px;
  color: var(--text);
}
.product-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 5px;
}
.product-price {
  font-size: 16px;
  color: var(--price);
  font-weight: bold;
}
.action-btns {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-right: 15px;
  flex-shrink: 0;
}
.view-btn {
  background: var(--primary);
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.del-btn {
  background: var(--danger);
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.empty {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .product-card {
    flex-direction: column;
    align-items: flex-start;
  }
  .product-img {
    width: 100%;
    height: 160px;
  }
  .action-btns {
    flex-direction: row;
    padding: 0 15px 15px;
  }
}
</style>
