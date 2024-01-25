import {createRouter, createWebHistory} from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import VotingDetails from '@/views/VotingDetails.vue'
import NotFound from "@/views/NotFound.vue";
import Test from "@/views/Test.vue";
import axios from "axios";

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
          axios.get(`http://localhost:8080/elections`)
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
          axios.get(`http://localhost:8080/election/info/detail/${to.params.id}`)
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
          axios.get(`http://localhost:8080/elections`)
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
          axios.get(`http://localhost:8080/users/info`)
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
      path: '/test',
      name: 'test',
      component: Test,
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
