<template>
  <div class="register-page">
    <div class="register-card">
      <h2>用户注册</h2>

      <div class="form-item">
        <input v-model="username" type="text" placeholder="请输入用户名" />
      </div>

      <div class="form-item">
        <input v-model="password" type="password" placeholder="请输入密码" />
      </div>

      <div class="form-item">
        <select v-model="identity" class="select-input">
          <option value="">请选择身份</option>
          <option value="学生">学生</option>
          <option value="教师">教师</option>
        </select>
      </div>

      <div class="btn-group">
        <button type="button" class="btn-register" @click="register">注册</button>
        <button type="button" class="btn-back" @click="toLogin">返回登录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const identity = ref('')

const register = async () => {
  if (!username.value || !password.value || !identity.value) {
    ElMessage.warning('请完善信息（含身份选择）')
    return
  }

  try {
    const res = await request.post('/user/register', {
      username: username.value,
      password: password.value,
      identity: identity.value
    })

    if (res.data === '注册成功') {
      ElMessage.success('注册成功！')
      router.push('/login')
    } else {
      ElMessage.error(res.data)
    }
  } catch (e) {
    ElMessage.error('注册失败')
  }
}

const toLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('../assets/login_bg.webp');
  background-size: cover;
  background-position: center;
  background-color: var(--bg);
}

.register-card {
  background: var(--card);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 15px var(--shadow-card);
  width: 380px;
  max-width: 90vw;
  text-align: center;
}

.register-card h2 {
  color: var(--text);
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item input,
.select-input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--input-border);
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 14px;
  color: var(--text);
  background-color: var(--card);
}

.select-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1024 1024'%3E%3Cpath d='M840.4 300H183.6c-19.7 0-30.7 23.5-18.5 37l329.4 380.8c7.4 8.6 22.2 8.6 29.6 0l329.4-380.8c12.2-13.5 1.1-37-18.5-37z' fill='%23999'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 12px;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 10px;
}
.btn-register {
  padding: 12px;
  background: var(--success);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.btn-back {
  padding: 12px;
  background: var(--card);
  color: var(--text-secondary);
  border: 1px solid var(--line);
  border-radius: 8px;
  cursor: pointer;
}
</style>
