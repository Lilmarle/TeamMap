import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/home/normal',
    name: 'HomeNormal',
    component: () => import('@/views/home/HomeNormal.vue'),
    meta: { title: '首页 - 普通球员' }
  },
  {
    path: '/home/sports',
    name: 'HomeSports',
    component: () => import('@/views/home/HomeSports.vue'),
    meta: { title: '首页 - 体育用户' }
  },
  {
    path: '/home/event-admin',
    name: 'HomeEventAdmin',
    component: () => import('@/views/home/HomeEventAdmin.vue'),
    meta: { title: '首页 - 赛事管理' }
  },
  {
    path: '/home/system-admin',
    name: 'HomeSystemAdmin',
    component: () => import('@/views/home/HomeSystemAdmin.vue'),
    meta: { title: '首页 - 系统管理' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: () => import('@/views/ChangePassword.vue'),
    meta: { title: '修改密码' }
  },
  {
    path: '/my-profile',
    name: 'MyProfile',
    component: () => import('@/views/MyProfile.vue'),
    meta: { title: '个人信息' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - TeamMap` : 'TeamMap'
  next()
})

export default router
