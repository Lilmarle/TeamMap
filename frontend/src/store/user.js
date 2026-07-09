import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  // 登录
  async function login(loginData) {
    const res = await userApi.login(loginData)
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', token.value)
    return res
  }

  // 注册
  async function register(registerData) {
    return await userApi.register(registerData)
  }

  // 获取当前用户
  async function getCurrentUser() {
    const res = await userApi.getCurrentUser()
    user.value = res.data
    return res
  }

  // 修改密码
  async function changePassword(passwordData) {
    return await userApi.changePassword(passwordData)
  }

  // 退出登录
  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    login,
    register,
    getCurrentUser,
    changePassword,
    logout
  }
})
