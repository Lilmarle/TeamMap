<template>
  <div class="home">
    <!-- 首页路由入口：根据 JWT 角色信息自动重定向 -->
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserRole, ROLE_ROUTE_MAP } from '@/utils/jwt'

const router = useRouter()

onMounted(() => {
  const role = getUserRole()
  if (role && ROLE_ROUTE_MAP[role]) {
    // 已登录：根据角色跳转到对应的首页
    router.replace(ROLE_ROUTE_MAP[role])
  } else {
    // 未登录或角色未知：跳转到普通用户首页（显示登录/注册入口）
    router.replace('/home/normal')
  }
})
</script>

<style scoped>
.home {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
