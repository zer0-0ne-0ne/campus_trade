<template>
  <div class="chat-float-btn" @click="goToChat">
    🤖 客服
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToChat = () => {
  router.push('/ai-chat')
}
</script>

<style scoped>
.chat-float-btn {
  position: fixed;
  right: 20px;
  bottom: 100px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  cursor: pointer;
  box-shadow: 0 2px 12px var(--shadow-card);
  z-index: 9999;
  transition: transform 0.2s;
  line-height: 1.2;
}
.chat-float-btn:hover {
  transform: scale(1.08);
}
</style>
