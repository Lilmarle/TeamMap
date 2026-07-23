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
            <!-- 加载中 -->
            <div v-if="teamLoading" class="loading-wrapper">
              <el-skeleton :rows="4" animated />
            </div>

            <!-- 已加入球队 → 展示球队详情 -->
            <HSMyTeam v-else-if="myTeam" :team="myTeam" @team-changed="handleTeamSwitch" />

            <!-- 未加入球队 → 展示可加入/创建球队 -->
            <HSJoinTeam v-else @joined="handleJoined" />
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
import { ref, computed, onMounted } from 'vue'
import HomeLayout from '@/components/General/HomeLayout.vue'
import HSTaps from '@/components/HomeSports/HSTaps.vue'
import HSMyTeam from '@/components/HomeSports/Team/HSMyTeam.vue'
import HSJoinTeam from '@/components/HomeSports/Team/HSJoinTeam.vue'
import { teamApi } from '@/api/team'

const activeTab = ref('events')

/** 球队加载状态 */
const teamLoading = ref(true)
/** 当前用户的所有球队成员记录 */
const memberships = ref([])
/** 所有球队信息 */
const allTeams = ref([])
/** 当前选中的球队ID */
const selectedTeamId = ref(null)

/** 角色名称映射 */
const ROLE_NAME_MAP = { 1: '队员', 2: '队长', 3: '教练', 4: '领队', 5: '创建者' }

/** 根据 selectedTeamId 计算当前展示的球队信息 */
const myTeam = computed(() => {
  if (!memberships.value.length || !selectedTeamId.value) return null

  const membership = memberships.value.find(m => m.teamId === selectedTeamId.value)
  if (!membership) return null

  const teamInfo = allTeams.value.find(t => t.teamId === membership.teamId)

  return {
    teamId: membership.teamId,
    teamName: teamInfo?.teamName || '',
    teamShortName: teamInfo?.teamShortName || '',
    teamLogo: teamInfo?.teamLogo || '',
    sportType: teamInfo?.sportType,
    sportTypeName: teamInfo?.sportTypeName || '',
    gender: teamInfo?.gender,
    teamGenderName: teamInfo?.genderName || '',
    teamDescription: teamInfo?.teamDescription || '',
    role: membership.role,
    roleName: ROLE_NAME_MAP[membership.role] || '未知',
    joinTime: membership.joinTime
  }
})

/**
 * 检查当前用户是否已加入球队，并初始化选中状态
 */
async function checkMyTeam() {
  teamLoading.value = true

  try {
    const [membershipRes, teamsRes] = await Promise.all([
      teamApi.getCurrentMembership(),
      teamApi.getAllTeams()
    ])
    memberships.value = membershipRes.data || []
    allTeams.value = teamsRes.data || []

    // 默认选中第一个球队
    if (memberships.value.length > 0 && !selectedTeamId.value) {
      selectedTeamId.value = memberships.value[0].teamId
    } else if (!memberships.value.length) {
      selectedTeamId.value = null
    }
  } catch (e) {
    console.warn('查询当前用户队伍信息失败:', e)
    memberships.value = []
    allTeams.value = []
    selectedTeamId.value = null
  } finally {
    teamLoading.value = false
  }
}

/** 处理球队切换 */
function handleTeamSwitch(teamId) {
  selectedTeamId.value = teamId
}

/** 加入/创建球队成功后的回调 */
function handleJoined() {
  selectedTeamId.value = null // 重置选中，让 checkMyTeam 重新初始化
  checkMyTeam()
}

onMounted(() => {
  checkMyTeam()
})
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

.loading-wrapper {
  padding: 40px 20px;
}
</style>
