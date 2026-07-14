<template>
  <HomeLayout v-model:activeTab="activeTab" :tabs="TABS">
    <template #default="{ activeTab }">
      <EventShowArea v-if="activeTab === 'events'" />
      <TeamShowArea v-if="activeTab === 'teams'" />
      <PlayerShowArea v-if="activeTab === 'players'" />
    </template>
  </HomeLayout>
</template>

<script setup>
import { ref } from 'vue'
import { Trophy, UserFilled, User } from '@element-plus/icons-vue'
import HomeLayout from '@/components/General/HomeLayout.vue'
import EventShowArea from '@/components/HomeEventAdmin.vue/Event/EventShowArea.vue'
import TeamShowArea from '@/components/HomeEventAdmin.vue/Teams/TeamShowArea.vue'
import PlayerShowArea from '@/components/HomeEventAdmin.vue/Players/PlayerShowArea.vue'

const TABS = [
  { key: 'events', icon: Trophy, label: '赛事' },
  { key: 'teams', icon: UserFilled, label: '球队' },
  { key: 'players', icon: User, label: '球员' }
]

const activeTab = ref('events')
</script>

<style scoped>
/* 布局样式由 HomeLayout 统一管理 */
</style>
