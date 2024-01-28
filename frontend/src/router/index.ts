import {createRouter, createWebHistory} from 'vue-router'
import axios from "axios";
import LoginView from '@/views/LoginView.vue'
import PasswordForgotten from "@/views/PasswordForgotten.vue";
import NotificationsView from '@/views/NotificationsView.vue'
import NotFound from "@/views/NotFound.vue";
import HomeView from "@/views/HomeView.vue";
import DashboardView from "@/views/DashboardView.vue";
import CreateElectionView from "@/views/CreateElectionView.vue";
import RegisterView from "@/views/RegisterView.vue";
import UserAreaView from "@/views/UserAreaView.vue";
import ElectionDetails from "@/views/ElectionDetails.vue";
import ElectionsTestView from '@/views/ElectionsTestView.vue';
import CodeInsertionView from '@/views/CodeInsertionView.vue';
import VoteView from "@/views/VoteView.vue";
import ElectionsView from "@/views/ElectionsView.vue";
import {useAuthStore} from "@/stores/auth";
import { useVotingStore } from '@/stores/voting'
import ErrorView from "@/views/ErrorView.vue";
import NoPermissionView from "@/views/NoPermissionView.vue";
import {Role} from "@/commons/utils";
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    // must be declared by every route
    allowed: Role[];
  }
}


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
      path: '/password-forgotten',
      name: 'password-forgotten',
      component: PasswordForgotten
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: {
        allowed: [Role.User, Role.Admin]
      }
    },
    {
      path: '/vote/:id',
      name: 'vote',
      beforeEnter: (to, from, next) => { // TODO: move to component / store ?
        try {
          if (useVotingStore().getOtpInUse() === '') {
            next({ name: 'not-found' });
          }

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
      component: VoteView,
      meta: {
        allowed: [Role.User]
      }
    },
    {
      path: '/elections',
      name: 'elections',
      props: route => ({ qualifier: route.query.qualifier }),
      component: ElectionsView,
      meta: {
        allowed: [Role.User, Role.Admin]
      }
    },
    {
      path: '/test-modal',
      name: 'test-modal',
      component: ElectionsTestView
    },
    {
      path: '/insert-code/:id',
      name: 'insert-code',
      component: CodeInsertionView,
      meta: {
        allowed: [Role.User]
      }
    },
    {
      path: '/elections/create',
      name: 'create-election',
      component: CreateElectionView,
      meta: {
        allowed: [Role.Admin]
      }
    },
    {
      path: '/elections/:id',
      name: 'election-details',
      component: ElectionDetails,
      meta: {
        allowed: [Role.User, Role.Admin]
      }
    },
    {
      path: '/user',
      name: 'user-area',
      component: UserAreaView,
      meta: {
        allowed: [Role.User, Role.Admin]
      }
    },
    {
      path: '/user/notifications',
      name: 'notifications',
      component: NotificationsView,
      meta: {
        allowed: [Role.User, Role.Admin]
      }
    },
    {
      // TODO: change path
      path: '/error',
      name: 'error',
      component: ErrorView,
    },
    {
      // TODO: change path
      path: '/no-permission',
      name: 'no-permission',
      component: NoPermissionView,
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFound,
    },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.meta.allowed) {
    if (!authStore.isLogged || !to.meta.allowed.includes(authStore.userRole!)) {
      return {
        path: '/login',
        query: { redirect: to.fullPath }
      }
    }
  }
})

export default router
