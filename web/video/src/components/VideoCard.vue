<template>
  <div class="grid gap-4 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
    <div
      v-for="item in list"
      class="cursor-pointer my-4 transition-all rounded-xl hover:shadow-xl border border-gray-200  animate__animated animate__zoomIn"
      @click="play(item)"
      :key="item.id"
    >
    
    <div class="relative">
      <img
        class="bg-no-repeat rounded-t-lg shadow"
        style="width: 100%; height: 150px; object-fit: contain"
        :style="{ background: `url('${item.poster}')` }"
        :src="item.poster"
      />
      <div class="absolute right-0 bottom-0 text-white text-xs m-1 px-1 rounded-xl bg-[#00000080]">
        {{ item.time&&item.time.split(".")[0] }}
    </div>
    </div>
      
      <div class="flex mt-3 p-2">
        <div v-if="item.user">
          <el-avatar
            class="cursor-pointer"
            :size="40"
            :src="
              item.user?.avatar ||
              'https://api.uomg.com/api/rand.avatar?sort=女&format=images'
            "
          />
        </div>
        <div class="flex-1 ml-6 truncate">
          <div class="w-full truncate">{{ item.title }}</div>
          <div class="mt-2 flex items-center" v-if="item.user">
            <div class="text-xs text-gray-600 mr-1">
              {{ item.user?.username }}
            </div>
            <el-icon size="13"><CircleCheckFilled /></el-icon>
          </div>
          <div class="text-xs text-gray-600">
            {{
              item.viewCount + "次观看 · " + monent(item.createTime).fromNow()
            }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import monent from "moment";
import {usePlayStore} from '@/stores'
import { toRefs } from "vue";
import { useRouter } from "vue-router";
const router = useRouter()
const {videoInfo} = toRefs(usePlayStore())
defineProps<{
  list?: Video.Info[];
}>();
function play(params:Video.Info){
  videoInfo.value = params
  router.push({name:'play',query:{id:videoInfo.value.id,userId:videoInfo.value.userId}})
}
</script>

<style scoped></style>
