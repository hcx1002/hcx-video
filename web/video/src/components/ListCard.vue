<template>
  <div>
    <div v-for="item in data">
      <div
        class="flex cursor-pointer p-3 items-center m-1 transition-all rounded-md hover:bg-slate-100"
        @click="emit('click',item)"
      >
        <el-icon size="20"> 
          <component :is="item.icon" />
        </el-icon>
        <div class="ml-3">{{ item.label }}</div>
        <div class="flex-1"></div>
        <div>
          <el-icon><ArrowRightBold /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
interface datas {
  label: string;
  icon: string;
}
const emit = defineEmits(['click'])
defineProps<{
  data:datas[]
}>();



</script>

<style scoped></style>
