<template>
  <el-table-column type="index" label="序号" width="60" align="center" />

  <el-table-column prop="name" label="赛事名称" min-width="200" show-overflow-tooltip />

  <el-table-column label="运动类型" width="100" align="center">
    <template #default="{ row }">
      {{ getTypeName(row.type) }}
    </template>
  </el-table-column>

  <el-table-column label="赛事状态" width="100" align="center">
    <template #default="{ row }">
      <el-tag :type="getStatusTagType(row.status)" size="small">
        {{ getStatusName(row.status) }}
      </el-tag>
    </template>
  </el-table-column>

  <el-table-column label="开始时间" width="170" align="center">
    <template #default="{ row }">
      {{ row.startTime }}
    </template>
  </el-table-column>

  <el-table-column label="结束时间" width="170" align="center">
    <template #default="{ row }">
      {{ row.endTime }}
    </template>
  </el-table-column>

  <el-table-column label="创建时间" width="170" align="center">
    <template #default="{ row }">
      {{ row.createTime }}
    </template>
  </el-table-column>

  <el-table-column label="操作" width="130" align="center" fixed="right">
    <template #default="{ row }">
      <el-button type="primary" size="small" :icon="Edit" circle @click="onEdit(row)" />
      <el-button type="danger" size="small" :icon="Delete" circle @click="onDelete(row)" />
    </template>
  </el-table-column>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  onEdit: {
    type: Function,
    default: null
  },
  onDelete: {
    type: Function,
    default: null
  }
})

/**
 * 获取运动类型名称
 * @param {number} type 1-足球，2-篮球，3-排球
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
</script>
