<template>
  <el-dialog
    :model-value="visible"
    title="创建小组"
    width="420px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="top"
    >
      <el-form-item label="小组名称" prop="name">
        <el-input v-model="formData.name" placeholder="如：A组、B组" maxlength="100" />
      </el-form-item>
      <el-form-item label="球队总数" prop="teamCount">
        <el-input-number v-model="formData.teamCount" :min="2" :max="32" />
      </el-form-item>
      <el-form-item label="直接出线数" prop="directAdvance">
        <el-input-number v-model="formData.directAdvance" :min="1" :max="8" />
      </el-form-item>
      <el-form-item label="间接出线数">
        <el-input-number v-model="formData.indirectAdvance" :min="0" :max="4" />
      </el-form-item>
      <el-form-item label="循环方式">
        <el-radio-group v-model="formData.roundType">
          <el-radio :value="1">单循环</el-radio>
          <el-radio :value="2">双循环</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleCreate">
        确认创建
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  tournamentId: { type: Number, required: true }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const formData = reactive({
  name: '',
  teamCount: 4,
  directAdvance: 1,
  indirectAdvance: 0,
  roundType: 1
})
const formRules = {
  name: [{ required: true, message: '请输入小组名称', trigger: 'blur' }],
  teamCount: [{ required: true, message: '请选择球队总数', trigger: 'change' }],
  directAdvance: [{ required: true, message: '请选择直接出线数', trigger: 'change' }]
}

async function handleCreate() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await tournamentApi.addGroupStage({
      tournamentId: props.tournamentId,
      ...formData
    })
    ElMessage.success(`小组「${formData.name}」创建成功`)
    formRef.value?.resetFields()
    emit('update:visible', false)
    emit('success')
  } catch (e) {
    ElMessage.error(e.message || '创建小组失败')
  } finally {
    submitting.value = false
  }
}

// 对话框关闭时重置表单
watch(() => props.visible, (val) => {
  if (!val) {
    formRef.value?.resetFields()
  }
})
</script>
