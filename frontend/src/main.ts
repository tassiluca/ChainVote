import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/main.scss'
import * as bootstrap from 'bootstrap'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import App from './App.vue'
import router from './router'

/** The endpoint of the API server. */
export const URL_API_SERVER = 'http://localhost:8080'; // process.env.API_SERVER_URL ||
/** The endpoint of the authentication server. */
export const URL_AUTH_SERVER = "http://localhost:8180"; // process.env.AUTH_SERVER_URL ||

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
