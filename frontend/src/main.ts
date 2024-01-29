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

const app = createApp(App)
app.use(createPinia())
// This needs to be called after app.use(pinia) but before mounting the root container
// otherwise may happen that the axios configuration is loaded after some components, on mounts,
// make requests, leading to errors in authentication requests or response not correctly handled.
axiosSetup()
app.use(router)
app.use(ToastPlugin)
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
