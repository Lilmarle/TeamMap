<template>
  <div class="team-show-area">
    <div class="area-header">
      <h2 class="area-title">球队管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索球队名称、简称、类型..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          size="default"
        />
        <el-select v-model="filterSport" placeholder="运动类型" clearable size="default" class="filter-select">
          <el-option
            v-for="item in sportTypeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select v-model="filterGender" placeholder="性别" clearable size="default" class="filter-select">
          <el-option
            v-for="item in genderOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select v-model="filterCollege" placeholder="所属学院" clearable size="default" class="filter-select">
          <el-option
            v-for="item in collegeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加球队</el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 错误状态 -->
    <el-alert
      v-else-if="loadFailed"
      title="加载失败"
      type="error"
      :description="errorMessage"
      show-icon
      closable
      @close="loadFailed = false"
    >
      <template #actions>
        <el-button size="small" type="primary" @click="fetchTeams">重试</el-button>
      </template>
    </el-alert>

    <!-- 搜索结果为空 -->
    <el-empty v-else-if="filteredTeams.length === 0" :description="searchKeyword ? '未匹配到相关球队' : '暂无球队数据'" />

    <!-- 球队表格 -->
    <el-table
      v-else
      :data="filteredTeams"
      border
      stripe
      style="width: 100%"
      size="default"
      row-key="teamId"
      class="team-table"
    >
      <TeamCard :onEdit="handleEdit" :onDelete="handleDelete" />
    </el-table>

    <!-- 编辑球队对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改球队信息" width="560px" :close-on-click-modal="false">
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
        label-position="right"
        size="default"
      >
        <!-- Logo 上传 -->
        <el-form-item label="球队 Logo" prop="teamLogo">
          <div class="logo-upload-wrapper">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeLogoUpload"
              :http-request="uploadLogo"
              accept="image/jpeg,image/png,image/gif,image/bmp,image/webp"
            >
              <el-avatar :size="72" :src="editForm.teamLogo" class="logo-preview">
                {{ editForm.teamName?.charAt(0) || '?' }}
              </el-avatar>
              <div class="logo-upload-overlay">
                <el-icon size="20"><Plus /></el-icon>
                <span>更换</span>
              </div>
            </el-upload>
            <span class="logo-hint">点击上传 Logo（支持 JPG/PNG/GIF，不超过 10MB）</span>
          </div>
        </el-form-item>

        <el-form-item label="球队名称" prop="teamName">
          <el-input v-model="editForm.teamName" placeholder="请输入球队名称" />
        </el-form-item>

        <el-form-item label="球队简称" prop="teamShortName">
          <el-input v-model="editForm.teamShortName" placeholder="请输入球队简称" maxlength="20" />
        </el-form-item>

        <el-form-item label="运动类型" prop="sportType">
          <el-select v-model="editForm.sportType" placeholder="请选择运动类型" style="width: 100%">
            <el-option :value="1" label="足球" />
            <el-option :value="2" label="篮球" />
            <el-option :value="3" label="排球" />
          </el-select>
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%">
            <el-option :value="1" label="男" />
            <el-option :value="2" label="女" />
            <el-option :value="3" label="混合" />
          </el-select>
        </el-form-item>

        <el-form-item label="队伍级别" prop="teamRank">
          <el-select v-model="editForm.teamRank" placeholder="请选择队伍级别" style="width: 100%">
            <el-option :value="1" label="院队" />
            <el-option :value="2" label="校队" />
            <el-option :value="3" label="班队" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="editForm.collegeId" placeholder="请选择所属学院" style="width: 100%">
            <el-option
              v-for="college in colleges"
              :key="college.id"
              :label="college.fullName"
              :value="college.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="teamDescription">
          <el-input v-model="editForm.teamDescription" type="textarea" :rows="3" placeholder="请输入球队描述" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { teamApi } from '@/api/team'
import { collegeApi } from '@/api/college'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import request from '@/api/request'
import TeamCard from './TeamCard.vue'

const teams = ref([])
const loading = ref(false)
const loadFailed = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')
const filterSport = ref('')
const filterGender = ref('')
const filterCollege = ref('')

// ---- 编辑对话框 ----
const editDialogVisible = ref(false)
const submitting = ref(false)
const editFormRef = ref(null)
const editingTeamId = ref(null)
const colleges = ref([])

const editForm = reactive({
  teamName: '',
  teamShortName: '',
  teamLogo: '',
  sportType: null,
  gender: null,
  teamRank: null,
  collegeId: null,
  teamDescription: ''
})

const editRules = {
  teamName: [
    { required: true, message: '请输入球队名称', trigger: 'blur' },
    { max: 100, message: '球队名称不能超过100个字符', trigger: 'blur' }
  ],
  sportType: [
    { required: true, message: '请选择运动类型', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  teamRank: [
    { required: true, message: '请选择队伍级别', trigger: 'change' }
  ]
}

function handleEdit(team) {
  editingTeamId.value = team.teamId
  editForm.teamName = team.teamName || ''
  editForm.teamShortName = team.teamShortName || ''
  editForm.teamLogo = team.teamLogo || ''
  editForm.sportType = team.sportType
  editForm.gender = team.gender
  editForm.teamRank = team.teamRank
  editForm.collegeId = null // 需从 colleges 列表匹配
  editForm.teamDescription = team.teamDescription || ''

  // 根据学院全称匹配 collegeId
  if (team.collegeFullName && colleges.value.length > 0) {
    const match = colleges.value.find(c => c.fullName === team.collegeFullName)
    if (match) editForm.collegeId = match.id
  }

  editDialogVisible.value = true
}

async function handleDelete(team) {
  try {
    await ElMessageBox.confirm(
      `确定要删除球队「${team.teamName}」吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    await teamApi.deleteTeam(team.teamId)
    ElMessage.success(`球队「${team.teamName}」已删除`)
    await fetchTeams()
  } catch {
    // 用户取消则不处理
  }
}

function beforeLogoUpload(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('仅支持 JPG/PNG/GIF/BMP/WEBP 格式的图片')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('Logo 图片不能超过 10MB')
    return false
  }
  return true
}

async function uploadLogo({ file }) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await request.post('/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.code === 200) {
      editForm.teamLogo = res.data
      ElMessage.success('Logo 上传成功')
    } else {
      ElMessage.error(res.message || 'Logo 上传失败')
    }
  } catch (e) {
    ElMessage.error(e.message || 'Logo 上传失败')
  }
}

async function submitEdit() {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await teamApi.updateTeam({
      teamId: editingTeamId.value,
      teamName: editForm.teamName,
      teamShortName: editForm.teamShortName,
      teamLogo: editForm.teamLogo,
      sportType: editForm.sportType,
      gender: editForm.gender,
      teamRank: editForm.teamRank,
      collegeId: editForm.collegeId,
      teamDescription: editForm.teamDescription
    })
    ElMessage.success('修改球队信息成功')
    editDialogVisible.value = false
    await fetchTeams()
  } catch (e) {
    ElMessage.error(e.message || '修改失败')
  } finally {
    submitting.value = false
  }
}

async function fetchColleges() {
  try {
    const res = await collegeApi.getAll()
    colleges.value = res.data || []
  } catch {
    // 学院列表加载失败不影响主体功能
  }
}

const sportTypeOptions = computed(() => {
  return [...new Set(teams.value.map(t => t.sportTypeName).filter(Boolean))]
})

const genderOptions = computed(() => {
  return [...new Set(teams.value.map(t => t.genderName).filter(Boolean))]
})

const collegeOptions = computed(() => {
  return [...new Set(teams.value.map(t => t.collegeFullName).filter(Boolean))]
})

const filteredTeams = computed(() => {
  let result = teams.value

  // 关键词模糊搜索
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword) {
    result = result.filter(team => {
      return (
        team.teamName?.toLowerCase().includes(keyword) ||
        team.teamShortName?.toLowerCase().includes(keyword) ||
        team.sportTypeName?.toLowerCase().includes(keyword) ||
        team.genderName?.toLowerCase().includes(keyword) ||
        team.collegeFullName?.toLowerCase().includes(keyword) ||
        team.rankName?.toLowerCase().includes(keyword) ||
        team.teamDescription?.toLowerCase().includes(keyword)
      )
    })
  }

  // 运动类型筛选
  if (filterSport.value) {
    result = result.filter(team => team.sportTypeName === filterSport.value)
  }

  // 性别筛选
  if (filterGender.value) {
    result = result.filter(team => team.genderName === filterGender.value)
  }

  // 学院筛选
  if (filterCollege.value) {
    result = result.filter(team => team.collegeFullName === filterCollege.value)
  }

  return result
})

async function fetchTeams() {
  loading.value = true
  loadFailed.value = false
  errorMessage.value = ''

  try {
    const res = await teamApi.getAllTeams()
    teams.value = res.data || []
  } catch (e) {
    loadFailed.value = true
    errorMessage.value = e.message || '获取球队列表失败'
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  ElMessage.info('添加球队功能待实现')
}

onMounted(() => {
  fetchTeams()
  fetchColleges()
})
</script>

<style scoped>
.team-show-area {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.area-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.area-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 220px;
}

.filter-select {
  width: 140px;
}

.loading-container {
  padding: 40px;
}

/* Logo 上传样式 */
.logo-upload-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.logo-upload-wrapper .el-upload {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  border: 2px dashed var(--color-primary-light);
  transition: border-color 0.3s;
}

.logo-upload-wrapper .el-upload:hover {
  border-color: var(--color-primary);
}

.logo-preview {
  display: block;
}

.logo-upload-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.3s;
}

.logo-upload-wrapper .el-upload:hover .logo-upload-overlay {
  opacity: 1;
}

.logo-hint {
  font-size: 12px;
  color: var(--color-text-secondary);
}

/* 品跑蓝主题表格样式 */
.team-table {
  --el-table-border-color: var(--color-primary-light);
  --el-table-border: 1px solid var(--color-primary-light);
  --el-table-header-bg-color: var(--color-primary-bg);
  --el-table-header-text-color: var(--color-primary-dark);
  --el-table-row-hover-bg-color: var(--color-primary-bg);
}

.team-table :deep(.el-table__body tr.el-table__row--striped td) {
  background-color: #F2F9FE;
}

.team-table :deep(.el-table__body tr.el-table__row--striped:hover td) {
  background-color: var(--color-primary-bg);
}

.team-table :deep(.el-table__header-wrapper) {
  border-bottom: 2px solid var(--color-primary);
}

.team-table :deep(.el-table__header th) {
  font-weight: 600;
}
</style>
