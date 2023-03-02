<template>
  <el-affix :offset="0">
    <div class="bg-blue-100">
      <el-row :gutter="0">
        <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 16, offset: 4 }">
          <div class="flex items-center transition-all" :class="[min?'py-1':'py-6']">
            <el-image
              class="cursor-pointer"
              style="width: 50px; height: 50px"
              :src="Logo"
              @click="home"
            />
            <div class="text-xl h-full ml-6 cursor-pointer" @click="home">
              幻彩希视频
            </div>
            <div class="flex-1"></div>
            <el-popover placement="bottom" :width="220" trigger="hover">
              <div v-if="isLogin" class="text-xl font-bold text-center mb-2">
                {{ userInfo.username }}
              </div>
              <div v-if="isLogin">
                <ListCard :data="data" @click="click1" />
              </div>
              <div v-else>
                <ListCard :data="data2" @click="click2" />
              </div>
              <template #reference>
                <el-avatar
                  class="cursor-pointer"
                  :size="50"
                  :src="
                    userInfo.avatar ||
                    'https://api.uomg.com/api/rand.avatar?sort=女&format=images'
                  "
                />
              </template>
            </el-popover>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-affix>

  <LoginCard
    v-model="showLoginTab"
    @changeCard="changeCard"
    @changePop="changePop"
    :type="loginType"
  />
</template>

<script setup lang="ts">
import Logo from "@/assets/Logo.svg";
import ListCard from "./ListCard.vue";
import LoginCard from "./LoginCard.vue";
import { useAuthStore } from "@/stores";
import { onMounted, ref, toRefs } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
const { clearAuth, logoutAuth, getUserInfoByToken } = useAuthStore();
const { userInfo, isLogin } = toRefs(useAuthStore());

withDefaults(defineProps<{
  min: boolean;
}>(), {
  min: false,
});
const showLoginTab = ref(false);
const router = useRouter();
const loginType = ref<string>("login");
const data = ref([
  {
    label: "个人中心",
    icon: "UserFilled",
  },
  {
    label: "视频投稿",
    icon: "VideoCamera",
  },
  {
    label: "退出登录",
    icon: "Close",
  },
]);
const data2 = ref([
  {
    label: "去登录",
    icon: "Promotion",
  },
]);
async function click1(item: { label: string; icon: string }) {
  switch (item.icon) {
    case "Close":
      await logoutAuth();
      await clearAuth();
      ElMessage.success("退出成功");
      break;
    case "UserFilled":
      router.push({ name: "my", query: { tab: "myVideo" } });
      break;
    case "VideoCamera":
      router.push({ name: "my", query: { tab: "uploadViewo" } });
  }
}
function click2(item: { label: string; icon: string }) {
  switch (item.icon) {
    case "Promotion":
      showLoginTab.value = true;
      break;
  }
}
function changeCard(tab: string) {
  loginType.value = tab;
}
function changePop(show: boolean) {
  showLoginTab.value = show;
}
function home() {
  router.replace({ name: "home" });
}
onMounted(async () => {
  await getUserInfoByToken();
});
</script>

<style scoped></style>
