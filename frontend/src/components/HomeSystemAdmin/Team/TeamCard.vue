<template>
  <el-table-column type="index" label="序号" width="60" align="center" />

  <el-table-column label="球队名称" min-width="180">
    <template #default="{ row }">
      <div class="team-name-cell">
        <el-avatar :size="28" :src="row.teamLogo">
          {{ row.teamName?.charAt(0) }}
        </el-avatar>
        <span class="team-name-text">{{ row.teamName }}</span>
      </div>
    </template>
  </el-table-column>

  <el-table-column prop="teamShortName" label="简称" width="90" align="center" />

  <el-table-column prop="sportTypeName" label="运动类型" width="100" align="center" />

  <el-table-column prop="genderName" label="性别" width="70" align="center" />

  <el-table-column prop="collegeFullName" label="所属学院" min-width="150" show-overflow-tooltip />

  <el-table-column label="等级" width="90" align="center">
    <template #default="{ row }">
      <el-tag :type="getRankTagType(row.teamRank)" size="small">
        {{ row.rankName }}
      </el-tag>
    </template>
  </el-table-column>

  <el-table-column label="成员" width="70" align="center">
    <template #default="{ row }">
      {{ row.memberCount ?? 0 }}
    </template>
  </el-table-column>

  <el-table-column label="队长" width="70" align="center">
    <template #default="{ row }">
      {{ row.captainCount ?? 0 }}
    </template>
  </el-table-column>

  <el-table-column label="球员" width="70" align="center">
    <template #default="{ row }">
      {{ row.playerCount ?? 0 }}
    </template>
  </el-table-column>

  <el-table-column label="教练" width="70" align="center">
    <template #default="{ row }">
      {{ row.coachCount ?? 0 }}
    </template>
  </el-table-column>

  <el-table-column label="领队" width="70" align="center">
    <template #default="{ row }">
      {{ row.leaderCount ?? 0 }}
    </template>
  </el-table-column>

  <el-table-column prop="teamDescription" label="描述" min-width="150" show-overflow-tooltip />

  <el-table-column label="操作" width="130" align="center" fixed="right">
    <template #default="{ row }">
      <el-button type="primary" size="small" :icon="Edit" circle @click="onEdit(row)" />
      <el-button type="danger" size="small" :icon="Delete" circle @click="onDelete(row)" />
    </template>
  </el-table-column>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  onEdit: {
    type: Function,
    default: null
  },
  onDelete: {
    type: Function,
    default: null
  }
})

function getRankTagType(rank) {
  switch (rank) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'success'
    default: return 'info'
  }
}
</script>

<style scoped>
.team-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-name-text {
  font-weight: 500;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
