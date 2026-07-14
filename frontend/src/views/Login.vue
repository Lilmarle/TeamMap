<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">登录</h2>
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-focus
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
        <div class="forgot-password">
          <router-link to="/change-password">忘记密码？</router-link>
        </div>
      </el-form>
      <div class="register-link">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已在拦截器中处理
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  border-radius: 8px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.title {
  text-align: center;
  margin-bottom: 24px;
  color: var(--color-text-primary);
  font-size: 24px;
  font-weight: 600;
}

.submit-btn {
  width: 100%;
}

.forgot-password {
  text-align: right;
  margin-top: -18px;
  margin-bottom: 6px;
}

.forgot-password a {
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 13px;
}

.forgot-password a:hover {
  color: var(--color-primary);
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.register-link a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  color: var(--color-primary-dark);
}
</style>
