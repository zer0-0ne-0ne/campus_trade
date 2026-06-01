<template>
  <div class="ai-chat-page">
    <div class="ai-chat-container">
      <div class="chat-header">
        <button class="back-btn" @click="goBack">← 返回</button>
        <div>
          <h2>校园二手平台 AI 客服</h2>
          <p>有问题随时问我</p>
        </div>
        <div class="header-spacer"></div>
      </div>

      <div class="chat-body" ref="chatBody">
        <div v-if="chatList.length === 0" class="message bot-message welcome-msg">
          <div class="avatar">🤖</div>
          <div class="bubble">同学你好！我是校园二手平台AI客服，你可以问我：<br>
            · 怎么发布商品<br>
            · 怎么联系卖家<br>
            · 交易安全提醒<br>
            · 平台规则等问题
          </div>
        </div>

        <div v-for="(item, index) in chatList" :key="index" class="message" :class="item.role === 'user' ? 'user-message' : 'bot-message'">
          <div class="avatar">{{ item.role === 'user' ? '👨‍🎓' : '🤖' }}</div>
          <div>
            <div class="bubble">{{ item.content }}</div>
            <div v-if="item.action" class="nav-btn" @click="goToPage(item.action.path)">
              {{ item.action.label }} →
            </div>
          </div>
        </div>

        <div class="message bot-message" v-if="loading">
          <div class="avatar">🤖</div>
          <div class="bubble loading">正在思考中...</div>
        </div>
      </div>

      <div class="chat-footer">
        <input v-model="inputText"
               @keyup.enter="sendMessage"
               placeholder="请输入你的问题..."
               class="chat-input" />
        <button @click="sendMessage" class="send-btn" :disabled="loading">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const inputText = ref('')
const chatList = ref([])
const loading = ref(false)
const chatBody = ref(null)

const getUserId = () => {
  const uid = localStorage.getItem('uid')
  const token = localStorage.getItem('token')
  if (token && uid && uid !== 'null' && uid !== 'undefined') {
    return uid
  }
  return null
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  const userId = getUserId()
  if (!userId) {
    chatList.value.push({
      role: 'bot',
      content: '请先登录后再使用客服功能'
    })
    nextTick(scrollToBottom)
    return
  }

  chatList.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true

  try {
    const res = await request.post('/chat', {
      question: text,
      userId: userId
    })

    chatList.value.push({
      role: 'bot',
      content: res.data.reply,
      action: res.data.action || null
    })
  } catch (err) {
    chatList.value.push({
      role: 'bot',
      content: '抱歉，服务暂时异常，请稍后再试～'
    })
  } finally {
    loading.value = false
    nextTick(scrollToBottom)
  }
}

const scrollToBottom = () => {
  if (chatBody.value) {
    chatBody.value.scrollTop = chatBody.value.scrollHeight
  }
}

const goBack = () => {
  router.back()
}

const goToPage = (path) => {
  router.push(path)
}

onMounted(async () => {
  const userId = getUserId()
  if (!userId) return
  try {
    const res = await request.get('/chat/history', { params: { userId } })
    if (res.data && res.data.length > 0) {
      chatList.value = res.data
    }
  } catch (e) {}
})

watch(chatList, () => nextTick(scrollToBottom), { deep: true })
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.ai-chat-container {
  width: 100%;
  max-width: 500px;
  height: calc(100vh - 100px);
  max-height: 750px;
  border-radius: 20px;
  box-shadow: 0 8px 30px var(--shadow-card);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--card);
}

.chat-header {
  background: var(--primary);
  color: white;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.chat-header h2 {
  margin: 0;
  font-size: 17px;
}
.chat-header p {
  margin: 2px 0 0;
  font-size: 13px;
  opacity: 0.9;
}
.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 14px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  flex-shrink: 0;
}
.back-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}
.header-spacer {
  width: 50px;
  flex-shrink: 0;
}

.chat-body {
  flex: 1;
  padding: 15px;
  background: var(--bg);
  overflow-y: scroll;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 75%;
}

.bot-message {
  align-self: flex-start;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--line);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.bubble {
  background: var(--card);
  padding: 10px 14px;
  border-radius: 15px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 2px 8px var(--shadow);
  word-break: break-word;
  color: var(--text);
}

.bot-message .bubble {
  border-bottom-left-radius: 5px;
}

.user-message .bubble {
  background: var(--primary);
  color: white;
  border-bottom-right-radius: 5px;
}

.loading {
  color: var(--text-muted);
}

.chat-footer {
  display: flex;
  padding: 12px;
  background: var(--card);
  border-top: 1px solid var(--line);
  gap: 10px;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
  height: 40px;
  border: 1px solid var(--input-border);
  border-radius: 20px;
  padding: 0 15px;
  outline: none;
  font-size: 14px;
  background: var(--card);
  color: var(--text);
}

.chat-input:focus {
  border-color: var(--primary);
}

.send-btn {
  height: 40px;
  padding: 0 20px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.nav-btn {
  display: inline-block;
  margin-top: 6px;
  padding: 5px 14px;
  background: var(--primary);
  color: #fff;
  border-radius: 14px;
  font-size: 13px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.nav-btn:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .ai-chat-page {
    padding: 0;
  }
  .ai-chat-container {
    height: calc(100vh - 60px);
    max-height: none;
    border-radius: 0;
  }
}
</style>
