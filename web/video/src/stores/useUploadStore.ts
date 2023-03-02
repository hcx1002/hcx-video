import { defineStore } from "pinia";
import { ref } from "vue";
import { uploadVideo, getTranscodingVideo } from "@/api";

export const useUploadStore = defineStore("upload-store", () => {
  //上传进度
  const process = ref(0);
  const transcodingVideo = ref<Video.Process>();
  const videoInfo = ref<Video.Info>();
  const timer = ref<any>();
  async function upload(formdata: FormData) {
    const { data } = await uploadVideo(formdata);
    if (data) videoInfo.value = data;
    //上传成功 然后获取视频转码进度
    if (data?.m3u8)
      timer.value = setInterval(async () => {
        await getTranscoding(data?.m3u8, data?.id);
      }, 1000);
  }
  async function getTranscoding(id: string, videoId: string) {
    try {
      const { data } = await getTranscodingVideo(id, videoId);
      if (data){
        transcodingVideo.value = data;
        if(data.process===1.0){
          clearInterval(timer.value);
        } 
      } 
    } catch (error) {
      clearInterval(timer.value)
    }
  }
  return {
    process,
    videoInfo,
    transcodingVideo,
    upload,
    getTranscoding,
  };
});
