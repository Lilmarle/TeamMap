<template>
  <HomeLayout>
    <template #default>
      <HSTaps v-model:activeTab="activeTab">
        <template #events>
          <div class="tab-content">
            <h3 class="tab-title">赛事</h3>
            <el-empty description="暂无赛事信息" />
          </div>
        </template>
        <template #teams>
          <div class="tab-content">
            <HSTools @team-changed="handleTeamChanged" />
            <TeamTaps v-model:activeTab="teamTab">
              <template #members>
                <HSTeamMembers v-if="selectedTeamId" :team-id="selectedTeamId" />
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
        <template #players>
          <div class="tab-content">
            <h3 class="tab-title">球员</h3>
            <el-empty description="暂无球员信息" />
          </div>
        </template>
      </HSTaps>
    </template>
  </HomeLayout>
</template>

<script setup>
import { ref } from 'vue'
import HomeLayout from '@/components/General/HomeLayout.vue'
import HSTaps from '@/components/HomeSports/HSTaps.vue'
import HSTools from '@/components/HomeSports/Team/HSTools.vue'
import TeamTaps from '@/components/HomeSports/Team/TeamTaps.vue'
import HSTeamMembers from '@/components/HomeSports/Team/HSTeamMembers.vue'

const activeTab = ref('events')
const teamTab = ref('members')
const selectedTeamId = ref(null)

/** 监听 HSTools 发出的球队切换事件 */
function handleTeamChanged(teamId) {
  selectedTeamId.value = teamId
}
</script>

<style scoped>
.tab-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tab-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border, #e4e7ed);
}

.tab-placeholder {
  padding: 20px 0;
}
</style>
