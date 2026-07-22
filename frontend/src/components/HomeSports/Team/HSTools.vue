<template>
  <div class="hs-tools">
    <!-- 左侧：当前球队信息 + 切换球队下拉 -->
    <div class="tools-left">
      <el-avatar :size="36" :src="currentTeam?.teamLogo">
        {{ currentTeam?.teamName?.charAt(0) }}
      </el-avatar>
      <div class="team-select-area">
        <el-select
          v-model="selectedTeamId"
          class="team-switcher"
          size="small"
          placeholder="切换球队"
          @change="handleTeamSwitch"
        >
          <el-option
            v-for="team in myTeams"
            :key="team.teamId"
            :label="team.teamName"
            :value="team.teamId"
          >
            <div class="team-option">
              <el-avatar :size="22" :src="team.teamLogo">
                {{ team.teamName?.charAt(0) }}
              </el-avatar>
              <span>{{ team.teamName }}</span>
              <el-tag size="small" type="warning" effect="plain">
                {{ team.roleName }}
              </el-tag>
            </div>
          </el-option>
        </el-select>
        <span class="team-role-tag" v-if="currentTeam?.roleName">
          {{ currentTeam.roleName }}
        </span>
      </div>
    </div>

    <!-- 右侧：操作按钮 -->
    <div class="tools-right">
      <el-button
        size="small"
        type="primary"
        :icon="Plus"
        @click="showInviteDialog = true"
        :disabled="!selectedTeamId"
      >
        邀请
      </el-button>
      <el-button
        size="small"
        :icon="Edit"
        @click="showEditDialog = true"
        :disabled="!selectedTeamId"
      >
        修改个人信息
      </el-button>
    </div>

    <!-- 邀请对话框 -->
    <el-dialog
      v-model="showInviteDialog"
      title="邀请成员"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form :model="inviteForm" label-width="80px" size="small">
        <el-form-item label="用户ID">
          <el-input-number
            v-model="inviteForm.userId"
            :min="1"
            style="width:100%"
            placeholder="输入要邀请的用户ID"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="inviteForm.role" style="width:100%">
            <el-option :value="1" label="队员" />
            <el-option :value="2" label="队长" />
            <el-option :value="3" label="教练" />
            <el-option :value="4" label="领队" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="showInviteDialog = false">取消</el-button>
        <el-button size="small" type="primary" :loading="inviting" @click="handleInvite">
          邀请
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改个人信息对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="修改个人信息"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="100px" size="small">
        <el-form-item label="球衣名称">
          <el-input v-model="editForm.jerseyName" placeholder="球衣上显示的名字" />
        </el-form-item>
        <el-form-item label="球衣号码">
          <el-input-number
            v-model="editForm.jerseyNumber"
            :min="0"
            :max="99"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="场上位置">
          <el-input v-model="editForm.position" placeholder="如：前锋、后卫" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="showEditDialog = false">取消</el-button>
        <el-button size="small" type="primary" :loading="saving" @click="handleEditInfo">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { teamApi } from '@/api/team'
import { playerApi } from '@/api/player'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const emit = defineEmits(['team-changed'])

/** 当前用户的所有球队成员记录 */
const myMemberships = ref([])
/** 所有球队信息（用于查找球队详情） */
const allTeams = ref([])
/** 当前选中的球队ID */
const selectedTeamId = ref(null)
/** 当前选中的球队信息 */
const currentTeam = ref(null)
/** 邀请状态 */
const showInviteDialog = ref(false)
const inviting = ref(false)
const inviteForm = ref({ userId: null, role: 1 })
/** 编辑状态 */
const showEditDialog = ref(false)
const saving = ref(false)
const editForm = ref({ jerseyName: '', jerseyNumber: 0, position: '' })
/** 当前用户的球员记录ID（用于更新球员信息） */
const currentPlayerId = ref(null)

/** 当前用户已加入的所有球队（含角色信息） */
const myTeams = computed(() => {
  if (!myMemberships.value.length || !allTeams.value.length) return []

  return myMemberships.value.map(m => {
    const teamInfo = allTeams.value.find(t => t.teamId === m.teamId)
    const roleMap = { 1: '队员', 2: '队长', 3: '教练', 4: '领队', 5: '创建者' }
    return {
      teamId: m.teamId,
      teamName: teamInfo?.teamName || `球队#${m.teamId}`,
      teamShortName: teamInfo?.teamShortName || '',
      teamLogo: teamInfo?.teamLogo || '',
      sportType: teamInfo?.sportType,
      sportTypeName: teamInfo?.sportTypeName || '',
      role: m.role,
      roleName: roleMap[m.role] || '未知',
      memberId: m.id
    }
  })
})

/** 初始化加载数据 */
async function init() {
  try {
    const [membershipRes, teamsRes] = await Promise.all([
      teamApi.getCurrentMembership(),
      teamApi.getAllTeams()
    ])
    myMemberships.value = membershipRes.data || []
    allTeams.value = teamsRes.data || []

    // 默认选中第一个球队
    if (myTeams.value.length > 0 && !selectedTeamId.value) {
      selectedTeamId.value = myTeams.value[0].teamId
      updateCurrentTeam()
    }
  } catch (e) {
    console.warn('加载球队数据失败:', e)
  }
}

/** 更新当前球队信息 */
function updateCurrentTeam() {
  currentTeam.value = myTeams.value.find(t => t.teamId === selectedTeamId.value) || null
  if (currentTeam.value) {
    loadPlayerInfo()
  }
  emit('team-changed', selectedTeamId.value)
}

/** 切换球队 */
function handleTeamSwitch(teamId) {
  selectedTeamId.value = teamId
  updateCurrentTeam()
}

/** 加载当前用户的球员信息（用于编辑表单回显） */
async function loadPlayerInfo() {
  const user = userStore.user
  if (!user?.id) return

  try {
    const res = await playerApi.getPlayerByUserId(user.id)
    const playerData = res.data
    if (playerData) {
      currentPlayerId.value = playerData.id
      editForm.value = {
        jerseyName: playerData.jerseyName || '',
        jerseyNumber: playerData.jerseyNumber ?? 0,
        position: playerData.position || ''
      }
    } else {
      // 没有球员记录时清空
      currentPlayerId.value = null
      editForm.value = { jerseyName: '', jerseyNumber: 0, position: '' }
    }
  } catch (e) {
    console.warn('加载球员信息失败:', e)
    currentPlayerId.value = null
  }
}

/** 邀请成员 */
async function handleInvite() {
  if (!inviteForm.value.userId) {
    ElMessage.warning('请输入要邀请的用户ID')
    return
  }
  if (!selectedTeamId.value) {
    ElMessage.warning('请先选择球队')
    return
  }

  inviting.value = true
  try {
    await teamApi.joinTeam({
      teamId: selectedTeamId.value,
      userId: inviteForm.value.userId,
      role: inviteForm.value.role
    })
    ElMessage.success('邀请成功')
    showInviteDialog.value = false
    inviteForm.value = { userId: null, role: 1 }
  } catch (e) {
    ElMessage.error(e.message || '邀请失败')
  } finally {
    inviting.value = false
  }
}

/** 修改个人信息 */
async function handleEditInfo() {
  if (!currentPlayerId.value) {
    ElMessage.warning('未找到球员记录，请先创建球员信息')
    return
  }

  saving.value = true
  try {
    await playerApi.updatePlayer(currentPlayerId.value, {
      jerseyName: editForm.value.jerseyName || undefined,
      jerseyNumber: editForm.value.jerseyNumber ?? undefined,
      position: editForm.value.position || undefined
    })
    ElMessage.success('个人信息已更新')
    showEditDialog.value = false
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  init()
})
</script>

<style scoped>
.hs-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: var(--color-bg-white, #fff);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
  gap: 16px;
  flex-wrap: wrap;
}

.tools-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.team-select-area {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.team-switcher {
  min-width: 180px;
  max-width: 260px;
}

.team-role-tag {
  font-size: 12px;
  color: var(--el-color-warning);
  white-space: nowrap;
}

.tools-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.team-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-option span {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
