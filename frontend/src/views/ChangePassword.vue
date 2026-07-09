<template>
  <div class="change-password-page">
    <el-card class="change-password-card">
      <h2 class="title">修改密码</h2>
      <el-form
        ref="formRef"
        :model="passwordForm"
        :rules="rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="原密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="新密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="确认新密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleChangePassword" :loading="loading">
            确认修改
          </el-button>
        </el-form-item>
      </el-form>
      <div class="nav-links">
        <router-link to="/login">返回登录</router-link>
        <span class="divider">|</span>
        <router-link to="/register">注册账号</router-link>
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
const loading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能小于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function handleChangePassword() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()

    // 检查是否已登录
    if (!userStore.token) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    loading.value = true
    await userStore.changePassword({
      userId: userStore.user?.id,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.change-password-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.change-password-card {
  width: 420px;
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

.nav-links {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.nav-links .divider {
  margin: 0 12px;
  color: var(--color-border);
}

.nav-links a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.nav-links a:hover {
  color: var(--color-primary-dark);
}
</style>
