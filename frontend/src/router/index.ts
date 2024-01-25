import {createRouter, createWebHistory} from 'vue-router'
import axios from "axios";
import LoginView from '@/views/LoginView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import NotFound from "@/views/NotFound.vue";
import HomeView from "@/views/HomeView.vue";
import DashboardView from "@/views/DashboardView.vue";
import CreateElectionView from "@/views/CreateElectionView.vue";
import RegisterView from "@/views/RegisterView.vue";
import UserAreaView from "@/views/UserAreaView.vue";
import ElectionDetails from "@/views/ElectionDetails.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
    },
    {
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => { // TODO: move to component / store ?
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
      component: () => import('@/views/ElectionsView.vue'),
    },
    {
      path: '/elections/create',
      name: 'create-election',
      component: CreateElectionView,
    },
    { // TODO: rename in elections/vote/:id ?
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => { // TODO: move to component / store ?
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
      path: '/elections/:id',
      name: 'election-details',
      component: ElectionDetails
    },
    {
      path: '/user',
      name: 'user-area',
      component: () => import('@/views/UserAreaView.vue'),
    },
    {
      path: '/user/notifications',
      name: 'notifications',
      component: NotificationsView
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
