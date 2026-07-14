<template>
  <HomeLayout>
    <template #default>
      <router-view />
    </template>
  </HomeLayout>
</template>

<script setup>
import HomeLayout from '@/components/General/HomeLayout.vue'
</script>

<style scoped>
/* 布局样式由 HomeLayout 统一管理 */
</style>
