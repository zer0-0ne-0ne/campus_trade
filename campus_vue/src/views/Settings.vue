<template>
  <div class="settings-page">
    <div class="container">
      <div class="back-bar">
        <button class="back-btn" @click="goBack">← 返回</button>
        <h2>设置</h2>
      </div>

      <div class="setting-box">
        <div class="setting-item">
          <span>深色模式</span>
          <div class="toggle" :class="{ active: isDark }" @click="toggleDark">
            <div class="circle"></div>
          </div>
        </div>

        <div class="setting-item">
          <span>字体大小</span>
          <div class="font-group">
            <button :class="['font-btn', { active: current === 'small' }]" @click="setFont('small')">小</button>
            <button :class="['font-btn', { active: current === 'normal' }]" @click="setFont('normal')">中</button>
            <button :class="['font-btn', { active: current === 'large' }]" @click="setFont('large')">大</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const isDark = ref(false)
const current = ref('normal')

onMounted(() => {
  const theme = localStorage.getItem('theme')
  if (theme === 'dark') {
    document.documentElement.classList.add('dark')
    isDark.value = true
  }
  const font = localStorage.getItem('font') || 'normal'
  document.documentElement.className = document.documentElement.className.replace(/font-\w+/g, '')
  document.documentElement.classList.add('font-' + font)
  current.value = font
})

const toggleDark = () => {
  isDark.value = !isDark.value
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

const setFont = (size) => {
  current.value = size
  const html = document.documentElement
  html.className = html.className.replace(/font-\w+/g, '')
  html.classList.add('font-' + size)
  localStorage.setItem('font', size)
}

const goBack = () => router.back()
</script>

<style scoped>
.settings-page {
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
  width: 650px;
  margin: 0 auto;
}
.back-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}
.back-btn {
  padding: 6px 14px;
  border: 1px solid var(--line);
  border-radius: 6px;
  background: var(--card);
  color: var(--text);
  cursor: pointer;
}
.back-bar h2 {
  color: var(--text);
  margin: 0;
}

.setting-box {
  background: var(--card);
  border-radius: 12px;
  padding: 20px 25px;
}
.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 0;
  border-bottom: 1px solid var(--line);
  color: var(--text);
  font-size: 15px;
}
.setting-item:last-child {
  border-bottom: 0;
}

.toggle {
  width: 46px;
  height: 25px;
  background: #ccc;
  border-radius: 999px;
  position: relative;
  cursor: pointer;
  transition: 0.2s;
}
.toggle.active {
  background: #409eff;
}
.circle {
  width: 19px;
  height: 19px;
  background: #fff;
  border-radius: 50%;
  position: absolute;
  left: 3px;
  top: 3px;
  transition: 0.2s;
}
.toggle.active .circle {
  left: 24px;
}

.font-group {
  display: flex;
  gap: 8px;
}
.font-btn {
  padding: 6px 12px;
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--text);
  border-radius: 6px;
  cursor: pointer;
}
.font-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
</style>