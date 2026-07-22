<template>
  <div class="group-stage-container">
    <!-- 顶部工具栏 -->
    <PageHeader title="小组赛管理">
      <template #actions>
        <el-button
          size="small"
          type="success"
          :disabled="groupStages.length === 0"
          @click="openAssignDialog(null)"
        >
          分配球队
        </el-button>
        <el-button type="primary" size="small" @click="showCreateDialog = true">
          创建小组
        </el-button>
      </template>
    </PageHeader>

    <AsyncContent
      :loading="loadingGroups"
      :load-failed="loadFailed"
      :error-message="errorMessage"
      :is-empty="groupStages.length === 0"
      empty-description="暂未创建小组"
      :empty-image-size="80"
      @retry="fetchGroupStages"
      @update:load-failed="loadFailed = $event"
    >
      <template #empty>
        <span>暂未创建小组，请点击上方按钮创建</span>
        <el-button type="primary" @click="showCreateDialog = true">创建小组</el-button>
      </template>

      <div class="group-cards">
        <EventGroupCard
          v-for="group in groupStages"
          :key="group.id"
          :group-stage="group"
          :tournament-id="tournamentId"
          :tournament-teams="tournamentTeams"
          :is-selected="selectedGroup?.id === group.id"
          @refresh="fetchGroupStages"
          @select-group="handleSelectGroup"
        />
      </div>
    </AsyncContent>

    <!-- 创建小组对话框（支持单个/批量创建） -->
    <EventGroupCreateDialog
      v-model:visible="showCreateDialog"
      :tournament-id="tournamentId"
      @success="fetchGroupStages"
    />

    <!-- 分配球队对话框（支持单组/全部分配 + 分档抽签） -->
    <EventGroupAssign
      v-model:visible="showAssignDialog"
      :group-stage="assignTargetGroup"
      :tournament-id="tournamentId"
      :tournament-teams="tournamentTeams"
      :existing-team-ids="assignExistingTeamIds"
      @success="fetchGroupStages"
    />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/General/PageHeader.vue'
import AsyncContent from '@/components/General/AsyncContent.vue'
import EventGroupCard from './EventGroupCard.vue'
import EventGroupCreateDialog from './EventGroupCreateDialog.vue'
import EventGroupAssign from './EventGroupAssign.vue'

const props = defineProps({
  tournamentId: { type: Number, default: null },
  /** 赛事中所有已通过的球队 */
  tournamentTeams: { type: Array, default: () => [] }
})

/** 小组列表 */
const groupStages = ref([])
const loadingGroups = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')

/** 当前选中的分组（用于工具栏操作） */
const selectedGroup = ref(null)

/** 创建小组对话框 */
const showCreateDialog = ref(false)

/** 分配球队对话框 */
const showAssignDialog = ref(false)
/** 分配目标小组（null=全部小组模式） */
const assignTargetGroup = ref(null)
/** 当前各小组已分配的球队ID（用于单组模式过滤） */
const allGroupTeamIds = ref({})

/** 计算当前目标小组已分配的球队ID */
const assignExistingTeamIds = computed(() => {
  if (!assignTargetGroup.value) return []
  return allGroupTeamIds.value[assignTargetGroup.value.id] || []
})

/**
 * 获取赛事的所有小组
 */
async function fetchGroupStages() {
  if (!props.tournamentId) {
    groupStages.value = []
    return
  }

  loadingGroups.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await tournamentApi.getGroupStages(props.tournamentId)
    groupStages.value = res.data || []
    // 同时加载每个小组的已有球队ID
    await fetchAllGroupTeamIds()
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取小组列表失败'
  } finally {
    loadingGroups.value = false
  }
}

/** 加载所有小组已分配的球队ID（用于分配对话框的过滤） */
async function fetchAllGroupTeamIds() {
  const map = {}
  for (const group of groupStages.value) {
    try {
      const res = await tournamentApi.getGroupStageDetail(group.id)
      map[group.id] = (res.data?.teams || []).map(t => t.teamId)
    } catch {
      map[group.id] = []
    }
  }
  allGroupTeamIds.value = map
}

/** 选中/取消选中分组 */
function handleSelectGroup(group) {
  if (selectedGroup.value?.id === group.id) {
    selectedGroup.value = null
  } else {
    selectedGroup.value = group
  }
}

/** 打开分配球队对话框（支持传入目标分组） */
function openAssignDialog(group) {
  assignTargetGroup.value = group || null
  showAssignDialog.value = true
}

/** 删除小组（取消分组） */
async function handleDeleteGroup(group) {
  try {
    await tournamentApi.deleteGroupStage(group.id)
    ElMessage.success(`已取消分组「${group.name}」，已分配的球队已移除`)
    await fetchGroupStages()
  } catch (e) {
    ElMessage.error(e.message || '取消分组失败')
  }
}

// 暴露给父组件的方法和状态（供工具栏操作使用）
defineExpose({
  selectedGroup,
  openAssignDialog,
  handleDeleteGroup
})

// 监听 tournamentId 变化
watch(() => props.tournamentId, (newId) => {
  if (newId) {
    fetchGroupStages()
  } else {
    groupStages.value = []
  }
}, { immediate: true })
</script>

<style scoped>
.group-stage-container {
  min-height: 200px;
}

/* ---- 小组卡片列表（网格：每行两张） ---- */
.group-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 1200px) {
  .group-cards {
    grid-template-columns: 1fr;
  }
}
</style>
