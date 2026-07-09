<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2 class="title">注册</h2>
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item prop="inviteCode">
          <el-input
            v-model="registerForm.inviteCode"
            placeholder="邀请码（选填，管理员注册需要）"
            prefix-icon="Key"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-link">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </div>
      <div class="invite-tip">
        <el-tooltip content="赛事管理员：dccshishabi | 系统管理员：hryshishabi" placement="bottom">
          <span class="tip-text">了解管理员身份</span>
        </el-tooltip>
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

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  inviteCode: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    await userStore.register({
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
      inviteCode: registerForm.inviteCode || undefined
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器中处理
  }
}
</script>

<style scoped>
.register-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 450px;
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

.login-link {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.login-link a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  color: var(--color-primary-dark);
}

.invite-tip {
  text-align: center;
  margin-top: 12px;
  font-size: 12px;
}

.tip-text {
  color: var(--color-text-light);
  cursor: pointer;
  border-bottom: 1px dashed var(--color-border);
}

.tip-text:hover {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}
</style>
