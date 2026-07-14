<template>
  <div class="async-content">
    <!-- 加载中 -->
    <div v-if="loading" class="async-loading">
      <el-skeleton :rows="skeletonRows" animated />
    </div>

    <!-- 加载失败 -->
    <el-alert
      v-else-if="loadFailed"
      title="加载失败"
      type="error"
      :description="errorMessage"
      show-icon
      closable
      @close="$emit('update:loadFailed', false)"
    >
      <template #actions>
        <slot name="retry">
          <el-button size="small" type="primary" @click="$emit('retry')">
            {{ retryLabel }}
          </el-button>
        </slot>
      </template>
    </el-alert>

    <!-- 空状态 -->
    <el-empty
      v-else-if="isEmpty"
      :description="emptyDescription"
      :image-size="emptyImageSize"
    >
      <slot name="empty" />
    </el-empty>

    <!-- 实际内容 -->
    <slot v-else />
  </div>
</template>

<script setup>
defineProps({
  /** 是否处于加载中 */
  loading: { type: Boolean, default: false },
  /** 是否加载失败 */
  loadFailed: { type: Boolean, default: false },
  /** 加载失败的错误信息 */
  errorMessage: { type: String, default: '' },
  /** 是否处于空数据状态 */
  isEmpty: { type: Boolean, default: false },
  /** 空状态描述文本 */
  emptyDescription: { type: String, default: '暂无数据' },
  /** 空状态图标尺寸 */
  emptyImageSize: { type: Number, default: 100 },
  /** 骨架屏行数 */
  skeletonRows: { type: Number, default: 3 },
  /** 重试按钮文本 */
  retryLabel: { type: String, default: '重试' }
})

defineEmits(['update:loadFailed', 'retry'])
</script>

<style scoped>
.async-content {
  min-height: 100px;
}

.async-loading {
  padding: 40px;
  background: var(--color-bg-white, #fff);
  border-radius: 8px;
}
</style>
