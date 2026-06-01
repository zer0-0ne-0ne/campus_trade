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
import request from '../utils/request'
import { getAvatar } from '../utils/images'

const router = useRouter()

const userName = ref('校园用户')
const userIdentity = ref('学生')
const userAvatar = ref(getAvatar(null))

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
    const resPublish = await request.get('/product/user/' + uid)
    publishCount.value = resPublish.data.length

    const resCollect = await request.get('/collect/myIds', {
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
  max-width: 500px;
  margin: 0 auto;
  padding: 30px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
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
  background-color: var(--card);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 10px var(--shadow);
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--line);
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
  color: var(--text);
}
.desc {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
}

.menu-list {
  background-color: var(--card);
  border-radius: 12px;
  margin-top: 16px;
  overflow: hidden;
  box-shadow: 0 2px 10px var(--shadow);
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--line);
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
  color: var(--text);
}

.stats-section {
  background-color: var(--card);
  border-radius: 12px;
  margin-top: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  box-shadow: 0 2px 10px var(--shadow);
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: var(--primary);
}
.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
}
.stat-divider {
  width: 1px;
  height: 30px;
  background-color: var(--line);
}

.logout-section {
  margin-top: 16px;
}
.logout-btn {
  width: 100%;
  padding: 14px;
  background-color: var(--card);
  color: var(--danger);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 2px 10px var(--shadow);
}
</style>
