import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Layout,
      children:[
        {
          path:'explore',
          name:'explore',
          component: () => import('@/views/ExploreView.vue'),
        },
        {
          path:'discover',
          name:'discover',
          component: () => import('@/views/HomeView.vue'),
        }
      ]
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
    },
    {
      path: '/publish',
      name: 'publish',
      component: () => import('@/views/PublishView.vue'),
      children:[
        {
          path:'/video',
          name:'publish-video',
          component: () => import('@/components/UploadVideo.vue'),
        },
        {
          path:'/image',
          name:'publish-image',
          component: () => import('@/components/UploadImage.vue'),
        },
        {
          path:'/article',
          name:'publish-article',
          component: () => import('@/components/UploadArticle.vue'),
        }
      ]
    },

  ],
})

export default router
