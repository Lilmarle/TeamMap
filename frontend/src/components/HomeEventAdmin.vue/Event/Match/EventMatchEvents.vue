<template>
  <el-dialog
    :model-value="visible"
    title="比赛事件管理"
    width="600px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="events-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>

      <template v-else>
        <!-- 事件列表 -->
        <div class="events-section" v-if="events.length > 0">
          <div class="section-title">
            事件记录
            <el-tag size="small" type="info">{{ events.length }} 个事件</el-tag>
          </div>

          <!-- 按队伍分组显示 -->
          <div class="team-events">
            <div class="team-event-group">
              <div class="team-event-header">
                <el-avatar :size="20" :src="match.team1Logo">
                  {{ match.team1Name?.charAt(0) }}
                </el-avatar>
                <span class="team-event-name">{{ match.team1ShortName || match.team1Name }}</span>
              </div>
              <div v-for="ev in team1Events" :key="ev.eventId" class="event-item">
                <span class="event-time">{{ ev.eventTime }}'</span>
                <el-tag :type="eventTagType(ev.type)" size="small" class="event-type-tag">
                  {{ eventTypeLabel(ev.type) }}
                </el-tag>
                <span class="event-player" v-if="ev.playerName">
                  #{{ ev.playerJersey ?? '' }} {{ ev.playerName }}
                </span>
                <span class="event-assist" v-if="ev.relatedPlayerName">
                  <span class="assist-arrow">←</span>
                  #{{ ev.relatedPlayerJersey ?? '' }} {{ ev.relatedPlayerName }}
                </span>
                <span class="event-desc" v-if="ev.description">{{ ev.description }}</span>
              </div>
              <div v-if="team1Events.length === 0" class="no-events">暂无事件</div>
            </div>

            <div class="team-event-group">
              <div class="team-event-header">
                <el-avatar :size="20" :src="match.team2Logo">
                  {{ match.team2Name?.charAt(0) }}
                </el-avatar>
                <span class="team-event-name">{{ match.team2ShortName || match.team2Name }}</span>
              </div>
              <div v-for="ev in team2Events" :key="ev.eventId" class="event-item">
                <span class="event-time">{{ ev.eventTime }}'</span>
                <el-tag :type="eventTagType(ev.type)" size="small" class="event-type-tag">
                  {{ eventTypeLabel(ev.type) }}
                </el-tag>
                <span class="event-player" v-if="ev.playerName">
                  #{{ ev.playerJersey ?? '' }} {{ ev.playerName }}
                </span>
                <span class="event-assist" v-if="ev.relatedPlayerName">
                  <span class="assist-arrow">←</span>
                  #{{ ev.relatedPlayerJersey ?? '' }} {{ ev.relatedPlayerName }}
                </span>
                <span class="event-desc" v-if="ev.description">{{ ev.description }}</span>
              </div>
              <div v-if="team2Events.length === 0" class="no-events">暂无事件</div>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无比赛事件" :image-size="60" />
      </template>

      <!-- 分隔线 -->
      <div class="divider-row" v-if="!loading">
        <el-divider>
          <el-button size="small" type="primary" text @click="showAddForm = !showAddForm">
            {{ showAddForm ? '收起添加表单' : '+ 添加事件' }}
          </el-button>
        </el-divider>
      </div>

      <!-- 添加事件表单 -->
      <el-form
        v-if="showAddForm && !loading"
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="90px"
        size="small"
        class="add-event-form"
      >
        <el-form-item label="所属队伍" prop="teamId">
          <el-select v-model="form.teamId" placeholder="选择队伍" style="width:100%">
            <el-option
              :value="match.team1Id"
              :label="match.team1ShortName || match.team1Name"
            />
            <el-option
              :value="match.team2Id"
              :label="match.team2ShortName || match.team2Name"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="事件类型" prop="type">
          <el-select v-model="form.type" placeholder="选择事件类型" style="width:100%">
            <el-option :value="1" label="⚽ 进球" />
            <el-option :value="2" label="🟥 红牌" />
            <el-option :value="3" label="🟨 黄牌" />
            <el-option :value="4" label="⚠️ 犯规" />
            <el-option :value="5" label="🔄 换人" />
          </el-select>
        </el-form-item>

        <el-form-item label="球员" prop="playerId" v-if="showPlayerField">
          <el-select
            v-model="form.playerId"
            placeholder="选择球员"
            style="width:100%"
            filterable
            :disabled="!form.teamId"
          >
            <el-option
              v-for="p in currentTeamPlayers"
              :key="p.playerId"
              :value="p.playerId"
              :label="`#${p.jerseyNumber ?? ''} ${p.playerName}`"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="关联球员" prop="relatedPlayerId" v-if="showRelatedPlayerField">
          <el-select
            v-model="form.relatedPlayerId"
            :placeholder="relatedPlayerPlaceholder"
            style="width:100%"
            filterable
            :disabled="!form.teamId"
          >
            <el-option
              v-for="p in currentTeamPlayers"
              :key="p.playerId"
              :value="p.playerId"
              :label="`#${p.jerseyNumber ?? ''} ${p.playerName}`"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="发生时间" prop="eventTime">
          <el-input-number
            v-model="form.eventTime"
            :min="0"
            :max="120"
            size="small"
            controls-position="right"
            style="width:140px"
          />
          <span class="time-unit">分钟</span>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            placeholder="可选，事件描述"
            type="textarea"
            :rows="2"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleAddEvent">
            {{ saving ? '添加中...' : '确认添加' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button size="small" @click="$emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  match: { type: Object, required: true }
})

const emit = defineEmits(['update:visible', 'updated'])

// ===== 事件数据 =====
const loading = ref(false)
const events = ref([])
const showAddForm = ref(false)
const saving = ref(false)
const formRef = ref(null)
const matchPlayers = ref([])

// ===== 事件类型映射 =====
const eventTypeMap = {
  1: { label: '进球', tag: 'danger' },
  2: { label: '红牌', tag: 'danger' },
  3: { label: '黄牌', tag: 'warning' },
  4: { label: '犯规', tag: 'info' },
  5: { label: '换人', tag: 'primary' }
}

function eventTypeLabel(type) {
  return eventTypeMap[type]?.label || `类型${type}`
}

function eventTagType(type) {
  return eventTypeMap[type]?.tag || 'info'
}

// ===== 表单 =====
const form = reactive({
  teamId: null,
  type: null,
  playerId: null,
  relatedPlayerId: null,
  eventTime: 0,
  description: ''
})

const formRules = {
  teamId: [{ required: true, message: '请选择所属队伍', trigger: 'change' }],
  type: [{ required: true, message: '请选择事件类型', trigger: 'change' }],
  playerId: [{ required: true, message: '请选择球员', trigger: 'change' }],
  relatedPlayerId: [
    {
      required: true,
      message: '请选择被换下球员',
      trigger: 'change',
      validator: (_rule, value, callback) => {
        if (form.type === 5 && !value) {
          callback(new Error('换人事件必须指定被换下球员'))
        } else {
          callback()
        }
      }
    }
  ],
  eventTime: [{ required: true, message: '请输入事件发生时间', trigger: 'blur' }]
}

/** 当前选中队伍的球员列表 */
const currentTeamPlayers = computed(() => {
  if (!form.teamId) return []
  return matchPlayers.value.filter(p => p.teamId === form.teamId)
})

/** 是否显示球员字段 */
const showPlayerField = computed(() => {
  return form.type !== null
})

/** 是否显示关联球员字段（助攻/被换下） */
const showRelatedPlayerField = computed(() => {
  return form.type === 1 || form.type === 5
})

/** 关联球员占位提示 */
const relatedPlayerPlaceholder = computed(() => {
  if (form.type === 1) return '选择助攻球员（可选）'
  if (form.type === 5) return '选择被换下球员'
  return '选择关联球员'
})

// 按队伍分组的事件
const team1Events = computed(() => {
  return events.value.filter(e => e.teamId === props.match.team1Id)
})

const team2Events = computed(() => {
  return events.value.filter(e => e.teamId === props.match.team2Id)
})

// ===== 方法 =====

/** 重置表单 */
function resetForm() {
  form.teamId = null
  form.type = null
  form.playerId = null
  form.relatedPlayerId = null
  form.eventTime = 0
  form.description = ''
}

/** 加载比赛事件和球员列表 */
async function loadData() {
  if (!props.match?.id) return

  loading.value = true
  try {
    const [eventsRes, playersRes] = await Promise.all([
      tournamentApi.getMatchEvents(props.match.id),
      tournamentApi.getMatchPlayers(props.match.id)
    ])
    events.value = eventsRes.data || []
    matchPlayers.value = playersRes.data || []
  } catch (e) {
    events.value = []
    matchPlayers.value = []
  } finally {
    loading.value = false
  }
}

/** 添加事件 */
async function handleAddEvent() {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await tournamentApi.addMatchEvent({
      matchId: props.match.id,
      sportType: props.match.sportType || 1,
      teamId: form.teamId,
      playerId: form.playerId,
      relatedPlayerId: form.type === 1 ? form.relatedPlayerId : // 进球：助攻
                       form.type === 5 ? form.relatedPlayerId : // 换人：被换下
                       null,
      type: form.type,
      description: form.description || undefined,
      eventTime: form.eventTime
    })
    ElMessage.success('事件添加成功')
    resetForm()
    showAddForm.value = false
    // 重新加载事件列表
    await loadData()
    emit('updated')
  } catch (e) {
    ElMessage.error(e.message || '添加事件失败')
  } finally {
    saving.value = false
  }
}

// ===== 监听对话框打开 =====
watch(() => props.visible, (val) => {
  if (val && props.match) {
    resetForm()
    showAddForm.value = false
    loadData()
  }
})
</script>

<style scoped>
.events-container {
  min-height: 200px;
}

.loading-state {
  padding: 40px;
}

/* ---- 事件列表 ---- */
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  margin-bottom: 12px;
}

.team-events {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.team-event-group {
  background: var(--color-bg-gray, #f8f9fa);
  border-radius: 8px;
  padding: 10px 14px;
}

.team-event-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--color-border, #e4e7ed);
}

.team-event-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.event-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 0;
  font-size: 13px;
  flex-wrap: wrap;
}

.event-time {
  font-weight: 700;
  color: var(--el-color-primary, #409eff);
  min-width: 32px;
  font-size: 12px;
  font-family: monospace;
}

.event-type-tag {
  flex-shrink: 0;
}

.event-player {
  color: var(--color-text-primary, #303133);
  font-weight: 500;
}

.event-assist {
  color: var(--color-text-secondary, #909399);
  font-size: 12px;
}

.assist-arrow {
  margin-right: 2px;
}

.event-desc {
  color: var(--color-text-placeholder, #c0c4cc);
  font-size: 12px;
  width: 100%;
  padding-left: 38px;
}

.no-events {
  font-size: 12px;
  color: var(--color-text-placeholder, #c0c4cc);
  text-align: center;
  padding: 8px 0;
}

/* ---- 分隔/添加按钮 ---- */
.divider-row {
  margin: 8px 0;
}

/* ---- 添加事件表单 ---- */
.add-event-form {
  padding: 8px 0;
}

.time-unit {
  margin-left: 8px;
  font-size: 13px;
  color: var(--color-text-secondary, #909399);
}
</style>
