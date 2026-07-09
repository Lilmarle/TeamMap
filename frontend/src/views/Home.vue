<template>
  <div class="home">
    <el-container>
      <el-header class="header">
        <div class="logo" @click="$router.push('/')">TeamMap</div>
        <div class="nav">
          <template v-if="userStore.user">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.user.nickname || userStore.user.username }}
            </span>
            <el-button @click="$router.push('/change-password')">修改密码</el-button>
            <el-button type="primary" @click="handleLogout">退出登录</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </el-header>
      <el-main class="main">
        <div class="welcome">
          <h1>欢迎使用 TeamMap</h1>
          <p>团队协作地图 - 让团队协作更高效</p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.home {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
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
}

.main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome {
  text-align: center;
  color: var(--color-text-white);
}

.welcome h1 {
  font-size: 48px;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.welcome p {
  font-size: 18px;
  opacity: 0.9;
}
</style>
