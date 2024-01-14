import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'bootstrap/dist/css/bootstrap.css'
import './assets/main.scss'
import * as bootstrap from 'bootstrap'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
