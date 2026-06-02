<template>
  <header class="md:hidden fixed top-0 left-0 right-0 h-[56px] bg-white flex items-center justify-between px-lg z-50">
    <div class="flex items-center gap-sm">
      <i class="fa-solid fa-book text-primary text-[22px]"></i>
      <span class="text-[20px] font-semibold text-primary tracking-wide">小蓝书</span>
    </div>
    <div class="flex-1 mx-lg">
      <div class="flex items-center bg-bg-page rounded-[18px] h-[36px] px-md">
        <i class="fa-solid fa-magnifying-glass text-text-aux text-[14px]"></i>
        <span class="ml-sm text-text-aux text-[14px]">搜索你感兴趣的内容</span>
      </div>
    </div>
    <div class="relative cursor-pointer" @click="$router.push('/messages')">
      <i class="fa-solid fa-bell text-text-primary text-[20px]"></i>
      <span class="absolute -top-1 -right-1 w-[16px] h-[16px] bg-danger rounded-full flex items-center justify-center">
        <span class="text-white text-tiny-m">3</span>
      </span>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
const $router = useRouter()
</script>
