/**
 * v-focus — 自动聚焦指令
 *
 * 在元素挂载后自动聚焦到内部的 input/textarea 元素。
 * 常用于对话框打开后让第一个输入框获得焦点。
 *
 * 用法：
 *   <el-input v-focus v-model="form.name" placeholder="请输入名称" />
 *   <input v-focus />
 */
export default {
  mounted(el) {
    // 尝试直接聚焦
    if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA') {
      el.focus()
      return
    }

    // Element Plus 的 el-input 是包裹层，需找到内部的 input
    const input = el.querySelector('input, textarea')
    if (input) {
      // 延迟一帧以确保 DOM 完全渲染（尤其适用于对话框中的 el-input）
      requestAnimationFrame(() => {
        input.focus()
        // 对于 el-input，光标放在末尾
        if (input.value) {
          input.setSelectionRange(input.value.length, input.value.length)
        }
      })
    }
  }
}
