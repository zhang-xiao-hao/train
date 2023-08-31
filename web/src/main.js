import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 前端组件框架
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
// 图标库
import * as Icons from '@ant-design/icons-vue';

const app = createApp(App)
app.use(store).use(router).use(Antd).mount('#app')

// 全局使用图标库
const icons = Icons
for (const i in icons){
    app.component(i, icons[i])
}