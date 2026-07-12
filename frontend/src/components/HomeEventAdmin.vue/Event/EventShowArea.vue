<template>
  <div class="event-show-area">
    <!-- 顶部功能条 -->
    <div class="area-header">
      <div class="header-left">
        <h2 class="area-title">我的赛事</h2>
        <el-select
          v-model="selectedEventId"
          placeholder="请选择赛事"
          size="default"
          class="event-select"
          :loading="loading"
          @change="handleEventChange"
        >
          <el-option
            v-for="event in events"
            :key="event.id"
            :label="event.name"
            :value="event.id"
          >
            <span class="event-option-label">{{ event.name }}</span>
            <el-tag
              :type="getStatusTagType(event.status)"
              size="small"
              class="event-option-tag"
            >
              {{ getStatusName(event.status) }}
            </el-tag>
          </el-option>
        </el-select>
      </div>

      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加赛事
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 错误状态 -->
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
        <el-button size="small" type="primary" @click="fetchMyEvents">重试</el-button>
      </template>
    </el-alert>

    <!-- 空状态：未创建任何赛事 -->
    <el-empty
      v-else-if="events.length === 0"
      description="暂未创建任何赛事，点击上方按钮添加"
    />

    <!-- 赛事详情标签页 -->
    <EventTabs
      v-else
      v-model:activeTab="activeTab"
      class="event-detail-area"
    >
      <template #teams>
        <div class="tab-content-placeholder">
          <el-empty description="参与球队管理" />
        </div>
      </template>

      <template #group>
        <div class="tab-content-placeholder">
          <el-empty description="小组赛管理" />
        </div>
      </template>

      <template #knockout>
        <div class="tab-content-placeholder">
          <el-empty description="淘汰赛管理" />
        </div>
      </template>

      <template #schedule>
        <div class="tab-content-placeholder">
          <el-empty description="赛程管理" />
        </div>
      </template>
    </EventTabs>

    <!-- 添加赛事对话框 -->
    <AddEvent ref="addEventRef" @success="handleAddSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import EventTabs from './EventTabs.vue'
import AddEvent from '@/components/General/AddEvent.vue'

const events = ref([])
const selectedEventId = ref(null)
const selectedEvent = ref(null)
const activeTab = ref('teams')
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const addEventRef = ref(null)

/**
 * 获取赛事状态名称
 * @param {number} status 1-筹办中，2-进行中，3-已结束
 */
function getStatusName(status) {
  switch (status) {
    case 1: return '筹办中'
    case 2: return '进行中'
    case 3: return '已结束'
    default: return '未知'
  }
}

/**
 * 获取状态标签类型
 * @param {number} status
 */
function getStatusTagType(status) {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'info'
    default: return 'info'
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
}

/**
 * 打开添加赛事对话框
 */
function handleAdd() {
  addEventRef.value?.open()
}

/**
 * 添加赛事成功后的回调
 */
function handleAddSuccess() {
  ElMessage.success('赛事已更新')
  fetchMyEvents()
}

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

/* ---- 顶部功能条 ---- */
.area-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px 20px;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.area-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
  white-space: nowrap;
}

.event-select {
  width: 280px;
}

.event-option-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-option-tag {
  margin-left: 8px;
  flex-shrink: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ---- 加载状态 ---- */
.loading-container {
  padding: 40px;
  background: var(--color-bg-white);
  border-radius: 8px;
}

/* ---- 赛事详情区域 ---- */
.event-detail-area {
  flex: 1;
  background: var(--color-bg-white);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.tab-content-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}
</style>
