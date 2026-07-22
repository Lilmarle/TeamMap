<template>
  <div
    class="match-card"
    :class="statusClass"
  >
    <!-- 上层：比赛名称 + 修改按钮 -->
    <div class="card-top">
      <span class="card-top-left"></span>
      <span class="match-name" v-if="match.name">{{ match.name }}</span>
      <span class="match-name-placeholder" v-else></span>
      <el-button
        size="small"
        circle
        class="manage-btn"
        @click="showManageWin = true"
        title="管理比赛"
      >
        ✎
      </el-button>
    </div>

    <!-- 中层：比赛队伍 + 比分 -->
    <div class="card-main">
      <div class="team team-home">
        <el-avatar :size="32" :src="match.team1Logo">
          {{ match.team1Name?.charAt(0) }}
        </el-avatar>
        <span class="team-name">{{ match.team1ShortName || match.team1Name }}</span>
      </div>

      <div class="match-center">
        <div class="score">
          <span class="score-num" :class="{ 'score-win': match.team1Score > match.team2Score }">{{ match.team1Score ?? 0 }}</span>
          <span class="score-colon">:</span>
          <span class="score-num" :class="{ 'score-win': match.team2Score > match.team1Score }">{{ match.team2Score ?? 0 }}</span>
        </div>
      </div>

      <div class="team team-away">
        <span class="team-name">{{ match.team2ShortName || match.team2Name }}</span>
        <el-avatar :size="32" :src="match.team2Logo">
          {{ match.team2Name?.charAt(0) }}
        </el-avatar>
      </div>
    </div>

    <!-- 底层：时间（左） 状态（中） 地点（右） -->
    <div class="card-bottom">
      <span class="bottom-left">
        <span class="match-time" v-if="match.matchTime">{{ formatTime(match.matchTime) }}</span>
      </span>
      <span class="bottom-center">
        <el-tag
          :type="statusTagType"
          size="small"
          class="status-tag"
        >
          {{ statusLabel }}
        </el-tag>
      </span>
      <span class="bottom-right">
        <span class="match-location" v-if="match.location">{{ match.location }}</span>
      </span>
    </div>
  </div>

  <!-- 管理对话框 -->
  <EventMatchManage
    v-model:visible="showManageWin"
    :match="match"
    @updated="$emit('updated')"
  />
</template>

<script setup>
import { ref, computed } from 'vue'
import EventMatchManage from './EventMatchManage.vue'

const props = defineProps({
  match: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['updated'])
const showManageWin = ref(false)

// ===== 状态展示 =====
const statusClass = computed(() => {
  const m = props.match
  if (m.status >= 3) return 'match-finished'
  if (m.status === 2) return 'match-active'
  return 'match-pending'
})

const statusTagType = computed(() => {
  const s = props.match.status
  if (s === 1) return 'info'
  if (s === 2) return 'warning'
  return 'success'
})

const statusLabel = computed(() => {
  const s = props.match.status
  if (s === 1) return '未开始'
  if (s === 2) return '进行中'
  return '已结束'
})

/** 格式化时间 */
function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr.replace(/-/g, '/'))
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}
</script>

<style scoped>
.match-card {
  display: flex;
  flex-direction: column;
  padding: 14px 20px 12px;
  background: var(--color-bg-white, #fff);
  border: 1px solid var(--color-border, #e4e7ed);
  border-radius: 12px;
  transition: all 0.25s ease;
  position: relative;
}

.match-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: transparent;
}

.match-pending {
  border-left: 4px solid var(--el-color-info, #909399);
}

.match-active {
  border-left: 4px solid var(--el-color-warning, #e6a23c);
  background: linear-gradient(135deg, var(--el-color-warning-light-9, #fdf6ec) 0%, #fff 80%);
}

.match-finished {
  border-left: 4px solid var(--el-color-success, #67c23a);
}

/* ---- 上层 ---- */
.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  min-height: 28px;
}

.match-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  text-align: center;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.match-name-placeholder {
  flex: 1;
}

.manage-btn {
  width: 28px;
  height: 28px;
  padding: 0;
  font-size: 14px;
  line-height: 28px;
  cursor: pointer;
  flex-shrink: 0;
}

.card-top-left {
  width: 28px;
  flex-shrink: 0;
}

/* ---- 中层 ---- */
.card-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* ---- 球队 ---- */
.team {
  display: flex;
  align-items: center;
  gap: 10px;
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
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ---- 比分 ---- */
.match-center {
  flex-shrink: 0;
  text-align: center;
}

.score {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 22px;
  font-weight: 700;
}

.score-num {
  min-width: 24px;
  text-align: center;
  color: var(--color-text-primary, #303133);
}

.score-win {
  color: var(--el-color-success, #67c23a);
}

.score-colon {
  color: var(--color-text-secondary, #909399);
}

/* ---- 底层 ---- */
.card-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid var(--color-border, #f0f0f0);
}

.bottom-left,
.bottom-right {
  flex: 1;
  min-width: 0;
}

.bottom-left {
  text-align: left;
}

.bottom-center {
  text-align: center;
  flex-shrink: 0;
}

.bottom-right {
  text-align: right;
}

.match-time {
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
}

.match-location {
  font-size: 12px;
  color: var(--color-text-placeholder, #c0c4cc);
}

.status-tag {
  font-weight: 600;
  flex-shrink: 0;
}
</style>
