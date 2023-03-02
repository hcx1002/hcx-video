import { defineStore } from "pinia";
import { login, regis,logout, getUserInfo } from "@/api";
import { ElMessage } from "element-plus";
interface AuthState {
  /** 用户信息 */
  userInfo: User.Info;
  /** 用户token */
  token: string;
  /** 登录的加载状态 */
  loginLoading: boolean;
}
export const useAuthStore = defineStore("auth-store", {
  state: (): AuthState => ({
    userInfo: JSON.parse(localStorage.getItem("userInfo") || "{}"),
    token: localStorage.getItem("token") || "",
    loginLoading: false,
  }),
  getters: {
    isLogin(state) {
      return Boolean(state.token);
    },
  },
  actions: {
    async userLogin(username: string, password: string) {
      this.loginLoading = true;
      try {
        const { data } = await login(username, password);
        localStorage.setItem("token", data || "");
        this.token = data || "";
        await this.getUserInfoByToken()
        ElMessage.success("登陆成功");
      } catch (error) {}
      this.loginLoading = false;
    },
    async userRegis(username: string, password: string) {
      this.loginLoading = true;
      try {
        const { data } = await regis(username, password);
        localStorage.setItem("token", data || "");
        this.token = data || "";
        await this.getUserInfoByToken()
        ElMessage.success("登陆成功");
      } catch (error) {}
      this.loginLoading = false;
    },
    async getUserInfoByToken() {
      if(this.token){
        const { data } = await getUserInfo();
        if (data) {
          localStorage.setItem("userInfo", JSON.stringify(data));
          this.userInfo = data;
        }
      }
     
    },
    async logoutAuth(){
      await logout();
    },
    clearAuth() {
      localStorage.removeItem("userInfo");
      localStorage.removeItem("token");
      this.$reset();
    },
  },
});
