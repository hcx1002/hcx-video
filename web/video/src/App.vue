<template>
  <el-scrollbar height="100vh" @scroll="scroll">
    <HeaderMenu :min="min" />
    <RouterView />
  </el-scrollbar>
</template>
<script setup lang="ts">
import { ref } from "vue";
import HeaderMenu from "./components/HeaderMenu.vue";
type data = { scrollLeft: number; scrollTop: number };
const min = ref(false);
function scroll(data: data) {
  if (data.scrollTop > 50) min.value = true;
  else min.value = false;
}
</script>

<style scoped>
:deep(.el-scrollbar__bar) {
  z-index: 9999999;
}
</style>
