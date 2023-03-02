<template>
  <el-dialog v-model="value" width="40%" align-center draggable>
    <img
      v-if="videoInfo?.status !== 2"
      :src="videoInfo?.poster"
      width="200"
      height="150"
      style="width: 200px; height: 150px; object-fit: cover; margin: auto"
    />
    <el-skeleton style="width: 200px" class="m-auto" animated v-else>
      <template #template>
        <el-skeleton-item variant="image" style="width: 200px; height: 200px" />
      </template>
    </el-skeleton>
    <div class="mt-7">
      <el-form
        ref="ruleFormRef"
        label-width="100px"
        :model="videoInfoAdd"
        style="max-width: 400px"
        :rules="rules"
        class="m-auto"
      >
        <el-form-item label="视频名称" prop="title">
          <el-input v-model="videoInfoAdd.title" />
        </el-form-item>
        <el-form-item label="视频等级" prop="grade">
          <el-input-number
            v-model.number="videoInfoAdd.grade"
            :min="1"
            :max="5"
            :controls="false"
          />
        </el-form-item>
        <el-form-item label="视频状态" prop="status">
          <el-select
            v-model="videoInfoAdd.status"
            :disabled="videoInfoAdd.status === 2"
            placeholder="请选择视频状态"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="发布" :value="1" />
            <el-option
              v-if="videoInfoAdd.status === 2"
              label="视频处理中"
              :value="2"
            />
          </el-select>
        </el-form-item>
        <div class="flex justify-center items-center">
          <el-button round class="w-16" @click="close">返回</el-button>

          <el-button
            round
            type="primary"
            class="w-16"
            @click="submitForm(ruleFormRef)"
            >提交</el-button
          >
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { useVideoStore } from "@/stores";
import type { FormInstance, FormRules } from "element-plus";
import { ref, reactive, watch, onMounted, computed } from "vue";
const { updateVideoInfo } = useVideoStore();
const props = defineProps<{
  videoInfo?: Video.Info;
  show: boolean;
}>();
const emit = defineEmits(["update:show", "success", "close"]);
const value = computed({
  get() {
    return props.show;
  },
  set(value) {
    emit("update:show", value);
  },
});
watch(props, (value) => {
  initAdd(value.videoInfo);
});
const videoInfoAdd = ref<Video.Update>({
  id: "",
  title: "",
  grade: 1,
  status: 1,
});

const ruleFormRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  title: [{ required: true, message: "请输入视频名称", trigger: "change" }],
  grade: [
    {
      required: true,
      message: "请输入等级",
      trigger: "change",
    },
  ],
  status: [
    {
      required: true,
      message: "请选择发布状态",
      trigger: "change",
    },
  ],
});
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      await updateVideoInfo(videoInfoAdd.value);
      emit("update:show", false);
      emit("success", videoInfoAdd);
    }
  });
};
function initAdd(videoInfo?: Video.Info) {
  videoInfoAdd.value.id = videoInfo?.id || "";
  videoInfoAdd.value.title = videoInfo?.title || "";
  videoInfoAdd.value.grade = videoInfo?.grade || 0;
  videoInfoAdd.value.status = videoInfo?.status || 0;
}
function close() {
  emit("update:show", false);
  emit("close");
}
onMounted(() => {
  initAdd();
});
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
