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
      <!-- 无小组 -->
      <el-empty v-if="groups.length === 0" description="暂无小组，请先在「小组赛」标签页创建小组并分配球队" :image-size="80" />

      <!-- 有小组 -->
      <template v-else>
        <!-- 操作栏 -->
        <div class="action-bar">
          <div class="action-left">
            <span class="action-title">小组赛程管理</span>
            <el-tag size="small" type="info" v-if="allMatchesCount > 0">
              共 {{ allMatchesCount }} 场比赛
            </el-tag>
          </div>
          <div class="action-right">
            <el-select v-model="selectedGroupId" placeholder="选择小组查看" size="small" style="width:160px;margin-right:8px">
              <el-option
                v-for="g in groups"
                :key="g.id"
                :label="g.name"
                :value="g.id"
              />
            </el-select>

            <!-- 生成当前小组赛程 -->
            <el-popover
              placement="bottom"
              :width="360"
              trigger="click"
              :hide-after="0"
              :disabled="!selectedGroupId"
            >
              <template #reference>
                <el-button size="small" :disabled="!selectedGroupId" :loading="generating">
                  ⚡ 生成小组赛程
                </el-button>
              </template>
              <div class="generate-config">
                <h4 class="config-title">生成「{{ getSelectedGroupName() }}」赛程</h4>
                <p class="config-hint">将清除该小组已有的赛程并重新生成</p>
                <div class="config-actions" style="justify-content:center">
                  <el-button type="primary" size="small" :loading="generating" @click="handleGenerate">
                    {{ generating ? '生成中...' : '确认生成' }}
                  </el-button>
                </div>
              </div>
            </el-popover>

            <!-- 一键生成全部小组赛程 -->
            <el-popover
              placement="bottom"
              :width="360"
              trigger="click"
              :hide-after="0"
            >
              <template #reference>
                <el-button type="primary" size="small">
                  🚀 一键生成全部
                </el-button>
              </template>
              <div class="generate-config">
                <h4 class="config-title">为所有小组统一排赛程</h4>
                <el-form label-width="100px" size="small">
                  <el-form-item label="开始日期">
                    <el-date-picker
                      v-model="genConfig.startDate"
                      type="date"
                      placeholder="默认当天"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      style="width:100%"
                    />
                  </el-form-item>
                  <el-form-item label="比赛地点">
                    <el-input v-model="genConfig.location" placeholder="可选（所有比赛共用）" clearable />
                  </el-form-item>
                  <el-form-item>
                    <div class="config-actions">
                      <el-button type="primary" size="small" :loading="generatingAll" @click="handleGenerateAll">
                        {{ generatingAll ? '生成中...' : '确认生成全部' }}
                      </el-button>
                      <el-button size="small" @click="genConfig = getDefaultConfig()">重置</el-button>
                    </div>
                  </el-form-item>
                </el-form>
                <p class="config-hint">
                  每天固定时段 <strong>12:00 · 14:00 · 16:00</strong>，每天最多3场，周末跳过，所有小组同步打同一轮
                </p>
              </div>
            </el-popover>
          </div>
        </div>

        <!-- 按轮次分组的赛程展示 -->
        <div class="schedule-content" v-if="Object.keys(roundsMap).length > 0">
          <div
            v-for="(round, roundIndex) in sortedRounds"
            :key="roundIndex"
            class="round-section"
          >
            <div class="round-header">
              <el-tag :type="roundIndex < 3 ? 'danger' : ''" effect="dark" size="small" class="round-badge">
                第 {{ roundIndex + 1 }} 轮
              </el-tag>
              <span class="round-meta" v-if="round[0]?.matchTime">
                {{ formatDate(round[0].matchTime) }}
              </span>
              <span class="round-count">{{ round.length }} 场</span>
            </div>
            <div class="round-matches">
              <div
                v-for="match in round"
                :key="match.id"
                class="match-card"
                :class="getMatchStatusClass(match)"
              >
                <!-- 主队 -->
                <div class="team team-home">
                  <el-avatar :size="28" :src="match.team1Logo">
                    {{ match.team1Name?.charAt(0) }}
                  </el-avatar>
                  <span class="team-name">{{ match.team1ShortName || match.team1Name }}</span>
                </div>

                <!-- 比分 / VS -->
                <div class="match-center">
                  <div class="score" v-if="match.status >= 3">
                    <span class="score-num" :class="{ 'score-win': match.team1Score > match.team2Score }">{{ match.team1Score }}</span>
                    <span class="score-colon">:</span>
                    <span class="score-num" :class="{ 'score-win': match.team2Score > match.team1Score }">{{ match.team2Score }}</span>
                  </div>
                  <div class="vs-text" v-else>VS</div>
                  <div class="match-time" v-if="match.matchTime">
                    {{ formatTime(match.matchTime) }}
                  </div>
                  <div class="match-location" v-if="match.location">
                    {{ match.location }}
                  </div>
                </div>

                <!-- 客队 -->
                <div class="team team-away">
                  <span class="team-name">{{ match.team2ShortName || match.team2Name }}</span>
                  <el-avatar :size="28" :src="match.team2Logo">
                    {{ match.team2Name?.charAt(0) }}
                  </el-avatar>
                </div>

                <!-- 状态标签 -->
                <div class="match-status-tag">
                  <el-tag v-if="match.status === 1" size="small" type="info">未开始</el-tag>
                  <el-tag v-else-if="match.status === 2" size="small" type="warning">进行中</el-tag>
                  <el-tag v-else-if="match.status >= 3" size="small" type="success">已结束</el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 无赛程 -->
        <el-empty
          v-else
          description="暂无赛程数据，选择小组后点击「生成赛程」自动生成"
          :image-size="60"
          class="schedule-empty"
        />
      </template>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  tournamentId: { type: Number, default: null }
})

// ===== 基础数据 =====
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const groups = ref([])
const selectedGroupId = ref(null)
const allMatches = ref([])
const generating = ref(false)
const generatingAll = ref(false)

// ===== 生成配置 =====
function getDefaultConfig() {
  return {
    startDate: '',
    location: ''
  }
}
const genConfig = reactive(getDefaultConfig())

// ===== 计算属性 =====
/** 将所有比赛按 matchTime 分组为轮次 */
const roundsMap = computed(() => {
  const map = {}
  if (!allMatches.value || allMatches.value.length === 0) return map

  // 按 matchTime 排序
  const sorted = [...allMatches.value].sort((a, b) => {
    if (!a.matchTime && !b.matchTime) return 0
    if (!a.matchTime) return 1
    if (!b.matchTime) return -1
    return a.matchTime.localeCompare(b.matchTime)
  })

  // 按 matchTime 分组（相同日期的算一轮）
  let roundIdx = 0
  let currentDate = ''
  for (const match of sorted) {
    const dateKey = match.matchTime ? match.matchTime.substring(0, 10) : ''
    if (dateKey !== currentDate) {
      currentDate = dateKey
      roundIdx++
    }
    if (!map[roundIdx]) map[roundIdx] = []
    map[roundIdx].push(match)
  }
  return map
})

/** 排序后的轮次列表 */
const sortedRounds = computed(() => {
  const keys = Object.keys(roundsMap.value).sort((a, b) => Number(a) - Number(b))
  return keys.map(k => roundsMap.value[k])
})

const allMatchesCount = computed(() => allMatches.value.length)

// ===== 方法 =====

/** 获取当前选中小组的赛程 */
async function fetchGroupSchedule() {
  if (!selectedGroupId.value) {
    allMatches.value = []
    return
  }

  try {
    const res = await tournamentApi.getGroupStageMatches(selectedGroupId.value)
    allMatches.value = res.data || []
  } catch (e) {
    ElMessage.error('获取赛程失败')
    allMatches.value = []
  }
}

/** 获取小组列表 */
async function fetchGroups() {
  if (!props.tournamentId) {
    groups.value = []
    return
  }

  try {
    const res = await tournamentApi.getGroupStages(props.tournamentId)
    groups.value = res.data || []
    // 默认选中第一个小组
    if (groups.value.length > 0 && !selectedGroupId.value) {
      selectedGroupId.value = groups.value[0].id
    }
  } catch (e) {
    ElMessage.error('获取小组列表失败')
    groups.value = []
  }
}

/** 加载所有数据 */
async function fetchAllData() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    await fetchGroups()
    if (selectedGroupId.value) {
      await fetchGroupSchedule()
    }
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

/** 获取选中小组的名称 */
function getSelectedGroupName() {
  const g = groups.value.find(g => g.id === selectedGroupId.value)
  return g ? g.name : ''
}

/** 为所有小组统一生成赛程 */
async function handleGenerateAll() {
  if (!props.tournamentId) {
    ElMessage.warning('请先选择一个赛事')
    return
  }

  try {
    await ElMessageBox.confirm(
      '将为该赛事所有小组统一生成赛程，清除旧赛程。每天最多3场比赛，周末跳过。是否继续？',
      '确认生成全部赛程',
      { confirmButtonText: '确认生成', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  generatingAll.value = true
  try {
    const params = {}
    if (genConfig.startDate) params.startDate = genConfig.startDate
    if (genConfig.location) params.location = genConfig.location

    const res = await tournamentApi.generateAllGroupSchedules(props.tournamentId, params)
    const schedule = res.data || []
    allMatches.value = schedule
    ElMessage.success(`全部赛程生成成功！共 ${schedule.length} 场比赛`)
  } catch (e) {
    ElMessage.error(e.message || '生成全部赛程失败')
  } finally {
    generatingAll.value = false
  }
}

/** 生成赛程 */
async function handleGenerate() {
  if (!selectedGroupId.value) {
    ElMessage.warning('请先选择一个小组')
    return
  }

  // 确认对话框
  try {
    await ElMessageBox.confirm(
      '生成赛程将清除该小组已有的赛程并重新生成，是否继续？',
      '确认生成赛程',
      {
        confirmButtonText: '确认生成',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return // 取消操作
  }

  generating.value = true
  try {
    const params = {}
    if (genConfig.startDate) params.startDate = genConfig.startDate
    if (genConfig.location) params.location = genConfig.location

    const res = await tournamentApi.generateGroupSchedule(selectedGroupId.value, params)
    const schedule = res.data || []
    allMatches.value = schedule
    ElMessage.success(`赛程生成成功！共 ${schedule.length} 场比赛`)
  } catch (e) {
    ElMessage.error(e.message || '生成赛程失败')
  } finally {
    generating.value = false
  }
}

/** 根据比赛状态获取 CSS class */
function getMatchStatusClass(match) {
  if (match.status >= 3) return 'match-finished'
  if (match.status === 2) return 'match-active'
  return 'match-pending'
}

/** 格式化日期 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr.replace(/-/g, '/'))
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

/** 格式化时间 */
function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr.replace(/-/g, '/'))
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

// ===== 监听 =====
watch(selectedGroupId, () => {
  fetchGroupSchedule()
})

/** 当赛事切换时重新加载 */
watch(() => props.tournamentId, () => {
  selectedGroupId.value = null
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
}

/* ---- 生成配置弹窗 ---- */
.generate-config {
  padding: 4px 0;
}

.config-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
}

.config-actions {
  display: flex;
  gap: 8px;
}

.config-hint {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
}

.unit {
  margin-left: 4px;
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
}

/* ---- 轮次区 ---- */
.round-section {
  margin-bottom: 24px;
}

.round-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-border, #e4e7ed);
}

.round-badge {
  font-weight: 700;
  letter-spacing: 1px;
}

.round-meta {
  font-size: 13px;
  color: var(--color-text-secondary, #909399);
}

.round-count {
  font-size: 12px;
  color: var(--color-text-placeholder, #c0c4cc);
  margin-left: auto;
}

/* ---- 比赛卡 ---- */
.round-matches {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 12px;
}

.match-card {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: var(--color-bg-white, #fff);
  border: 1px solid var(--color-border, #ebeef5);
  border-radius: 8px;
  transition: all 0.2s ease;
  gap: 8px;
}

.match-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.match-pending {
  border-left: 3px solid var(--el-color-info, #909399);
}

.match-active {
  border-left: 3px solid var(--el-color-warning, #e6a23c);
  background: var(--el-color-warning-light-9, #fdf6ec);
}

.match-finished {
  border-left: 3px solid var(--el-color-success, #67c23a);
}

/* ---- 球队 ---- */
.team {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.team-home {
  justify-content: flex-start;
}

.team-away {
  justify-content: flex-end;
}

.team-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-primary, #303133);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 80px;
}

/* ---- 比赛中心 ---- */
.match-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
  flex-shrink: 0;
}

.score {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 18px;
  font-weight: 700;
}

.score-num {
  min-width: 20px;
  text-align: center;
  color: var(--color-text-primary, #303133);
}

.score-win {
  color: var(--el-color-success, #67c23a);
}

.score-colon {
  color: var(--color-text-secondary, #909399);
}

.vs-text {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-secondary, #909399);
  letter-spacing: 1px;
}

.match-time {
  font-size: 11px;
  color: var(--color-text-secondary, #909399);
  margin-top: 2px;
}

.match-location {
  font-size: 10px;
  color: var(--color-text-placeholder, #c0c4cc);
  margin-top: 1px;
}

/* ---- 状态标签 ---- */
.match-status-tag {
  position: absolute;
  top: -6px;
  right: -6px;
}

.match-card {
  position: relative;
}

/* ---- 空状态 ---- */
.schedule-empty {
  margin-top: 40px;
}
</style>
