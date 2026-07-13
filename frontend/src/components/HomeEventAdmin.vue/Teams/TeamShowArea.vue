<template>
  <div class="team-show-area">
    <div class="area-header">
      <h2 class="area-title">球队管理</h2>
    </div>

    <el-empty description="球队管理（待开发）" />
  </div>
</template>

<script setup>
</script>

<style scoped>
.team-show-area {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

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

.area-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}
</style>
