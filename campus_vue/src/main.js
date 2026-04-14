import './assets/main.css'

import { createApp } from 'vue'
//import './style.css'
import App from './App.vue'

// 路由
import router from './router'

// Element Plus 组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 创建应用
const app = createApp(App)

// 挂载插件
app.use(router)
app.use(ElementPlus)

// 挂载到页面
app.mount('#app')
