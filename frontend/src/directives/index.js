/**
 * 自定义指令统一注册入口
 *
 * 在 main.js 中导入：
 *   import directives from './directives'
 *   app.use(directives)
 */
import debounce from './debounce'
import focus from './focus'
import ellipsisTooltip from './ellipsisTooltip'
import copy from './copy'

export default {
  install(app) {
    app.directive('debounce', debounce)
    app.directive('focus', focus)
    app.directive('ellipsis-tooltip', ellipsisTooltip)
    app.directive('copy', copy)
  }
}
