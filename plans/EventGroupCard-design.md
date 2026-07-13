# EventGroupCard.vue 设计方案

## 1. 概述

`EventGroupCard.vue` 是小组赛管理中的一个卡片组件，用于展示单个小组的信息以及该小组内各球队的战绩排名。它将被 `EventGroup.vue`（当前为占位组件）引用。

## 2. 数据模型

### 2.1 GroupStage（小组赛）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 小组ID |
| tournamentId | Integer | 赛事ID |
| name | String | 小组名称（如 A组、B组） |
| teamCount | Integer | 球队总数 |
| directAdvance | Integer | 直接出线数 |
| indirectAdvance | Integer | 间接出线数 |
| roundType | Integer | 循环数：1-单循环，2-双循环 |

### 2.2 GroupStageTeam（小组球队成绩）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 关联ID |
| groupStageId | Integer | 小组ID |
| teamId | Integer | 球队ID |
| win | Integer | 胜场数 |
| draw | Integer | 平场数 |
| loss | Integer | 负场数 |
| points | Integer | 积分 |
| goalsFor | Integer | 进球数（得分） |
| goalsAgainst | Integer | 失球数（失分） |
| goalDifference | Integer | 净胜球 |

## 3. 后端 API 依赖

| 方法 | 路径 | 用途 |
|------|------|------|
| GET | `/api/group-stage/tournament/{tournamentId}` | 获取赛事的所有小组 |
| GET | `/api/group-stage-team/group/{groupStageId}` | 获取某小组的所有球队及成绩 |
| POST | `/api/group-stage` | 添加单个小组 |
| POST | `/api/group-stage/batch` | 批量添加小组 |
| POST | `/api/group-stage-team/tournament` | 为赛事所有小组分配球队 |
| POST | `/api/group-stage-team/batch` | 为单个小组批量添加球队 |

## 4. 前端 API 扩展

需要在 `frontend/src/api/tournament.js` 中新增以下方法：

```js
// 小组赛 API
getGroupStages(tournamentId)          // GET /api/group-stage/tournament/{tournamentId}
addGroupStage(data)                   // POST /api/group-stage
batchAddGroupStages(data)             // POST /api/group-stage/batch
getGroupStageTeams(groupStageId)      // GET /api/group-stage-team/group/{groupStageId}
addTeamsToGroup(data)                 // POST /api/group-stage-team/batch
assignTeamsToGroups(data)             // POST /api/group-stage-team/tournament
```

## 5. 组件设计

### 5.1 Props

```js
props: {
  groupStage: { type: Object, required: true },      // 小组信息 (GroupStage)
  tournamentId: { type: Number, required: true },    // 所属赛事ID
  allTeams: { type: Array, default: () => [] }       // 赛事中已通过的所有球队 (用于分配)
}
```

### 5.2 Emits

```js
emit: ['refresh']   // 数据变更后通知父组件刷新
```

### 5.3 内部状态

```js
const groupTeams = ref([])            // 小组内球队列表及成绩
const loadingTeams = ref(false)       // 加载球队列表状态
const showTeamSelector = ref(false)   // 是否显示球队选择器
```

### 5.4 模板结构

```
<div class="group-card">
  <!-- 卡片头部 -->
  <div class="group-card-header">
    <span class="group-name">{{ groupStage.name }}</span>
    <div class="group-info-tags">
      <el-tag>直接出线: {{ groupStage.directAdvance }}</el-tag>
      <el-tag v-if="groupStage.indirectAdvance">间接出线: {{ groupStage.indirectAdvance }}</el-tag>
      <el-tag>{{ groupStage.roundType === 1 ? '单循环' : '双循环' }}</el-tag>
    </div>
    <div class="group-actions">
      <el-button @click="showTeamSelector = true" size="small">分配球队</el-button>
    </div>
  </div>

  <!-- 球队排名表格 -->
  <el-table :data="sortedTeams" class="group-teams-table">
    <el-table-column type="index" label="排名" width="60" />
    <el-table-column label="球队" min-width="150">
      <template #default="{ row }">
        <el-avatar :size="24" :src="row.teamLogo">{{ row.teamName?.[0] }}</el-avatar>
        <span>{{ row.teamName }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="win" label="胜" width="50" />
    <el-table-column prop="draw" label="平" width="50" />
    <el-table-column prop="loss" label="负" width="50" />
    <el-table-column prop="goalsFor" label="进球" width="60" />
    <el-table-column prop="goalsAgainst" label="失球" width="60" />
    <el-table-column prop="goalDifference" label="净胜球" width="70" />
    <el-table-column prop="points" label="积分" width="60" />
  </el-table>

  <!-- 空状态 -->
  <el-empty v-if="groupTeams.length === 0" description="暂未分配球队" />

  <!-- 分配球队对话框 -->
  <el-dialog v-model="showTeamSelector" title="分配球队">
    <el-transfer ... />  <!-- 待选球队列表 -->
  </el-dialog>
</div>
```

### 5.5 核心逻辑

```js
// 加载小组内的球队
async function fetchGroupTeams() {
  const res = await tournamentApi.getGroupStageTeams(groupStage.id)
  groupTeams.value = res.data || []
}

// 球队按积分/净胜球排序
const sortedTeams = computed(() => {
  return [...groupTeams.value].sort((a, b) => {
    if (b.points !== a.points) return b.points - a.points
    return b.goalDifference - a.goalDifference
  })
})

// 分配球队到小组
async function assignTeams(teamIds) {
  await tournamentApi.addTeamsToGroup({
    groupStageId: groupStage.id,
    teamIds: teamIds
  })
  showTeamSelector.value = false
  emit('refresh')
}
```

## 6. 父子组件数据流

```
EventShowArea.vue
  ├── EventToolbar.vue
  ├── EventTabs.vue
  │   ├── EventTeams.vue         ← 已实现
  │   ├── EventGroup.vue         → 包含多个 EventGroupCard.vue
  │   │   └── EventGroupCard.vue  ← 此组件（新建）
  │   ├── EventKnockout.vue
  │   └── EventSchedule.vue
  ├── AddEvent.vue
  └── EventInvite.vue
```

### EventGroup.vue 改造

当前 `EventGroup.vue` 是空占位组件，需要改为：

```vue
<template>
  <div class="group-stage-container">
    <!-- 批量创建小组按钮 -->
    <div class="toolbar">
      <el-button @click="showCreateDialog = true">创建小组</el-button>
    </div>

    <!-- 小组卡片列表 -->
    <div class="group-cards">
      <EventGroupCard
        v-for="group in groupStages"
        :key="group.id"
        :group-stage="group"
        :tournament-id="tournamentId"
        @refresh="fetchGroups"
      />
    </div>
  </div>
</template>
```

## 7. 样式设计

- 卡片使用白色背景、圆角、阴影（与现有设计一致）
- 卡片头部展示小组名称和配置标签
- 排名表格使用紧凑型布局
- 排名第1名高亮显示（绿色背景或特殊标记）

## 8. 实施步骤

1. 在 `tournament.js` 中新增 GroupStage 相关 API 方法
2. 实现 `EventGroupCard.vue`（展示小组信息 + 球队排名表 + 分配球队）
3. 改造 `EventGroup.vue`（加载小组列表，渲染多个 EventGroupCard）
4. 更新 `EventShowArea.vue` 传递 `tournamentId` 给 EventGroup
5. 构建验证
