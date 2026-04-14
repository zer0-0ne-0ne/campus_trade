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
import axios from 'axios'
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
    const res = await axios.post('http://localhost:8080/user/login', {
      username: username.value,
      password: password.value
    })

    if (res.data != null) {
      const user = res.data

      localStorage.setItem('token', 'LOGIN_SUCCESS')
      localStorage.setItem('username', user.username)
      localStorage.setItem('password', user.password)
      localStorage.setItem('phone', user.phone || '') // 保存手机号
      localStorage.setItem('email', user.email || '') // 保存邮箱
      localStorage.setItem('uid', user.uid + '') // 保存UID
      localStorage.setItem('identity',user.identity)
      localStorage.setItem('status',user.status)
      localStorage.setItem('avatar',user.avatar)
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
      ElMessage.error('账号或密码错误')
    }
  } catch (err) {
    ElMessage.error('登录失败')
  }
}

const toRegister = () => {
  console.log("点击去注册")
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url("https://picsum.photos/id/1039/1920/1080");
  background-size: cover;
  background-position: center;
}

.login-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
  width: 380px;
  text-align: center;
  position: relative;
  z-index: 10;
}

.login-card h2 {
  margin-bottom: 30px;
  color: #333;
}

.form-item {
  margin-bottom: 20px;
}

.form-item input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  box-sizing: border-box;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 10px;
}

.btn-login {
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  position: relative;
  z-index: 100;
  pointer-events: auto;
}

.btn-to-register {
  padding: 12px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  position: relative;
  z-index: 100;
  pointer-events: auto;
}
</style>