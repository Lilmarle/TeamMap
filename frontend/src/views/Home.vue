<template>
  <div class="home">
    <el-container>
      <el-header class="header">
        <div class="logo">TeamMap</div>
        <div class="nav">
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <div class="welcome">
          <h1>欢迎使用 TeamMap</h1>
          <p>团队协作地图 - 让团队协作更高效</p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome {
  text-align: center;
  color: #fff;
}

.welcome h1 {
  font-size: 48px;
  margin-bottom: 16px;
}

.welcome p {
  font-size: 18px;
  opacity: 0.9;
}
</style>
