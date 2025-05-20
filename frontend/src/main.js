import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

// Configure axios
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.timeout = 5000
axios.defaults.headers.common['Content-Type'] = 'application/json'
axios.defaults.headers.common['Accept'] = 'application/json'

// Add response interceptor for better error handling
axios.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error);
    if (error.code === 'ERR_NETWORK') {
      console.error('Network error - Please check if the backend is running');
    } else if (error.response) {
      console.error('Server response data:', error.response.data);
      console.error('Server response status:', error.response.status);
    }
    return Promise.reject(error);
  }
)

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light'
  }
})

const app = createApp(App)
app.use(vuetify)
app.mount('#app') 
