import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import VoteView from '@/views/VoteView.vue'
import { makeRequest } from '@/assets/utils'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => {
        try {
          const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWE5ZGM2YTNmN2ZhMTUzYzU0ZjJiIiwiZW1haWwiOiJhZG1pbi50ZXN0QHRlc3QuaXQiLCJwYXNzd29yZCI6IiQyYiQxMCRjMjB4NS9Xdjhqa0ZSVHlaV3VZU0J1dVJiQ2RwNTBIUGozMWFMeHhHbjRjN0VTd1JKUzg1UyIsImZpcnN0TmFtZSI6Ikdpb3BhaW4iLCJzZWNvbmROYW1lIjoiTm9HYWluIiwicm9sZSI6ImFkbWluIiwiX192IjowfSwiaWF0IjoxNzA1MzYwMTYxLCJleHAiOjE3MDUzNjEwNjEsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.mkE6xwTWfwksUSFPMX4UIQ37HteOBpkS9BDRWFehBfXQHYLpcZ83gdjQ6d9Nt5Rc7Lfo3Wdd9jBdxepr0XUCxP2mAGqfGtuaBYfFW2kKWFzKMWlZk6CNHu7NEU_-f9GzdsTfx-to0VcBeOubSWjUvjDY-3UqAjDr9Rz83jRyGPjrReoMSiAJO1uVIvADSMHK0UpZnhtRf2CSS4t9_uFB2rDtR9vmLtozGj-_Pv07Oz7CwvUKt2fEApPq5BFK9QiYAkHaT665vDfALyh7AAejl-exDn5UZ-F3RFb-jJz_ZKR7ee8p8DrYt5oXqmAQKiYVIFFHuokyc7yZpA_Ly6uOLgCfNx2vFe1jUzSdGO4iJS8m1T_gRYoFLoK5zv1UXeTM41F4yXycOUOCOAbYIRW0Eiyqnf1JJIXU3FnKiiE6K4vlA0lSodYccmTvDh_ZUsDbcdRSJiGvAhMFSksdbV25qR7b42oL5I7ebNHZyzWLYgCYgdiAvaakoIhDDskaLbvRQrsPBpyyoRbHWH4Kfx7sC-G-bIUxUt5Sp_A5QPKXuWd3PpG2mxPywRewF-mw2Apn5NebMXKkPwI_h_yX-9921Fy_kwB0_UK__kBOgj2WYon5CxNdmUvbLnRU4VxyNjwcvNf0Km0PFBE4jgq-jw2HPqiyBJsMEhlqzDjIt2JM6eM";
          makeRequest(`http://localhost:8080/election/info/detail/${2027020974}`, "GET", jwtToken)
            .then((response) => {
              const data = response.data.data;
              to.meta.data = data;
              next();
            });
        } catch (error) {
          console.log(error);
        }
         
      },
      component: VoteView
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router
