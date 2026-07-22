<template>
  <div class="hs-my-team">
    <!-- 球队信息头部 -->
    <div class="team-header">
      <el-avatar :size="56" :src="team?.teamLogo">
        {{ team?.teamName?.charAt(0) }}
      </el-avatar>
      <div class="team-header-info">
        <h3 class="team-name">{{ team?.teamName }}</h3>
        <div class="team-meta">
          <el-tag v-if="team?.teamShortName" size="small" type="info">
            {{ team.teamShortName }}
          </el-tag>
          <el-tag :type="getSportTagType(team?.sportType)" size="small">
            {{ team?.sportTypeName }}
          </el-tag>
          <el-tag v-if="team?.teamGenderName" size="small">
            {{ team.teamGenderName }}
          </el-tag>
          <el-tag type="warning" effect="dark" size="small">
            {{ team?.roleName }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 横向标签页：详情、成员、比赛、训练、数据、荣誉 -->
    <TeamTaps v-model:activeTab="activeTab">
      <template #members>
        <HSTeamMembers :team-id="team?.teamId" />
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
import { ref } from 'vue'
import TeamTaps from './TeamTaps.vue'
import HSTeamMembers from './HSTeamMembers.vue'

const props = defineProps({
  /** 当前用户的球队信息（PlayerInfoVO） */
  team: { type: Object, default: null }
})

/** 运动类型标签颜色映射 */
const SPORT_TAG_MAP = { 1: '', 2: 'success', 3: 'warning' }

const activeTab = ref('detail')

function getSportTagType(type) {
  return SPORT_TAG_MAP[type] || ''
}
</script>

<style scoped>
.hs-my-team {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.team-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: var(--color-bg-white, #fff);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.team-header-info {
  flex: 1;
  min-width: 0;
}

.team-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 6px 0;
  color: var(--color-text-primary, #303133);
}

.team-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tab-placeholder {
  padding: 20px 0;
}
</style>
