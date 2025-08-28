import './assets/main.css'
import 'amfe-flexible'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Vant UI
import Vant from 'vant'
import 'vant/lib/index.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Vant)

app.mount('#app')
