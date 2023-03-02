import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getVideoPages,
  getVideoById,
  getUserVideo,
  setVideoView,
  getMyVideo,
  setVideoInfo,
  delVideo
} from "@/api";

export const useVideoStore = defineStore("video-store", () => {
  const params = ref<Video.Page>({
    current: 1,
    size: 9,
    sortField: "",
    sortOrder: "",
    title: "",
  });
  const page = ref<Common.Page<Video.Info>>({
    current: 0,
    total: 0,
    size: 0,
    pages: 1,
    records: [],
  });
  const list = ref<Video.Info[]>();
  const videoInfo = ref<Video.Info>();
  const userList = ref<Video.Info[]>();
  async function getVideo() {
    const { data } = await getVideoPages(params.value);
    if (data) list.value = data.records;
    page.value.current = data?.current || 0;
    page.value.total = data?.total || 0;
    page.value.pages = data?.pages || 0;
    page.value.size = data?.size || 0;
  }
  async function getVideoDetail(videoId: string) {
    const { data } = await getVideoById(videoId);
    if (data) videoInfo.value = data;
  }
  async function getUserVideoList(userId: string) {
    const { data } = await getUserVideo(userId);
    if (data) userList.value = data;
  }
  async function getMyVideoList() {
    const { data } = await getMyVideo();
    if (data) userList.value = data;
  }
  async function setVideoViewCount(videoId: string) {
    await setVideoView(videoId);
  }
  async function delVideoInfo(videoId: string) {
    await delVideo(videoId);
    await getMyVideoList()
  }
  async function updateVideoInfo(params: Video.Update) {
    const { data } = await setVideoInfo(params);
    if (data) videoInfo.value = data;
  }

  return {
    params,
    list,
    videoInfo,
    userList,
    page,
    getVideo,
    getVideoDetail,
    getUserVideoList,
    getMyVideoList,
    setVideoViewCount,
    updateVideoInfo,
    delVideoInfo
  };
});
