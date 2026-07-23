<template>
  <el-dialog
    :model-value="modelValue"
    title="修改个人信息"
    width="450px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-form :model="form" label-width="100px" size="small">
      <!-- 定妆照 -->
      <el-form-item label="定妆照">
        <div class="portrait-upload">
          <el-avatar :size="80" :src="form.portraitPhoto" shape="square">
            <el-icon :size="32"><Camera /></el-icon>
          </el-avatar>
          <div class="portrait-upload-actions">
            <el-upload
              :show-file-list="false"
              :before-upload="handlePortraitUpload"
              accept="image/*"
            >
              <el-button size="small" type="primary" :loading="uploading">
                上传照片
              </el-button>
            </el-upload>
            <span class="portrait-hint">支持 JPG/PNG/GIF 格式</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="球衣名称">
        <el-input v-model="form.jerseyName" placeholder="球衣上显示的名字" />
      </el-form-item>
      <el-form-item label="球衣号码">
        <el-input-number
          v-model="form.jerseyNumber"
          :min="0"
          :max="99"
          style="width:100%"
        />
      </el-form-item>
      <el-form-item label="场上位置">
        <el-input v-model="form.position" placeholder="如：前锋、后卫" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button size="small" @click="$emit('update:modelValue', false)">取消</el-button>

      <!-- 无球员记录时：显示"注册球员"按钮 -->
      <el-button
        v-if="!currentPlayerId"
        size="small"
        type="success"
        :loading="registering"
        @click="handleRegisterPlayer"
      >
        注册球员
      </el-button>

      <!-- 保存按钮：更新队伍成员信息（含球员联动） -->
      <el-button size="small" type="primary" :loading="saving" @click="handleSave">
        保存
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { teamApi } from '@/api/team'

const props = defineProps({
  /** 对话框可见性（v-model） */
  modelValue: { type: Boolean, default: false },
  /** 当前球队信息（用于获取 memberId） */
  currentTeam: { type: Object, default: null },
  /** 当前球员记录ID（null 表示尚无球员记录） */
  currentPlayerId: { type: Number, default: null },
  /** 表单初始数据 */
  editForm: { type: Object, default: () => ({
    jerseyName: '', jerseyNumber: 0, position: '', portraitPhoto: ''
  })}
})

const emit = defineEmits(['update:modelValue', 'saved'])

const saving = ref(false)
const uploading = ref(false)
const registering = ref(false)

/** 本地表单数据，从 editForm prop 同步 */
const form = reactive({ ...props.editForm })

// 当 editForm prop 变化时同步到本地 form
watch(() => props.editForm, (val) => {
  Object.assign(form, val)
}, { deep: true, immediate: true })

/** 上传定妆照 */
async function handlePortraitUpload(file) {
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await request.post('/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.portraitPhoto = res.data
    ElMessage.success('照片上传成功')
  } catch (e) {
    ElMessage.error('照片上传失败')
  } finally {
    uploading.value = false
  }
  return false // 阻止 el-upload 默认上传行为
}

/** 保存：更新队伍成员信息（定妆照），若有球员记录则联动更新球员信息 */
async function handleSave() {
  if (!props.currentTeam?.memberId) {
    ElMessage.warning('未找到队伍成员信息')
    return
  }

  saving.value = true
  try {
    await teamApi.updateTeamMember(props.currentTeam.memberId, {
      portraitPhoto: form.portraitPhoto || undefined,
      jerseyName: form.jerseyName || undefined,
      jerseyNumber: form.jerseyNumber ?? undefined,
      position: form.position || undefined
    })
    ElMessage.success('个人信息已更新')
    emit('update:modelValue', false)
    emit('saved')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

/** 注册球员：为当前队伍成员创建球员记录 */
async function handleRegisterPlayer() {
  if (!props.currentTeam?.memberId) {
    ElMessage.warning('未找到队伍成员信息')
    return
  }

  registering.value = true
  try {
    await teamApi.registerPlayerForMember(props.currentTeam.memberId, {
      jerseyName: form.jerseyName || undefined,
      jerseyNumber: form.jerseyNumber ?? undefined,
      position: form.position || undefined
    })
    ElMessage.success('球员注册成功')
    emit('update:modelValue', false)
    emit('saved')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    registering.value = false
  }
}
</script>

<style scoped>
.portrait-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.portrait-upload-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.portrait-hint {
  font-size: 12px;
  color: var(--color-text-secondary, #909399);
}
</style>
