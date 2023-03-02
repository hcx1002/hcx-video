<template>
  <el-row :gutter="0">
    <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 16, offset: 4 }">
      <div class="w-full flex items-center" v-if="isLogin">
        <div class="mt-4">
          <el-avatar
            class="cursor-pointer"
            :size="100"
            :src="
              userInfo?.avatar ||
              'https://api.uomg.com/api/rand.avatar?sort=女&format=images'
            "
          />
        </div>
        <div class="ml-10 flex-1">
          <div class="flex items-center">
            <span class="font-bold text-2xl mr-2">{{ userInfo.username }}</span>
            <el-tag class="ml-2" type="success" size="small" round
              >VIP{{ userInfo.vip }}</el-tag
            >
          </div>
          <div class="text-xs text-gray-500 my-1">
            用户ID：{{ userInfo.id }}
          </div>
          <div class="text-xs text-gray-500 my-1">
            用户权限：<el-tag
              size="small"
              class="ml-2"
              type="danger"
              round
              v-if="userInfo.permission === 'upload'"
              effect="plain"
              >上传</el-tag
            >
          </div>
        </div>
        <div>
          <el-button
            size="large"
            type="success"
            round
            icon="UploadFilled"
            @click="showUpload = true"
            >上传</el-button
          >
        </div>
      </div>
      <div v-if="isLogin">
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane label="我的视频" name="myVideo">
            <el-table :data="userList" style="width: 100%" :loading="loading">
              <el-table-column
                align="center"
                prop="poster"
                label="视频截图"
                width="180"
              >
                <template #default="{ row, column, $index }">
                  <!-- <img :src="row.poster" width="200" height="150" class="img" /> -->
                  <el-image
                    style="width: 200px; height: 150px"
                    :src="row.poster"
                    fit="cover"
                    preview-teleported
                    hide-on-click-modal
                    :z-index="9999999999"
                    :preview-src-list="[row.poster]"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="title"
                label="视频名称"
                width="180"
              />
              <el-table-column prop="size" label="视频大小">
                <template #default="{ row }">
                  <div>{{ (row.size / 1024000).toFixed(2) + "MB" }}</div>
                </template>
              </el-table-column>
              <el-table-column align="center" prop="time" label="视频时长" />
              <el-table-column
                align="center"
                prop="viewCount"
                label="视频播放量"
              />
              <el-table-column align="center" prop="grade" label="视频等级" />
              <el-table-column align="center" prop="status" label="视频状态">
                <template #default="{ row }">
                  <el-tag class="ml-2" type="warning" v-if="row.status == 0"
                    >草稿</el-tag
                  >
                  <el-tag class="ml-2" type="success" v-if="row.status == 1"
                    >已发布</el-tag
                  >
                  <el-tag class="ml-2" type="danger" v-if="row.status == 2"
                    >{{ Math.round((transcodingVideo?.process||1)*100)+'%' }}</el-tag
                  >
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                fixed="right"
                label="操作"
                width="120"
              >
                <template #default="{ row }">
                  <el-button link type="primary" @click="showInfo(row)"
                    >编辑</el-button
                  >
                  <el-popover
                    :ref="row.id"
                    trigger="click"
                    placement="top"
                    :width="160"
                  >
                    <div class="p-2">
                      <div class="text-center my-3">是否删除?</div>
                      <el-button size="small" @click="ctx.$refs[row.id].hide()"
                        >取消</el-button
                      >
                      <el-button
                        size="small"
                        type="primary"
                        @click="delVideo(row.id)"
                        >确认</el-button
                      >
                    </div>
                    <template #reference>
                      <el-button link type="danger">删除</el-button>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
        <VideoInfo
          :video-info="info"
          v-model:show="showLog"
          @success="success"
          @close="close"
        ></VideoInfo>
        <VideoUpload v-model:show="showUpload" @success="successUpload" />
      </div>
      <div v-if="!isLogin" class="font-bold text-2xl text-center mt-20">
        请先登录！
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import VideoInfo from "@/components/VideoInfo.vue";
import VideoUpload from "@/components/VideoUpload.vue";
import { useAuthStore, useVideoStore, useUploadStore } from "@/stores";
import { ElMessage } from "element-plus";
import { getCurrentInstance, onMounted, ref, toRefs, watch } from "vue";
import { useRoute } from "vue-router";
const { getUserInfoByToken } = useAuthStore();
const { getMyVideoList, delVideoInfo } = useVideoStore();
const route = useRoute();
const { userInfo, isLogin } = toRefs(useAuthStore());
const { videoInfo: uploadVideo, transcodingVideo } = toRefs(useUploadStore());
const { userList } = toRefs(useVideoStore());
const activeName = ref("myVideo");
const showLog = ref(false);
const showUpload = ref(false);
const loading = ref(false);
const info = ref<Video.Info>();
if (route.query.tab === "uploadViewo") showUpload.value = true;
watch(isLogin, (value) => {
  if (value) init();
});
watch(route, (value) => {
  if (value.query.tab === "uploadViewo") showUpload.value = true;
});
watch(transcodingVideo, async (value) => {
  console.log(value?.process);
  if (value?.process === 1) await init();
});
const { ctx } = getCurrentInstance() as any;

async function init() {
  loading.value = true;
  await getUserInfoByToken();
  await getMyVideoList();
  loading.value = false;
}
function showInfo(videoInfo: Video.Info) {
  info.value = videoInfo;
  showLog.value = true;
}
async function successUpload() {
  if (uploadVideo.value) showInfo(uploadVideo.value);
  await init();
}
async function success(params: Video.Update) {
  showUpload.value = false;
  await init();
}
async function close() {
  showUpload.value = false;
}
async function delVideo(videoId: string) {
  ctx.$refs[videoId].hide();
  await delVideoInfo(videoId);
  ElMessage.success("删除成功");
}
onMounted(async () => {
  await init();
});
</script>

<style scoped>
.img {
  width: 200px;
  height: 150px;
  margin: auto;
}
</style>
