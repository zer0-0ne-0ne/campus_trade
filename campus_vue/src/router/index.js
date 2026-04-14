import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Orders from '../views/Orders.vue'
import Publish from '../views/Publish.vue'
import User from '../views/User.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import MyPublish from '../views/MyPublish.vue'
import MyCollect from '../views/MyCollect.vue'
import Settings from '../views/Settings.vue'
import ProductDetail from '../views/ProductDetail.vue'

const routes = [
    { path: '/', component: Home },
    { path: '/category', component: Orders },
    { path: '/publish', component: Publish },
    { path: '/user', component: User },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/profile', component: Profile },
    { path: '/my-publish', component: MyPublish },
    { path: '/my-collect', component: MyCollect },
    { path: '/settings', component: Settings },
    { path: '/product/:pid', component: ProductDetail }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    //白名单数组
    const whiteList = ['/login', '/register']

    if (whiteList.includes(to.path)) {
        next()
        return
    }

    if (token) {
        next()
    } else {
        next('/login')
    }
})

export default router