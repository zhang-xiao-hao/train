import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios';
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

// 打印请求信息日志
/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    console.log('返回错误：', error);
    return Promise.reject(error);
});
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);