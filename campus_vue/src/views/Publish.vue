<template>
  <div class="publish-page">
    <div class="container">
      <div class="back-bar">
        <button class="btn-back" @click="goBack">← 返回</button>
      </div>

      <div class="publish-card">
        <h2 class="card-title">发布商品</h2>
        <form class="publish-form">
          <div class="form-item">
            <label class="required">名称</label>
            <input type="text" class="form-input" placeholder="请输入商品名称" v-model="form.title">
          </div>

          <div class="form-item">
            <label class="required">价格</label>
            <input type="number" class="form-input" placeholder="请输入价格" v-model="form.price">
          </div>

          <div class="form-item">
            <label>图片</label>
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
              {{ imageUrl ? '重新选择图片' : '选择图片' }}
            </button>
            <div v-if="imageUrl" class="preview-img">
              <img :src="imageUrl" alt="预览">
            </div>
          </div>

          <div class="form-item">
            <label>上架状态</label>
            <select class="form-select" v-model="form.status">
              <option value="">请选择</option>
              <option value="1">上架</option>
              <option value="0">下架</option>
            </select>
          </div>

          <div class="form-item">
            <label>分类</label>
            <select class="form-select" v-model="form.cid">
              <option value="">请选择</option>
              <option v-for="cat in categoryList" :key="cat.cid" :value="cat.cid">
                {{ cat.categoryName }}
              </option>
            </select>
          </div>

          <div class="form-item form-textarea">
            <label>详情</label>
            <textarea class="form-input" rows="5" placeholder="请输入商品详情" v-model="form.description"></textarea>
          </div>

          <div class="form-item form-submit">
            <button type="button" class="btn-submit" @click="handleSubmit">确认发布</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const categoryList = ref([])

const form = ref({
  title: '',
  price: 0,
  imagePath: '',
  status: 1,
  cid: '',
  description: '',
  condition: '全新',
  keywords: '',
  releaseTime: new Date()
})

onMounted(async () => {
  try {
    const res = await request.get('/category/list')
    categoryList.value = res.data
  } catch (e) {}
})

const goBack = () => {
  router.back()
}

const handleSubmit = async () => {
  if (!form.value.title || form.value.price <= 0 || !form.value.cid || !form.value.description) {
    ElMessage.warning('请填写完整必填项！')
    return
  }

  const imgUrl = await uploadImage()

  const submitData = {
    ...form.value,
    imagePath: imgUrl,
    uid: localStorage.getItem('uid')
  }

  try {
    const res = await request.post('/product/add', submitData)
    if (res.data === '发布成功') {
      ElMessage.success('发布成功！')
      router.push('/')
    } else {
      ElMessage.error(res.data)
    }
  } catch (err) {
    ElMessage.error('发布失败')
  }
}

const fileInput = ref(null)
const imageUrl = ref('')
let selectedFile = null

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  selectedFile = file
  imageUrl.value = URL.createObjectURL(file)
}

const uploadImage = async () => {
  if (!selectedFile) return ''

  const formData = new FormData()
  formData.append('file', selectedFile)

  try {
    const res = await request.post('/product/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    ElMessage.success('图片上传成功')
    return res.data.url
  } catch (err) {
    ElMessage.error('图片上传失败')
    return ''
  }
}
</script>

<style scoped>
.publish-page {
  padding: 20px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.container {
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}
.back-bar {
  margin-bottom: 15px;
}
.btn-back {
  padding: 6px 14px;
  border: 1px solid var(--line);
  background: var(--card);
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  color: var(--text);
}

.publish-card {
  background: var(--card);
  padding: 40px 60px;
  border-radius: 12px;
  box-shadow: 0 2px 10px var(--shadow);
}
.card-title {
  text-align: center;
  font-size: 26px;
  margin: 0 0 35px;
  color: var(--text);
}

.publish-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.form-item {
  display: flex;
  align-items: center;
  gap: 16px;
}
.form-item label {
  width: 90px;
  font-size: 15px;
  color: var(--text);
  font-weight: 500;
  flex-shrink: 0;
}
.form-item .required::before {
  content: "*";
  color: var(--danger);
  margin-right: 4px;
}
.form-input {
  flex: 1;
  padding: 12px 14px;
  border: 1px solid var(--input-border);
  border-radius: 8px;
  font-size: 15px;
  background: var(--card);
  color: var(--text);
}
.form-select {
  flex: 1;
  padding: 12px 14px;
  border: 1px solid var(--input-border);
  border-radius: 8px;
  background: var(--card);
  font-size: 15px;
  color: var(--text);
}
.btn-upload {
  background-color: var(--primary);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}
.preview-img {
  margin-top: 10px;
}
.preview-img img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.form-textarea {
  align-items: flex-start;
}
.form-textarea .form-input {
  resize: vertical;
}

.form-submit {
  justify-content: center;
  margin-top: 15px;
}
.btn-submit {
  background-color: var(--primary);
  color: white;
  border: none;
  padding: 14px 50px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: 0.2s;
}
.btn-submit:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .publish-card {
    padding: 20px;
  }
  .form-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  .form-item label {
    width: auto;
  }
  .form-input,
  .form-select {
    width: 100%;
  }
}
</style>
