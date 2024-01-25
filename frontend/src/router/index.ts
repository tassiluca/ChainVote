import {makeRequest} from '@/assets/utils'
import {createRouter, createWebHistory} from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import VotingDetails from '@/views/VotingDetails.vue'
import NotFound from "@/views/NotFound.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      beforeEnter: (to, from, next) => {
        try {
          makeRequest(`http://localhost:8080/elections`, "GET")
            .then((response) => {
              to.meta.data = response.data.data;
              next();
            })
            .catch((error) => {
              console.log(error);
              to.meta.data = [{
                id: "id1",
                name: "Elezione del presidente del consiglio dei ministri prova 1",
                start: new Date("2021-10-04T10:00"),
                end: new Date("2026-11-04T10:00"),
                affluence: "20%",
                choices: [
                  "choice 0",
                  "choice 1",
                ]
              },
                {
                  id: "id2",
                  name: "Election 2",
                  start: new Date("2021-10-04T10:00"),
                  end: new Date("2025-11-04T10:00"),
                  affluence: "20%",
                  choices: [
                    "choice 0",
                    "choice 1",
                  ]
                },
                {
                  id: "id3",
                  name: "Election 3",
                  start: new Date("2027-10-04T10:00"),
                  end: new Date("2028-11-04T10:00"),
                  affluence: "20%",
                  choices: [
                    "choice 0",
                    "choice 1",
                  ]
                }
              ]
              next();
            });
        } catch (error) {
          console.log(error);
        }
      },
      component: () => import('@/views/DashboardView.vue'),
    },
    {
      path: '/elections',
      name: 'elections',
      component: () => import('@/views/ElectionsTestView.vue'),
    },
    {
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => {
        try {
          const jwtToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOnsiX2lkIjoiNjVhYjA5MWMxMWY3Mjc3OWZiNDU0ZmY3IiwiZW1haWwiOiJnaW9yaTIucHJvdmFAdGVzdC5pdCIsInBhc3N3b3JkIjoiJDJiJDEwJDQ5bFUvMzc1Y0RwWVpaNi8ueGdRdi5HL3BuaW1PUUQvN2pRTjlvbjk4RG1WVXlXUkx1bjdhIiwiZmlyc3ROYW1lIjoiR2lvcGFpbiIsInNlY29uZE5hbWUiOiJOb0dhaW4iLCJyb2xlIjoidXNlciIsIl9fdiI6MH0sImlhdCI6MTcwNTk1MjAzNywiZXhwIjoxNzA1OTUyOTM3LCJhdWQiOiJodHRwczovL3d3dy5jaGFpbnZvdGUuY29tIiwiaXNzIjoiQ2hhaW5Wb3RlIn0.xKYQsgBGYlXUSYQguoXoKsnRMtZrYCXnjf6vx6E84NlmmofcLbzui2Bxi-NoE9PxxQr8VpF89KNXtaDWYtRF9k6FDlkfPuO6yiDpFaeyLo44aNVZcRMvQQSj_TPIf0xB0D8rqxyF8aH4sFAZUxrOlGjP_pwKYZXvmsEQSnGR5Oc3lg_-bv_kpeV5PWPzep8FkI0-YhirzSPERXY7PoFnay1Y6boZbZmcWQQQV6MXxutRpCBumIO31MbHwoc6VVryKPJ2egM80i5UqX57puJ0hPA_MbS89R3UkS3csAFS8PwxLTxmIXmL03vqi72ZNlbV3qn4K4MPu8cfB--4ABzmSt-urDP5AIB5AAno_0O8NDLX5kuvq4kKbnXBSPbeA6jLKxvjwlIFLAN_znhfSuWoGhqQAusXyLPyXWjtEkfVvGMNHoWVEPZYjILI9p6xB9timHwvDtDUxT9B80mVy0T4sbSV9AknB163gVy1YFYfOjr-EZyyc3Z9IM3lrvSJNR5xF-8vDQy50g6R0pM5vngwVnsXIQH2z0fasfsLSAcemFYjgvMNxbWrmPualcQ-BiMtmQDo8jPj4BZmR-aH0_hWH_WEWquE-1To93RUeQISlqm7WuhwUSiH80s3Fp_kE70lF8HZyn2hX4rFovAfglkXY43ZyPrm1dmgH-b4Ved17nM";
          makeRequest(`http://localhost:8080/election/info/detail/${to.params.id}`, "GET", null, jwtToken)
            .then((response) => {
              to.meta.data = response.data.data;
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
      path: '/elections',
      name: 'elections',
      props: route => ({ qualifier: route.query.qualifier }),
      beforeEnter: (to, from, next) => {
        try {
          makeRequest(`http://localhost:8080/elections`, "GET")
            .then((response) => {
              to.meta.data = response.data.data;
              next();
            })
            .catch((error) => {
              // TODO: handle error here
              console.log(error);
              to.meta.data = [{
                  id: "id1",
                  name: "Elezione del presidente del consiglio dei ministri prova 1",
                  start: new Date("October 13, 2022 10:00"),
                  end: new Date("October 13, 2025 10:00"),
                  status: "open",
                  affluence: "20%",
                  choices: [
                    "choice 0",
                    "choice 1",
                  ]
                },
                {
                  id: "id2",
                  name: "Election 2",
                  start: new Date("2021-10-04T10:00"),
                  end: new Date("2025-11-04T10:00"),
                  status: "closed",
                  affluence: "20%",
                  choices: [
                    "choice 0",
                    "choice 1",
                    "choice 0",
                    "choice 1",
                    "choice 0",
                    "choice 1",
                    "choice 0",
                    "choice 1",
                    "choice 0",
                    "choice 1",
                    "choice 0",
                    "choice 1",
                  ]
                },
                {
                  id: "id3",
                  name: "Election 3",
                  start: new Date("2021-10-04T10:00"),
                  end: new Date("2021-11-04T10:00"),
                  status: "soon",
                  affluence: "20%",
                  choices: [
                    "choice 0",
                    "choice 1",
                  ]
                }
              ]
              // next({ name: 'error' });
              next();
            });
        } catch (error) {
          console.log(error);
          next({ name: 'error' });
        }
      },
      component: () => import('@/views/ElectionsView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/user/notifications',
      name: 'notifications',
      component: NotificationsView
    },
    {
      path: '/voting/details/:id',
      name: 'voting-details',
      component: VotingDetails
    },
    {
      path: '/user',
      name: 'user-area',
      beforeEnter: (to, from, next) => {
        try {
          // TODO: correct url
          makeRequest(`http://localhost:8080/users/info`, "GET")
              .then((response) => {
                to.meta.data = response.data.data;
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
      component: () => import('@/views/UserAreaView.vue'),
    },
    {
      path: '/register', 
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
    },
    {
      path: '/elections/create',
      name: 'create-election',
      component: () => import('@/views/CreateElectionView.vue'),
    },
    {
      // TODO: change path
      path: '/error',
      name: 'error',
      component: () => import('@/views/ErrorView.vue'),
    },
    {
      // TODO: change path
      path: '/no-permission',
      name: 'no-permission',
      component: () => import('@/views/NoPermissionView.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFound,
    },
  ]
})

export default router
