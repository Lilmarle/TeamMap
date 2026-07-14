/**
 * v-ellipsis-tooltip — 文本溢出自动 Tooltip 指令
 *
 * 当元素文本内容超出容器宽度（CSS text-overflow: ellipsis）时，
 * 自动设置 title 属性，以便 hover 时显示完整文本。
 *
 * 用法：
 *   <span v-ellipsis-tooltip class="user-name">{{ nickname }}</span>
 *   <span v-ellipsis-tooltip="'自定义提示文本'" class="truncate">...</span>
 */
export default {
  mounted(el, binding) {
    checkOverflow(el, binding)
  },

  // 当绑定的数据更新时重新检查
  updated(el, binding) {
    checkOverflow(el, binding)
  }
}

function checkOverflow(el, binding) {
  // 优先使用指令传入的值，否则使用元素文本内容
  const tooltipText = binding.value || el.textContent

  if (el.scrollWidth > el.clientWidth || el.scrollHeight > el.clientHeight) {
    el.setAttribute('title', tooltipText)
    el.style.cursor = 'default'
  } else {
    el.removeAttribute('title')
    el.style.cursor = ''
  }
}
