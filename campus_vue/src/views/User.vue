<template>
  <div class="mine-page">
    <div class="user-card">
      <div class="avatar">
        <img :src="userAvatar" alt="头像" />
      </div>
      <div class="info">
        <div class="name">{{ userName }}</div>
        <div class="desc">{{ userIdentity }}</div>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item card-hover" @click="toMyPublish">
        <div class="menu-icon">📦</div>
        <div class="menu-title">我的发布</div>
      </div>

      <div class="menu-item card-hover" @click="toMyCollect">
        <div class="menu-icon">❤️</div>
        <div class="menu-title">我的收藏</div>
      </div>

      <div class="menu-item card-hover" @click="toProfile">
        <div class="menu-icon">📝</div>
        <div class="menu-title">个人资料</div>
      </div>

      <div class="menu-item card-hover" @click="toSettings">
        <div class="menu-icon">⚙️</div>
        <div class="menu-title">设置</div>
      </div>
    </div>

    <div class="stats-section card-hover">
      <div class="stat-item">
        <div class="stat-value">{{ publishCount }}</div>
        <div class="stat-label">发布商品</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-value">{{ collectCount }}</div>
        <div class="stat-label">收藏商品</div>
      </div>
    </div>

    <div class="logout-section">
      <button class="logout-btn card-hover" @click="logout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const userName = ref('校园用户')
const userIdentity = ref('学生')
const userAvatar = ref('https://picsum.photos/100/100')

const publishCount = ref(0)
const collectCount = ref(0)

onMounted(() => {
  const savedUsername = localStorage.getItem('username')
  const savedIdentity = localStorage.getItem('identity')
  if (savedUsername) userName.value = savedUsername
  if (savedIdentity) userIdentity.value = savedIdentity

  loadRealCount()
})

const loadRealCount = async () => {
  const uid = localStorage.getItem('uid')
  if (!uid) return

  try {
    // 我的发布数量
    const resPublish = await axios.get('http://localhost:8080/product/user/' + uid)
    publishCount.value = resPublish.data.length

    // 我的收藏数量
    const resCollect = await axios.get('http://localhost:8080/collect/myIds', {
      params: { uid }
    })
    collectCount.value = resCollect.data.ids?.length || 0

  } catch (e) {
    console.error(e)
  }
}

const toProfile = () => router.push('/profile')
const toMyPublish = () => router.push('/my-publish')
const toMyCollect = () => router.push('/my-collect')
const toSettings = () => router.push('/settings')

const logout = () => {
  localStorage.clear()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.mine-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  overflow-y: auto;
}

.card-hover {
  transition: all 0.22s ease;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.card-hover:active {
  transform: scale(0.96);
  opacity: 0.92;
}

.user-card {
  background-color: white;
  margin: 160px 700px 10px 700px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  pointer-events: none;
  cursor: default;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #f0f0f0;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info {
  flex: 1;
}
.name {
  font-size: 18px;
  font-weight: 600;
  color: #222;
}
.desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.menu-list {
  background-color: white;
  border-radius: 12px;
  margin: 0 700px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-icon {
  font-size: 20px;
  margin-right: 12px;
}
.menu-title {
  font-size: 16px;
  color: #333;
}

.stats-section {
  background-color: white;
  border-radius: 12px;
  margin: 20px 700px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #409eff;
}
.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.stat-divider {
  width: 1px;
  height: 30px;
  background-color: #f0f0f0;
}

.logout-section {
  margin: 0 680px;
  padding: 0 20px;
}
.logout-btn {
  width: 100%;
  padding: 14px;
  background-color: white;
  color: #ff4d4f;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}
</style>