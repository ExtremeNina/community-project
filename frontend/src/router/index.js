import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/discover',
    name: 'Discover',
    component: () => import('@/views/Discover.vue')
  },
  {
    path: '/detail/:id',
    name: 'Detail',
    component: () => import('@/views/Detail.vue')
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/Publish.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('@/views/Messages.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue')
  },
  {
    path: '/note-manage',
    name: 'NoteManage',
    component: () => import('@/views/NoteManage.vue')
  },
  {
    path: '/data-center',
    name: 'DataCenter',
    component: () => import('@/views/DataCenter.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
