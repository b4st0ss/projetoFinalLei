import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import store from './store'
import i18n from './i18n'
import ToastPlugin from 'vue-toast-notification'
import 'vue-toast-notification/dist/theme-default.css'

const app = createApp(App)

app.use(store)
app.use(router)
app.use(i18n)
app.use(ToastPlugin)

app.mount('#app')