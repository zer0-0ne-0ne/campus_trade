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
import AIChat from '../views/AIChat.vue'

const routes = [
    { path: '/', component: Home },
    { path: '/orders', component: Orders },
    { path: '/publish', component: Publish },
    { path: '/user', component: User },
    { path: '/login', component: Login, meta: { hideNav: true } },
    { path: '/register', component: Register, meta: { hideNav: true } },
    { path: '/profile', component: Profile },
    { path: '/my-publish', component: MyPublish },
    { path: '/my-collect', component: MyCollect },
    { path: '/settings', component: Settings },
    { path: '/product/:pid', component: ProductDetail },
    { path: '/ai-chat', component: AIChat }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const uid = localStorage.getItem('uid')
    const token = localStorage.getItem('token')
    const whiteList = ['/login', '/register']

    if (whiteList.includes(to.path)) {
        if (uid && token) {
            next('/')
            return
        }
        next()
        return
    }

    if (uid && token) {
        next()
    } else {
        next('/login')
    }
})

export default router
