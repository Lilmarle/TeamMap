<template>
  <el-dialog
    v-model="dialogVisible"
    title="添加赛事"
    width="520px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      label-position="right"
      size="default"
    >
      <el-form-item label="赛事名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入赛事名称" maxlength="100" show-word-limit />
      </el-form-item>

      <el-form-item label="运动类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择运动类型" style="width: 100%">
          <el-option :value="1" label="足球" />
          <el-option :value="2" label="篮球" />
          <el-option :value="3" label="排球" />
        </el-select>
      </el-form-item>

      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="请选择开始时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="请选择结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledEndDate"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['success'])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  name: '',
  type: null,
  startTime: '',
  endTime: ''
})

const rules = {
  name: [
    { required: true, message: '请输入赛事名称', trigger: 'blur' },
    { max: 100, message: '赛事名称不能超过100个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择运动类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

/**
 * 限制结束时间不能早于开始时间
 */
function disabledEndDate(time) {
  if (form.startTime) {
    const start = new Date(form.startTime).getTime()
    return time.getTime() <= start
  }
  return false
}

/**
 * 打开对话框
 */
function open() {
  dialogVisible.value = true
}

/**
 * 关闭对话框后的重置
 */
function handleClosed() {
  form.name = ''
  form.type = null
  form.startTime = ''
  form.endTime = ''
  formRef.value?.clearValidate()
}

/**
 * 提交表单
 */
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await tournamentApi.add({
      name: form.name,
      type: form.type,
      startTime: form.startTime,
      endTime: form.endTime
    })
    ElMessage.success(`赛事「${form.name}」创建成功`)
    dialogVisible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error(e.message || '创建赛事失败')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
