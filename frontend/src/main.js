import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import directives from './directives'

const app = createApp(App)

// 全局注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局注册自定义指令：v-debounce / v-focus / v-ellipsis-tooltip / v-copy
app.use(directives)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
