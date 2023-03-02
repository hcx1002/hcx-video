import { createApp } from "vue";
import pinia from "./stores";
import App from "./App.vue";
import router from "./router";
import "./styles/main.css";
import "element-plus/dist/index.css";
import 'animate.css';
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import moment from "moment";
import "moment/dist/locale/zh-cn";

const app = createApp(App);
for (const [key, component] of (<any>Object).entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

moment.locale("zh-cn");

app.use(pinia);
app.use(router);

app.mount("#app");
