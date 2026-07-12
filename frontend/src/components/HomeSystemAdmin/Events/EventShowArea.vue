<template>
  <div class="event-show-area">
    <div class="area-header">
      <h2 class="area-title">赛事管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索赛事名称..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          size="default"
        />
        <el-select v-model="filterType" placeholder="运动类型" clearable size="default" class="filter-select">
          <el-option
            v-for="item in typeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select v-model="filterStatus" placeholder="赛事状态" clearable size="default" class="filter-select">
          <el-option
            v-for="item in statusOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加赛事</el-button>
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
        <el-button size="small" type="primary" @click="fetchEvents">重试</el-button>
      </template>
    </el-alert>

    <!-- 搜索结果为空 -->
    <el-empty v-else-if="filteredEvents.length === 0" :description="searchKeyword ? '未匹配到相关赛事' : '暂无赛事数据'" />

    <!-- 赛事表格 -->
    <el-table
      v-else
      :data="filteredEvents"
      border
      stripe
      style="width: 100%"
      size="default"
      row-key="id"
      class="event-table"
    >
      <EventCard :onEdit="handleEdit" :onDelete="handleDelete" />
    </el-table>

    <!-- 添加赛事对话框 -->
    <AddEvent ref="addEventRef" @success="fetchEvents" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import EventCard from './EventCard.vue'
import AddEvent from '@/components/General/AddEvent.vue'

const events = ref([])
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')
const addEventRef = ref(null)

/**
 * 运动类型名称映射
 */
function getTypeName(type) {
  switch (type) {
    case 1: return '足球'
    case 2: return '篮球'
    case 3: return '排球'
    default: return '未知'
  }
}

/**
 * 赛事状态名称映射
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
 * 所有出现的类型名称（用于筛选下拉框）
 */
const typeOptions = computed(() => {
  return [...new Set(events.value.map(e => getTypeName(e.type)).filter(Boolean))]
})

/**
 * 所有出现的状态名称（用于筛选下拉框）
 */
const statusOptions = computed(() => {
  return [...new Set(events.value.map(e => getStatusName(e.status)).filter(Boolean))]
})

/**
 * 筛选后的赛事列表
 */
const filteredEvents = computed(() => {
  let result = events.value

  // 关键词模糊搜索（赛事名称）
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword) {
    result = result.filter(event => {
      return event.name?.toLowerCase().includes(keyword)
    })
  }

  // 运动类型筛选
  if (filterType.value) {
    result = result.filter(event => getTypeName(event.type) === filterType.value)
  }

  // 赛事状态筛选
  if (filterStatus.value) {
    result = result.filter(event => getStatusName(event.status) === filterStatus.value)
  }

  return result
})

/**
 * 获取所有赛事
 */
async function fetchEvents() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await tournamentApi.getAll()
    events.value = res.data || []
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取赛事列表失败'
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  addEventRef.value?.open()
}

function handleEdit(row) {
  ElMessage.info('编辑赛事功能待实现')
}

function handleDelete(row) {
  ElMessage.info('删除赛事功能待实现')
}

onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.event-show-area {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.area-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.area-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 220px;
}

.filter-select {
  width: 140px;
}

.loading-container {
  padding: 40px;
}

/* 赛事表格样式 */
.event-table {
  --el-table-border-color: var(--color-primary-light);
  --el-table-border: 1px solid var(--color-primary-light);
  --el-table-header-bg-color: var(--color-primary-bg);
  --el-table-header-text-color: var(--color-primary-dark);
  --el-table-row-hover-bg-color: var(--color-primary-bg);
}

.event-table :deep(.el-table__body tr.el-table__row--striped td) {
  background-color: #F2F9FE;
}
</style>
