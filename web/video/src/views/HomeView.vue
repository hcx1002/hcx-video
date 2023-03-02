<template>
  <el-row :gutter="0">
    <el-col :xs="{ span: 24, offset: 0 }" :sm="{ span: 16, offset: 4 }">
      <VideoCard :list="list" />
      <div class="mb-12">
        <el-pagination
          class="justify-end"
          background
          v-model:page-size="page.size"
          :page-count="page.pages"
          layout="prev, pager, next"
          :total="page.total"
          @current-change="change"
        />
      </div>
    </el-col>
  </el-row>

  <el-backtop :right="100" :bottom="100" />
</template>

<script setup lang="ts">
import VideoCard from "@/components/VideoCard.vue";
import { useVideoStore } from "@/stores";
import { onMounted, toRefs } from "vue";

const { getVideo } = useVideoStore();
const { list, page, params } = toRefs(useVideoStore());
async function change(value: number) {
  params.value.current = value;
  await getVideo();
}
onMounted(async () => {
  await getVideo();
});
</script>

<style scoped></style>
