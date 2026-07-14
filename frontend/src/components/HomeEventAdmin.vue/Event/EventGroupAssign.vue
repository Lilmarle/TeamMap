<template>
  <el-dialog
    :model-value="visible"
    :title="dialogTitle"
    :width="assignScope === 'single' ? '500px' : '860px'"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="assign-content">
      <!-- 分配范围切换（仅当未指定具体小组时可切换） -->
      <div v-if="!fixedGroup" class="scope-bar">
        <el-radio-group v-model="assignScope" size="small">
          <el-radio-button value="single">分配当前小组</el-radio-button>
          <el-radio-button value="all">分配全部小组</el-radio-button>
        </el-radio-group>
        <span class="scope-hint" v-if="assignScope === 'single' && groupStages.length > 0">
          选择小组：<el-select v-model="selectedSingleGroupId" size="small" style="width:120px">
            <el-option
              v-for="g in groupStages"
              :key="g.id"
              :label="g.name"
              :value="g.id"
            />
          </el-select>
        </span>
      </div>

      <p class="assign-hint">{{ assignHint }}</p>

      <!-- 加载小组 -->
      <div v-if="loadingGroups" class="loading-state">
        <el-skeleton :rows="2" animated />
      </div>

      <!-- 无小组 -->
      <el-empty v-else-if="groupStages.length === 0" description="请先创建小组" :image-size="60" />

      <!-- 无可分配球队 -->
      <el-empty v-else-if="availableTeams.length === 0" description="暂无可用球队" :image-size="60" />

      <template v-else>
        <!-- ====== 单组分配模式 ====== -->
        <template v-if="assignScope === 'single'">
          <div class="single-assign">
            <el-checkbox-group v-model="selectedTeamIds">
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
        </template>

        <!-- ====== 全部小组分配模式 ====== -->
        <template v-if="assignScope === 'all'">
          <!-- 模式切换 -->
          <div class="sub-mode-bar">
            <el-radio-group v-model="allMode" size="small">
              <el-radio-button value="manual">手动勾选</el-radio-button>
              <el-radio-button value="tiered">分档随机抽签</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 手动模式 -->
          <template v-if="allMode === 'manual'">
            <div class="global-actions">
              <el-button size="small" @click="autoDistributeTeams">自动均分球队</el-button>
              <el-button size="small" @click="clearAllSelections">清空所有选择</el-button>
            </div>
            <div
              v-for="group in sortedGroups"
              :key="group.id"
              class="assign-group-item"
            >
              <div class="assign-group-header">
                <span class="assign-group-title">
                  {{ group.name }}
                  <el-tag size="small" type="info">
                    已选 {{ selectedMap[group.id]?.length || 0 }}/{{ group.teamCount || '∞' }} 队
                  </el-tag>
                </span>
                <el-button size="small" text @click="groupExpandMap[group.id] = !groupExpandMap[group.id]">
                  {{ groupExpandMap[group.id] ? '收起' : '展开' }}
                </el-button>
              </div>
              <div v-if="groupExpandMap[group.id]" class="assign-group-teams">
                <el-checkbox-group v-model="selectedMap[group.id]">
                  <el-checkbox
                    v-for="team in availableTeams"
                    :key="team.teamId"
                    :label="team.teamId"
                    :disabled="isTeamAssignedToOtherGroup(team.teamId, group.id)"
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
              </div>
            </div>
          </template>

          <!-- 分档随机抽签模式 -->
          <template v-if="allMode === 'tiered'">
            <div class="tiered-area">
              <div class="tier-actions">
                <el-button size="small" type="primary" @click="autoSuggestTiers">智能建议分档</el-button>
                <el-button size="small" @click="addTier">添加档位</el-button>
                <el-button size="small" @click="clearAllTiers">清空分档</el-button>
              </div>

              <!-- 未分档球队池 -->
              <div class="unassigned-pool">
                <div class="pool-header">
                  <span class="pool-title">未分档球队 ({{ unassignedTeams.length }})</span>
                  <span class="pool-hint">点击球队选择目标档位，关闭按钮加入第一档</span>
                </div>
                <div class="pool-teams">
                  <el-popover
                    v-for="team in unassignedTeams"
                    :key="team.teamId"
                    :ref="el => setPoolPopoverRef(el, team.teamId)"
                    trigger="click"
                    placement="bottom"
                    :width="160"
                    :hide-after="0"
                  >
                    <template #reference>
                      <el-tag closable class="team-tag" @close="moveToTier(team, 0)">
                        <el-avatar :size="16" :src="team.teamLogo" style="margin-right:4px">
                          {{ team.teamName?.charAt(0) }}
                        </el-avatar>
                        {{ team.teamShortName || team.teamName }}
                      </el-tag>
                    </template>
                    <div class="tier-select-popup">
                      <div class="tier-select-title">加入哪个档位？</div>
                      <el-button
                        v-for="(tier, tIdx) in tiers"
                        :key="tIdx"
                        size="small"
                        :type="tIdx === 0 ? 'primary' : 'default'"
                        class="tier-select-btn"
                        @click="moveToTier(team, tIdx); closePoolPopover(team.teamId)"
                      >
                        {{ getTierLabel(tIdx) }} ({{ tier.teams.length }}队)
                      </el-button>
                    </div>
                  </el-popover>
                  <span v-if="unassignedTeams.length === 0" class="pool-empty">全部已分档</span>
                </div>
              </div>

              <!-- 档位列表 -->
              <div v-for="(tier, tIdx) in tiers" :key="tIdx" class="tier-item">
                <div class="tier-header">
                  <div class="tier-title">
                    <span class="tier-name">{{ getTierLabel(tIdx) }}</span>
                    <el-tag size="small" :type="tier.teams.length > 0 ? 'success' : 'info'">
                      {{ tier.teams.length }} 队
                    </el-tag>
                  </div>
                  <div class="tier-header-actions">
                    <el-button size="small" text :disabled="tier.teams.length === 0" @click="randomShuffleTier(tIdx)">
                      随机打乱
                    </el-button>
                    <el-button v-if="tiers.length > 1" size="small" text type="danger" @click="removeTier(tIdx)">
                      删除
                    </el-button>
                  </div>
                </div>
                <div class="tier-teams">
                  <el-tag
                    v-for="team in tier.teams"
                    :key="team.teamId"
                    closable
                    class="team-tag"
                    @close="moveOutOfTier(team, tIdx)"
                  >
                    <el-avatar :size="16" :src="team.teamLogo" style="margin-right:4px">
                      {{ team.teamName?.charAt(0) }}
                    </el-avatar>
                    {{ team.teamShortName || team.teamName }}
                  </el-tag>
                  <span v-if="tier.teams.length === 0" class="tier-empty">点击「智能建议分档」或手动拖入球队</span>
                </div>
              </div>

              <!-- 抽签状态提示 -->
              <el-alert
                v-if="tiersReadyForDraw && !drawState.isComplete"
                title="可以开始抽签"
                type="success"
                :closable="false"
                show-icon
                class="draw-alert"
              >
                <template #default>
                  共 {{ tiers.length }} 个档位,
                  <span v-for="(tier, tIdx2) in tiers" :key="tIdx2">
                    {{ getTierLabel(tIdx2) }}{{ tier.teams.length }}队<template v-if="tIdx2 < tiers.length - 1">、</template>
                  </span>
                </template>
              </el-alert>
              <el-alert v-else-if="!tiersReadyForDraw" title="请至少将一个球队放入档位" type="info" :closable="false" show-icon class="draw-alert" />

              <!-- 抽签操作区 -->
              <div class="draw-section">
                <!-- 逐队抽签按钮 -->
                <el-button
                  type="danger"
                  size="small"
                  :disabled="!tiersReadyForDraw || drawState.isComplete || drawState.noMoreTeams"
                  @click="drawNextTeam"
                  class="draw-step-btn"
                >
                  <template v-if="!drawState.isStarted">
                    🎯 开始逐队抽签
                  </template>
                  <template v-else-if="!drawState.isComplete">
                    🎯 抽下一支球队
                    <small>({{ drawState.currentPotLabel }})</small>
                  </template>
                  <template v-else>
                    ✅ 抽签完成
                  </template>
                </el-button>

                <!-- 一键自动抽签 -->
                <el-button size="small" :disabled="!tiersReadyForDraw || drawState.isStarted" @click="runSnakeDraw">
                  一键全部抽签
                </el-button>

                <!-- 重置抽签 -->
                <el-button
                  v-if="drawState.isStarted"
                  size="small"
                  text
                  @click="resetDraw"
                >
                  重置
                </el-button>
              </div>

              <!-- 当前抽签状态 -->
              <div v-if="drawState.isStarted" class="draw-status-bar">
                <div class="draw-progress">
                  <el-progress
                    :percentage="drawState.progressPercent"
                    :stroke-width="12"
                    :text-inside="true"
                    :status="drawState.isComplete ? 'success' : ''"
                  >
                    {{ drawState.drawnCount }} / {{ drawState.totalTeams }}
                  </el-progress>
                </div>
                <div class="draw-current-info">
                  <span v-if="drawState.lastDraw">
                    <strong>抽取：</strong>
                    <el-tag size="small" effect="dark" type="warning" class="drawn-team-tag">
                      <el-avatar :size="14" :src="drawState.lastDraw.team.teamLogo" style="margin-right:3px">
                        {{ drawState.lastDraw.team.teamName?.charAt(0) }}
                      </el-avatar>
                      {{ drawState.lastDraw.team.teamShortName || drawState.lastDraw.team.teamName }}
                    </el-tag>
                    → <strong>{{ drawState.lastDraw.groupName }}</strong>
                  </span>
                  <span v-else-if="!drawState.isComplete">
                    点击「🎯 开始逐队抽签」开始
                  </span>
                  <span v-else>✅ 所有球队已抽完</span>
                </div>
              </div>

              <!-- 抽签结果 -->
              <div v-if="drawState.isStarted || lastDrawResult" class="draw-preview">
                <div class="draw-preview-title">抽签结果：</div>
                <div class="draw-preview-groups">
                  <div v-for="group in sortedGroups" :key="group.id" class="preview-group">
                    <div class="preview-group-name">{{ group.name }}</div>
                    <div class="preview-group-teams">
                      <el-tag
                        v-for="(team, ti) in (drawState.result[group.id] || [])"
                        :key="team.teamId"
                        size="small"
                        class="preview-team"
                        :class="{ 'preview-team-latest': drawState.lastDraw && drawState.lastDraw.team.teamId === team.teamId && ti === (drawState.result[group.id] || []).length - 1 }"
                      >
                        <el-avatar :size="14" :src="team.teamLogo" style="margin-right:3px">
                          {{ team.teamName?.charAt(0) }}
                        </el-avatar>
                        {{ team.teamShortName || team.teamName }}
                      </el-tag>
                    </div>
                  </div>
                </div>
                <el-button
                  v-if="drawState.drawnCount > 0"
                  size="small"
                  type="primary"
                  @click="applyDrawResult"
                  class="apply-btn"
                >
                  应用抽签结果到小组
                </el-button>
              </div>
            </div>
          </template>
        </template>
      </template>
    </div>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button
        type="primary"
        :disabled="(assignScope === 'single' ? selectedTeamIds.length === 0 : totalSelectedTeams === 0) || groupStages.length === 0"
        :loading="submitting"
        @click="handleConfirm"
      >
        <template v-if="assignScope === 'single'">
          确认分配 ({{ selectedTeamIds.length }})
        </template>
        <template v-else>
          确认分配 ({{ totalSelectedTeams }} 支球队)
        </template>
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  /** 指定具体小组时进入单组模式，否则可切换单组/全部 */
  groupStage: { type: Object, default: null },
  tournamentId: { type: Number, required: true },
  tournamentTeams: { type: Array, default: () => [] },
  /** 已分配到此小组的球队ID（单组模式过滤用） */
  existingTeamIds: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:visible', 'success'])

const submitting = ref(false)
const loadingGroups = ref(false)
const groupStages = ref([])

// ===== 模式 =====
/** true: 从卡片传入的固定小组（不可切换范围） */
const fixedGroup = computed(() => props.groupStage)
/** 分配范围: 'single' | 'all' */
const assignScope = ref('single')
/** 全部小组模式下的子模式: 'manual' | 'tiered' */
const allMode = ref('manual')
/** 单组模式下选择具体哪个小组 */
const selectedSingleGroupId = ref(null)

// ===== 单组模式 =====
const selectedTeamIds = ref([])

// ===== 全部小组模式 =====
const selectedMap = reactive({})
const groupExpandMap = reactive({})
const tiers = reactive([])
const lastDrawResult = ref(null)

/** 逐步抽签状态 */
const drawState = reactive({
  /** 是否已开始抽签 */
  isStarted: false,
  /** 是否已完成 */
  isComplete: false,
  /** 是否无更多球队可抽 */
  noMoreTeams: false,
  /** 当前档位索引 */
  currentPotIndex: 0,
  /** 当前档位内已抽数量（用于蛇形定位） */
  currentPickInPot: 0,
  /** 已抽总数 */
  drawnCount: 0,
  /** 总球队数 */
  totalTeams: 0,
  /** 抽签结果 { [groupId]: team[] } */
  result: {},
  /** 最后一次抽取的信息 */
  lastDraw: null
})

/** 计算当前档位标签 */
const currentPotLabel = computed(() => {
  if (drawState.currentPotIndex < tiers.length) {
    return getTierLabel(drawState.currentPotIndex)
  }
  return ''
})

/** 抽签进度百分比 */
const drawProgressPercent = computed(() => {
  if (drawState.totalTeams === 0) return 0
  return Math.round((drawState.drawnCount / drawState.totalTeams) * 100)
})

/** 未分档球队池中每个球队的 popover 引用 */
const poolPopoverRefs = ref({})
function setPoolPopoverRef(el, teamId) {
  if (el) poolPopoverRefs.value[teamId] = el
}
function closePoolPopover(teamId) {
  nextTick(() => {
    poolPopoverRefs.value[teamId]?.hide?.()
  })
}

// ===== 计算属性 =====
const dialogTitle = computed(() => {
  if (assignScope.value === 'single') {
    const g = getTargetGroup()
    return g ? `分配球队 - ${g.name}` : '分配球队'
  }
  return '批量分配球队到所有小组'
})

const assignHint = computed(() => {
  if (assignScope.value === 'single') {
    return fixedGroup.value
      ? `从赛事已通过的球队中选择分配到「${fixedGroup.value.name}」（已分配的不再显示）`
      : '选择一个小组，然后勾选要分配的球队'
  }
  return '支持手动勾选分配，或先分档再蛇形随机抽签'
})

const availableTeams = computed(() => {
  if (assignScope.value === 'single') {
    // 单组模式：过滤掉已分配到此组的
    const existingIds = new Set(props.existingTeamIds)
    return props.tournamentTeams.filter(t => !existingIds.has(t.teamId))
  }
  return props.tournamentTeams || []
})

const totalSelectedTeams = computed(() =>
  Object.values(selectedMap).reduce((sum, ids) => sum + (ids?.length || 0), 0)
)

const sortedGroups = computed(() =>
  [...groupStages.value].sort((a, b) => (a.teamCount || 99) - (b.teamCount || 99))
)

const unassignedTeams = computed(() => {
  const assignedIds = new Set()
  tiers.forEach(tier => tier.teams.forEach(t => assignedIds.add(t.teamId)))
  return availableTeams.value.filter(t => !assignedIds.has(t.teamId))
})

const tiersReadyForDraw = computed(() => tiers.some(t => t.teams.length > 0))

function getTargetGroup() {
  if (fixedGroup.value) return fixedGroup.value
  return groupStages.value.find(g => g.id === selectedSingleGroupId.value) || groupStages.value[0] || null
}

// ===== 加载 =====
async function fetchGroupStages() {
  if (!props.tournamentId) { groupStages.value = []; return }
  loadingGroups.value = true
  try {
    const res = await tournamentApi.getGroupStages(props.tournamentId)
    groupStages.value = res.data || []
    if (!selectedSingleGroupId.value && groupStages.value.length > 0) {
      selectedSingleGroupId.value = groupStages.value[0].id
    }
  } catch (e) {
    ElMessage.error('获取小组列表失败')
    groupStages.value = []
  } finally {
    loadingGroups.value = false
  }
}

function initMaps() {
  Object.keys(selectedMap).forEach(k => delete selectedMap[k])
  Object.keys(groupExpandMap).forEach(k => delete groupExpandMap[k])
  groupStages.value.forEach(g => {
    selectedMap[g.id] = []
    groupExpandMap[g.id] = true
  })
  tiers.splice(0, tiers.length)
  lastDrawResult.value = null
}

// ===== 单组模式方法 =====
async function handleConfirm() {
  if (assignScope.value === 'single') {
    const group = getTargetGroup()
    if (!group) { ElMessage.warning('请选择小组'); return }
    if (selectedTeamIds.value.length === 0) return
    submitting.value = true
    try {
      await tournamentApi.addTeamsToGroup({
        groupStageId: group.id,
        teamIds: selectedTeamIds.value
      })
      ElMessage.success(`已分配 ${selectedTeamIds.value.length} 支球队到「${group.name}」`)
      emit('update:visible', false)
      emit('success')
    } catch (e) {
      ElMessage.error(e.message || '分配球队失败')
    } finally {
      submitting.value = false
    }
  } else {
    // 全部小组模式
    const assignments = []
    for (const group of groupStages.value) {
      const ids = selectedMap[group.id] || []
      if (ids.length > 0) assignments.push({ groupStageId: group.id, teamIds: ids })
    }
    if (assignments.length === 0) { ElMessage.warning('请至少为一个小组分配球队'); return }
    submitting.value = true
    try {
      await tournamentApi.assignTeamsToGroups({ tournamentId: props.tournamentId, assignments })
      ElMessage.success(`成功分配 ${totalSelectedTeams.value} 支球队到 ${assignments.length} 个小组`)
      emit('update:visible', false)
      emit('success')
    } catch (e) {
      ElMessage.error(e.message || '批量分配球队失败')
    } finally {
      submitting.value = false
    }
  }
}

// ===== 全部小组 - 手动模式方法 =====
function isTeamAssignedToOtherGroup(teamId, currentGroupId) {
  for (const gId of Object.keys(selectedMap)) {
    if (Number(gId) === currentGroupId) continue
    if (selectedMap[gId]?.includes(teamId)) return true
  }
  return false
}

function clearAllSelections() {
  groupStages.value.forEach(g => { selectedMap[g.id] = [] })
}

function autoDistributeTeams() {
  const teams = [...availableTeams.value]
  const groups = sortedGroups.value
  groups.forEach(g => { selectedMap[g.id] = [] })
  let idx = 0
  for (const group of groups) {
    const cap = group.teamCount || 99
    const count = Math.min(cap, teams.length - idx)
    if (count <= 0) break
    selectedMap[group.id] = teams.slice(idx, idx + count).map(t => t.teamId)
    idx += count
  }
}

// ===== 分档模式方法 =====
function getTierLabel(index) {
  const names = ['一','二','三','四','五','六','七','八','九','十']
  return names[index] ? `第${names[index]}档` : `第${index + 1}档`
}

function addTier() { tiers.push({ teams: [] }) }
function removeTier(index) { tiers.splice(index, 1) }
function clearAllTiers() { tiers.splice(0, tiers.length); lastDrawResult.value = null }

function moveToTier(team, targetIdx) {
  for (const t of tiers) {
    const i = t.teams.findIndex(x => x.teamId === team.teamId)
    if (i !== -1) t.teams.splice(i, 1)
  }
  if (targetIdx >= 0 && targetIdx < tiers.length) tiers[targetIdx].teams.push(team)
}

function moveOutOfTier(team, idx) {
  if (idx >= 0 && idx < tiers.length) {
    const i = tiers[idx].teams.findIndex(x => x.teamId === team.teamId)
    if (i !== -1) tiers[idx].teams.splice(i, 1)
  }
}

function randomShuffleTier(idx) {
  if (idx < 0 || idx >= tiers.length) return
  const arr = tiers[idx].teams
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[arr[i], arr[j]] = [arr[j], arr[i]]
  }
  tiers[idx].teams = [...arr]
}

function shuffle(a) {
  const r = [...a]
  for (let i = r.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[r[i], r[j]] = [r[j], r[i]]
  }
  return r
}

function autoSuggestTiers() {
  clearAllTiers()
  const gc = groupStages.value.length
  if (gc === 0) return
  const teams = [...availableTeams.value]
  const tc = Math.ceil(teams.length / gc)
  for (let i = 0; i < tc; i++) {
    const slice = teams.slice(i * gc, (i + 1) * gc)
    if (slice.length > 0) tiers.push({ teams: slice })
  }
}

function initDrawState() {
  drawState.isStarted = false
  drawState.isComplete = false
  drawState.noMoreTeams = false
  drawState.currentPotIndex = 0
  drawState.currentPickInPot = 0
  drawState.drawnCount = 0
  drawState.totalTeams = tiers.reduce((sum, t) => sum + t.teams.length, 0)
  drawState.result = {}
  drawState.lastDraw = null
  sortedGroups.value.forEach(g => { drawState.result[g.id] = [] })
}

/** 逐步抽签：每次点击抽1支球队 */
function drawNextTeam() {
  if (!tiersReadyForDraw.value || drawState.isComplete) return

  // 首次调用时初始化
  if (!drawState.isStarted) {
    initDrawState()
    drawState.isStarted = true
  }

  // 找到当前有球队的档位
  while (drawState.currentPotIndex < tiers.length && tiers[drawState.currentPotIndex].teams.length === 0) {
    drawState.currentPotIndex++
    drawState.currentPickInPot = 0
  }

  if (drawState.currentPotIndex >= tiers.length) {
    drawState.isComplete = true
    drawState.noMoreTeams = true
    lastDrawResult.value = { ...drawState.result }
    ElMessage.success('所有球队已抽签完成！')
    return
  }

  const currentTier = tiers[drawState.currentPotIndex]
  const groups = sortedGroups.value
  const isRev = drawState.currentPotIndex % 2 === 1
  const groupIndices = groups.map((_, i) => isRev ? groups.length - 1 - i : i)

  // 找到可以分配的目标小组
  let targetGroup = null
  let targetGIdx = -1
  const startGIdx = drawState.currentPickInPot

  for (let offset = 0; offset < groupIndices.length; offset++) {
    const gIdx = (startGIdx + offset) % groupIndices.length
    const actualGIdx = groupIndices[gIdx]
    const group = groups[actualGIdx]
    if ((drawState.result[group.id] || []).length < (group.teamCount || 99)) {
      targetGroup = group
      targetGIdx = gIdx
      break
    }
  }

  if (!targetGroup) {
    // 当前档位所有小组已满，尝试下一档
    drawState.currentPotIndex++
    drawState.currentPickInPot = 0
    // 递归重试
    drawNextTeam()
    return
  }

  // 从当前档位随机抽1支球队
  const teamIndex = Math.floor(Math.random() * currentTier.teams.length)
  const drawnTeam = currentTier.teams.splice(teamIndex, 1)[0]

  // 分配到目标小组
  drawState.result[targetGroup.id].push(drawnTeam)
  drawState.drawnCount++
  drawState.currentPickInPot = (targetGIdx + 1) % groupIndices.length

  // 记录本次抽取
  drawState.lastDraw = {
    team: drawnTeam,
    groupName: targetGroup.name,
    groupId: targetGroup.id
  }

  // 检查当前档位是否已抽完
  if (currentTier.teams.length === 0) {
    drawState.currentPotIndex++
    drawState.currentPickInPot = 0
  }

  // 检查是否全部完成
  if (drawState.drawnCount >= drawState.totalTeams) {
    drawState.isComplete = true
    lastDrawResult.value = { ...drawState.result }
    ElMessage.success('🎉 所有球队已抽签完成！')
  } else {
    // 同步更新 lastDrawResult 供应用按钮使用
    lastDrawResult.value = { ...drawState.result }
  }
}

/** 重置抽签 */
function resetDraw() {
  // 把抽出的球队放回原档位（从 result 中回收）
  for (const gId of Object.keys(drawState.result)) {
    const teams = drawState.result[gId] || []
    for (const team of teams) {
      // 放回第一档（用户可手动调整）
      if (tiers.length > 0) {
        tiers[0].teams.push(team)
      }
    }
  }
  initDrawState()
  lastDrawResult.value = null
  ElMessage.info('抽签已重置')
}

function runSnakeDraw() {
  if (!tiersReadyForDraw.value) return
  const groups = sortedGroups.value
  const result = {}
  groups.forEach(g => { result[g.id] = [] })

  for (let tIdx = 0; tIdx < tiers.length; tIdx++) {
    const shuffled = shuffle(tiers[tIdx].teams)
    const isRev = tIdx % 2 === 1
    const indices = groups.map((_, i) => isRev ? groups.length - 1 - i : i)

    let ti = 0
    while (ti < shuffled.length) {
      for (const gi of indices) {
        if (ti >= shuffled.length) break
        const g = groups[gi]
        if (result[g.id].length < (g.teamCount || 99)) {
          result[g.id].push(shuffled[ti])
          ti++
        }
      }
    }
  }
  lastDrawResult.value = result
  // 同步更新到 drawState 以便应用按钮工作
  drawState.result = result
  drawState.drawnCount = Object.values(result).reduce((s, arr) => s + arr.length, 0)
  drawState.totalTeams = drawState.drawnCount
  drawState.isStarted = true
  drawState.isComplete = true
}

function applyDrawResult() {
  const source = lastDrawResult.value || drawState.result
  if (!source || Object.keys(source).length === 0) return
  groupStages.value.forEach(g => { selectedMap[g.id] = [] })
  for (const gId of Object.keys(source)) {
    selectedMap[Number(gId)] = source[gId].map(t => t.teamId)
  }
  ElMessage.success('抽签结果已应用到下方小组，点击「确认分配」提交')
}

// ===== 监听 =====
watch(() => props.visible, async (val) => {
  if (val) {
    // 确定初始模式
    if (fixedGroup.value) {
      assignScope.value = 'single'
    } else {
      assignScope.value = 'single'
    }
    allMode.value = 'manual'
    selectedTeamIds.value = []
    await fetchGroupStages()
    initMaps()
    if (!selectedSingleGroupId.value && groupStages.value.length > 0) {
      selectedSingleGroupId.value = groupStages.value[0].id
    }
  }
})
</script>

<style scoped>
.assign-content {
  max-height: 520px;
  overflow-y: auto;
}

.scope-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border, #ebeef5);
  flex-wrap: wrap;
}

.scope-hint {
  font-size: 13px;
  color: var(--color-text-secondary, #606266);
  display: flex;
  align-items: center;
  gap: 6px;
}

.assign-hint {
  font-size: 13px;
  color: var(--color-text-secondary, #909399);
  margin-bottom: 12px;
}

.loading-state { padding: 20px 0; }

/* ---- 单组分配 ---- */
.single-assign {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* ---- 子模式切换 ---- */
.sub-mode-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

/* ---- 全局操作栏 ---- */
.global-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

/* ---- 小组分配卡片 ---- */
.assign-group-item {
  border: 1px solid var(--color-border, #dcdfe6);
  border-radius: 8px;
  margin-bottom: 10px;
  overflow: hidden;
}

.assign-group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 14px;
  background: var(--color-bg-page, #f5f7fa);
  border-bottom: 1px solid var(--color-border, #ebeef5);
}

.assign-group-title {
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.assign-group-teams {
  padding: 10px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.team-checkbox-item { margin-right: 0; }
.team-checkbox-label { display: flex; align-items: center; gap: 8px; }

/* ---- 分档区域 ---- */
.tiered-area { display: flex; flex-direction: column; gap: 10px; }

.tier-actions { display: flex; gap: 8px; flex-wrap: wrap; }

.unassigned-pool {
  border: 1px dashed var(--color-border, #dcdfe6);
  border-radius: 8px;
  padding: 8px 12px;
  background: var(--color-bg-page, #f5f7fa);
}

.pool-header { margin-bottom: 6px; }
.pool-title { font-weight: 600; font-size: 13px; color: var(--color-text-secondary, #606266); }
.pool-teams { display: flex; flex-wrap: wrap; gap: 6px; min-height: 28px; }
.pool-empty { font-size: 12px; color: var(--color-text-placeholder, #c0c4cc); font-style: italic; }

.tier-item {
  border: 1px solid var(--color-border, #dcdfe6);
  border-radius: 8px;
  overflow: hidden;
}

.tier-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  background: var(--el-color-primary-light-9, #ecf5ff);
  border-bottom: 1px solid var(--color-border, #ebeef5);
}

.tier-title { display: flex; align-items: center; gap: 8px; }
.tier-name { font-weight: 600; font-size: 14px; color: var(--el-color-primary, #409eff); }
.tier-header-actions { display: flex; gap: 4px; }
.tier-teams { padding: 8px 12px; display: flex; flex-wrap: wrap; gap: 6px; min-height: 36px; }
.tier-empty { font-size: 12px; color: var(--color-text-placeholder, #c0c4cc); font-style: italic; }

.team-tag { cursor: pointer; display: inline-flex; align-items: center; max-width: 200px; }
.team-tag :deep(.el-tag__content) { display: inline-flex; align-items: center; overflow: hidden; }

.draw-alert { margin: 2px 0; }
.draw-section { display: flex; align-items: center; gap: 12px; }
.draw-hint { font-size: 12px; color: var(--color-text-secondary, #909399); }

.draw-preview {
  border: 2px solid var(--el-color-warning, #e6a23c);
  border-radius: 8px;
  padding: 10px 12px;
  background: #fef7e0;
}

.draw-preview-title {
  font-weight: 600; font-size: 13px;
  color: var(--el-color-warning, #e6a23c);
  margin-bottom: 8px;
}

.draw-preview-groups { display: flex; flex-direction: column; gap: 6px; }
.preview-group { display: flex; align-items: flex-start; gap: 8px; }
.preview-group-name { font-weight: 600; font-size: 13px; min-width: 50px; line-height: 24px; }
.preview-group-teams { display: flex; flex-wrap: wrap; gap: 4px; }
.preview-team { display: inline-flex; align-items: center; }
.apply-btn { margin-top: 10px; }

/* ---- 档位选择弹出框 ---- */
.tier-select-popup {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;
}

.tier-select-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  margin-bottom: 4px;
}

.tier-select-btn {
  width: 100%;
  justify-content: flex-start;
}

/* ---- 未分档池提示 ---- */
.pool-hint {
  font-size: 12px;
  color: var(--color-text-secondary, #c0c4cc);
}

/* ---- 逐步抽签 ---- */
.draw-step-btn {
  font-weight: 600;
}

.draw-step-btn small {
  font-weight: 400;
  opacity: 0.8;
}

.draw-status-bar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px 12px;
  background: var(--color-bg-page, #f5f7fa);
  border: 1px solid var(--color-border, #dcdfe6);
  border-radius: 8px;
}

.draw-progress {
  flex: 1;
}

.draw-current-info {
  font-size: 14px;
  color: var(--color-text-primary, #303133);
  display: flex;
  align-items: center;
  gap: 4px;
}

.drawn-team-tag {
  display: inline-flex;
  align-items: center;
  animation: team-draw-flash 0.6s ease-out;
}

@keyframes team-draw-flash {
  0% { transform: scale(1.3); background-color: var(--el-color-warning-light-3, #f3d19e); }
  100% { transform: scale(1); }
}

.preview-team-latest {
  border: 2px solid var(--el-color-warning, #e6a23c) !important;
  animation: team-draw-flash 0.6s ease-out;
}
</style>
