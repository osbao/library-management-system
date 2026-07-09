import App from './App.vue';
import { createSSRApp } from 'vue';
import { createStore } from 'vuex';
import storeConfig from './store';
import './utils/request.js';

export function createApp() {
  const app = createSSRApp(App);
  app.use(createStore(storeConfig));
  app.config.globalProperties.$api = 'http://localhost:9091/api';
  return { app };
}