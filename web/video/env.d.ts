/// <reference types="vite/client" />

// 推荐使用
declare module "*.vue" {
  import { ComponentOptions } from "vue";
  const componentOptions: ComponentOptions;
  export default componentOptions;
}
declare module "vue3-video-play";
