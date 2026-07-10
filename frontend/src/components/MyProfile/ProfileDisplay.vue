<template>
  <div class="profile-display">
    <div class="avatar-section">
      <el-avatar :size="100" :src="profile.avatar || defaultAvatar" />
    </div>

    <el-descriptions :column="1" border class="profile-descriptions">
      <el-descriptions-item label="用户名">
        {{ profile.username }}
      </el-descriptions-item>
      <el-descriptions-item label="昵称">
        {{ profile.nickname || '未设置' }}
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag :type="roleTagType" size="small">
          {{ profile.roleName || '未知' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ profile.genderName || '未设置' }}
      </el-descriptions-item>
      <el-descriptions-item label="年龄">
        {{ profile.age != null ? profile.age + ' 岁' : '未设置' }}
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  profile: {
    type: Object,
    required: true
  }
})

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const roleTagType = computed(() => {
  const map = {
    '系统管理员': 'danger',
    '赛事管理员': 'warning',
    '球员': 'success',
    '普通用户': 'info'
  }
  return map[props.profile?.roleName] || 'info'
})
</script>

<style scoped>
.profile-display {
  padding-bottom: 8px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.profile-descriptions {
  margin-top: 16px;
}
</style>
