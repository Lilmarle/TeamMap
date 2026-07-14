/**
 * v-debounce — 输入防抖指令
 *
 * 防抖输入事件，避免每次按键都触发昂贵的计算（如搜索过滤）。
 *
 * 用法：
 *   <!-- 方式1：传入回调函数（推荐） -->
 *   <el-input v-debounce:300="onSearchInput" v-model="searchKeyword" />
 *
 *   <!-- 方式2：传入 ref 对象（自动更新 ref.value） -->
 *   <el-input v-debounce:500="debouncedKeyword" />
 *
 * 修饰符：
 *   :300  防抖延迟毫秒数（默认 300）
 *
 * 注意：
 *   与 v-model 配合使用时，v-model 仍会即时更新视图（让用户看到输入），
 *   而 v-debounce 的回调会在用户停止输入后才触发。
 */
export default {
  mounted(el, binding) {
    const delay = Number(binding.arg) || 300
    let timer = null

    // 兼容 el-input 包裹层和原生 input
    const input = el.tagName === 'INPUT'
      ? el
      : el.querySelector('input, textarea')

    if (!input) {
      console.warn('[v-debounce] 未找到 input 元素', el)
      return
    }

    input.__debounceHandler = (e) => {
      clearTimeout(timer)
      timer = setTimeout(() => {
        const value = e.target.value

        // 方式1：binding.value 是回调函数
        if (typeof binding.value === 'function') {
          binding.value(value, e)
        }
        // 方式2：binding.value 是 ref 对象（需暴露 .value）
        else if (binding.value && typeof binding.value === 'object' && 'value' in binding.value) {
          binding.value.value = value
        }
      }, delay)
    }

    input.addEventListener('input', input.__debounceHandler)
  },

  unmounted(el) {
    const input = el.tagName === 'INPUT'
      ? el
      : el.querySelector('input, textarea')

    if (input?.__debounceHandler) {
      input.removeEventListener('input', input.__debounceHandler)
      delete input.__debounceHandler
    }
  }
}
