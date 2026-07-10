<template>
  <el-main class="home-main">
    <div class="welcome">
      <h1>欢迎使用 TeamMap</h1>
      <p>校园体育赛事平台 - 让运动更精彩</p>
    </div>
  </el-main>
</template>

<script setup>
</script>

<style scoped>
.home-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome {
  text-align: center;
  color: var(--color-text-white);
}

.welcome h1 {
  font-size: 48px;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.welcome p {
  font-size: 18px;
  opacity: 0.9;
}
</style>
