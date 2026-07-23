<template>
  <div class="hs-my-team">
    <!-- 工具条：切换球队 + 邀请 + 修改个人信息 -->
    <HSTools @team-changed="handleTeamSwitch" />

    <!-- 横向标签页：成员、比赛、训练、数据、荣誉 -->
    <TeamTaps v-model:activeTab="activeTab">
      <template #members>
        <HSTeamMembers :team-id="displayTeam?.teamId" />
      </template>

      <template #matches>
        <div class="tab-placeholder">
          <el-empty description="比赛记录" />
        </div>
      </template>

      <template #training>
        <div class="tab-placeholder">
          <el-empty description="训练安排" />
        </div>
      </template>

      <template #stats>
        <div class="tab-placeholder">
          <el-empty description="数据统计" />
        </div>
      </template>

      <template #honor>
        <div class="tab-placeholder">
          <el-empty description="荣誉奖杯" />
        </div>
      </template>
    </TeamTaps>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import HSTools from './HSTools.vue'
import TeamTaps from './TeamTaps.vue'
import HSTeamMembers from './HSTeamMembers.vue'

const props = defineProps({
  /** 当前用户的球队信息（PlayerInfoVO） */
  team: { type: Object, default: null }
})

const emit = defineEmits(['team-changed'])

const activeTab = ref('members')

/** 本地展示的球队信息，由 team prop 初始化，后续由 HSTools 切换事件更新 */
const displayTeam = ref(null)

/** 同步外部 team prop 变化 */
watch(() => props.team, (newTeam) => {
  displayTeam.value = newTeam
}, { immediate: true })

/** 处理 HSTools 发出的球队切换事件 */
function handleTeamSwitch(teamId) {
  // 向上通知父组件切换球队，由父组件重新计算 team 数据传下来
  emit('team-changed', teamId)
}
</script>

<style scoped>
.hs-my-team {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tab-placeholder {
  padding: 20px 0;
}
</style>
