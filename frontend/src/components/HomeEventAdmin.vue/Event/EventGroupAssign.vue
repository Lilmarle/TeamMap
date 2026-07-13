<template>
  <el-dialog
    :model-value="visible"
    :title="`分配球队 - ${groupStage.name}`"
    width="500px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="team-selector-content">
      <p class="team-selector-hint">
        从赛事已通过的球队中选择分配到本小组（已分配的不再显示）
      </p>
      <el-checkbox-group v-model="selectedTeamIds" class="team-checkbox-list">
        <el-checkbox
          v-for="team in availableTeams"
          :key="team.teamId"
          :label="team.teamId"
          class="team-checkbox-item"
        >
          <div class="team-checkbox-label">
            <el-avatar :size="20" :src="team.teamLogo">
              {{ team.teamName?.charAt(0) }}
            </el-avatar>
            <span>{{ team.teamName }}</span>
          </div>
        </el-checkbox>
      </el-checkbox-group>
      <el-empty v-if="availableTeams.length === 0" description="无可分配的球队" :image-size="60" />
    </div>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :disabled="selectedTeamIds.length === 0" :loading="submitting" @click="handleConfirm">
        确认分配 ({{ selectedTeamIds.length }})
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  groupStage: { type: Object, required: true },
  /** 赛事中所有已通过的球队 */
  tournamentTeams: { type: Array, default: () => [] },
  /** 已分配到此小组的球队ID列表（用于过滤） */
  existingTeamIds: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:visible', 'success'])

const selectedTeamIds = ref([])
const submitting = ref(false)

/** 可供分配的球队：过滤掉已分配到此小组的 */
const availableTeams = computed(() => {
  const existingIds = new Set(props.existingTeamIds)
  return props.tournamentTeams.filter(t => !existingIds.has(t.teamId))
})

/** 确认分配 */
async function handleConfirm() {
  if (selectedTeamIds.value.length === 0) return

  submitting.value = true
  try {
    await tournamentApi.addTeamsToGroup({
      groupStageId: props.groupStage.id,
      teamIds: selectedTeamIds.value
    })
    ElMessage.success(`已分配 ${selectedTeamIds.value.length} 支球队到「${props.groupStage.name}」`)
    selectedTeamIds.value = []
    emit('update:visible', false)
    emit('success')
  } catch (e) {
    ElMessage.error(e.message || '分配球队失败')
  } finally {
    submitting.value = false
  }
}

// 对话框打开时重置选择
watch(() => props.visible, (val) => {
  if (val) {
    selectedTeamIds.value = []
  }
})
</script>

<style scoped>
.team-selector-content {
  max-height: 400px;
  overflow-y: auto;
}

.team-selector-hint {
  font-size: 13px;
  color: var(--color-text-secondary, #909399);
  margin-bottom: 12px;
}

.team-checkbox-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.team-checkbox-item {
  display: flex;
  align-items: center;
  margin-right: 0;
}

.team-checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
