<template>
  <nav class="home-tabs">
    <el-menu
      :default-active="activeTab"
      class="tabs-menu"
      @select="handleSelect"
    >
      <slot name="items">
        <el-menu-item
          v-for="tab in tabs"
          :key="tab.key"
          :index="tab.key"
        >
          <el-icon v-if="tab.icon">
            <component :is="tab.icon" />
          </el-icon>
          <span>{{ tab.label }}</span>
        </el-menu-item>
      </slot>
    </el-menu>
  </nav>
</template>

<script setup>
import { Trophy, UserFilled, User } from '@element-plus/icons-vue'

const props = defineProps({
  activeTab: {
    type: String,
    default: 'events'
  },
  /** 菜单项配置数组：{ key, icon, label } */
  tabs: {
    type: Array,
    default: () => [
      { key: 'events', icon: Trophy, label: '赛事' },
      { key: 'teams', icon: UserFilled, label: '球队' },
      { key: 'players', icon: User, label: '球员' }
    ]
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
