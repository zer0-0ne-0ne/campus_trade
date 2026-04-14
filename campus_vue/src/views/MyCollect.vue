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
            <img
                :src="p.imagePath || 'https://picsum.photos/400/300?random='+p.pid"
                alt="商品图片"
            >
          </div>

          <div class="card-content">
            <h3 class="product-name">{{ p.title }}</h3>
            <p class="product-desc">{{ p.description }}</p>
            <div class="product-price">¥{{ p.price }}</div>
          </div>

          <!-- 查看详情 -->
          <button class="view-btn" @click="goDetail(p.pid)">查看</button>

          <!-- 取消收藏 -->
          <button class="del-btn" @click="cancelCollect(p.pid)">取消收藏</button>
        </div>

        <div v-if="productList.length === 0" style="text-align:center; padding:40px; color:#999;">
          暂无收藏商品
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const productList = ref([]) // 收藏的商品列表

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
    // 获取收藏的商品ID
    const resIds = await axios.get('http://localhost:8080/collect/myIds', {
      params: { uid }
    })
    const ids = resIds.data.ids || []

    if (ids.length === 0) {
      productList.value = []
      return
    }

    // 根据商品ID批量获取详情
    const list = []
    for (const pid of ids) {
      const res = await axios.get(`http://localhost:8080/product/detail/${pid}`)
      if (res.data) {
        list.push(res.data)
      }
    }
    productList.value = list
  } catch (e) {
    console.error(e)
    ElMessage.error('加载收藏失败')
  }
}

// 跳转到详情
const goDetail = (pid) => {
  router.push(`/product/${pid}`)
}

const cancelCollect = async (pid) => {
  const uid = localStorage.getItem('uid')
  if (!uid) return

  if (!confirm('确定取消收藏该商品吗？')) return

  try {
    await axios.post('http://localhost:8080/collect/delete', null, {
      params: { uid, pid }
    })
    ElMessage.success('取消收藏成功')
    getMyCollectList() // 刷新列表
  } catch (e) {
    ElMessage.error('取消收藏失败')
  }
}

// 返回
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.my-collect-page {
  position: fixed;
  top: 60px;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: var(--bg);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  overflow-y: auto;
  padding: 30px;
}
.container {
  width: 700px;
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
  border: 1px solid var(--line);
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
}
.back-bar h2 {
  margin: 0;
  font-size: 18px;
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
  box-shadow: 0 1px 5px rgba(0,0,0,0.05);
}
.product-img {
  width: 120px;
  height: 120px;
}
.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-content {
  flex: 1;
  padding: 15px;
}
.product-name {
  font-size: 16px;
  margin: 0 0 5px;
  color: var(--text);
}
.product-desc {
  font-size: 13px;
  color: var(--text);
  margin: 0 0 5px;
}
.product-price {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}

.view-btn {
  margin-right: 10px;
  background: #409eff;
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.del-btn {
  margin-right: 15px;
  background: #ff4d4f;
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}
</style>