<template>
  <el-dialog
    :model-value="visible"
    title="管理比赛"
    width="420px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form :model="form" label-width="90px" size="small">
      <el-form-item label="队伍1">
        <div class="form-team-row">
          <span class="form-team-name">{{ match.team1ShortName || match.team1Name }}</span>
          <el-input-number v-model="form.team1Score" :min="0" :max="99" size="small" controls-position="right" style="width:120px" />
        </div>
      </el-form-item>
      <el-form-item label="队伍2">
        <div class="form-team-row">
          <span class="form-team-name">{{ match.team2ShortName || match.team2Name }}</span>
          <el-input-number v-model="form.team2Score" :min="0" :max="99" size="small" controls-position="right" style="width:120px" />
        </div>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status" style="width:100%">
          <el-option :value="1" label="未开始" />
          <el-option :value="2" label="进行中" />
          <el-option :value="3" label="已结束" />
        </el-select>
      </el-form-item>
      <el-form-item label="比赛名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="比赛时间">
        <el-date-picker v-model="form.matchTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
      </el-form-item>
      <el-form-item label="比赛地点">
        <el-input v-model="form.location" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button size="small" @click="$emit('update:visible', false)">取消</el-button>
      <el-button size="small" type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { tournamentApi } from '@/api/tournament'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  match: { type: Object, required: true }
})

const emit = defineEmits(['update:visible', 'updated'])

const saving = ref(false)

const form = reactive({
  team1Score: 0,
  team2Score: 0,
  status: 1,
  name: '',
  matchTime: '',
  location: ''
})

// 对话框打开时同步数据
watch(() => props.visible, (val) => {
  if (val && props.match) {
    form.team1Score = props.match.team1Score ?? 0
    form.team2Score = props.match.team2Score ?? 0
    form.status = props.match.status ?? 1
    form.name = props.match.name || ''
    form.matchTime = props.match.matchTime || ''
    form.location = props.match.location || ''
  }
})

async function handleSave() {
  saving.value = true
  try {
    await tournamentApi.updateMatch(props.match.id, {
      team1Score: form.team1Score,
      team2Score: form.team2Score,
      status: form.status,
      name: form.name || undefined,
      matchTime: form.matchTime || undefined,
      location: form.location || undefined
    })
    ElMessage.success('保存成功')
    emit('update:visible', false)
    emit('updated')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.form-team-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
.form-team-name {
  font-weight: 500;
  font-size: 14px;
}
</style>
