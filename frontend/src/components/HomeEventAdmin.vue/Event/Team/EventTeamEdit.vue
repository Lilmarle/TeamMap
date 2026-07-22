<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`球队管理 - ${team?.teamName || ''}`"
    width="500px"
    :close-on-click-modal="false"
    @open="handleOpen"
    @closed="handleClosed"
  >
    <!-- 球队基本信息 -->
    <div class="team-info">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="球队名称">{{ team?.teamName }}</el-descriptions-item>
        <el-descriptions-item label="简称">{{ team?.teamShortName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="运动类型">{{ team?.sportTypeName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ team?.genderName }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getTeamStatusTagType(team?.status)" size="small">
            {{ team?.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="成员人数">{{ team?.memberCount }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ team?.applyTime }}</el-descriptions-item>
        <el-descriptions-item v-if="team?.auditTime" label="审核时间" :span="2">{{ team?.auditTime }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 操作按钮区 -->
    <div class="action-buttons">
      <!-- 状态为申请中(1)时显示准入/拒绝按钮 -->
      <template v-if="team?.status === 1">
        <el-popconfirm
          title="确定要准入该球队吗？"
          confirm-button-text="确定准入"
          cancel-button-text="取消"
          confirm-button-type="primary"
          @confirm="handleApproveTeam"
        >
          <template #reference>
            <el-button type="success" :icon="Check" :loading="approving">
              准入球队
            </el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
          title="确定要拒绝该球队吗？"
          confirm-button-text="确定拒绝"
          cancel-button-text="取消"
          confirm-button-type="danger"
          @confirm="handleRejectTeam"
        >
          <template #reference>
            <el-button type="danger" :icon="Close" :loading="rejecting">
              拒绝球队
            </el-button>
          </template>
        </el-popconfirm>
      </template>
      <el-button type="primary" :icon="UserFilled" @click="handleViewPlayers">
        查看报名球员
      </el-button>
    </div>

    <!-- 球员名单对话框（内嵌） -->
    <el-dialog
      v-model="playerDialogVisible"
      :title="`球员名单 - ${team?.teamName || ''}`"
      width="800px"
      :close-on-click-modal="false"
      append-to-body
      @closed="handlePlayerDialogClosed"
    >
      <div v-if="playersLoading" class="loading-wrapper">
        <el-skeleton :rows="5" animated />
      </div>

      <el-alert
        v-else-if="playersLoadFailed"
        title="加载失败"
        type="error"
        :description="playersErrorMessage"
        show-icon
        closable
        @close="playersLoadFailed = false"
      >
        <template #actions>
          <el-button size="small" type="primary" @click="loadTeamPlayers">重试</el-button>
        </template>
      </el-alert>

      <el-empty
        v-else-if="teamPlayers.length === 0"
        description="该球队尚未提交球员名单"
      />

      <el-table v-else :data="teamPlayers" style="width: 100%" max-height="450px">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column label="定妆照" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.portraitPhoto">
              {{ row.username?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="用户名" min-width="120" prop="username" />
        <el-table-column label="球衣名" width="120" prop="jerseyName" />
        <el-table-column label="球衣号码" width="90" prop="jerseyNumber" align="center" />
        <el-table-column label="场上位置" width="100" prop="position" />
        <el-table-column label="角色" width="80" prop="roleName" align="center" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.playerStatus === 1 ? 'success' : row.playerStatus === 0 ? 'warning' : 'danger'"
              size="small"
            >
              {{ row.playerStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名时间" width="170" prop="registerTime" />
      </el-table>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Check, Close, UserFilled } from '@element-plus/icons-vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  /** 对话框可见性 */
  visible: { type: Boolean, default: false },
  /** 当前选中的球队（TournamentTeamInfoVO） */
  team: { type: Object, default: null },
  /** 赛事ID */
  tournamentId: { type: Number, default: null }
})

const emit = defineEmits(['update:visible', 'refresh'])

/** 球队状态标签颜色映射（1-申请中，2-已通过，3-未通过） */
const TEAM_STATUS_TAG_MAP = { 1: 'warning', 2: 'success', 3: 'danger' }

const dialogVisible = ref(false)

/** 准入/拒绝加载状态 */
const approving = ref(false)
const rejecting = ref(false)

// ==================== 球员名单对话框 ====================

/** 球员列表对话框可见性 */
const playerDialogVisible = ref(false)
/** 球员列表 */
const teamPlayers = ref([])
/** 加载中 */
const playersLoading = ref(false)
/** 加载失败 */
const playersLoadFailed = ref(false)
/** 错误信息 */
const playersErrorMessage = ref('')

watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

function getTeamStatusTagType(status) {
  return TEAM_STATUS_TAG_MAP[status] || 'info'
}

function handleOpen() {
  // 打开时重置状态
  approving.value = false
  rejecting.value = false
}

function handleClosed() {
  // 清理球员列表状态
  teamPlayers.value = []
  playersLoadFailed.value = false
  playersErrorMessage.value = ''
}

/**
 * 准入球队（通过申请）
 */
async function handleApproveTeam() {
  if (!props.team?.relId) return

  approving.value = true
  try {
    await tournamentApi.approveTeam(props.team.relId, 2)
    ElMessage.success(`已准入球队「${props.team.teamName}」`)
    dialogVisible.value = false
    emit('refresh')
  } catch (e) {
    ElMessage.error(e.message || '准入球队失败')
  } finally {
    approving.value = false
  }
}

/**
 * 拒绝球队（驳回申请）
 */
async function handleRejectTeam() {
  if (!props.team?.relId) return

  rejecting.value = true
  try {
    await tournamentApi.approveTeam(props.team.relId, 3)
    ElMessage.success(`已拒绝球队「${props.team.teamName}」`)
    dialogVisible.value = false
    emit('refresh')
  } catch (e) {
    ElMessage.error(e.message || '拒绝球队失败')
  } finally {
    rejecting.value = false
  }
}

/**
 * 打开球员名单对话框
 */
async function handleViewPlayers() {
  playerDialogVisible.value = true
  await loadTeamPlayers()
}

/**
 * 加载球队的球员报名列表
 */
async function loadTeamPlayers() {
  if (!props.tournamentId || !props.team?.teamId) return

  playersLoading.value = true
  playersLoadFailed.value = false
  playersErrorMessage.value = ''

  try {
    const res = await tournamentApi.getTeamPlayers(props.tournamentId, props.team.teamId)
    teamPlayers.value = res.data || []
  } catch (e) {
    playersLoadFailed.value = true
    playersErrorMessage.value = e.message || '加载球员名单失败'
    teamPlayers.value = []
  } finally {
    playersLoading.value = false
  }
}

/**
 * 关闭球员对话框时的清理
 */
function handlePlayerDialogClosed() {
  teamPlayers.value = []
  playersLoadFailed.value = false
  playersErrorMessage.value = ''
}
</script>

<style scoped>
.team-info {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.loading-wrapper {
  padding: 40px 20px;
}
</style>
