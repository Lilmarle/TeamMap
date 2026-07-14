<template>
  <div class="event-show-area">
    <!-- 顶部功能条 -->
    <EventToolbar
      :events="events"
      :selected-event-id="selectedEventId"
      :loading="loading"
      @update:selected-event-id="handleEventChange"
      @invite="handleInvite"
      @delete="handleDelete"
      @add="handleAdd"
    />

    <AsyncContent
      :loading="loading"
      :load-failed="loadFailed"
      :error-message="errorMessage"
      :is-empty="events.length === 0"
      empty-description="暂未创建任何赛事，点击上方按钮添加"
      @retry="fetchMyEvents"
      @update:load-failed="loadFailed = $event"
    >
      <!-- 赛事详情标签页 -->
      <EventTabs
        v-model:activeTab="activeTab"
        class="event-detail-area"
      >
        <template #teams>
          <EventTeams
            :teams="tournamentTeams"
            :tournament-id="selectedEventId"
            :loading="loadingTeams"
            :load-failed="teamsLoadFailed"
            :error-message="teamsErrorMessage"
            @update:load-failed="teamsLoadFailed = $event"
            @refresh="fetchTournamentTeams"
          />
        </template>

        <template #group>
          <EventGroup
            :tournament-id="selectedEventId"
            :tournament-teams="tournamentTeams"
          />
        </template>

        <template #knockout>
          <EventKnockout />
        </template>

        <template #schedule>
          <EventSchedule />
        </template>
      </EventTabs>
    </AsyncContent>

    <!-- 添加赛事对话框 -->
    <AddEvent ref="addEventRef" @success="handleAddSuccess" />

    <!-- 邀请球队对话框 -->
    <EventInvite
      ref="eventInviteRef"
      :tournament="selectedEvent"
      @success="handleInviteSuccess"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage, ElMessageBox } from 'element-plus'
import AsyncContent from '@/components/General/AsyncContent.vue'
import EventToolbar from './EventToolbar.vue'
import EventTabs from './EventTabs.vue'
import EventTeams from './EventTeams.vue'
import EventGroup from './EventGroup.vue'
import EventKnockout from './EventKnockout.vue'
import EventSchedule from './EventSchedule.vue'
import AddEvent from '@/components/General/AddEvent.vue'
import EventInvite from './EventInvite.vue'

const events = ref([])
const selectedEventId = ref(null)
const selectedEvent = ref(null)
const activeTab = ref('teams')
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const addEventRef = ref(null)
const eventInviteRef = ref(null)

/** 参赛球队相关状态 */
const tournamentTeams = ref([])
const loadingTeams = ref(false)
const teamsLoadFailed = ref(false)
const teamsErrorMessage = ref('')

/**
 * 查询参赛球队列表
 */
async function fetchTournamentTeams() {
  if (!selectedEventId.value) {
    tournamentTeams.value = []
    return
  }

  loadingTeams.value = true
  teamsLoadFailed.value = false
  teamsErrorMessage.value = ''

  try {
    const res = await tournamentApi.getTournamentTeams(selectedEventId.value)
    tournamentTeams.value = res.data || []
  } catch (e) {
    teamsLoadFailed.value = true
    teamsErrorMessage.value = e.message || '获取参赛球队列表失败'
  } finally {
    loadingTeams.value = false
  }
}

/**
 * 获取当前管理员创建的赛事列表
 */
async function fetchMyEvents() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await tournamentApi.getMy()
    events.value = res.data || []

    // 默认选中第一个赛事
    if (events.value.length > 0) {
      selectedEventId.value = events.value[0].id
      selectedEvent.value = events.value[0]
    }
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取赛事列表失败'
  } finally {
    loading.value = false
  }
}

/**
 * 切换选中的赛事
 */
function handleEventChange(eventId) {
  selectedEvent.value = events.value.find(e => e.id === eventId) || null
  activeTab.value = 'teams' // 切换赛事时回到第一个标签页
  fetchTournamentTeams()
}

/**
 * 打开添加赛事对话框
 */
function handleAdd() {
  addEventRef.value?.open()
}

/**
 * 删除当前选中的赛事
 */
async function handleDelete() {
  if (!selectedEventId.value) return

  try {
    await ElMessageBox.confirm(
      `确定要删除赛事「${selectedEvent.value?.name}」吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await tournamentApi.delete(selectedEventId.value)
    ElMessage.success('赛事已删除')

    await fetchMyEvents()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除赛事失败')
    }
  }
}

/**
 * 添加赛事成功后的回调
 */
function handleAddSuccess() {
  ElMessage.success('赛事已更新')
  fetchMyEvents()
}

/**
 * 打开邀请球队对话框
 */
function handleInvite() {
  eventInviteRef.value?.open()
}

/**
 * 邀请球队成功后的回调
 */
function handleInviteSuccess() {
  ElMessage.success('球队邀请成功')
  fetchMyEvents()
  fetchTournamentTeams()
}

// 当选中的赛事 ID 变化时，加载该赛事的参赛球队
watch(selectedEventId, (newVal) => {
  if (newVal) {
    fetchTournamentTeams()
  } else {
    tournamentTeams.value = []
  }
})

onMounted(() => {
  fetchMyEvents()
})
</script>

<style scoped>
.event-show-area {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ---- 赛事详情区域 ---- */
.event-detail-area {
  flex: 1;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

</style>
