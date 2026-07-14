<template>
  <el-dialog
    :model-value="visible"
    :title="createMode === 'single' ? '创建小组' : '批量创建小组'"
    :width="createMode === 'single' ? '420px' : '700px'"
    @update:model-value="$emit('update:visible', $event)"
  >
    <!-- 模式切换 -->
    <div class="create-mode-bar">
      <el-radio-group v-model="createMode" size="small">
        <el-radio-button value="single">单个创建</el-radio-button>
        <el-radio-button value="batch">批量创建</el-radio-button>
      </el-radio-group>
    </div>

    <!-- ========== 单个创建 ========== -->
    <template v-if="createMode === 'single'">
      <el-form
        ref="singleFormRef"
        :model="singleForm"
        :rules="singleRules"
        label-width="100px"
        label-position="top"
      >
        <el-form-item label="小组名称" prop="name">
          <el-input v-model="singleForm.name" placeholder="如：A组、B组" maxlength="100" />
        </el-form-item>
        <el-form-item label="球队总数" prop="teamCount">
          <el-input-number v-model="singleForm.teamCount" :min="2" :max="32" />
        </el-form-item>
        <el-form-item label="直接出线数" prop="directAdvance">
          <el-input-number v-model="singleForm.directAdvance" :min="1" :max="8" />
        </el-form-item>
        <el-form-item label="间接出线数">
          <el-input-number v-model="singleForm.indirectAdvance" :min="0" :max="4" />
        </el-form-item>
        <el-form-item label="循环方式">
          <el-radio-group v-model="singleForm.roundType">
            <el-radio :value="1">单循环</el-radio>
            <el-radio :value="2">双循环</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </template>

    <!-- ========== 批量创建 ========== -->
    <template v-if="createMode === 'batch'">
      <p class="batch-hint">可一次性添加多个小组，每个小组的名称需唯一。</p>
      <div class="batch-group-list">
        <div
          v-for="(group, index) in batchGroups"
          :key="index"
          class="batch-group-item"
        >
          <div class="batch-group-header">
            <span class="batch-group-title">小组 {{ index + 1 }}</span>
            <el-button
              v-if="batchGroups.length > 1"
              type="danger"
              size="small"
              text
              @click="removeBatchGroup(index)"
            >
              删除
            </el-button>
          </div>
          <el-form
            :ref="el => setBatchFormRef(el, index)"
            :model="group"
            :rules="batchRules"
            label-width="100px"
            size="small"
          >
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="小组名称" prop="name" required>
                  <el-input v-model="group.name" placeholder="如：A组" maxlength="100" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="球队总数" prop="teamCount" required>
                  <el-input-number v-model="group.teamCount" :min="2" :max="32" style="width:100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="直接出线数" prop="directAdvance" required>
                  <el-input-number v-model="group.directAdvance" :min="1" :max="8" style="width:100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="间接出线数">
                  <el-input-number v-model="group.indirectAdvance" :min="0" :max="4" style="width:100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="循环方式" label-width="100px">
              <el-radio-group v-model="group.roundType">
                <el-radio :value="1">单循环</el-radio>
                <el-radio :value="2">双循环</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="add-batch-group-btn">
        <el-button type="warning" plain @click="addBatchGroup">+ 添加小组</el-button>
      </div>
    </template>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleCreate">
        <template v-if="createMode === 'single'">确认创建</template>
        <template v-else>批量创建 ({{ batchGroups.length }} 个小组)</template>
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

const createMode = ref('single')

// ===== 单个创建 =====
const singleFormRef = ref(null)
const submitting = ref(false)
const singleForm = reactive({
  name: '',
  teamCount: 4,
  directAdvance: 1,
  indirectAdvance: 0,
  roundType: 1
})
const singleRules = {
  name: [{ required: true, message: '请输入小组名称', trigger: 'blur' }],
  teamCount: [{ required: true, message: '请选择球队总数', trigger: 'change' }],
  directAdvance: [{ required: true, message: '请选择直接出线数', trigger: 'change' }]
}

// ===== 批量创建 =====
const batchFormRefs = ref([])
function setBatchFormRef(el, index) {
  batchFormRefs.value[index] = el
}

function createEmptyGroup() {
  return { name: '', teamCount: 4, directAdvance: 1, indirectAdvance: 0, roundType: 1 }
}
const batchGroups = reactive([createEmptyGroup()])

const batchRules = {
  name: [
    { required: true, message: '请输入小组名称', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const names = batchGroups.map(g => g.name.trim())
        if (names.filter(n => n === value.trim()).length > 1) {
          callback(new Error('小组名称不能重复'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  teamCount: [{ required: true, message: '请选择球队总数', trigger: 'change' }],
  directAdvance: [{ required: true, message: '请选择直接出线数', trigger: 'change' }]
}

function addBatchGroup() {
  batchGroups.push(createEmptyGroup())
  batchFormRefs.value.push(null)
}

function removeBatchGroup(index) {
  batchGroups.splice(index, 1)
  batchFormRefs.value.splice(index, 1)
}

// ===== 提交 =====
async function handleCreate() {
  if (createMode.value === 'single') {
    const valid = await singleFormRef.value?.validate().catch(() => false)
    if (!valid) return
    submitting.value = true
    try {
      await tournamentApi.addGroupStage({
        tournamentId: props.tournamentId,
        ...singleForm
      })
      ElMessage.success(`小组「${singleForm.name}」创建成功`)
      singleFormRef.value?.resetFields()
      emit('update:visible', false)
      emit('success')
    } catch (e) {
      ElMessage.error(e.message || '创建小组失败')
    } finally {
      submitting.value = false
    }
  } else {
    // 批量创建
    for (let i = 0; i < batchGroups.length; i++) {
      const ref = batchFormRefs.value[i]
      if (ref) {
        try { await ref.validate() } catch {
          ElMessage.warning(`请完善「小组 ${i + 1}」的信息`)
          return
        }
      }
    }
    submitting.value = true
    try {
      await tournamentApi.batchAddGroupStages({
        tournamentId: props.tournamentId,
        groups: batchGroups.map(g => ({
          name: g.name.trim(),
          teamCount: g.teamCount,
          directAdvance: g.directAdvance,
          indirectAdvance: g.indirectAdvance,
          roundType: g.roundType
        }))
      })
      ElMessage.success(`成功创建 ${batchGroups.length} 个小组`)
      batchGroups.splice(0, batchGroups.length, createEmptyGroup())
      batchFormRefs.value = []
      emit('update:visible', false)
      emit('success')
    } catch (e) {
      ElMessage.error(e.message || '批量创建小组失败')
    } finally {
      submitting.value = false
    }
  }
}

// 打开时重置
watch(() => props.visible, (val) => {
  if (val) {
    createMode.value = 'single'
    singleFormRef.value?.resetFields()
    batchGroups.splice(0, batchGroups.length, createEmptyGroup())
    batchFormRefs.value = []
  }
})
</script>

<style scoped>
.create-mode-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border, #ebeef5);
}

/* ---- 批量创建 ---- */
.batch-hint {
  font-size: 13px;
  color: var(--color-text-secondary, #909399);
  margin-bottom: 12px;
}

.batch-group-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 400px;
  overflow-y: auto;
}

.batch-group-item {
  border: 1px solid var(--color-border, #dcdfe6);
  border-radius: 8px;
  padding: 14px;
  background: var(--color-bg-page, #f5f7fa);
}

.batch-group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px dashed var(--color-border, #dcdfe6);
}

.batch-group-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-text-primary, #303133);
}

.add-batch-group-btn {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>
