import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/main.scss'

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import App from './App.vue'
import router from './router'

const app = createApp(App)

/*
 * TODO: if we need api endpoints to be globally available, we can do it in two ways
 *  (see [https://stackoverflow.com/questions/63100658/add-global-variable-in-vue-js-3]):
 *  - app.config.globalProperties.$apiEndpoints = apiEndpoints
 *  - app.provide('apiEndpoints', apiEndpoints)
 */

app.use(createPinia())
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
