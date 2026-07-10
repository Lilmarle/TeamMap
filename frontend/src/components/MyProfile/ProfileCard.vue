<template>
  <el-card class="profile-card" v-loading="loading">
    <template #header>
      <div class="card-header">
        <h2 class="title">{{ title }}</h2>
        <div class="header-actions">
          <slot name="actions" />
        </div>
      </div>
    </template>

    <!-- 加载失败 -->
    <el-empty v-if="loadFailed" description="加载用户信息失败">
      <el-button type="primary" @click="$emit('retry')">重新加载</el-button>
    </el-empty>

    <!-- 空数据 -->
    <el-empty v-else-if="!hasData && !loading" description="暂无用户信息" />

    <!-- 默认内容 -->
    <slot v-else />
  </el-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '个人信息'
  },
  loading: {
    type: Boolean,
    default: false
  },
  loadFailed: {
    type: Boolean,
    default: false
  },
  hasData: {
    type: Boolean,
    default: false
  }
})

defineEmits(['retry'])
</script>

<style scoped>
.profile-card {
  width: 600px;
  max-width: 100%;
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 20px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>
