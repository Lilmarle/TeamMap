<template>
  <div class="team-taps">
    <el-tabs
      v-model="activeTab"
      type="border-card"
      class="taps-container"
      @tab-click="handleTabClick"
    >
      <el-tab-pane label="成员" name="members">
        <template #label>
          <span class="tab-label">
            <el-icon><UserFilled /></el-icon>
            <span>成员</span>
          </span>
        </template>
        <slot name="members" />
      </el-tab-pane>

      <el-tab-pane label="比赛" name="matches">
        <template #label>
          <span class="tab-label">
            <el-icon><Trophy /></el-icon>
            <span>比赛</span>
          </span>
        </template>
        <slot name="matches" />
      </el-tab-pane>

      <el-tab-pane label="训练" name="training">
        <template #label>
          <span class="tab-label">
            <el-icon><Clock /></el-icon>
            <span>训练</span>
          </span>
        </template>
        <slot name="training" />
      </el-tab-pane>

      <el-tab-pane label="数据" name="stats">
        <template #label>
          <span class="tab-label">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据</span>
          </span>
        </template>
        <slot name="stats" />
      </el-tab-pane>

      <el-tab-pane label="荣誉" name="honor">
        <template #label>
          <span class="tab-label">
            <el-icon><Medal /></el-icon>
            <span>荣誉</span>
          </span>
        </template>
        <slot name="honor" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { UserFilled, Trophy, Clock, DataAnalysis, Medal } from '@element-plus/icons-vue'

const props = defineProps({
  activeTab: { type: String, default: 'members' }
})

const emit = defineEmits(['update:activeTab'])

const activeTab = ref(props.activeTab)

function handleTabClick(tab) {
  activeTab.value = tab.props.name
  emit('update:activeTab', tab.props.name)
}
</script>

<style scoped>
.team-taps {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.taps-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.tab-label .el-icon {
  font-size: 16px;
}
</style>

<style>
.team-taps .el-tabs__content {
  flex: 1 !important;
  padding: 20px !important;
  overflow-y: auto !important;
}
</style>
