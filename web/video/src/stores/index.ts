import { createPinia } from "pinia";
export * from "./useAuthStore";
export * from "./useVideoStore";
export * from "./usePlayStore";
export * from "./useUploadStore";

const pinia = createPinia();
pinia.use(({ store }) => {
  const initialState = JSON.parse(JSON.stringify(store.$state));
  store.$reset = () => {
    store.$state = JSON.parse(JSON.stringify(initialState));
  };
});
export default pinia;
