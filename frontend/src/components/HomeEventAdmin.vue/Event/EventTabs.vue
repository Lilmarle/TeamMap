<template>
  <div class="event-tabs">
    <el-tabs
      v-model="activeTab"
      type="border-card"
      class="tabs-container"
      @tab-click="handleTabClick"
    >
      <el-tab-pane
        label="参与球队"
        name="teams"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><UserFilled /></el-icon>
            <span>参与球队</span>
          </span>
        </template>
        <!-- 参与球队内容插槽 -->
        <slot name="teams" />
      </el-tab-pane>

      <el-tab-pane
        label="小组赛"
        name="group"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><Grid /></el-icon>
            <span>小组赛</span>
          </span>
        </template>
        <!-- 小组赛内容插槽 -->
        <slot name="group" />
      </el-tab-pane>

      <el-tab-pane
        label="淘汰赛"
        name="knockout"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><Medal /></el-icon>
            <span>淘汰赛</span>
          </span>
        </template>
        <!-- 淘汰赛内容插槽 -->
        <slot name="knockout" />
      </el-tab-pane>

      <el-tab-pane
        label="赛程"
        name="schedule"
      >
        <template #label>
          <span class="tab-label">
            <el-icon><Calendar /></el-icon>
            <span>赛程</span>
          </span>
        </template>
        <!-- 赛程内容插槽 -->
        <slot name="schedule" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { UserFilled, Grid, Medal, Calendar } from '@element-plus/icons-vue'
import { ElTabs } from 'element-plus'

const props = defineProps({
  /** 当前激活的标签页名称 */
  activeTab: {
    type: String,
    default: 'teams'
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
.event-tabs {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tabs-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.tabs-container :deep(.el-tabs__content) {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 500;
}

.tab-label .el-icon {
  font-size: 18px;
}
</style>
