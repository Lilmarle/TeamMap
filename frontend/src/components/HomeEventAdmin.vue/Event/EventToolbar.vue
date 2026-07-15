<template>
  <div class="area-header">
    <div class="header-left">
      <slot name="title">
        <h2 class="area-title">我的赛事</h2>
      </slot>
      <slot name="selector">
        <el-select
          :model-value="selectedEventId"
          placeholder="请选择赛事"
          size="default"
          class="event-select"
          :loading="loading"
          @change="$emit('update:selectedEventId', $event)"
        >
          <el-option
            v-for="event in events"
            :key="event.id"
            :label="event.name"
            :value="event.id"
          >
            <span v-ellipsis-tooltip class="event-option-label">{{ event.name }}</span>
            <el-tag
              :type="getStatusTagType(event.status)"
              size="small"
              class="event-option-tag"
            >
              {{ getStatusName(event.status) }}
            </el-tag>
          </el-option>
        </el-select>
      </slot>
    </div>

    <div class="header-actions">
      <slot name="actions">
        <!-- 分组操作按钮（仅在选中分组时显示） -->
        <template v-if="showGroupActions && selectedGroupStage">
          <el-divider direction="vertical" class="action-divider" />
          <span class="action-group-name">{{ selectedGroupStage.name }}</span>
          <el-popconfirm
            title="确定取消此分组？已分配的球队将被移除。"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="$emit('delete-group', selectedGroupStage)"
          >
            <template #reference>
              <el-button size="small" type="danger" plain>
                取消分组
              </el-button>
            </template>
          </el-popconfirm>
          <el-button
            size="small"
            type="primary"
            @click="$emit('assign-team', selectedGroupStage)"
          >
            分配球队
          </el-button>
          <el-divider direction="vertical" class="action-divider" />
        </template>

        <el-button
          type="primary"
          :icon="UserFilled"
          :disabled="!selectedEventId"
          @click="$emit('invite')"
        >
          邀请球队
        </el-button>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="!selectedEventId"
          @click="$emit('delete')"
        >
          删除赛事
        </el-button>
        <el-button type="primary" :icon="Plus" @click="$emit('add')">
          添加赛事
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { Plus, Delete, UserFilled } from '@element-plus/icons-vue'

defineProps({
  events: { type: Array, default: () => [] },
  selectedEventId: { type: [Number, null], default: null },
  loading: { type: Boolean, default: false },
  /** 是否显示分组操作按钮 */
  showGroupActions: { type: Boolean, default: false },
  /** 当前选中的分组阶段 */
  selectedGroupStage: { type: Object, default: null }
})

defineEmits(['update:selectedEventId', 'invite', 'delete', 'add', 'assign-team', 'delete-group'])

function getStatusName(status) {
  switch (status) {
    case 1: return '筹办中'
    case 2: return '进行中'
    case 3: return '已结束'
    default: return '未知'
  }
}

function getStatusTagType(status) {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'info'
    default: return 'info'
  }
}
</script>

<style scoped>
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

.action-divider {
  height: 24px;
}

.action-group-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary, #303133);
  white-space: nowrap;
}
</style>
