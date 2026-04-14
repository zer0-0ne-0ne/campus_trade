<template>
  <div class="app-container">
    <div class="top-nav">
      <router-link to="/">首页</router-link>
      <router-link to="/category">订单</router-link>
      <router-link to="/publish">发布</router-link>
      <router-link to="/user">我的</router-link>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
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
  --line: #eee;
}
html.dark {
  --bg: #121212;
  --card: #1e1e1e;
  --text: #e0e0e0;
  --line: #333;
}

html { font-size: 16px !important; }
html.font-s { font-size: 14px !important; }
html.font-m { font-size: 16px !important; }
html.font-l { font-size: 18px !important; }

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: var(--card) !important;
  border-bottom: 1px solid var(--line) !important;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.top-nav a {
  color: var(--text) !important;
  text-decoration: none;
}
.top-nav a.router-link-active {
  color: #409eff !important;
}

.app-container {
  min-height: 100vh;
  padding-top: 60px;
  background: var(--bg) !important;
}
.content {
  background: var(--bg) !important;
  min-height: calc(100vh - 60px);
}
</style>