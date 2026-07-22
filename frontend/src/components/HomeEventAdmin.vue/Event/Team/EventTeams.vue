<template>
  <div class="teams-tab-content">
    <AsyncContent
      :loading="loading"
      :load-failed="loadFailed"
      :error-message="errorMessage"
      :is-empty="teams.length === 0"
      empty-description="暂无球队参与该赛事"
      :skeleton-rows="4"
      @update:load-failed="$emit('update:loadFailed', $event)"
      @retry="$emit('refresh')"
    >
      <div class="teams-toolbar">
        <span class="teams-count">
          共 <strong>{{ teams.length }}</strong> 支球队
        </span>
      </div>
      <el-table :data="teams" style="width: 100%" max-height="500px">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column label="Logo" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.teamLogo">
              {{ row.teamName?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="球队名称" min-width="180">
          <template #default="{ row }">
            <div class="team-cell">
              <span class="team-name">{{ row.teamName }}</span>
              <el-tag v-if="row.teamShortName" size="small" type="info" effect="plain">
                {{ row.teamShortName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="运动类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getSportTagType(row.sportType)" size="small">
              {{ row.sportTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="70" prop="genderName" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTeamStatusTagType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="人数" width="70" prop="memberCount" />
        <el-table-column label="申请时间" width="180" prop="applyTime" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="Edit"
              @click="handleEditTeam(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </AsyncContent>

    <!-- 球队编辑对话框（准入/拒绝 + 查看球员） -->
    <EventTeamEdit
      v-model:visible="editDialogVisible"
      :team="currentTeam"
      :tournament-id="tournamentId"
      @refresh="$emit('refresh')"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import AsyncContent from '@/components/General/AsyncContent.vue'
import EventTeamEdit from './EventTeamEdit.vue'

/** 运动类型标签颜色映射 */
const SPORT_TAG_MAP = { 1: '', 2: 'success', 3: 'warning' }

/** 球队在赛事中的状态标签颜色映射（1-申请中，2-已通过，3-未通过） */
const TEAM_STATUS_TAG_MAP = { 1: 'warning', 2: 'success', 3: 'danger' }

const props = defineProps({
  teams: { type: Array, default: () => [] },
  tournamentId: { type: Number, default: null },
  loading: { type: Boolean, default: false },
  loadFailed: { type: Boolean, default: false },
  errorMessage: { type: String, default: '' }
})

const emit = defineEmits(['update:loadFailed', 'refresh'])

function getSportTagType(type) {
  return SPORT_TAG_MAP[type] || ''
}

function getTeamStatusTagType(status) {
  return TEAM_STATUS_TAG_MAP[status] || 'info'
}

// ==================== 编辑对话框 ====================

/** 编辑对话框可见性 */
const editDialogVisible = ref(false)
/** 当前选中的球队 */
const currentTeam = ref(null)

/**
 * 打开球队编辑对话框
 */
function handleEditTeam(row) {
  currentTeam.value = row
  editDialogVisible.value = true
}
</script>

<style scoped>
.teams-tab-content {
  min-height: 200px;
}

.teams-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.teams-count {
  font-size: 14px;
  color: var(--color-text-secondary, #606266);
}

.teams-count strong {
  color: var(--color-primary, #409eff);
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-cell .team-name {
  font-weight: 500;
}
</style>
