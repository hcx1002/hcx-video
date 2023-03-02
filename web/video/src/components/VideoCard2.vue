<template>
  <div
    v-for="item in list"
    class="flex cursor-pointer transition-all m-3 rounded-xl hover:shadow-lg border border-gray-200"
    @click="play(item)"
    :key="item.id"
  >
    <div class="relative">
      <div style="width: 100px; height: 100px;">
        <img
        class="shadow rounded-l-lg w-full h-full"
        style="object-fit: contain;"
        :style="{ background: `url('${item.poster}')` }"
        :src="item.poster"
      />
      </div>
     
      <div
        class="absolute right-0 bottom-0 text-white text-xs m-1 px-1 rounded-xl bg-[#00000080]"
      >
        {{ item.time && item.time.split(".")[0] }}
      </div>
    </div>

    <div class="flex flex-col justify-between truncate ml-3 py-3">
      <div class="w-full truncate">{{ item.title }}</div>
      <div class="truncate justify-between">
        <div class="text-xs text-gray-600">
          {{ item.viewCount + "次观看 · " + monent(item.createTime).fromNow() }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import monent from "moment";
import { useRouter } from "vue-router";
const router = useRouter();
defineProps<{
  list?: Video.Info[];
}>();
function play(params: Video.Info) {
  router.push({ name: "play", query: { id: params.id } });
}
</script>

<style scoped></style>
