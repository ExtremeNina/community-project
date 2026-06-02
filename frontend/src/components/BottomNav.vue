<template>
  <nav class="fixed bottom-0 left-0 right-0 h-[56px] bg-white flex items-center justify-around z-50">
    <div
      v-for="tab in tabs"
      :key="tab.key"
      class="flex flex-col items-center gap-[2px] cursor-pointer relative"
      @click="onTabClick(tab)"
    >
      <div v-if="tab.key === 'publish'" class="absolute -top-[18px] w-[48px] h-[48px] bg-primary rounded-full flex items-center justify-center shadow-medium">
        <i class="fa-solid fa-plus text-white text-[22px]"></i>
      </div>
      <i v-else :class="[tab.icon, activeTab === tab.key ? 'text-primary text-[22px]' : 'text-text-aux text-[22px]']"></i>
      <span
        :class="[
          'text-tiny-m',
          activeTab === tab.key ? 'text-primary' : 'text-text-aux',
          tab.key === 'publish' ? 'mt-[18px]' : ''
        ]"
      >
        {{ tab.label }}
      </span>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { useRouter } from 'vue-router'

const store = useAppStore()
const router = useRouter()

const tabs = [
  { key: 'home', label: '首页', icon: 'fa-solid fa-house', path: '/home' },
  { key: 'discover', label: '发现', icon: 'fa-solid fa-compass', path: '/discover' },
  { key: 'publish', label: '发布', icon: 'fa-solid fa-plus', path: '/publish' },
  { key: 'messages', label: '消息', icon: 'fa-solid fa-comment-dots', path: '/messages' },
  { key: 'profile', label: '我的', icon: 'fa-solid fa-user', path: '/profile' }
]

const activeTab = computed(() => store.activeTab)

function onTabClick(tab) {
  store.setActiveTab(tab.key)
  router.push(tab.path)
}
</script>
