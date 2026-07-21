<template>
  <el-dialog v-model="visible" title="修改球员信息" width="500px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="球衣名" prop="jerseyName">
        <el-input v-model="form.jerseyName" placeholder="请输入球衣名" maxlength="50" />
      </el-form-item>

      <el-form-item label="球衣号码" prop="jerseyNumber">
        <el-input-number
          v-model="form.jerseyNumber"
          :min="0"
          :max="99"
          :step="1"
          :value-on-clear="null"
          placeholder="0-99"
        />
      </el-form-item>

      <el-form-item label="场上位置" prop="position">
        <el-input v-model="form.position" placeholder="如：前锋、后卫、守门员" maxlength="50" />
      </el-form-item>

      <el-form-item label="球员状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">可出战</el-radio>
          <el-radio :value="2">禁赛中</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitEdit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { playerApi } from '@/api/player'

const emit = defineEmits(['refresh'])

const visible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editingPlayerId = ref(null)

const form = reactive({
  jerseyName: '',
  jerseyNumber: null,
  position: '',
  status: 1
})

const rules = {
  jerseyNumber: [
    {
      validator: (rule, value, callback) => {
        if (value != null && (value < 0 || value > 99)) {
          callback(new Error('球衣号码必须在 0 到 99 之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  status: [
    { required: true, message: '请选择球员状态', trigger: 'change' }
  ]
}

/** 打开编辑对话框并填充当前行数据 */
function open(player) {
  editingPlayerId.value = player.playerId
  form.jerseyName = player.jerseyName || ''
  form.jerseyNumber = player.jerseyNumber ?? null
  form.position = player.position || ''
  form.status = player.status ?? 1
  visible.value = true
}

/** 提交编辑 */
async function submitEdit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await playerApi.updatePlayer(editingPlayerId.value, {
      jerseyName: form.jerseyName,
      jerseyNumber: form.jerseyNumber,
      position: form.position,
      status: form.status
    })
    ElMessage.success('球员信息修改成功')
    visible.value = false
    emit('refresh')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
