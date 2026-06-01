<template>
  <div class="home-page">
    <header class="app-header">
      <h1 class="app-title">校园二手交易平台</h1>
    </header>

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

    <div class="category-bar">
      <button class="cat-btn" :class="{active: currentCat === null}" @click="currentCat = null">全部</button>
      <button
          v-for="cat in categoryList"
          :key="cat.cid"
          class="cat-btn"
          :class="{active: currentCat === cat.cid}"
          @click="currentCat = cat.cid"
      >{{ cat.categoryName }}</button>
    </div>

    <main class="product-grid">
      <div v-if="loading" class="skeleton-wrapper">
        <div class="skeleton-card" v-for="i in 6" :key="i"></div>
      </div>

      <div v-else v-for="p in showList" :key="p.pid" class="product-card">
        <div class="product-img">
          <img :src="getProductImage(p.imagePath, p.pid)" alt="">
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
import request from '../utils/request'
import { getProductImage } from '../utils/images'
import { useRouter } from 'vue-router'

const productList = ref([])
const categoryList = ref([])
const loading = ref(true)
const router = useRouter()

const currentCat = ref(null)
const keyword = ref('')
const searchKey = ref('')

onMounted(async () => {
  try {
    const [productRes, categoryRes] = await Promise.all([
      request.get('/product/list'),
      request.get('/category/list')
    ])
    productList.value = productRes.data
    categoryList.value = categoryRes.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

const doSearch = () => {
  searchKey.value = keyword.value.trim()
}

const resetAll = () => {
  keyword.value = ''
  searchKey.value = ''
  currentCat.value = null
}

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
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.app-header {
  background: var(--card);
  padding: 22px 24px;
  box-shadow: 0 1px 4px var(--shadow);
  margin-bottom: 10px;
  border-radius: 0 0 8px 8px;
}
.app-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--text);
  margin: 0;
  text-align: center;
}

.search-box {
  display: flex;
  gap: 8px;
  padding: 0 5px 15px;
  align-items: center;
}
.back-btn {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid var(--line);
  background: var(--card);
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text);
}
.search-input {
  flex: 1;
  height: 38px;
  padding: 0 15px;
  border: 1px solid var(--line);
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  background: var(--card);
  color: var(--text);
}
.search-input:focus {
  border-color: var(--primary);
}
.search-btn {
  height: 38px;
  padding: 0 18px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}

.category-bar {
  display: flex;
  gap: 10px;
  padding: 0 5px 15px;
  flex-wrap: wrap;
}
.cat-btn {
  padding: 8px 14px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: var(--card);
  cursor: pointer;
  color: var(--text);
  transition: all 0.2s;
}
.cat-btn.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  padding: 0 5px;
}
.product-card {
  background: var(--card);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 3px 12px var(--shadow-card);
  transition: transform 0.2s, box-shadow 0.2s;
}
.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px var(--shadow-card);
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
  color: var(--text);
}
.product-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  font-size: 20px;
  color: var(--price);
  font-weight: bold;
}
.view-btn {
  width: calc(100% - 36px);
  margin: 0 18px 18px;
  padding: 10px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.view-btn:hover {
  opacity: 0.9;
}

.skeleton-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.skeleton-card {
  height: 340px;
  background: linear-gradient(90deg, var(--card) 25%, var(--line) 50%, var(--card) 75%);
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
  color: var(--text-muted);
  font-size: 16px;
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
  .product-img {
    height: 130px;
  }
  .app-title {
    font-size: 20px;
  }
}
</style>
