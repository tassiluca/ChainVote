import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/main.scss'
import * as bootstrap from 'bootstrap'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import App from './App.vue'
import router from './router'
import axiosSetup from "@/commons/axios";

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')

axiosSetup(); // This needs to be called after app.use(pinia)!
