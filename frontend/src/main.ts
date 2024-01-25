import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/main.scss'
import * as bootstrap from 'bootstrap'
import 'vue-toast-notification/dist/theme-sugar.css';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import App from './App.vue'
import router from './router'
import axiosSetup from "@/commons/axios";
import ToastPlugin from 'vue-toast-notification'

createApp(App)
  .use(createPinia())
  .use(router)
  .use(ToastPlugin)
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')

axiosSetup(); // This needs to be called after app.use(pinia)!
