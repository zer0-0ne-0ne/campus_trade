<template>
  <div class="profile-page">
    <div class="container">
      <div class="back-bar">
        <button class="back-btn" @click="back">← 返回</button>
        <h2>个人资料</h2>
      </div>

      <div class="card">
        <div class="form-item">
          <label>头像</label>
          <input
              type="file"
              ref="fileInput"
              accept="image/*"
              @change="handleFileChange"
              style="display:none"
          />
          <button class="btn-upload" type="button" @click="fileInput.click()">
            {{ form.avatar ? '重新选择头像' : '选择头像' }}
          </button>
          <div v-if="form.avatar" class="avatar-preview">
            <img :src="form.avatar" alt="头像">
          </div>
        </div>

        <div class="item">
          <label>用户名</label>
          <input v-model="form.username" />
        </div>

        <div class="item">
          <label>密码</label>
          <input type="password" v-model="form.password" placeholder="留空则不修改" />
        </div>

        <div class="item">
          <label>手机号</label>
          <input v-model="form.phone" />
        </div>

        <div class="item">
          <label>邮箱</label>
          <input v-model="form.email" />
        </div>

        <div class="item">
          <label>UID</label>
          <input disabled v-model="form.uid" placeholder="无" />
        </div>

        <div class="item">
          <label>身份</label>
          <input disabled v-model="form.identity" placeholder="无" />
        </div>

        <div class="item">
          <label>账号状态</label>
          <input disabled v-model="form.statusText" placeholder="无" />
        </div>

        <div class="btns">
          <button class="btn-save" @click="save">保存修改</button>
          <button class="btn-cancel" @click="back">返回</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const fileInput = ref(null)

const form = ref({
  uid: '',
  identity: '',
  statusText: '',
  username: '',
  password: '',
  phone: '',
  email: '',
  avatar: ''
})

onMounted(() => {
  form.value.uid = localStorage.getItem('uid') || ''
  form.value.identity = localStorage.getItem('identity') || ''
  form.value.username = localStorage.getItem('username') || ''
  form.value.phone = localStorage.getItem('phone') || ''
  form.value.email = localStorage.getItem('email') || ''
  form.value.avatar = localStorage.getItem('avatar') || ''

  const status = localStorage.getItem('status') || '0'
  form.value.statusText = status == '0' ? '正常' : '禁用'
})

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (ev) => {
    form.value.avatar = ev.target.result
  }
  reader.readAsDataURL(file)
}

const save = async () => {
  try {
    const data = {
      uid: form.value.uid,
      username: form.value.username,
      password: form.value.password || '',
      phone: form.value.phone || '',
      email: form.value.email || '',
      avatar: form.value.avatar || ''
    }

    const res = await request.put('/user/update', data)

    if (res.data === 'ok') {
      ElMessage.success('保存成功！')
      localStorage.setItem('username', form.value.username)
      localStorage.setItem('phone', form.value.phone)
      localStorage.setItem('email', form.value.email)
      localStorage.setItem('avatar', form.value.avatar)
    } else {
      ElMessage.error('保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const back = () => {
  router.back()
}
</script>

<style scoped>
.profile-page {
  padding: 20px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.container {
  max-width: 600px;
  margin: 0 auto;
  width: 100%;
}
.back-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}
.back-bar h2 {
  color: var(--text);
  margin: 0;
}
.back-btn {
  padding: 6px 12px;
  border: 1px solid var(--line);
  border-radius: 6px;
  background: var(--card);
  cursor: pointer;
  color: var(--text);
}

.card {
  background: var(--card);
  padding: 35px 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px var(--shadow);
}

.form-item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}
.form-item label {
  width: 95px;
  font-size: 14px;
  color: var(--text);
  flex-shrink: 0;
}
.btn-upload {
  padding: 8px 16px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.avatar-preview {
  margin-top: 10px;
}
.avatar-preview img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}
.item label {
  width: 95px;
  font-size: 14px;
  color: var(--text);
  flex-shrink: 0;
}
.item input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--input-border);
  border-radius: 6px;
  background: var(--card);
  color: var(--text);
}
.item input:disabled {
  background: var(--input-bg-disabled);
  color: var(--text-muted);
}

.btns {
  display: flex;
  gap: 15px;
  margin-top: 35px;
}
.btn-save {
  flex: 1;
  padding: 12px;
  border-radius: 6px;
  border: none;
  background: var(--primary);
  color: #fff;
  cursor: pointer;
}
.btn-cancel {
  flex: 1;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid var(--line);
  background: var(--card);
  color: var(--text);
  cursor: pointer;
}

@media (max-width: 768px) {
  .card {
    padding: 20px;
  }
  .form-item,
  .item {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  .form-item label,
  .item label {
    width: auto;
  }
  .item input {
    width: 100%;
  }
}
</style>
