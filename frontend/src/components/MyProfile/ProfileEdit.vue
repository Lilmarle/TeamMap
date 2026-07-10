<template>
  <div class="profile-edit">
    <div class="avatar-section">
      <div class="avatar-wrapper">
        <el-avatar :size="100" :src="form.avatar || defaultAvatar" />
        <div class="avatar-overlay" @click="triggerUpload">
          <el-icon :size="24"><Camera /></el-icon>
          <span>更换头像</span>
        </div>
      </div>
      <input
        ref="fileInputRef"
        type="file"
        accept="image/jpeg,image/png,image/gif,image/bmp,image/webp"
        style="display: none"
        @change="handleFileChange"
      />
      <el-text v-if="!uploading" class="avatar-hint" type="info" size="small">
        支持 JPG/PNG/GIF/BMP/WEBP，最大 10MB
      </el-text>
      <el-text v-else class="avatar-hint" type="primary" size="small">
        上传中...
      </el-text>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      size="large"
    >
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="form.nickname"
          placeholder="请输入昵称"
          maxlength="30"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="头像" prop="avatar">
        <div class="avatar-input-wrapper">
          <el-input
            v-model="form.avatar"
            placeholder="输入头像 URL，或点击上方上传"
            clearable
          />
          <el-button
            class="upload-btn"
            :loading="uploading"
            :icon="Upload"
            @click="triggerUpload"
          >
            本地上传
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio :value="0">未知</el-radio>
          <el-radio :value="1">男</el-radio>
          <el-radio :value="2">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="年龄" prop="age">
        <el-input-number
          v-model="form.age"
          :min="0"
          :max="150"
          :step="1"
          placeholder="请输入年龄"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, Upload } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { isUrl } from '@/utils/validate'

const props = defineProps({
  profile: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['save', 'cancel'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const formRef = ref(null)
const fileInputRef = ref(null)
const uploading = ref(false)

const form = reactive({
  nickname: '',
  avatar: '',
  gender: 0,
  age: null
})

/** 将性别名称映射为数值 */
function mapGenderNameToValue(genderName) {
  if (genderName === '男') return 1
  if (genderName === '女') return 2
  return 0
}

/** 初始化表单数据 */
function initForm() {
  form.nickname = props.profile.nickname || ''
  form.avatar = props.profile.avatar || ''
  form.gender = mapGenderNameToValue(props.profile.genderName)
  form.age = props.profile.age
}

watch(() => props.profile, initForm, { immediate: true })

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 30, message: '昵称长度在 1 到 30 个字符', trigger: 'blur' }
  ],
  avatar: [
    {
      validator: (rule, value, callback) => {
        if (value) {
          // 允许完整的 URL（http/https）或相对路径（以 / 开头）
          if (/^(https?:\/\/|\/)/.test(value)) {
            callback()
          } else {
            callback(new Error('请输入有效的 URL 地址或上传头像'))
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  age: [
    {
      validator: (rule, value, callback) => {
        if (value != null && (value < 0 || value > 150)) {
          callback(new Error('年龄必须在 0 到 150 之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

/** 触发文件选择 */
function triggerUpload() {
  fileInputRef.value?.click()
}

/** 处理文件选择并上传 */
async function handleFileChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  // 校验文件大小（10MB）
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB')
    return
  }

  uploading.value = true
  try {
    const res = await userApi.uploadAvatar(file)
    form.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    uploading.value = false
    // 清空 input 以允许重复选择同一文件
    event.target.value = ''
  }
}

/** 提交表单 */
async function submit() {
  if (!formRef.value) return false
  try {
    await formRef.value.validate()
    emit('save', {
      nickname: form.nickname,
      avatar: form.avatar || null,
      gender: form.gender,
      age: form.age
    })
    return true
  } catch {
    return false
  }
}

/** 取消编辑 */
function cancel() {
  emit('cancel')
}

defineExpose({ submit })
</script>

<style scoped>
.profile-edit {
  padding: 0 8px 8px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  margin-top: 8px;
}

.avatar-input-wrapper {
  display: flex;
  width: 100%;
  gap: 8px;
}

.avatar-input-wrapper .el-input {
  flex: 1;
}

.upload-btn {
  flex-shrink: 0;
}
</style>
