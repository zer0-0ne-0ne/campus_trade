<template>
  <div id="root">
    <div class="app-container">
      <div v-if="showNav" class="top-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/orders">订单</router-link>
        <router-link to="/publish">发布</router-link>
        <router-link to="/user">我的</router-link>
      </div>

      <div class="content" :class="{ 'no-nav': !showNav }">
        <router-view />
      </div>
    </div>

    <AICustomerService v-if="showNav" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AICustomerService from './components/AICustomerService.vue'

const route = useRoute()
const hideNavRoutes = ['/login', '/register']
const showNav = computed(() => !hideNavRoutes.includes(route.path))
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg: #f5f7fa;
  --card: #fff;
  --text: #333;
  --text-secondary: #666;
  --text-muted: #999;
  --line: #eee;
  --primary: #409eff;
  --danger: #ff4d4f;
  --success: #00b42a;
  --warning: #ff7d00;
  --price: #ff4d4f;
  --input-border: #dcdfe6;
  --input-bg-disabled: #f5f7fa;
  --shadow: rgba(0, 0, 0, 0.06);
  --shadow-card: rgba(0, 0, 0, 0.07);
}
html.dark {
  --bg: #121212;
  --card: #1e1e1e;
  --text: #e0e0e0;
  --text-secondary: #aaa;
  --text-muted: #777;
  --line: #333;
  --primary: #409eff;
  --danger: #ff4d4f;
  --success: #00b42a;
  --warning: #ff7d00;
  --price: #ff6b6b;
  --input-border: #444;
  --input-bg-disabled: #2a2a2a;
  --shadow: rgba(0, 0, 0, 0.3);
  --shadow-card: rgba(0, 0, 0, 0.4);
}

html { font-size: 16px !important; }
html.font-small { font-size: 14px !important; }
html.font-normal { font-size: 16px !important; }
html.font-large { font-size: 18px !important; }

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: var(--card);
  border-bottom: 1px solid var(--line);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.top-nav a {
  color: var(--text);
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.top-nav a.router-link-active {
  color: var(--primary);
}

.app-container {
  min-height: 100vh;
  padding-top: 60px;
  background: var(--bg);
}
.content {
  background: var(--bg);
  min-height: calc(100vh - 60px);
}
.content.no-nav {
  padding-top: 0;
  min-height: 100vh;
}
.app-container:has(.content.no-nav) {
  padding-top: 0;
}
</style>
