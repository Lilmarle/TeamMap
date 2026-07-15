<template>
  <div class="group-card">
    <!-- 卡片头部 -->
    <div class="group-card-header">
      <div class="group-header-left">
        <span class="group-name">{{ groupStage.name }}</span>
        <div class="group-info-tags">
          <el-tag size="small" effect="plain">
            直接出线: {{ groupStage.directAdvance }} 队
          </el-tag>
          <el-tag v-if="groupStage.indirectAdvance" size="small" type="warning" effect="plain">
            间接出线: {{ groupStage.indirectAdvance }} 队
          </el-tag>
          <el-tag size="small" type="info" effect="plain">
            {{ groupStage.roundType === 1 ? '单循环' : '双循环' }}
          </el-tag>
        </div>
      </div>
      <div
        class="group-select-indicator"
        :class="{ 'is-selected': isSelected }"
        @click="$emit('select-group', groupStage)"
      >
        <el-tag :type="isSelected ? 'primary' : 'info'" size="small" effect="plain">
          {{ isSelected ? '已选中' : '点击选中' }}
        </el-tag>
      </div>
    </div>

    <!-- 加载球队列表状态 -->
    <div v-if="loadingTeams" class="group-teams-loading">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 球队排名表格 -->
    <template v-else>
      <el-table
        v-if="sortedTeams.length > 0"
        :data="sortedTeams"
        :row-class-name="getRowClassName"
        stripe
        size="small"
        class="group-teams-table"
      >
        <el-table-column label="排名" width="60" align="center">
          <template #default="{ $index }">
            <span :class="{ 'rank-first': $index === 0 }">
              {{ $index + 1 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="球队" min-width="160">
          <template #default="{ row }">
            <div class="team-cell">
              <el-avatar :size="24" :src="row.logo || row.teamLogo">
                {{ (row.teamShortName || row.teamName)?.charAt(0) }}
              </el-avatar>
              <span class="team-name">{{ row.teamShortName || row.teamName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="win" label="胜" width="50" align="center" />
        <el-table-column prop="draw" label="平" width="50" align="center" />
        <el-table-column prop="loss" label="负" width="50" align="center" />
        <el-table-column prop="goalsFor" label="进球" width="65" align="center" />
        <el-table-column prop="goalsAgainst" label="失球" width="65" align="center" />
        <el-table-column prop="goalDifference" label="净胜球" width="70" align="center" />
        <el-table-column prop="points" label="积分" width="60" align="center">
          <template #default="{ row }">
            <el-tag :type="row.points >= groupStage.teamCount ? 'success' : ''" size="small">
              {{ row.points }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-else description="暂未分配球队" :image-size="80" />
    </template>

  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { tournamentApi } from '@/api/tournament'

const props = defineProps({
  groupStage: { type: Object, required: true },
  tournamentId: { type: Number, required: true },
  /** 赛事中所有已通过的球队（用于分配球队时选择） */
  tournamentTeams: { type: Array, default: () => [] },
  /** 该分组是否处于选中状态（用于工具栏操作） */
  isSelected: { type: Boolean, default: false }
})

const emit = defineEmits(['refresh', 'select-group'])

/** 小组内球队列表及成绩 */
const groupTeams = ref([])
const loadingTeams = ref(false)

/**
 * 加载小组详情（含球队成绩）
 * 使用 detail API 已联查球队名称和Logo
 */
async function fetchGroupTeams() {
  if (!props.groupStage?.id) return

  loadingTeams.value = true
  try {
    const res = await tournamentApi.getGroupStageDetail(props.groupStage.id)
    // detail 返回 { id, name, teamCount, directAdvance, indirectAdvance, teams: [...] }
    groupTeams.value = res.data?.teams || []
  } catch (e) {
    console.error('获取小组详情失败:', e.message)
    groupTeams.value = []
  } finally {
    loadingTeams.value = false
  }
}

/**
 * 按积分(降序) → 净胜球(降序) → 进球数(降序)排序
 */
const sortedTeams = computed(() => {
  return [...groupTeams.value].sort((a, b) => {
    if (b.points !== a.points) return b.points - a.points
    if (b.goalDifference !== a.goalDifference) return b.goalDifference - a.goalDifference
    return b.goalsFor - a.goalsFor
  })
})

/**
 * 根据排名返回行样式名
 * - 直接出线行（rank <= directAdvance）：绿色背景
 * - 间接出线行（rank <= directAdvance + indirectAdvance）：黄色背景
 * - 未出线行：无背景
 */
function getRowClassName({ row, rowIndex }) {
  const rank = rowIndex + 1
  const directEnd = props.groupStage.directAdvance || 0
  const indirectEnd = directEnd + (props.groupStage.indirectAdvance || 0)

  if (rank <= directEnd) return 'row-qualified-direct'
  if (rank <= indirectEnd) return 'row-qualified-indirect'
  return ''
}

// 监听 groupStage.id 变化，重新加载
watch(() => props.groupStage?.id, (newId) => {
  if (newId) {
    fetchGroupTeams()
  } else {
    groupTeams.value = []
  }
}, { immediate: true })
</script>

<style scoped>
.group-card {
  background: var(--color-bg-white, #fff);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

/* ---- 卡片头部 ---- */
.group-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border, #ebeef5);
}

.group-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.group-info-tags {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ---- 分组选中指示器 ---- */
.group-select-indicator {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.group-select-indicator:hover {
  background-color: var(--color-bg-page, #f5f7fa);
}

.group-select-indicator.is-selected {
  background-color: rgba(64, 158, 255, 0.08);
}

/* ---- 球队列表加载 ---- */
.group-teams-loading {
  padding: 20px 16px;
}

/* ---- 球队排名表格 ---- */
.group-teams-table {
  margin: 0;
}

.group-teams-table :deep(.el-table__header-wrapper th) {
  background-color: var(--color-bg-page, #f5f7fa);
  color: var(--color-text-secondary, #606266);
  font-weight: 500;
}

.rank-first {
  color: var(--el-color-danger, #f56c6c);
  font-weight: 700;
}

.team-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-name {
  font-weight: 500;
  color: var(--color-text-primary, #303133);
}

/* ---- 出线状态行背景色（需作用在 td 上以覆盖 Element Plus 的 stripe 样式） ---- */
.group-teams-table :deep(.row-qualified-direct) td {
  background-color: #ecf7f0 !important;
}

.group-teams-table :deep(.row-qualified-direct:hover) td {
  background-color: #dff0e6 !important;
}

.group-teams-table :deep(.row-qualified-indirect) td {
  background-color: #fef7e0 !important;
}

.group-teams-table :deep(.row-qualified-indirect:hover) td {
  background-color: #fdf0cc !important;
}
</style>
