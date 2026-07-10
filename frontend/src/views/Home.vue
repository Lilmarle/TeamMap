<template>
  <div class="home">
    <el-container>
      <HomeHeader />
      <HomeWelcome />
    </el-container>
  </div>
</template>

<script setup>
import HomeHeader from '@/components/Home/HomeHeader.vue'
import HomeWelcome from '@/components/Home/HomeWelcome.vue'
</script>

<style scoped>
.home {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
