<template>
  <header
    class="h-[56px] md:h-[64px] bg-white shadow-light flex items-center px-lg md:px-2xl gap-lg md:gap-[48px] flex-shrink-0 z-40"
  >
    <div class="flex items-center">
      <div class="flex items-center gap-sm cursor-pointer" @click="$router.push('/home')">
        <i class="fa-solid fa-book text-primary text-[22px] md:text-[26px]"></i>
        <span class="text-[20px] md:text-[26px] font-semibold text-primary tracking-wide">小蓝书</span>
      </div>
    </div>

    <div class="hidden md:flex items-center flex-1 max-w-[400px]">
      <div
        class="flex-1 flex items-center bg-bg-page rounded-[20px] h-[40px] px-lg border border-border transition-all duration-200"
        :class="searchFocused ? 'border-primary bg-white' : ''"
        :style="searchFocused ? 'box-shadow: inset 0 0 0 1px #1677FF' : ''"
      >
        <i class="fa-solid fa-magnifying-glass text-text-aux text-[15px]"></i>
        <input
          type="text"
          placeholder="搜索你感兴趣的内容"
          class="flex-1 ml-sm text-[15px] text-text-primary bg-transparent"
          v-model="searchText"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
        />
      </div>
    </div>

    <div class="flex md:hidden flex-1 justify-end">
      <div class="flex-1 flex items-center bg-bg-page rounded-[18px] h-[36px] px-md" @click="showMobileSearch = true">
        <i class="fa-solid fa-magnifying-glass text-text-aux text-[14px]"></i>
        <span class="ml-sm text-text-aux text-[14px]">搜索你感兴趣的内容</span>
      </div>
    </div>

    <div class="flex items-center gap-xl">
      <router-link to="/messages" class="block md:hidden relative cursor-pointer">
        <i class="fa-regular fa-bell text-text-primary text-[20px]"></i>
        <span class="absolute -top-1 -right-1 w-[16px] h-[16px] bg-danger rounded-full flex items-center justify-center">
          <span class="text-white text-tiny-m">3</span>
        </span>
      </router-link>
    </div>
  </header>

  <Teleport to="body">
    <div v-if="showMobileSearch" class="fixed inset-0 bg-white z-[100] flex flex-col md:hidden">
      <div class="flex items-center h-[56px] px-lg gap-md border-b border-border">
        <div class="flex-1 flex items-center bg-bg-page rounded-[18px] h-[36px] px-md">
          <i class="fa-solid fa-magnifying-glass text-text-aux text-[14px]"></i>
          <input
            ref="mobileSearchInput"
            type="text"
            placeholder="搜索你感兴趣的内容"
            class="flex-1 ml-sm text-[14px] text-text-primary bg-transparent"
            v-model="searchText"
          />
        </div>
        <span class="text-[14px] text-primary cursor-pointer" @click="showMobileSearch = false">取消</span>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const $router = useRouter()

const searchText = ref('')
const searchFocused = ref(false)
const showMobileSearch = ref(false)
const mobileSearchInput = ref(null)

function onSearchFocus() {
  searchFocused.value = true
}

function onSearchBlur() {
  searchFocused.value = false
}
</script>
