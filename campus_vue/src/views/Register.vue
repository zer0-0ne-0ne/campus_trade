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

      <!-- 身份选择 -->
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
import axios from 'axios'
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
    const res = await axios.post('http://localhost:8080/user/register', {
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
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url("https://picsum.photos/1920/1080?random=1");
  background-size: cover;
  background-position: center;
  pointer-events: none;
}

.register-card {
  background: rgba(255, 255, 255, 0.9);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  width: 380px;
  text-align: center;
  pointer-events: auto;
}

.form-item {
  margin-bottom: 20px;
}

.form-item input,
.select-input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 14px;
  color: #666;
  background-color: white;
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
  background: #67c23a;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.btn-back {
  padding: 12px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
}
</style>

<style>
.el-message {
  z-index: 9999999 !important;
}
</style>