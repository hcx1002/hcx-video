<template>
  <el-dialog v-model="props.value" align-center draggable>
    <div class="flex flex-col justify-center items-center">
      <el-icon size="50" color="#234ac8"><VideoCameraFilled /></el-icon>
      <div class="text-2xl font-bold text-black">
        {{ type === "regis" ? "注册" : "登录" }}
      </div>
      <div class="text-sm text-gray-500 mt-3">分享精彩瞬间，记录美好生活</div>
      <div class="mt-5">
        <el-form :model="formLabelAlign" :rules="rules" ref="ruleFormRef">
          <el-form-item label="" prop="username">
            <el-input v-model="formLabelAlign.username" placeholder="用户名" />
          </el-form-item>
          <el-form-item label="" prop="password">
            <el-input
              v-model="formLabelAlign.password"
              placeholder="密码"
              @keyup.enter="submitForm(ruleFormRef)"
            />
          </el-form-item>
          <el-button
            class="w-full"
            round
            @click="submitForm(ruleFormRef)"
            type="primary"
            :loading="loginLoading"
            >{{ type === "regis" ? "注册" : "登录" }}</el-button
          >
        </el-form>
        <div
          v-if="type === 'login'"
          class="text-xs text-gray-500 text-center mt-4"
        >
          还未拥有账号？<span
            class="text-red-600 cursor-pointer"
            @click="changeTab('regis')"
            >注册</span
          >
        </div>
        <div v-else class="text-xs text-gray-500 text-center mt-4">
          已经拥有账号？<span
            class="text-red-600 cursor-pointer"
            @click="changeTab('login')"
            >登录</span
          >
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { useAuthStore } from "@/stores";
import type { FormInstance, FormRules } from "element-plus";
import { reactive, ref, toRefs } from "vue";
const { userLogin, userRegis } = useAuthStore();
const { loginLoading } = toRefs(useAuthStore());
interface Props {
  value?: boolean;
  type: string;
}
interface Emit {
  (e: "changeCard", value: string): void;
  (e: "changePop", value: boolean): void;
}
const props = withDefaults(defineProps<Props>(), {
  value: false,
});
const emit = defineEmits<Emit>();
const ruleFormRef = ref<FormInstance>();
const formLabelAlign = reactive({
  username: "",
  password: "",
});
const rules = reactive<FormRules>({
  username: [{ required: true, message: "请输入用户名", trigger: "change" }],
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: "change",
    },
  ],
});
function changeTab(tab: string) {
  emit("changeCard", tab);
  ruleFormRef.value?.resetFields();
}
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      if (props.type === "login")
        await userLogin(formLabelAlign.username, formLabelAlign.password);
      else if (props.type === "regis")
        await userRegis(formLabelAlign.username, formLabelAlign.password);
      emit("changePop", false);
      formEl.resetFields();
    }
  });
};
</script>

<style scoped>
:deep(.el-input__wrapper) {
  box-shadow: none;
  border-bottom: 2px solid rgb(153, 153, 153);
  border-radius: 0;
}
:deep(.el-input__wrapper:hover) {
  box-shadow: none;
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow: none;
  border-bottom: 2px solid #234ac8;
  border-radius: 0;
}
:deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: none;
  border-bottom: 2px solid red;
}

</style>
