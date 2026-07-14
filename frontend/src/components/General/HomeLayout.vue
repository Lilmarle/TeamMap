<template>
  <div class="home-layout">
    <HomeHeader>
      <template #nav>
        <slot name="header-nav" />
      </template>
    </HomeHeader>
    <div class="home-body">
      <slot name="sidebar">
        <HomeTabs
          v-if="tabsConfig"
          :model-value="activeTab"
          :tabs="tabsConfig"
          @update:activeTab="handleTabChange"
        />
      </slot>
      <main class="content-area">
        <slot :activeTab="activeTab" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import HomeHeader from './HomeHeader.vue'
import HomeTabs from './HomeTabs.vue'

const props = defineProps({
  /** 当前激活的标签页（支持 v-model） */
  activeTab: { type: String, default: 'events' },
  /** 侧边栏菜单配置项数组：{ key, icon, label }，不传则不渲染 HomeTabs */
  tabs: { type: Array, default: null }
})

const emit = defineEmits(['update:activeTab'])

/** 用于传递给 HomeTabs 的配置 */
const tabsConfig = computed(() => props.tabs)

function handleTabChange(key) {
  emit('update:activeTab', key)
}
</script>

<style scoped>
.home-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.home-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.content-area {
  flex: 1;
  background: var(--color-bg-page, #f5f7fa);
  overflow-y: auto;
  padding: 20px;
}
</style>
