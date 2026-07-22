<template>
  <el-dialog
    v-model="dialogVisible"
    title="创建球队"
    width="550px"
    :close-on-click-modal="false"
    @open="handleOpen"
    @closed="handleClosed"
  >
    <el-form
      ref="createFormRef"
      :model="createForm"
      :rules="createRules"
      label-width="90px"
      @submit.prevent="handleCreate"
    >
      <el-form-item label="球队名称" prop="name">
        <el-input v-model="createForm.name" placeholder="请输入球队名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="球队简称" prop="shortName">
        <el-input v-model="createForm.shortName" placeholder="请输入球队简称（选填）" maxlength="20" />
      </el-form-item>
      <el-form-item label="运动类型" prop="type">
        <el-select v-model="createForm.type" placeholder="请选择运动类型" style="width: 100%">
          <el-option label="足球" :value="1" />
          <el-option label="篮球" :value="2" />
          <el-option label="排球" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="createForm.gender">
          <el-radio :value="1">男子</el-radio>
          <el-radio :value="2">女子</el-radio>
          <el-radio :value="3">混合</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="队伍级别" prop="rank">
        <el-select v-model="createForm.rank" placeholder="请选择队伍级别" style="width: 100%">
          <el-option label="班队" :value="3" />
          <el-option label="院队" :value="1" />
          <el-option label="校队" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="createForm.rank === 1" label="所属学院" prop="collegeId">
        <el-select
          v-model="createForm.collegeId"
          placeholder="请选择学院"
          style="width: 100%"
          :loading="collegesLoading"
        >
          <el-option
            v-for="col in colleges"
            :key="col.id"
            :label="col.fullName || col.name"
            :value="col.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="球队描述" prop="description">
        <el-input
          v-model="createForm.description"
          type="textarea"
          :rows="3"
          placeholder="请输入球队描述（选填）"
          maxlength="200"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="creating" @click="handleCreate">
        创建
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { teamApi } from '@/api/team'
import { collegeApi } from '@/api/college'

const props = defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = ref(false)

watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const creating = ref(false)
const createFormRef = ref(null)

/** 学院列表 */
const colleges = ref([])
const collegesLoading = ref(false)

const createForm = ref({
  name: '',
  shortName: '',
  type: null,
  gender: 1,
  rank: 3,
  collegeId: null,
  description: ''
})

const createRules = {
  name: [
    { required: true, message: '请输入球队名称', trigger: 'blur' },
    { min: 2, max: 50, message: '球队名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择运动类型', trigger: 'change' }
  ],
  rank: [
    { required: true, message: '请选择队伍级别', trigger: 'change' }
  ],
  collegeId: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (createForm.value.rank === 1 && !value) {
          callback(new Error('院队必须选择所属学院'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

async function loadColleges() {
  collegesLoading.value = true
  try {
    const res = await collegeApi.getAll()
    colleges.value = res.data || []
  } catch (e) {
    console.error('加载学院列表失败:', e)
    colleges.value = []
  } finally {
    collegesLoading.value = false
  }
}

async function handleOpen() {
  await loadColleges()
}

async function handleCreate() {
  const valid = await createFormRef.value?.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    // 构建 AddTeamRequest 格式：{ team: {...}, rank, collegeId }
    const payload = {
      team: {
        name: createForm.value.name,
        shortName: createForm.value.shortName || null,
        type: createForm.value.type,
        gender: createForm.value.gender,
        description: createForm.value.description || null
      },
      rank: createForm.value.rank,
      collegeId: createForm.value.collegeId
    }

    await teamApi.addTeam(payload)
    ElMessage.success('球队创建成功')
    dialogVisible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error(e.message || '创建球队失败')
  } finally {
    creating.value = false
  }
}

function handleClosed() {
  createForm.value = {
    name: '', shortName: '', type: null, gender: 1,
    rank: 3, collegeId: null, description: ''
  }
  createFormRef.value?.resetFields()
}
</script>
