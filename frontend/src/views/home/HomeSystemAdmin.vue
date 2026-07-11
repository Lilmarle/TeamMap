<template>
  <div class="home-system-admin">
    <HomeHeader />
    <div class="home-body">
      <HomeTabs />
      <main class="content-area">
        <TeamShowArea />
      </main>
    </div>
  </div>
</template>

<script setup>
import HomeHeader from '@/components/General/HomeHeader.vue'
import HomeTabs from '@/components/General/HomeTabs.vue'
import TeamShowArea from '@/components/HomeSystemAdmin/Team/TeamShowArea.vue'
</script>

<style scoped>
.home-system-admin {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.home-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.content-area {
  flex: 1;
  background: var(--color-bg-page);
  overflow-y: auto;
  padding: 20px;
}
</style>
