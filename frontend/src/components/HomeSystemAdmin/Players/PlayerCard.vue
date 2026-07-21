<template>
  <!-- ====== 表格列定义 ====== -->
  <el-table-column type="index" label="序号" width="60" align="center" />

  <el-table-column label="头像/姓名" min-width="160">
    <template #default="{ row }">
      <div class="player-name-cell">
        <el-avatar :size="32" :src="row.portraitPhoto">
          {{ row.jerseyName?.charAt(0) || row.roleName?.charAt(0) || '?' }}
        </el-avatar>
        <div class="player-name-info">
          <span class="player-name-text">{{ row.jerseyName || '未设置' }}</span>
          <span v-if="row.jerseyNumber" class="player-number">#{{ row.jerseyNumber }}</span>
        </div>
      </div>
    </template>
  </el-table-column>

  <el-table-column label="所属球队" min-width="160">
    <template #default="{ row }">
      <div class="team-cell">
        <el-avatar :size="24" :src="row.teamLogo">
          {{ row.teamShortName?.charAt(0) || row.teamName?.charAt(0) }}
        </el-avatar>
        <span class="team-name-text">{{ row.teamShortName || row.teamName }}</span>
      </div>
    </template>
  </el-table-column>

  <el-table-column label="角色" width="80" align="center">
    <template #default="{ row }">
      <el-tag :type="getRoleTagType(row.role)" size="small" effect="plain">
        {{ row.roleName }}
      </el-tag>
    </template>
  </el-table-column>

  <el-table-column label="场上位置" width="100" align="center">
    <template #default="{ row }">
      {{ row.position || '-' }}
    </template>
  </el-table-column>

  <el-table-column prop="sportTypeName" label="运动类型" width="90" align="center" />

  <el-table-column prop="teamGenderName" label="性别" width="70" align="center" />

  <el-table-column label="状态" width="90" align="center">
    <template #default="{ row }">
      <el-tag :type="getStatusTagType(row.status)" size="small">
        {{ row.statusName }}
      </el-tag>
    </template>
  </el-table-column>

  <el-table-column prop="joinTime" label="加入时间" width="170" align="center" />

  <el-table-column label="操作" width="130" align="center" fixed="right">
    <template #default="{ row }">
      <el-button type="primary" size="small" :icon="Edit" circle @click="onEdit(row)" />
      <el-button type="danger" size="small" :icon="Delete" circle @click="handleDelete(row)" />
    </template>
  </el-table-column>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { playerApi } from '@/api/player'

const props = defineProps({
  onEdit: {
    type: Function,
    default: null
  }
})

const emit = defineEmits(['refresh'])

/** 删除球员 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除球员「${row.jerseyName || '未命名'}」(#${row.jerseyNumber ?? '?'}) 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    await playerApi.deletePlayer(row.playerId)
    ElMessage.success(`球员「${row.jerseyName || '未命名'}」已删除`)
    emit('refresh')
  } catch {
    // 用户取消则不处理
  }
}

/**
 * 获取角色标签类型
 * @param {number} role 1-队员，2-队长，3-教练，4-领队
 */
function getRoleTagType(role) {
  switch (role) {
    case 2: return 'warning'   // 队长
    case 3: return 'success'   // 教练
    case 4: return 'primary'   // 领队
    default: return 'info'     // 队员
  }
}

/**
 * 获取状态标签类型
 * @param {number} status 1-可出战，2-禁赛中
 */
function getStatusTagType(status) {
  switch (status) {
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}
</script>

<style scoped>
.player-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.player-name-info {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.player-name-text {
  font-weight: 500;
  color: var(--color-text-primary, #303133);
}

.player-number {
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-name-text {
  font-weight: 500;
  color: var(--color-text-primary, #303133);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
