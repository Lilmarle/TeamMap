<template>
  <div class="schedule-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 错误状态 -->
    <el-result v-else-if="loadFailed" icon="error" title="加载失败" :sub-title="errorMessage">
      <template #extra>
        <el-button type="primary" @click="fetchAllData">重新加载</el-button>
      </template>
    </el-result>

    <template v-else>
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="action-left">
          <span class="action-title">赛程管理</span>
          <el-tag size="small" type="info" v-if="allMatches.length > 0">
            共 {{ allMatches.length }} 场比赛
          </el-tag>
        </div>
        <div class="action-right">
          <el-button type="primary" size="small" @click="showCreateDialog = true">
            ＋ 创建赛程
          </el-button>
        </div>
      </div>

      <!-- 按日期分组的赛程 -->
      <div class="schedule-content" v-if="allMatches.length > 0">
        <div
          v-for="dateKey in sortedDates"
          :key="dateKey"
          class="date-section"
        >
          <div class="date-header">
            <span class="date-label">{{ formatDateLabel(dateKey) }}</span>
            <span class="date-count">{{ matchesByDate[dateKey].length }} 场</span>
          </div>
          <div class="date-matches">
            <EventMatchCard
              v-for="match in matchesByDate[dateKey]"
              :key="match.id"
              :match="match"
              @updated="fetchMatches"
            />
          </div>
        </div>
      </div>

      <!-- 无赛程 -->
      <el-empty
        v-else
        description="暂无赛程数据，点击上方「创建赛程」按钮自动生成"
        :image-size="60"
        class="schedule-empty"
      />

      <!-- 创建赛程对话框 -->
      <el-dialog
        v-model="showCreateDialog"
        title="创建赛程"
        width="420px"
        :close-on-click-modal="false"
      >
        <el-form label-width="100px" size="small">
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="createConfig.startDate"
              type="date"
              placeholder="默认从当天开始"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width:100%"
            />
          </el-form-item>
          <el-form-item label="比赛地点">
            <el-input v-model="createConfig.location" placeholder="可选（所有比赛共用）" clearable />
          </el-form-item>
        </el-form>
        <p class="dialog-hint">
          将为赛事所有小组统一生成赛程，每天固定时段 <strong>12:00 · 14:00 · 16:00</strong>，每天最多3场，周末跳过，所有小组同步打同一轮
        </p>
        <template #footer>
          <el-button size="small" @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" size="small" :loading="creating" @click="handleCreateSchedule">
            {{ creating ? '生成中...' : '确认创建' }}
          </el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage, ElMessageBox } from 'element-plus'
import EventMatchCard from './EventMatchCard.vue'

const props = defineProps({
  tournamentId: { type: Number, default: null }
})

// ===== 基础数据 =====
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const allMatches = ref([])

// ===== 创建赛程 =====
const showCreateDialog = ref(false)
const creating = ref(false)
const createConfig = reactive({
  startDate: '',
  location: ''
})

function resetCreateConfig() {
  createConfig.startDate = ''
  createConfig.location = ''
}

/** 自动生成赛程 */
async function handleCreateSchedule() {
  if (!props.tournamentId) {
    ElMessage.warning('请先选择一个赛事')
    return
  }

  try {
    await ElMessageBox.confirm(
      '将为该赛事所有小组统一生成赛程，清除旧赛程。每天最多3场比赛，周末跳过。是否继续？',
      '确认创建赛程',
      { confirmButtonText: '确认创建', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  creating.value = true
  try {
    const params = {}
    if (createConfig.startDate) params.startDate = createConfig.startDate
    if (createConfig.location) params.location = createConfig.location

    const res = await tournamentApi.generateAllGroupSchedules(props.tournamentId, params)
    const schedule = res.data || []
    allMatches.value = schedule
    showCreateDialog.value = false
    resetCreateConfig()
    ElMessage.success(`赛程创建成功！共 ${schedule.length} 场比赛`)
  } catch (e) {
    ElMessage.error(e.message || '创建赛程失败')
  } finally {
    creating.value = false
  }
}

// ===== 计算属性 =====

/** 提取日期的 key（YYYY-MM-DD） */
function getDateKey(match) {
  if (!match.matchTime) return ''
  return match.matchTime.substring(0, 10)
}

/** 将所有比赛按日期分组，组内按时间排序 */
const matchesByDate = computed(() => {
  const map = {}
  if (!allMatches.value || allMatches.value.length === 0) return map

  // 先按 matchTime 排序
  const sorted = [...allMatches.value].sort((a, b) => {
    if (!a.matchTime && !b.matchTime) return 0
    if (!a.matchTime) return 1
    if (!b.matchTime) return -1
    return a.matchTime.localeCompare(b.matchTime)
  })

  for (const match of sorted) {
    const key = getDateKey(match)
    if (!key) continue
    if (!map[key]) map[key] = []
    map[key].push(match)
  }
  return map
})

/** 排序后的日期列表 */
const sortedDates = computed(() => {
  return Object.keys(matchesByDate.value).sort()
})

// ===== 方法 =====

/** 获取赛事的所有比赛 */
async function fetchMatches() {
  if (!props.tournamentId) {
    allMatches.value = []
    return
  }

  try {
    const res = await tournamentApi.getTournamentMatches(props.tournamentId)
    allMatches.value = res.data || []
  } catch (e) {
    allMatches.value = []
  }
}

/** 加载所有数据 */
async function fetchAllData() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    await fetchMatches()
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

/** 格式化为友好的日期标签 */
function formatDateLabel(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr.replace(/-/g, '/'))
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = d.getMonth() + 1
  const day = d.getDate()
  const wd = weekdays[d.getDay()]
  return `${month}月${day}日 ${wd}`
}

// ===== 监听 =====

/** 当赛事切换时重新加载 */
watch(() => props.tournamentId, () => {
  allMatches.value = []
  fetchAllData()
})

onMounted(() => {
  fetchAllData()
})
</script>

<style scoped>
.schedule-container {
  padding: 4px 0;
}

.loading-state {
  padding: 40px;
}

/* ---- 操作栏 ---- */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: var(--color-bg-gray, #f8f9fa);
  border-radius: 8px;
}

.action-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ---- 创建赛程对话框 ---- */
.dialog-hint {
  margin: 0;
  padding: 0 0 0 100px;
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
  line-height: 1.6;
}

/* ---- 日期区 ---- */
.date-section {
  margin-bottom: 28px;
}

.date-matches {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 12px;
}

.date-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-border, #e4e7ed);
}

.date-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
}

.date-count {
  font-size: 12px;
  color: var(--color-text-placeholder, #c0c4cc);
  margin-left: auto;
}

/* ---- 空状态 ---- */
.schedule-empty {
  margin-top: 40px;
}
</style>
