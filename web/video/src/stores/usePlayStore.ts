import { ref } from 'vue';
import { defineStore } from "pinia";

export const usePlayStore = defineStore("play-store", () => {
    const videoInfo = ref<Video.Info>()
    return{
        videoInfo
    }
})