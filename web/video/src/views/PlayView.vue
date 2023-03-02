<template>
  <el-row :gutter="0">
    <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 16, offset: 4 }">
      <div class="flex mt-8 ">
        <div class="w-3/4" v-if="showVideo">
          <vue3videoPlay
            :src="videoInfo?.m3u8"
            :title="videoInfo?.title"
            :poster="videoInfo?.poster"
            :controlBtns="controlBtns"
            type="m3u8"
            :autoPlay="false"
            class="w-full"
            @ended="setVideoViewCount(videoInfo?.id || '')"
          ></vue3videoPlay>
          <div class="text-black font-bold text-xl mt-2">
            {{ videoInfo?.title }}
          </div>
          <div class="flex mt-3">
            <el-avatar
              class="cursor-pointer"
              :size="50"
              :src="
                videoInfo?.user?.avatar ||
                'https://api.uomg.com/api/rand.avatar?sort=女&format=images'
              "
            />
            <div class="flex-1 ml-6 truncate">
              <div class="w-full truncate">{{ videoInfo?.title }}</div>
              <div class="flex justify-between">
                <div class="mt-2 flex items-center">
                  <div class="text-gray-600 mr-1">
                    {{ videoInfo?.user?.username }}
                  </div>
                  <el-icon size="13"><CircleCheckFilled /></el-icon>
                </div>
                <div class="text-xs text-gray-600">
                  {{
                    videoInfo?.viewCount +
                    "次观看 · " +
                    monent(videoInfo?.createTime).fromNow()
                  }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty class="w-3/4" v-else :image-size="200" description="升级会员即可观看" />
        <div class="w-1/4">
          <div class="ml-3">他的视频</div>
          <el-scrollbar height="500px">
            <VideoCard2 :list="userList" />
          </el-scrollbar>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import monent from "moment";
import VideoCard2 from "@/components/VideoCard2.vue";
import { useVideoStore } from "@/stores";
import { onMounted, ref, toRefs, watch } from "vue";
import { useRoute } from "vue-router";
import vue3videoPlay from "vue3-video-play"; // 引入组件
import "vue3-video-play/dist/style.css"; // 引入css
const { getVideoDetail, getUserVideoList, setVideoViewCount } = useVideoStore();
const { videoInfo, userList } = toRefs(useVideoStore());
const route = useRoute();
const showVideo = ref(true);
const controlBtns = [
  "audioTrack",
  "speedRate",
  "volume",
  "setting",
  "pip",
  "pageFullScreen",
  "fullScreen",
];
async function init() {
  try {
    await getVideoDetail(route.query.id?.toString() || "");
    showVideo.value = true
  } catch (e) {
    //需要更高权限
    showVideo.value = false;
  }
  console.log(videoInfo.value);

  if (route.query.userId) await getUserVideoList(route.query.userId.toString());
}
watch(route, async (route) => {
  await init();
});
onMounted(async () => {
  await init();
});
</script>

<style scoped>
:deep(.d-player-wrap) {
  width: auto;
}
</style>
