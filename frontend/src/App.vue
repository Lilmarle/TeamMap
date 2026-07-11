<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(async () => {
  // 页面刷新时，尝试从 localStorage 中的 token 恢复用户会话
  await userStore.restoreSession()
})
</script>

<style>
@import './styles/variables.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: var(--color-text-primary);
  background-color: var(--color-bg-page);
}

#app {
  height: 100%;
}

/* 全局链接样式 */
a {
  color: var(--color-primary);
  text-decoration: none;
  transition: color 0.3s ease;
}

a:hover {
  color: var(--color-primary-dark);
}

/* 全局输入框聚焦样式覆盖 */
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--color-border-focus) inset !important;
}

/* Element Plus 按钮样式覆盖 - 主按钮使用渐变 */
.el-button--primary {
  background: linear-gradient(135deg, var(--color-gradient-start), var(--color-gradient-end)) !important;
  border: none !important;
  color: #fff !important;
  transition: opacity 0.3s ease !important;
}

.el-button--primary:hover {
  opacity: 0.9;
}

.el-button--primary:active {
  opacity: 0.8;
}
</style>
