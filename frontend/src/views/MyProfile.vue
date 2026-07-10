<template>
  <div class="my-profile-page">
    <ProfileCard
      :loading="pageLoading"
      :load-failed="loadFailed"
      :has-data="!!profile"
      :title="isEditing ? '编辑个人信息' : '个人信息'"
      @retry="fetchProfile"
    >
      <template #actions>
        <el-button
          v-if="!isEditing"
          type="primary"
          :icon="EditIcon"
          @click="startEdit"
        >
          编辑
        </el-button>
        <template v-else>
          <el-button @click="cancelEdit">取消</el-button>
          <el-button
            type="primary"
            :loading="saving"
            @click="handleSave"
          >
            保存
          </el-button>
        </template>
      </template>

      <ProfileDisplay v-if="!isEditing && profile" :profile="profile" />

      <ProfileEdit
        v-else-if="isEditing && profile"
        ref="editFormRef"
        :profile="profile"
        @save="onSave"
        @cancel="cancelEdit"
      />
    </ProfileCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Edit as EditIcon } from '@element-plus/icons-vue'

import ProfileCard from '@/components/MyProfile/ProfileCard.vue'
import ProfileDisplay from '@/components/MyProfile/ProfileDisplay.vue'
import ProfileEdit from '@/components/MyProfile/ProfileEdit.vue'

const router = useRouter()
const userStore = useUserStore()

const pageLoading = ref(false)
const saving = ref(false)
const loadFailed = ref(false)
const isEditing = ref(false)
const profile = ref(null)
const editFormRef = ref(null)

async function fetchProfile() {
  const userId = userStore.user?.id
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  pageLoading.value = true
  loadFailed.value = false

  try {
    const res = await userApi.getUserProfile(userId)
    profile.value = res.data
  } catch {
    loadFailed.value = true
  } finally {
    pageLoading.value = false
  }
}

function startEdit() {
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function handleSave() {
  // 通过子组件暴露的 submit 方法触发表单校验并获取数据
  const valid = await editFormRef.value?.submit()
  // submit 内部已 emit('save', ...)，handleSave 作为兜底提交入口
}

async function onSave(formData) {
  const userId = userStore.user?.id
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  saving.value = true
  try {
    await userApi.updateProfile({
      userId,
      ...formData
    })

    ElMessage.success('个人信息修改成功')
    isEditing.value = false
    await fetchProfile()
  } catch {
    // 错误已在拦截器中处理，保留编辑内容
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.my-profile-page {
  min-height: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background: var(--color-bg-page);
  padding: 40px 20px;
}

@media (max-width: 640px) {
  .my-profile-page {
    padding: 16px 12px;
  }
}
</style>
