import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'
import { parseToken, getHomeRouteByRole } from '@/utils/jwt'

export const useUserStore = defineStore('user', () => {
  const savedToken = localStorage.getItem('token')
  const token = ref(savedToken || '')

  // 从 JWT token 中同步恢复基本用户信息（避免刷新后 user 为 null）
  const initialUser = savedToken ? (() => {
    const payload = parseToken(savedToken)
    if (payload) {
      return {
        id: Number(payload.sub),
        username: payload.username,
        role: payload.role
      }
    }
    return null
  })() : null
  const user = ref(initialUser)

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

  // 刷新页面后从 Token 恢复用户会话
  async function restoreSession() {
    const savedToken = localStorage.getItem('token')
    if (!savedToken) {
      return false
    }
    token.value = savedToken
    try {
      await getCurrentUser()
      return true
    } catch {
      // Token 过期或无效，清除登录状态
      logout()
      return false
    }
  }

  // 根据角色获取首页路由
  const homeRoute = computed(() => {
    if (!user.value) return '/'
    return getHomeRouteByRole(user.value.role)
  })

  return {
    user,
    token,
    login,
    register,
    getCurrentUser,
    changePassword,
    logout,
    restoreSession,
    homeRoute
  }
})
