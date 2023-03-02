import axios from "axios";
import type { AxiosError, AxiosInstance, AxiosRequestConfig } from "axios";
import { useAuthStore } from "@/stores";
import { ElMessage } from "element-plus";
/**
 * 封装axios请求类
 * @author Soybean<honghuangdc@gmail.com>
 */
export default class CustomAxiosInstance {
  instance: AxiosInstance;
  /**
   * @param axiosConfig - axios配置
   */
  constructor(axiosConfig: AxiosRequestConfig) {
    this.instance = axios.create(axiosConfig);
    this.setInterceptor();
  }

  /** 设置请求拦截器 */
  setInterceptor() {
    this.instance.interceptors.request.use(
      async (config) => {
        const handleConfig = { ...config };
        if (handleConfig.headers) {
          // 设置token
          const token = localStorage.getItem("token");
          if (token) handleConfig.headers.token = token;
        }
        return handleConfig;
      },
      (axiosError: AxiosError) => {
        return axiosError;
      }
    );
    this.instance.interceptors.response.use(
      async (response) => {
        const { status } = response;

        if (status === 200 || status < 300 || status === 304) {
          const backend = response.data;
          // 请求成功

          if (backend.code === 200) {
            return backend;
          } else if (backend.code === 401) {
            const { clearAuth } = useAuthStore();
            clearAuth();
            ElMessage.error("登陆失效，请重新登录");
            return Promise.reject(backend.message);
          }
          ElMessage.error(backend.message);
          return Promise.reject(backend.message);
        }
        return response;
      },
      (axiosError: AxiosError) => {
        return axiosError;
      }
    );
  }
}
