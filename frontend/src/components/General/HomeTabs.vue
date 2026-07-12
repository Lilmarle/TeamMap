<template>
  <nav class="home-tabs">
    <el-menu
      :default-active="activeTab"
      class="tabs-menu"
      @select="handleSelect"
    >
      <el-menu-item index="events">
        <el-icon><Trophy /></el-icon>
        <span>赛事</span>
      </el-menu-item>
      <el-menu-item index="teams">
        <el-icon><UserFilled /></el-icon>
        <span>球队</span>
      </el-menu-item>
      <el-menu-item index="players">
        <el-icon><User /></el-icon>
        <span>球员</span>
      </el-menu-item>
    </el-menu>
  </nav>
</template>

<script setup>
import { Trophy, UserFilled, User } from '@element-plus/icons-vue'

const props = defineProps({
  activeTab: {
    type: String,
    default: 'events'
  }
})

const emit = defineEmits(['update:activeTab'])

function handleSelect(index) {
  emit('update:activeTab', index)
}
</script>

<style scoped>
.home-tabs {
  width: 200px;
  min-width: 200px;
  background: var(--color-bg-white);
  border-right: 1px solid var(--color-border);
  padding-top: 16px;
  overflow-y: auto;
}

.tabs-menu {
  border-right: none;
}

.tabs-menu .el-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 56px;
  font-size: 16px;
  color: var(--color-text-primary);
}

.tabs-menu .el-menu-item.is-active {
  color: var(--color-primary);
  background: var(--color-primary-bg);
  border-right: 3px solid var(--color-primary);
}

.tabs-menu .el-menu-item:hover {
  background: var(--color-primary-bg);
}
</style>
