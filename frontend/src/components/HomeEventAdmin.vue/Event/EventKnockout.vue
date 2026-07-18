<template>
  <div class="knockout-container">
    <AsyncContent
      :loading="loading"
      :load-failed="loadFailed"
      :error-message="errorMessage"
      :is-empty="allRounds.length === 0"
      empty-description="暂无淘汰赛数据，请点击上方按钮添加比赛"
      @retry="fetchKnockoutMatches"
    >
      <!-- 操作栏 -->
      <div class="knockout-toolbar">
        <div class="toolbar-left">
          <span class="toolbar-title">淘汰赛对阵图</span>
          <el-tag v-if="allRounds.length > 0" size="small" type="info">
            共 {{ totalMatches }} 场比赛
          </el-tag>
        </div>
        <div class="toolbar-right">
          <el-button size="small" type="primary" @click="showAddDialog = true">
            ＋ 添加比赛
          </el-button>
        </div>
      </div>

      <!-- 对阵图 -->
      <div class="bracket-wrapper" v-if="allRounds.length > 0">
        <div
          v-for="(round, rIdx) in allRounds"
          :key="round.stage"
          class="bracket-column"
        >
          <div class="round-header">{{ round.name }}</div>

          <div
            v-for="match in round.matches"
            :key="match.id"
            class="bracket-node"
            :class="getMatchStatusClass(match)"
            @click="openEditDialog(match)"
          >
            <!-- 队伍1 -->
            <div class="bracket-team" :class="{ 'is-winner': match.status >= 3 && match.team1Score > match.team2Score }">
              <div class="team-info">
                <img v-if="match.team1Logo" :src="match.team1Logo" class="team-logo" @error="$event.target.style.display='none'" />
                <span v-else class="team-logo-fallback">{{ match.team1Name?.charAt(0) }}</span>
                <span class="team-name">{{ match.team1ShortName || match.team1Name }}</span>
              </div>
              <span class="team-score" :class="{ 'score-high': match.status >= 3 && match.team1Score > match.team2Score }">
                {{ match.team1Score ?? '-' }}
              </span>
            </div>

            <!-- 队伍2 -->
            <div class="bracket-team" :class="{ 'is-winner': match.status >= 3 && match.team2Score > match.team1Score }">
              <div class="team-info">
                <img v-if="match.team2Logo" :src="match.team2Logo" class="team-logo" @error="$event.target.style.display='none'" />
                <span v-else class="team-logo-fallback">{{ match.team2Name?.charAt(0) }}</span>
                <span class="team-name">{{ match.team2ShortName || match.team2Name }}</span>
              </div>
              <span class="team-score" :class="{ 'score-high': match.status >= 3 && match.team2Score > match.team1Score }">
                {{ match.team2Score ?? '-' }}
              </span>
            </div>

            <div class="match-status-tag">
              <el-tag :type="statusTagType(match.status)" size="small" effect="plain">
                {{ statusLabel(match.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </AsyncContent>

    <!-- 添加比赛对话框 -->
    <el-dialog v-model="showAddDialog" title="添加淘汰赛比赛" width="460px" :close-on-click-modal="false">
      <el-form :model="addForm" label-width="100px" size="small">
        <el-form-item label="比赛阶段" required>
          <el-select v-model="addForm.stage" placeholder="选择阶段" style="width:100%">
            <el-option v-for="s in knockoutStages" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍1" required>
          <el-select v-model="addForm.team1Id" placeholder="选择队伍" filterable style="width:100%">
            <el-option v-for="t in teams" :key="t.teamId" :label="t.teamName" :value="t.teamId" />
          </el-select>
        </el-form-item>
        <el-form-item label="队伍2" required>
          <el-select v-model="addForm.team2Id" placeholder="选择队伍" filterable style="width:100%">
            <el-option v-for="t in teams" :key="t.teamId" :label="t.teamName" :value="t.teamId" />
          </el-select>
        </el-form-item>
        <el-form-item label="比赛名称">
          <el-input v-model="addForm.name" placeholder="可选：比赛名称" />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-date-picker v-model="addForm.matchTime" type="datetime" placeholder="选择时间" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="比赛地点">
          <el-input v-model="addForm.location" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="showAddDialog = false">取消</el-button>
        <el-button size="small" type="primary" :loading="submitting" @click="handleAdd">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- 编辑比赛对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑比赛" width="420px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="90px" size="small">
        <el-form-item label="队伍1">
          <div class="edit-team-row">
            <span class="edit-team-name">{{ editForm.team1Name }}</span>
            <el-input-number v-model="editForm.team1Score" :min="0" :max="99" size="small" controls-position="right" style="width:120px" />
          </div>
        </el-form-item>
        <el-form-item label="队伍2">
          <div class="edit-team-row">
            <span class="edit-team-name">{{ editForm.team2Name }}</span>
            <el-input-number v-model="editForm.team2Score" :min="0" :max="99" size="small" controls-position="right" style="width:120px" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width:100%">
            <el-option :value="1" label="未开始" />
            <el-option :value="2" label="进行中" />
            <el-option :value="3" label="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item label="比赛名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-date-picker v-model="editForm.matchTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="比赛地点">
          <el-input v-model="editForm.location" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="showEditDialog = false">取消</el-button>
        <el-button size="small" type="primary" :loading="saving" @click="handleUpdate">保存</el-button>
        <el-button v-if="editForm.id" size="small" type="danger" :loading="deleting" @click="handleDelete(editForm.id)" style="float:left">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, reactive } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage, ElMessageBox } from 'element-plus'
import AsyncContent from '@/components/General/AsyncContent.vue'

const props = defineProps({
  tournamentId: { type: Number, default: null },
  teams: { type: Array, default: () => [] }
})

const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const knockoutMatches = ref([])
const submitting = ref(false)
const saving = ref(false)
const deleting = ref(false)
const showAddDialog = ref(false)
const showEditDialog = ref(false)

/** 淘汰赛阶段选项 */
const knockoutStages = [
  { value: 4, label: '八分之一决赛' },
  { value: 5, label: '四分之一决赛' },
  { value: 6, label: '半决赛' },
  { value: 7, label: '三四名决赛' },
  { value: 8, label: '决赛' }
]

const STAGE_ORDER = { 4: 0, 5: 1, 6: 2, 7: 3, 8: 4 }
const STAGE_NAMES = { 4: '八分之一决赛', 5: '四分之一决赛', 6: '半决赛', 7: '三四名决赛', 8: '决赛' }

/** 添加表单 */
const addForm = reactive({
  stage: 6,
  team1Id: null,
  team2Id: null,
  name: '',
  matchTime: '',
  location: ''
})

/** 编辑表单 */
const editForm = reactive({
  id: null,
  team1Name: '',
  team2Name: '',
  team1Score: 0,
  team2Score: 0,
  status: 1,
  name: '',
  matchTime: '',
  location: ''
})

/** 按轮次分组的淘汰赛比赛 */
const allRounds = computed(() => {
  if (!knockoutMatches.value.length) return []
  const groups = {}
  for (const m of knockoutMatches.value) {
    if (!groups[m.stage]) groups[m.stage] = []
    groups[m.stage].push(m)
  }
  return Object.entries(groups)
    .map(([stage, matches]) => ({
      stage: Number(stage),
      name: STAGE_NAMES[stage] || `第${stage}轮`,
      order: STAGE_ORDER[stage] ?? 99,
      matches
    }))
    .sort((a, b) => a.order - b.order)
})

const totalMatches = computed(() => knockoutMatches.value.length)

// ---- 状态辅助 ----
function statusTagType(status) {
  return status >= 3 ? 'success' : status === 2 ? 'warning' : 'info'
}
function statusLabel(status) {
  return status >= 3 ? '已结束' : status === 2 ? '进行中' : '未开始'
}
function getMatchStatusClass(match) {
  return match.status >= 3 ? 'match-done' : match.status === 2 ? 'match-live' : 'match-pending'
}

/** 获取淘汰赛比赛 */
async function fetchKnockoutMatches() {
  if (!props.tournamentId) { knockoutMatches.value = []; return }
  loading.value = true; loadFailed.value = false; errorMessage.value = ''
  try {
    const res = await tournamentApi.getTournamentMatches(props.tournamentId)
    knockoutMatches.value = (res.data || []).filter(m => m.stage >= 4)
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取淘汰赛数据失败'
    knockoutMatches.value = []
  } finally { loading.value = false }
}

/** 打开编辑对话框 */
function openEditDialog(match) {
  editForm.id = match.id
  editForm.team1Name = match.team1ShortName || match.team1Name
  editForm.team2Name = match.team2ShortName || match.team2Name
  editForm.team1Score = match.team1Score ?? 0
  editForm.team2Score = match.team2Score ?? 0
  editForm.status = match.status ?? 1
  editForm.name = match.name || ''
  editForm.matchTime = match.matchTime || ''
  editForm.location = match.location || ''
  showEditDialog.value = true
}

/** 添加比赛 */
async function handleAdd() {
  if (!addForm.team1Id || !addForm.team2Id) {
    ElMessage.warning('请选择两支队伍')
    return
  }
  if (addForm.team1Id === addForm.team2Id) {
    ElMessage.warning('两支队伍不能相同')
    return
  }
  submitting.value = true
  try {
    await tournamentApi.addMatch({
      tournamentId: props.tournamentId,
      team1Id: addForm.team1Id,
      team2Id: addForm.team2Id,
      stage: addForm.stage,
      name: addForm.name || undefined,
      matchTime: addForm.matchTime || undefined,
      location: addForm.location || undefined
    })
    ElMessage.success('比赛添加成功')
    showAddDialog.value = false
    addForm.team1Id = null; addForm.team2Id = null; addForm.name = ''
    addForm.matchTime = ''; addForm.location = ''
    await fetchKnockoutMatches()
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  } finally { submitting.value = false }
}

/** 更新比赛 */
async function handleUpdate() {
  saving.value = true
  try {
    await tournamentApi.updateMatch(editForm.id, {
      team1Score: editForm.team1Score,
      team2Score: editForm.team2Score,
      status: editForm.status,
      name: editForm.name || undefined,
      matchTime: editForm.matchTime || undefined,
      location: editForm.location || undefined
    })
    ElMessage.success('保存成功')
    showEditDialog.value = false
    await fetchKnockoutMatches()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally { saving.value = false }
}

/** 删除比赛 */
async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这场比赛吗？', '确认删除', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning', confirmButtonClass: 'el-button--danger'
    })
  } catch { return }

  deleting.value = true
  try {
    const { default: request } = await import('@/api/request')
    await request.delete(`/match/${id}`)
    ElMessage.success('比赛已删除')
    showEditDialog.value = false
    await fetchKnockoutMatches()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  } finally { deleting.value = false }
}

watch(() => props.tournamentId, () => fetchKnockoutMatches())
onMounted(() => fetchKnockoutMatches())
</script>

<style scoped>
.knockout-container { min-height: 200px; }

/* ---- 操作栏 ---- */
.knockout-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 20px; padding: 12px 16px;
  background: var(--color-bg-gray, #f8f9fa); border-radius: 8px;
}
.toolbar-left { display: flex; align-items: center; gap: 10px; }
.toolbar-title { font-size: 16px; font-weight: 600; color: var(--color-text-primary); }

/* ---- 对阵图 ---- */
.bracket-wrapper {
  display: flex; gap: 0; padding: 20px 0; overflow-x: auto; min-height: 350px;
}
.bracket-column {
  display: flex; flex-direction: column; align-items: center; min-width: 210px; flex-shrink: 0;
}
.bracket-column + .bracket-column {
  margin-left: 32px; padding-left: 32px;
  border-left: 2px solid var(--color-border, #e4e7ed);
}
.round-header {
  font-size: 13px; font-weight: 600; color: var(--color-text-secondary);
  text-align: center; margin-bottom: 16px; padding: 4px 14px;
  background: var(--color-bg-page, #f5f7fa); border-radius: 4px; white-space: nowrap;
}

/* ---- 比赛节点 ---- */
.bracket-node {
  position: relative; width: 100%; cursor: pointer;
  background: var(--color-bg-white, #fff);
  border: 1px solid var(--color-border, #e4e7ed); border-radius: 6px; overflow: hidden;
  transition: box-shadow 0.2s;
}
.bracket-node:hover { box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.bracket-node + .bracket-node { margin-top: 10px; }

/* ---- 队伍行 ---- */
.bracket-team {
  display: flex; align-items: center; justify-content: space-between;
  padding: 7px 10px; gap: 8px; transition: background-color 0.2s;
}
.bracket-team + .bracket-team { border-top: 1px solid var(--color-border, #f0f0f0); }
.bracket-team.is-winner { background-color: rgba(64,158,255,0.06); }

.team-info { display: flex; align-items: center; gap: 6px; min-width: 0; flex: 1; }
.team-logo { width: 22px; height: 22px; border-radius: 50%; object-fit: cover; flex-shrink: 0; background: var(--color-bg-page); }
.team-logo-fallback { display: inline-flex; align-items: center; justify-content: center; width: 22px; height: 22px; border-radius: 50%; flex-shrink: 0; background: var(--color-bg-page); color: var(--color-text-secondary); font-size: 10px; font-weight: 600; }
.team-name { font-size: 12px; font-weight: 500; color: var(--color-text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.team-score { font-size: 14px; font-weight: 700; color: var(--color-text-secondary); min-width: 22px; text-align: center; flex-shrink: 0; }
.team-score.score-high { color: var(--el-color-success, #67c23a); }

.match-status-tag { position: absolute; top: -6px; right: -6px; }
.match-done { border-color: var(--el-color-success-light-5, #b3e19d); }
.match-live { border-color: var(--el-color-warning, #e6a23c); box-shadow: 0 0 0 1px var(--el-color-warning-light-7); }
.match-pending { border-color: var(--color-border); }

/* ---- 编辑对话框 ---- */
.edit-team-row { display: flex; align-items: center; justify-content: space-between; width: 100%; }
.edit-team-name { font-weight: 500; font-size: 14px; }
</style>
