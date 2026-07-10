<template>
  <el-header class="home-header">
    <div class="logo" @click="goHome">TeamMap</div>
    <div class="nav">
      <template v-if="userStore.user">
        <span class="user-info" @click="goProfile">
          <el-icon><User /></el-icon>
          {{ userStore.user.nickname || userStore.user.username }}
        </span>
        <el-button @click="goChangePassword">修改密码</el-button>
        <el-button type="primary" @click="handleLogout">退出登录</el-button>
      </template>
      <template v-else>
        <el-button type="primary" @click="goLogin">登录</el-button>
        <el-button @click="goRegister">注册</el-button>
      </template>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

function goHome() {
  router.push('/')
}

function goLogin() {
  router.push('/login')
}

function goRegister() {
  router.push('/register')
}

function goProfile() {
  router.push('/my-profile')
}

function goChangePassword() {
  router.push('/change-password')
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--color-bg-white);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: var(--color-primary);
  cursor: pointer;
  user-select: none;
}

.user-info {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-right: 12px;
  color: var(--color-text-primary);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.user-info:hover {
  color: var(--color-primary);
}
</style>
