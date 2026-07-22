<template>
  <div class="hs-tabs">
    <el-tabs
      v-model="activeTab"
      tab-position="left"
      class="tabs-container"
      @tab-click="handleTabClick"
    >
      <el-tab-pane
        label="赛事"
        name="events"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><Trophy /></el-icon>
            <span>赛事</span>
          </span>
        </template>
        <slot name="events" />
      </el-tab-pane>

      <el-tab-pane
        label="球队"
        name="teams"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><UserFilled /></el-icon>
            <span>球队</span>
          </span>
        </template>
        <slot name="teams" />
      </el-tab-pane>

      <el-tab-pane
        label="球员"
        name="players"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><Avatar /></el-icon>
            <span>球员</span>
          </span>
        </template>
        <slot name="players" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Trophy, UserFilled, Avatar } from '@element-plus/icons-vue'

const props = defineProps({
  /** 当前激活的标签页名称 */
  activeTab: {
    type: String,
    default: 'events'
  }
})

const emit = defineEmits(['update:activeTab'])

const activeTab = ref(props.activeTab)

/**
 * 标签页切换事件处理
 */
function handleTabClick(tab) {
  activeTab.value = tab.props.name
  emit('update:activeTab', tab.props.name)
}
</script>

<style scoped>
.hs-tabs {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tabs-container {
  flex: 1;
  display: flex;
  min-height: 0;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  padding: 4px 0;
}

.tab-label .el-icon {
  font-size: 18px;
}
</style>

<style>
.hs-tabs .el-tabs__content {
  flex: 1 !important;
  padding: 20px !important;
  overflow-y: auto !important;
}

.hs-tabs .el-tabs__header {
  margin-right: 0 !important;
}

.hs-tabs .el-tabs__nav-wrap {
  padding: 12px 0;
}

.hs-tabs .el-tabs__item {
  height: auto !important;
  line-height: 1.5 !important;
  padding: 12px 20px !important;
}
</style>
