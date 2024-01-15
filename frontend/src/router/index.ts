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
          const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWE5ZGM2YTNmN2ZhMTUzYzU0ZjJiIiwiZW1haWwiOiJhZG1pbi50ZXN0QHRlc3QuaXQiLCJwYXNzd29yZCI6IiQyYiQxMCRjMjB4NS9Xdjhqa0ZSVHlaV3VZU0J1dVJiQ2RwNTBIUGozMWFMeHhHbjRjN0VTd1JKUzg1UyIsImZpcnN0TmFtZSI6Ikdpb3BhaW4iLCJzZWNvbmROYW1lIjoiTm9HYWluIiwicm9sZSI6ImFkbWluIiwiX192IjowfSwiaWF0IjoxNzA1MzYxNzc2LCJleHAiOjE3MDUzNjI2NzYsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.MozOYjb184eKcw5Mzlj72J7CrMgjnIiZcCyKWRhxfEZ1kBO9JhyK98nihAPFmMJT3pNlEaoWaaCE-kCLQU7JuhaOH67TUC2pIMWe7gp-GBvYnCAup72o1VY1wXtPoilUxuAf7dLmgab4vRb0fap1GKcqLmqu_72GIJuZ6cvZ9KDzSkc6VNS-gFTA4P-0d24r3_-R1H0PemBOtK6dz7kEBey__Ig8kn_7IJteZo_lcFaNID1HBVz8rKdVOyGe-GBX84TXZ6v5xClPLqD8llGpi5Q2Qbu65lj-YgMRTvuB9_cXAv_wiXNbYxOrTo8I0PMajF86pVSsLGSvauTC82Ep_yQqcPsTd8jNP8bZVwFADg1JKReSrI2hry_yDvKQNs0HEGMO7oilUtUcRXNnC4fCY25j3j_Dgby4--1SffkIU1BdFYCS_tzgMFbIOWV0_UbSXptHxabrcLxEC1CQnNKKYH-edMeIL7GiqVb5Zv6kB93kAUpR-G68YlQr4wVOCs3oNcwdRR0qVkZUPifl7ePzqRCkScKA2hLyVjksekIzvInTlf7cNsX0xfkZvQ7AxD668NNoDNXOxmq97tkqFc4yJzcD6e8clRWt9GCRIPDO94pTABZ8w-SsvfrlmyOJlMImv77pHLmPhyBZoFzKo0TdYIJIu2ZexR2Bsug_k84mpw4";
          makeRequest(`http://localhost:8080/election/info/detail/${to.params.id}`, "GET", jwtToken)
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
