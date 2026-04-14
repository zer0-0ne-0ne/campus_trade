<template>
  <div class="app-container">
    <header class="app-header">
      <h1 class="app-title">校园二手交易平台</h1>
    </header>

    <!-- 🔥 修复：永远显示推荐栏 -->
    <div class="recommend-container">
      <h3 class="recommend-title">为你推荐</h3>
      <div class="recommend-scroll">
        <div class="recommend-item"
             v-for="p in recommendList"
             :key="p.pid"
             @click="showInfo(p)">
          <img :src="p.imagePath || 'https://picsum.photos/400/300?' + p.pid" alt="" />
          <p class="rec-name">{{ p.title }}</p>
          <p class="rec-price">¥{{ p.price }}</p>
        </div>
        <div v-if="recommendList.length === 0" style="font-size:13px;color:#999;padding:10px;">
          暂无推荐商品
        </div>
      </div>
    </div>

    <!-- 搜索栏：返回 + 输入框 + 确定 -->
    <div class="search-box">
      <button class="back-btn" @click="resetAll">←</button>

      <input
          v-model="keyword"
          placeholder="输入商品名称搜索..."
          class="search-input"
          @keyup.enter="doSearch"
      />
      <button class="search-btn" @click="doSearch">确定</button>
    </div>

    <!-- 分类栏 -->
    <div class="category-bar">
      <button class="cat-btn" :class="{active: currentCat === null}" @click="currentCat = null">全部</button>
      <button class="cat-btn" :class="{active: currentCat === 4}" @click="currentCat = 4">电子产品</button>
      <button class="cat-btn" :class="{active: currentCat === 5}" @click="currentCat = 5">生活用品</button>
      <button class="cat-btn" :class="{active: currentCat === 6}" @click="currentCat = 6">书籍资料</button>
      <button class="cat-btn" :class="{active: currentCat === 7}" @click="currentCat = 7">文具办公</button>
      <button class="cat-btn" :class="{active: currentCat === 8}" @click="currentCat = 8">体育用品</button>
    </div>

    <main class="product-grid">
      <div v-if="loading" class="skeleton-wrapper">
        <div class="skeleton-card" v-for="i in 6"></div>
      </div>

      <div v-else v-for="p in showList" :key="p.pid" class="product-card">
        <div class="product-img">
          <img :src="p.imagePath || 'https://picsum.photos/400/300?random='+p.pid" alt="">
        </div>
        <div class="card-content">
          <h3 class="product-name">{{ p.title }}</h3>
          <p class="product-desc">{{ p.description }}</p>
          <div class="product-price">¥{{ p.price }}</div>
        </div>
        <button class="view-btn" @click="showInfo(p)">立即查看</button>
      </div>

      <div v-if="!loading && showList.length === 0" class="empty-tip">
        暂无相关商品
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const productList = ref([])
const recommendList = ref([])
const loading = ref(true)
const router = useRouter()

const currentCat = ref(null)
const keyword = ref('')
const searchKey = ref('')
const uid = localStorage.getItem('uid')

onMounted(() => {
  // 加载商品列表
  axios.get('http://localhost:8080/product/list')
      .then(res => {
        productList.value = res.data
      })
      .finally(() => {
        loading.value = false
      })

  // 🔥 强制加载推荐（不管有没有登录）
  console.log("当前用户ID：", uid)
  axios.get(`http://localhost:8080/recommend/user/${uid || 1}`) // 没登录就用 uid=1
      .then(res => {
        console.log("推荐返回数据：", res.data)
        recommendList.value = res.data
      })
      .catch(err => {
        console.error("推荐接口失败：", err)
      })
})

// 搜索
const doSearch = () => {
  searchKey.value = keyword.value.trim()
}

// 重置所有
const resetAll = () => {
  keyword.value = ''
  searchKey.value = ''
  currentCat.value = null
}

// 过滤
const showList = computed(() => {
  return productList.value.filter(p => {
    const statusOk = p.status !== 3
    const catOk = currentCat.value === null || p.cid === currentCat.value
    const title = (p.title || '').toLowerCase()
    const key = searchKey.value.toLowerCase()
    const searchOk = key === '' || title.includes(key)
    return statusOk && catOk && searchOk
  })
})

const showInfo = (p) => {
  router.push('/product/' + p.pid)
}
</script>

<style scoped>
/* 你的样式完全不变，我一点没动 */
.app-container {
  min-height: 100vh;
  background: #f8fafc;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.app-header {
  background: #fff;
  padding: 22px 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  margin-bottom: 10px;
}
.app-title {
  font-size: 26px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  text-align: center;
}

.recommend-container {
  padding: 0 24px 16px;
  max-width: 1200px;
  margin: 0 auto;
}
.recommend-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 4px;
}
.recommend-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.recommend-item {
  width: 130px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
}
.recommend-item img {
  width: 100%;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 6px;
}
.rec-name {
  font-size: 13px;
  margin: 0 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-price {
  font-size: 13px;
  color: #ef4444;
  font-weight: bold;
  margin: 0;
}

.search-box {
  display: flex;
  gap: 8px;
  padding: 0 25px 15px;
  max-width: 1200px;
  margin: 0 auto;
  align-items: center;
}
.back-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.search-input {
  flex: 1;
  height: 38px;
  padding: 0 15px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 15px;
  outline: none;
}
.search-btn {
  height: 38px;
  padding: 0 18px;
  background: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.category-bar {
  display: flex;
  gap: 10px;
  padding: 0 25px 15px;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto;
}
.cat-btn {
  padding: 8px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}
.cat-btn.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  padding: 0 24px 60px;
  max-width: 1200px;
  margin: 0 auto;
}
.product-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 3px 12px rgba(0,0,0,0.07);
}
.product-img {
  width: 100%;
  height: 180px;
}
.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-content {
  padding: 18px;
}
.product-name {
  font-size: 18px;
  margin: 0 0 8px;
}
.product-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  font-size: 20px;
  color: #ef4444;
  font-weight: bold;
}
.view-btn {
  width: calc(100% - 36px);
  margin: 0 18px 18px;
  padding: 10px;
  background: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.skeleton-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
.skeleton-card {
  height: 340px;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.2s infinite;
  border-radius: 16px;
}
@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
.empty-tip {
  grid-column: 1/-1;
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
  font-size: 16px;
}
</style>