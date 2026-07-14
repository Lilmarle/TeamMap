<template>
  <HomeLayout v-model:activeTab="activeTab" :tabs="TABS">
    <template #default="{ activeTab }">
      <EventShowArea v-if="activeTab === 'events'" />
      <TeamShowArea v-if="activeTab === 'teams'" />
    </template>
  </HomeLayout>
</template>

<script setup>
import { ref } from 'vue'
import { Trophy, UserFilled } from '@element-plus/icons-vue'
import HomeLayout from '@/components/General/HomeLayout.vue'
import EventShowArea from '@/components/HomeSystemAdmin/Events/EventShowArea.vue'
import TeamShowArea from '@/components/HomeSystemAdmin/Team/TeamShowArea.vue'

const TABS = [
  { key: 'events', icon: Trophy, label: '赛事' },
  { key: 'teams', icon: UserFilled, label: '球队' }
]

const activeTab = ref('events')
</script>

<style scoped>
/* 布局样式由 HomeLayout 统一管理 */
</style>
