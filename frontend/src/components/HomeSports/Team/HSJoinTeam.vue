<template>
  <div class="hs-join-team">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="4" animated />
    </div>

    <!-- 加载失败 -->
    <el-alert
      v-else-if="loadFailed"
      :title="errorMessage || '加载失败'"
      type="error"
      show-icon
      closable
      @close="loadFailed = false"
    >
      <template #actions>
        <el-button size="small" type="primary" @click="init">重试</el-button>
      </template>
    </el-alert>

    <!-- 未加入球队 → 直接展示可加入的球队列表 -->
    <template v-else>
      <div class="section-header">
        <h4 class="section-title">可加入的球队</h4>
        <el-button type="success" size="small" :icon="Plus" @click="showCreatePanel = true">
          创建球队
        </el-button>
      </div>

      <!-- 可加入球队列表 -->
      <div v-if="teamsLoading" class="loading-wrapper">
        <el-skeleton :rows="4" animated />
      </div>

      <el-empty
        v-else-if="availableTeams.length === 0"
        description="暂无可用球队，你可以创建一个"
      />

      <div v-else class="team-list">
        <el-card
          v-for="team in availableTeams"
          :key="team.teamId"
          class="team-item-card"
          shadow="hover"
        >
          <div class="team-item">
            <el-avatar :size="48" :src="team.teamLogo">
              {{ team.teamName?.charAt(0) }}
            </el-avatar>
            <div class="team-item-info">
              <div class="team-item-name">
                <span class="name">{{ team.teamName }}</span>
                <el-tag v-if="team.teamShortName" size="small" type="info" effect="plain">
                  {{ team.teamShortName }}
                </el-tag>
              </div>
              <div class="team-item-meta">
                <el-tag :type="getSportTagType(team.sportType)" size="small">
                  {{ team.sportTypeName }}
                </el-tag>
                <span class="gender-tag">{{ team.genderName || getGenderName(team.gender) }}</span>
                <span v-if="team.teamDescription" class="desc-text">{{ team.teamDescription }}</span>
              </div>
            </div>
            <el-button
              type="primary"
              size="small"
              :loading="joiningTeamId === team.teamId"
              :icon="UserFilled"
              @click="handleJoinTeam(team)"
            >
              加入
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 创建球队对话框 -->
      <HSCreateTeam
        v-model:visible="showCreatePanel"
        @success="handleCreateSuccess"
      />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserFilled, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { teamApi } from '@/api/team'
import { useUserStore } from '@/store/user'
import HSCreateTeam from './HSCreateTeam.vue'

const userStore = useUserStore()
const emit = defineEmits(['joined'])

/** 运动类型标签颜色映射 */
const SPORT_TAG_MAP = { 1: '', 2: 'success', 3: 'warning' }

/** 加载状态 */
const loading = ref(true)
const loadFailed = ref(false)
const errorMessage = ref('')

/** 可选球队列表 */
const availableTeams = ref([])
const teamsLoading = ref(false)
const joiningTeamId = ref(null)

/** 创建球队对话框 */
const showCreatePanel = ref(false)

/**
 * 初始化：加载可用球队列表
 */
async function init() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    await loadAvailableTeams()
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

/**
 * 加载所有球队
 */
async function loadAvailableTeams() {
  teamsLoading.value = true
  try {
    const res = await teamApi.getAllTeams()
    availableTeams.value = res.data || []
  } catch (e) {
    ElMessage.error(e.message || '获取球队列表失败')
    availableTeams.value = []
  } finally {
    teamsLoading.value = false
  }
}

/**
 * 加入球队
 */
async function handleJoinTeam(team) {
  if (!team.teamId) return

  const currentUser = userStore.user
  if (!currentUser?.id) {
    ElMessage.warning('请先登录')
    return
  }

  joiningTeamId.value = team.teamId
  try {
    // POST /api/team-members { teamId, userId, role: 1(队员) }
    await teamApi.joinTeam({
      teamId: team.teamId,
      userId: currentUser.id,
      role: 1
    })
    ElMessage.success(`已加入球队「${team.teamName}」`)

    // 通知父组件重新检查球队状态
    emit('joined')
  } catch (e) {
    ElMessage.error(e.message || '加入球队失败')
  } finally {
    joiningTeamId.value = null
  }
}

/**
 * 创建球队成功回调
 */
function handleCreateSuccess() {
  showCreatePanel.value = false
  ElMessage.success('球队创建成功，即将刷新')
  emit('joined')
}

function getSportTagType(type) {
  return SPORT_TAG_MAP[type] || ''
}

function getSportTypeName(type) {
  const map = { 1: '足球', 2: '篮球', 3: '排球' }
  return map[type] || '未知'
}

function getGenderName(gender) {
  const map = { 0: '混合', 1: '男子', 2: '女子' }
  return map[gender] || ''
}

onMounted(() => {
  init()
})
</script>

<style scoped>
.hs-join-team {
  height: 100%;
}

.loading-wrapper {
  padding: 40px 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  margin: 0;
}

/* 可加入球队列表 */
.team-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.team-item-card {
  cursor: default;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.team-item-info {
  flex: 1;
  min-width: 0;
}

.team-item-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.team-item-name .name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.team-item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.gender-tag {
  font-size: 12px;
  color: var(--color-text-secondary, #606266);
}

.desc-text {
  font-size: 12px;
  color: var(--color-text-placeholder, #c0c4cc);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}
</style>
