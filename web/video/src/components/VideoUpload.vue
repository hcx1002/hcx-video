<template>
  <el-dialog v-model="value" width="30%" align-center draggable>
    <div class="flex flex-col items-center my-12">
      <el-upload
        ref="uploadRef"
        :auto-upload="true"
        :headers="{ token }"
        accept="video/*"
        :disabled="disabled"
        :limit="1"
        :show-file-list="false"
        :http-request="httpRequest"
        @change="change"
        @success="success"
      >
        <template #trigger>
          <el-progress type="circle" :percentage="process">
            <div v-if="handleUpload">视频上传中</div>
            <div v-else>上传视频</div>
          </el-progress>
        </template>
      </el-upload>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { useAuthStore, useUploadStore } from "@/stores";
import type {
  UploadFile,
  UploadInstance,
  UploadRequestOptions,
} from "element-plus";
import { ElNotification } from "element-plus";
import { computed, ref, toRefs } from "vue";
const { token } = useAuthStore();
const { upload } = useUploadStore();
const { process } = toRefs(useUploadStore());
const disabled = ref(false);
const handleUpload = ref(false);
const uploadRef = ref<UploadInstance>();
const config = ref<Video.TranscodeConfig>({
  poster: "00:00:00.001",
  tsSeconds: 15,
  cutStart: "",
  cutEnd: "",
});
const props = defineProps<{
  show: boolean;
}>();
const emit = defineEmits(["update:show",'success']);
const value = computed({
  get() {
    return props.show;
  },
  set(value) {
    emit("update:show", value);
  },
});

function change(file: UploadFile) {
  console.log(file);
}
function success() {
  process.value = 100;
  handleUpload.value = false;
  ElNotification({
    title: "视频上传进度",
    message: "上传成功！",
    duration: 2000,
  });
  process.value = 0
  emit('success')
  uploadRef.value&&uploadRef.value.clearFiles()
}
async function httpRequest(params: UploadRequestOptions) {
  handleUpload.value = true;
  const formdata = new FormData();
  formdata.append("file", params.file);
  formdata.append(
    "config",
    new Blob([JSON.stringify(config.value)], {
      type: "application/json; charset=utf-8",
    })
  );
  await upload(formdata);
  
}
</script>

<style scoped></style>
