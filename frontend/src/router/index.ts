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
          const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWM0NDE2YTNmN2ZhMTUzYzU0ZjY0IiwiZW1haWwiOiJnaW9yaS5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkYTBOQVRNRjdiaDBIbXlZL0Vrd1lTT0ZkZU12dFZ1RmZYV2VTcGtOdW1JaWxXN2pNL0I5TzIiLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNzA1MzY1Mzg5LCJleHAiOjE3MDUzNjYyODksImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.nJBJN0th7SBUmzNc5ET6f-DJ6EoRwpyUEoQXQyeqoWlzePhL20NWW8tdN4fBkrLCljIm__8IzBb6-AcxkMd2Gu0LVjkDP-elcATY2oPYJEp51lWL8Qo9Bi2JKzwUvZU-z3zknptAxP4VlDdO4Lu7Po8hRynn-r5i17yH3Ubw0xzrs29dfQ3ws6qrW4Gha76ZoLI0W4XXGCsUmVXuyQ1kdBwS-fH1FE-lfjPomOz8rUSB8m13n47Com7X20ZUU62nxIuK-ksf6PjoqHwtBDtO-6bsAlDBBVxgqNSup_490wrLuMxx6_QfTOw0G2N36OQfU4aEG8XjnC_WtpUnYh1S8Ozwr4XBPbaas7gkpo_ERDdhnfl2c9oKlPThrIkShWFwbSa9Ncs34sZawu-ztYA3QGLsG1KKnX3DTpK1_iKnq_dwiKmqqv_pJuVGKsyy6xomjGPXS6K1On5vA-7MdlYFT5NI6Cg3g8LakrPYk31__vPpX2ga8R14EFyhHuCaw6kSlYyWG3e-6uzgnBNRaMGs6OLV48SJeid6UC5HFyjaP5BQyYGzzeSUnRqkXCATfgNAQAXYOhCLWcWCrm_2xjdQ3b7zGEFZOLgA9OmNp2MRzyOxB8TsjHM52YJVbKEFc0J9Jj2YRzgbRzL4PEycdPaLJLkn_VbCCEN9VYPGYZc3mFw";
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
