import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import(/* webpackChunkName: "about" */ '../views/login.vue')
  },
  {
    path: '/',
    component: () => import(/* webpackChunkName: "about" */ '../views/main.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
