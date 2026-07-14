<template>
  <div class="group-stage-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <span class="toolbar-title">小组赛管理</span>
      <div class="toolbar-actions">
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
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loadingGroups" class="loading-container">
      <el-skeleton :rows="3" animated />
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
        <el-button size="small" type="primary" @click="fetchGroupStages">重试</el-button>
      </template>
    </el-alert>

    <!-- 空状态 -->
    <el-empty v-else-if="groupStages.length === 0" description="暂未创建小组" :image-size="80">
      <template #description>
        <span>暂未创建小组，请点击上方按钮创建</span>
      </template>
      <el-button type="primary" @click="showCreateDialog = true">创建小组</el-button>
    </el-empty>

    <!-- 小组卡片列表 -->
    <template v-else>
      <div class="group-cards">
        <EventGroupCard
          v-for="group in groupStages"
          :key="group.id"
          :group-stage="group"
          :tournament-id="tournamentId"
          :tournament-teams="tournamentTeams"
          @refresh="fetchGroupStages"
          @assign-team="openAssignDialog"
        />
      </div>
    </template>

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

/** 打开分配球队对话框 */
function openAssignDialog(group) {
  assignTargetGroup.value = group || null
  showAssignDialog.value = true
}

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

/* ---- 工具栏 ---- */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.toolbar-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ---- 加载状态 ---- */
.loading-container {
  padding: 20px 0;
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
