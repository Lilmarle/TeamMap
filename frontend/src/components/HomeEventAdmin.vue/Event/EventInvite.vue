<template>
  <el-dialog
    v-model="visible"
    :title="`邀请球队 - ${tournament?.name || ''}`"
    width="700px"
    :close-on-click-modal="false"
    @open="handleOpen"
    @closed="handleClosed"
  >
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 加载失败 -->
    <el-alert
      v-else-if="loadFailed"
      title="加载失败"
      type="error"
      :description="errorMessage"
      show-icon
      closable
      @close="loadFailed = false"
    >
      <template #actions>
        <el-button size="small" type="primary" @click="loadTeams">重试</el-button>
      </template>
    </el-alert>

    <!-- 可邀请列表为空 -->
    <el-empty
      v-else-if="availableTeams.length === 0"
      description="暂无符合条件的球队可邀请"
    />

    <!-- 可邀请球队列表 -->
    <template v-else>
      <div class="invite-header">
        <span class="invite-count">
          共 <strong>{{ availableTeams.length }}</strong> 支球队可邀请，
          已选 <strong>{{ selectedIds.length }}</strong> 支
        </span>
        <div class="invite-actions">
          <el-checkbox
            v-model="selectAll"
            :indeterminate="isIndeterminate"
            @change="handleSelectAllChange"
          >
            全选
          </el-checkbox>
        </div>
      </div>

      <el-table
        ref="tableRef"
        :data="availableTeams"
        style="width: 100%"
        max-height="400px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column label="Logo" width="65" align="center">
          <template #default="{ row }">
            <el-avatar :size="32" :src="row.logo">
              {{ row.name?.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="球队名称" min-width="160">
          <template #default="{ row }">
            <div class="team-name-cell">
              <span class="team-name">{{ row.name }}</span>
              <el-tag v-if="row.shortName" size="small" type="info" effect="plain">
                {{ row.shortName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="运动类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getSportTagType(row.type)" size="small">
              {{ getSportTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ getGenderName(row.gender) }}
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="inviting"
          :disabled="selectedIds.length === 0"
          @click="handleInvite"
        >
          邀请加入（{{ selectedIds.length }}）
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { teamApi } from '@/api/team'
import { tournamentApi } from '@/api/tournament'

/** 运动类型名称映射 */
const SPORT_TYPE_MAP = { 1: '足球', 2: '篮球', 3: '排球' }
/** 运动类型标签颜色映射 */
const SPORT_TAG_MAP = { 1: '', 2: 'success', 3: 'warning' }
/** 性别名称映射 */
const GENDER_MAP = { 1: '男', 2: '女', 3: '混合' }

const props = defineProps({
  /** 当前选中的赛事对象，需包含 id, name, type */
  tournament: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['success'])

const visible = ref(false)
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const inviting = ref(false)

/** 所有可邀请的球队（同运动类型且未参赛） */
const availableTeams = ref([])
/** 当前选中的球队ID列表 */
const selectedIds = ref([])
/** 表格组件引用 */
const tableRef = ref(null)

/** 是否全选 */
const selectAll = ref(false)
/** 是否半选状态 */
const isIndeterminate = ref(false)

/**
 * 获取运动类型名称
 */
function getSportTypeName(type) {
  return SPORT_TYPE_MAP[type] || '未知'
}

/**
 * 获取运动类型标签颜色
 */
function getSportTagType(type) {
  return SPORT_TAG_MAP[type] || ''
}

/**
 * 获取性别名称
 */
function getGenderName(gender) {
  return GENDER_MAP[gender] || '未知'
}

/**
 * 打开对话框
 */
function open() {
  visible.value = true
}

/**
 * 对话框打开时加载数据
 */
async function handleOpen() {
  await loadTeams()
}

/**
 * 对话框关闭时重置状态
 */
function handleClosed() {
  availableTeams.value = []
  selectedIds.value = []
  selectAll.value = false
  isIndeterminate.value = false
  loading.value = false
  loadFailed.value = false
  errorMessage.value = ''
}

/**
 * 加载可邀请的球队列表
 * 1. 查询与赛事相同运动类型的球队
 * 2. 排除已在赛事中的球队
 */
async function loadTeams() {
  if (!props.tournament?.id || !props.tournament?.type) {
    errorMessage.value = '赛事信息不完整'
    loadFailed.value = true
    return
  }

  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    // 并行请求：获取同类型的球队 和 已参赛的球队
    const [typeRes, tournamentTeamsRes] = await Promise.all([
      teamApi.getTeamsByType(props.tournament.type),
      tournamentApi.getTournamentTeams(props.tournament.id)
    ])

    const allTeamsByType = typeRes.data || []
    const tournamentTeams = tournamentTeamsRes.data || []

    // 提取已参赛的球队ID集合（状态为已通过的）
    const joinedTeamIds = new Set(
      tournamentTeams
        .filter(t => t.status === 2)
        .map(t => t.teamId)
    )

    // 过滤：只保留未参赛的球队
    availableTeams.value = allTeamsByType.filter(
      team => !joinedTeamIds.has(team.id)
    )
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '加载球队列表失败'
  } finally {
    loading.value = false
  }
}

/**
 * 表格选择变化时更新选中列表
 */
function handleSelectionChange(selection) {
  selectedIds.value = selection.map(row => row.id)
  // 更新全选状态
  if (selection.length === 0) {
    selectAll.value = false
    isIndeterminate.value = false
  } else if (selection.length === availableTeams.value.length) {
    selectAll.value = true
    isIndeterminate.value = false
  } else {
    selectAll.value = false
    isIndeterminate.value = true
  }
}

/**
 * 全选/取消全选
 */
function handleSelectAllChange(val) {
  tableRef.value?.clearSelection()
  if (val) {
    availableTeams.value.forEach(row => {
      tableRef.value?.toggleRowSelection(row, true)
    })
  }
  isIndeterminate.value = false
}

/**
 * 执行批量邀请
 */
async function handleInvite() {
  if (selectedIds.value.length === 0) return

  inviting.value = true
  try {
    const res = await tournamentApi.inviteBatch(
      props.tournament.id,
      selectedIds.value
    )
    ElMessage.success(res.message || '邀请成功')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error(e.message || '邀请失败')
  } finally {
    inviting.value = false
  }
}

// 暴露 open 方法给父组件
defineExpose({ open })
</script>

<style scoped>
.loading-wrapper {
  padding: 20px 0;
}

.invite-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 0 4px;
}

.invite-count {
  font-size: 14px;
  color: var(--color-text-secondary, #606266);
}

.invite-count strong {
  color: var(--color-primary, #409eff);
}

.invite-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.team-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.team-name {
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
