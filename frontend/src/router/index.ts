import { makeRequest } from '@/assets/utils'
import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: () => import('@/views/Dashboard.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => {
        try {
          const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhNWM0NDE2YTNmN2ZhMTUzYzU0ZjY0IiwiZW1haWwiOiJnaW9yaS5wcm92YUB0ZXN0Lml0IiwicGFzc3dvcmQiOiIkMmIkMTAkYTBOQVRNRjdiaDBIbXlZL0Vrd1lTT0ZkZU12dFZ1RmZYV2VTcGtOdW1JaWxXN2pNL0I5TzIiLCJmaXJzdE5hbWUiOiJHaW9wYWluIiwic2Vjb25kTmFtZSI6Ik5vR2FpbiIsInJvbGUiOiJ1c2VyIiwiX192IjowfSwiaWF0IjoxNzA1NDQyNzcwLCJleHAiOjE3MDU0NDM2NzAsImF1ZCI6Imh0dHBzOi8vd3d3LmNoYWludm90ZS5jb20iLCJpc3MiOiJDaGFpblZvdGUifQ.PUwOiTQFE6la7Vw1fpqerl6gQRKDj2yPTsp01ED3UhpdXxvzDDQ4Ul07pFvuL2-p3Lmd3MvpxyqMIOGnMSlLc84yKNleEgfd7uIpuRAgQ5wi-bAF-XXeEMfHgP9wdw3V_a9ufQTMLrZ2wq3ieyxMrJaRBTThVbJx5NuSUYzTnybMu7p6zcuomSsWzMGs4L4tchkbLhqPKgYdX1jb39wUX3SIxwW-FWShTHO34iKy-BM_ixHkj8ry-_cCt2KZ4eoT1SvYyym-gvK_g0wULy7gLJ66MKiZB5jcgtg77Tan3EnVTvdf__LmMbVIjP55vwKzpj9vzCK3gsznCgINuTaHLlbrDMPJrxcyEOYytwTp6DcuDOFe1-8kKwnwQPy6Sm-A3IZaOG4J6sYyk4NOwzxQI0v2HVwWyT5ON5TCyS_PU2ghlaTbrQnVL0lhwOmtr09V9aaBYNhZvGUs7LEq8N4NHxWcUqsxVivqqAefO-7uY4q10-SlEGUgGCPu1YLT6A2KcpEOejeiRS--hJOiO1LnzuX_prsAhE8NiIRij23zpqxe8XWPwJRX2YnHt-8frDZtTpSnu8sDlXcBu-7CI6EctmuwP3TPyJCyqHfIS5yT_qGT-gEGOr5zZkCR12oT-hXg5JvlbAPgooj8chHZI0IUXBreKG_zZQ3pD5iXZnAHR-E";
          makeRequest(`http://localhost:8080/election/info/detail/${to.params.id}`, "GET", null, jwtToken)
            .then((response) => {
              const data = response.data.data;
              to.meta.data = data;
              next();
            });
        } catch (error) {
          console.log(error);
          // next({ name: 'not-found' });
        }
      },
      component: () => import('@/views/VoteView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('@/views/AboutView.vue'),
    },
    {
      path: '/user',
      name: 'user-area',
      beforeEnter: (to, from, next) => {
        try {
          // TODO: correct url
          makeRequest(`http://localhost:8080/users/info`, "GET")
              .then((response) => {
                const data = response.data.data;
                to.meta.data = data;
                next();
              })
              .catch((error) => {
                // TODO: handle error here
                console.log('setting default user')
                console.log(error);
                to.meta.data = {
                  name: "Test User",
                  surname: "test surname",
                  password: "password",
                  email: "prova@unibo.it",
                  role: "user",
                }
                next();
              });
        } catch (error) {
          // TODO: handle error
          // console.log(error);
          // next({ err: 'user-not-found' });
          to.meta.data = {
            user: {
              name: "Test User",
              surname: "test surname",
              password: "password",
              email: "prova@unibo.it",
              role: "user",
            },
          }
          next();
        }
      },
      component: () => import('@/views/UserArea.vue'),
    },
  ]
})

export default router
