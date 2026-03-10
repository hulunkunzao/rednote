import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'
import ExploreView from '@/views/ExploreView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Layout,
      children: [
        {
          path: '/explore',
          name: 'explore',
          component: ExploreView,
        }
      ]
    },
    {
      path:'/profile',
      name: 'profile',
      component:()=>(import('@/views/ProfileView.vue'))
    }

  ],
})

export default router
