/**
 * v-copy — 一键复制指令
 *
 * 点击元素时将指定文本复制到剪贴板。
 *
 * 用法：
 *   <!-- 复制元素文本内容 -->
 *   <span v-copy>点击复制这段文字</span>
 *
 *   <!-- 复制指令传入的值 -->
 *   <el-button v-copy="'要复制的文本'">复制</el-button>
 *
 *   <!-- 复制动态数据 -->
 *   <el-button v-copy="eventId">复制赛事ID</el-button>
 */
import { ElMessage } from 'element-plus'

export default {
  mounted(el, binding) {
    el.__copyHandler = async () => {
      // 优先使用指令绑定的值，否则使用元素文本内容
      const text = binding.value !== undefined && binding.value !== null
        ? String(binding.value)
        : el.textContent

      if (!text) {
        ElMessage.warning('无可复制的内容')
        return
      }

      try {
        // 优先使用 Clipboard API（现代浏览器）
        await navigator.clipboard.writeText(text)
        ElMessage.success('已复制到剪贴板')
      } catch {
        // 降级方案：使用 textarea + execCommand
        const textarea = document.createElement('textarea')
        textarea.value = text
        textarea.style.position = 'fixed'
        textarea.style.left = '-9999px'
        textarea.style.top = '-9999px'
        document.body.appendChild(textarea)
        textarea.select()

        try {
          document.execCommand('copy')
          ElMessage.success('已复制到剪贴板')
        } catch {
          ElMessage.error('复制失败，请手动复制')
        } finally {
          document.body.removeChild(textarea)
        }
      }
    }

    el.addEventListener('click', el.__copyHandler)
    // 添加可点击的视觉提示
    el.style.cursor = 'pointer'
  },

  unmounted(el) {
    if (el.__copyHandler) {
      el.removeEventListener('click', el.__copyHandler)
      delete el.__copyHandler
    }
  }
}
