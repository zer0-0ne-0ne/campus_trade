<template>
  <div class="profile-page">
    <div class="card">
      <h2 style="text-align:center; margin-bottom:25px;">个人资料</h2>

      <div class="form-item">
        <label>头像</label>
        <input
            type="file"
            ref="fileInput"
            accept="image/*"
            @change="handleFileChange"
            style="display:none"
        />
        <button
            class="btn-upload"
            type="button"
            @click="fileInput.click()"
        >
          {{ form.avatar ? '重新选择头像' : '选择头像' }}
        </button>
        <div v-if="form.avatar" style="margin-top:10px;">
          <img :src="form.avatar" style="width:120px;height:120px;object-fit:cover;border-radius:8px;">
        </div>
      </div>

      <div class="item">
        <label>用户名</label>
        <input v-model="form.username" />
      </div>

      <div class="item">
        <label>密码</label>
        <input
            type="password"
            v-model="form.password"
            :placeholder="'•'.repeat(form.passwordLength || 8)"
        />
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
        <button @click="save">保存修改</button>
        <button @click="back">返回</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const fileInput = ref(null) // 对应 ref="fileInput"

const form = ref({
  uid: '',
  identity: '',
  statusText: '',
  username: '',
  password: '',
  phone: '',
  email: '',
  avatar: '',
  passwordLength: 0
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

  const realPwd = localStorage.getItem('password') || ''
  form.value.passwordLength = realPwd.length
})

// 图片选择
const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (ev) => {
    form.value.avatar = ev.target.result
  }
  reader.readAsDataURL(file)
}

// 保存
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

    const res = await axios.put('http://localhost:8080/user/update', data)

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
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.1);
}
.card{
  background:#fff;
  padding:45px;
  border-radius:12px;
  width:480px;
  box-shadow:0 2px 12px rgba(0,0,0,0.08);
}
.form-item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}
.form-item label {
  width: 95px;
  font-size: 14px;
  color: #333;
}
.btn-upload {
  padding: 8px 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.item{
  margin-bottom:22px;
  display:flex;
  align-items:center;
}
label{
  width:95px;
  font-size:14px;
  color:#333;
}
input{
  flex:1;
  padding:10px 14px;
  border:1px solid #dcdfe6;
  border-radius:6px;
}
input:disabled{
  background:#f5f7fa;
  color:#909399;
}
.btns{
  display:flex;
  gap:15px;
  margin-top:35px;
}
button{
  flex:1;
  padding:12px;
  border-radius:6px;
  border:none;
  background:#409eff;
  color:#fff;
  cursor:pointer;
}
</style>