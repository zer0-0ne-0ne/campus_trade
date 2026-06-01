<template>
  <div class="login-page">
    <div class="login-card">
      <h2>校园二手交易平台</h2>
      <div class="form-item">
        <input v-model="username" type="text" placeholder="请输入用户名" />
      </div>
      <div class="form-item">
        <input v-model="password" type="password" placeholder="请输入密码" />
      </div>
      <div class="btn-group">
        <button class="btn-login" @click.stop="login">登录</button>
        <button class="btn-to-register" @click.stop="toRegister">去注册</button>
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

const login = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入账号密码')
    return
  }

  try {
    const res = await request.post('/user/login', {
      username: username.value,
      password: password.value
    })

    if (res.data != null) {
      const user = res.data

      localStorage.setItem('token', 'LOGIN_SUCCESS')
      localStorage.setItem('username', user.username)
      localStorage.setItem('phone', user.phone || '')
      localStorage.setItem('email', user.email || '')
      localStorage.setItem('uid', user.uid + '')
      localStorage.setItem('identity', user.identity)
      localStorage.setItem('status', user.status)
      localStorage.setItem('avatar', user.avatar)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('账号或密码错误')
    }
  } catch (err) {
    ElMessage.error('登录失败')
  }
}

const toRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('../assets/login_bg.webp');
  background-size: cover;
  background-position: center;
  background-color: var(--bg);
}

.login-card {
  background: var(--card);
  backdrop-filter: blur(5px);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 15px var(--shadow-card);
  width: 380px;
  max-width: 90vw;
  text-align: center;
}

.login-card h2 {
  margin-bottom: 30px;
  color: var(--text);
}

.form-item {
  margin-bottom: 20px;
}

.form-item input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--input-border);
  border-radius: 8px;
  font-size: 15px;
  box-sizing: border-box;
  background: var(--card);
  color: var(--text);
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 10px;
}

.btn-login {
  padding: 12px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.btn-to-register {
  padding: 12px;
  background: var(--card);
  color: var(--text-secondary);
  border: 1px solid var(--line);
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
}
</style>
