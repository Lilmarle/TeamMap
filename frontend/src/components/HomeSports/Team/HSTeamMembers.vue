<template>
  <div class="hs-team-members">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 加载失败 -->
    <el-alert
      v-else-if="loadFailed"
      :title="errorMessage || '加载成员列表失败'"
      type="error"
      show-icon
      closable
      @close="loadFailed = false"
    >
      <template #actions>
        <el-button size="small" type="primary" @click="loadMembers">重试</el-button>
      </template>
    </el-alert>

    <!-- 空列表 -->
    <el-empty
      v-else-if="members.length === 0"
      description="暂无成员"
    />

    <!-- 成员列表 -->
    <div v-else class="members-list">
      <el-card
        v-for="member in members"
        :key="member.memberId || member.id"
        class="member-card"
        shadow="hover"
      >
        <div class="member-item">
          <el-avatar :size="48" :src="member.portraitPhoto">
            {{ getMemberInitial(member) }}
          </el-avatar>
          <div class="member-info">
            <div class="member-name-row">
              <!-- 有球员记录：显示球衣名；仅成员记录：显示用户ID -->
              <span class="member-name">
                {{ member.jerseyName || `用户#${member.userId}` }}
              </span>
              <el-tag
                :type="getRoleTagType(member.role)"
                size="small"
                effect="dark"
              >
                {{ member.roleName || getRoleName(member.role) }}
              </el-tag>
            </div>
            <div class="member-details">
              <template v-if="member.jerseyNumber">
                <span class="detail-item">#{{ member.jerseyNumber }}</span>
                <el-divider direction="vertical" />
              </template>
              <template v-if="member.position">
                <span class="detail-item">{{ member.position }}</span>
                <el-divider direction="vertical" />
              </template>
              <span class="detail-item">
                {{ member.joinTime ? formatTime(member.joinTime) : '' }}
              </span>
            </div>
          </div>
          <!-- 状态标签 -->
          <el-tag
            v-if="member.playerStatus !== undefined"
            :type="member.playerStatus === 1 ? 'success' : 'danger'"
            size="small"
          >
            {{ member.playerStatusName || (member.playerStatus === 1 ? '可出战' : '禁赛') }}
          </el-tag>
          <el-tag
            v-else-if="member.status !== undefined"
            :type="member.status === 2 ? 'success' : member.status === 1 ? 'warning' : 'info'"
            size="small"
          >
            {{ getMemberStatusName(member.status) }}
          </el-tag>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { teamApi } from '@/api/team'

const props = defineProps({
  /** 球队ID */
  teamId: { type: Number, required: true }
})

const loading = ref(true)
const loadFailed = ref(false)
const errorMessage = ref('')
const members = ref([])

const ROLE_TAG_MAP = { 1: '', 2: 'warning', 3: 'success', 4: 'info', 5: 'danger' }

async function loadMembers() {
  if (!props.teamId) return

  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await teamApi.getTeamMembers(props.teamId)
    members.value = res.data || []
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '加载成员列表失败'
    members.value = []
  } finally {
    loading.value = false
  }
}

function getMemberInitial(member) {
  return (member.jerseyName || '').charAt(0) || (member.roleName || '').charAt(0) || '?'
}

function getRoleName(role) {
  const map = { 1: '队员', 2: '队长', 3: '教练', 4: '领队', 5: '创建者' }
  return map[role] || '未知'
}

function getRoleTagType(role) {
  return ROLE_TAG_MAP[role] || ''
}

function getMemberStatusName(status) {
  const map = { 1: '申请中', 2: '已加入', 3: '已退出' }
  return map[status] || '未知'
}

function formatTime(time) {
  if (!time) return ''
  // 如果是 "2024-01-01T12:00:00" 格式，截取到秒
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadMembers()
})
</script>

<style scoped>
.hs-team-members {
  height: 100%;
}

.loading-wrapper {
  padding: 40px 20px;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.member-card {
  cursor: default;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.member-info {
  flex: 1;
  min-width: 0;
}

.member-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.member-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.member-details {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-secondary, #606266);
}
</style>
