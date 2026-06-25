import { createRouter, createWebHistory } from 'vue-router'
import CreateOrder from '@/views/CreateOrder.vue'
import PayOrder from '@/views/PayOrder.vue'
import QueryResult from '@/views/QueryResult.vue'
import Login from '@/views/login.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },{
    path: '/create',
    name: 'CreateOrder',
    component: CreateOrder
  },
  {
    path: '/pay/:orderId',
    name: 'PayOrder',
    component: PayOrder
  },
  {
    path: '/query',
    name: 'QueryResult',
    component: QueryResult
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.name !== 'Login' && !token) {
    next('/')
  } else {
    next()
  }
})

export default router