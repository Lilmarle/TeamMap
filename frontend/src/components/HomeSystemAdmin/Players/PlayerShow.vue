<template>
  <div class="player-show-area">
    <PageHeader title="球员管理">
      <template #actions>
        <el-input
          v-model="searchKeyword"
          v-debounce:300="onSearchInput"
          placeholder="搜索球员名、球队、位置..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          size="default"
        />
        <el-select v-model="filterSport" placeholder="运动类型" clearable size="default" class="filter-select">
          <el-option
            v-for="item in sportTypeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select v-model="filterRole" placeholder="角色" clearable size="default" class="filter-select">
          <el-option
            v-for="item in roleOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态" clearable size="default" class="filter-select">
          <el-option
            v-for="item in statusOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </template>
    </PageHeader>

    <AsyncContent
      :loading="loading"
      :load-failed="loadFailed"
      :error-message="errorMessage"
      :is-empty="filteredPlayers.length === 0"
      :empty-description="searchKeyword ? '未匹配到相关球员' : '暂无球员数据'"
      @retry="fetchPlayers"
      @update:load-failed="loadFailed = $event"
    >
      <el-table
        :data="filteredPlayers"
        border
        stripe
        style="width: 100%"
        size="default"
        row-key="playerId"
        class="player-table"
      >
        <PlayerCard :onEdit="handleEdit" />
      </el-table>
    </AsyncContent>

    <!-- 编辑球员对话框（放在 el-table 外部，避免被 table 过滤） -->
    <PlayerEdit ref="playerEditRef" @refresh="fetchPlayers" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { playerApi } from '@/api/player'
import { Search } from '@element-plus/icons-vue'
import PageHeader from '@/components/General/PageHeader.vue'
import AsyncContent from '@/components/General/AsyncContent.vue'
import PlayerCard from './PlayerCard.vue'
import PlayerEdit from './PlayerEdit.vue'

const players = ref([])
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')
const filterSport = ref('')
const filterRole = ref('')
const filterStatus = ref('')

/** 防抖后的搜索关键词（由 v-debounce 更新） */
const debouncedKeyword = ref('')

/** PlayerEdit 组件引用 */
const playerEditRef = ref(null)

/** 搜索输入防抖回调 */
function onSearchInput(value) {
  debouncedKeyword.value = value
}

/**
 * 所有出现的运动类型名称（用于筛选下拉框）
 */
const sportTypeOptions = computed(() => {
  return [...new Set(players.value.map(p => p.sportTypeName).filter(Boolean))]
})

/**
 * 所有出现的角色名称（用于筛选下拉框）
 */
const roleOptions = computed(() => {
  return [...new Set(players.value.map(p => p.roleName).filter(Boolean))]
})

/**
 * 所有出现的状态名称（用于筛选下拉框）
 */
const statusOptions = computed(() => {
  return [...new Set(players.value.map(p => p.statusName).filter(Boolean))]
})

/**
 * 筛选后的球员列表
 */
const filteredPlayers = computed(() => {
  let result = players.value

  // 关键词模糊搜索（使用防抖后的关键词）
  const keyword = debouncedKeyword.value.trim().toLowerCase()
  if (keyword) {
    result = result.filter(player => {
      return (
        player.jerseyName?.toLowerCase().includes(keyword) ||
        player.teamName?.toLowerCase().includes(keyword) ||
        player.teamShortName?.toLowerCase().includes(keyword) ||
        player.roleName?.toLowerCase().includes(keyword) ||
        player.position?.toLowerCase().includes(keyword) ||
        player.sportTypeName?.toLowerCase().includes(keyword) ||
        String(player.jerseyNumber || '').includes(keyword)
      )
    })
  }

  // 运动类型筛选
  if (filterSport.value) {
    result = result.filter(player => player.sportTypeName === filterSport.value)
  }

  // 角色筛选
  if (filterRole.value) {
    result = result.filter(player => player.roleName === filterRole.value)
  }

  // 状态筛选
  if (filterStatus.value) {
    result = result.filter(player => player.statusName === filterStatus.value)
  }

  return result
})

/** 打开编辑对话框 */
function handleEdit(row) {
  playerEditRef.value?.open(row)
}

/**
 * 获取所有球员信息
 */
async function fetchPlayers() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await playerApi.getAllPlayers()
    players.value = res.data || []
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取球员列表失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPlayers()
})
</script>

<style scoped>
.player-show-area {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input {
  width: 220px;
}

.filter-select {
  width: 140px;
}

/* 球员表格样式 */
.player-table {
  --el-table-border-color: var(--color-primary-light);
  --el-table-border: 1px solid var(--color-primary-light);
  --el-table-header-bg-color: var(--color-primary-bg);
  --el-table-header-text-color: var(--color-primary-dark);
  --el-table-row-hover-bg-color: var(--color-primary-bg);
}

.player-table :deep(.el-table__body tr.el-table__row--striped td) {
  background-color: #F2F9FE;
}
</style>
