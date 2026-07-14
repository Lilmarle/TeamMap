<template>
  <el-header class="home-header">
    <div class="header-left">
      <slot name="logo">
        <div class="logo" @click="goHome">TeamMap</div>
      </slot>
      <slot name="header-prepend" />
    </div>
    <div class="nav">
      <slot name="header-append" />
      <slot name="nav">
        <template v-if="userStore.user">
          <span class="user-info" @click="goProfile">
            <el-avatar
              :size="32"
              :src="avatar"
              class="user-avatar"
            >
              <el-icon><User /></el-icon>
            </el-avatar>
            <span v-ellipsis-tooltip class="user-name">{{ nickname || userStore.user.username }}</span>
          </span>
          <el-button @click="goChangePassword">修改密码</el-button>
          <el-button type="primary" @click="handleLogout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="goLogin">登录</el-button>
          <el-button @click="goRegister">注册</el-button>
        </template>
      </slot>
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const nickname = ref('')
const avatar = ref('')

onMounted(async () => {
  if (userStore.user?.id) {
    try {
      const res = await userApi.getUserProfile(userStore.user.id)
      if (res.data) {
        nickname.value = res.data.nickname || ''
        avatar.value = res.data.avatar || ''
      }
    } catch {
      // 静默失败，使用默认显示
    }
  }
})

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

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
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
  gap: 8px;
  margin-right: 12px;
  color: var(--color-text-primary);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.user-info:hover {
  color: var(--color-primary);
}

.user-avatar {
  flex-shrink: 0;
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
