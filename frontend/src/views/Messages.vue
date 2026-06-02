<template>
  <div class="flex flex-col h-full">
    <div class="bg-white flex-shrink-0">
      <div class="flex px-lg py-md gap-lg">
        <span
          v-for="tab in msgTabs"
          :key="tab"
          class="text-[14px] md:text-[15px] cursor-pointer pb-[6px] relative transition-colors hover:text-primary"
          :class="activeMsgTab === tab ? 'text-primary' : 'text-text-aux'"
          @click="activeMsgTab = tab"
        >
          {{ tab }}
        </span>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto pc-scrollbar">
      <div
        v-for="msg in filteredMessages"
        :key="msg.id"
        class="flex items-center px-lg md:px-xl cursor-pointer hover:bg-bg-page transition-colors"
        :style="{ height: '72px' }"
      >
        <div class="relative flex-shrink-0">
          <img :src="msg.avatar" class="w-[48px] md:w-[52px] h-[48px] md:h-[52px] rounded-full object-cover" />
          <span v-if="msg.unread" class="absolute top-0 right-0 w-[10px] h-[10px] bg-primary rounded-full border-2 border-white"></span>
        </div>
        <div class="flex-1 ml-md md:ml-lg overflow-hidden">
          <p :class="['text-body', msg.unread ? 'text-text-primary' : 'text-text-aux', 'truncate']">
            {{ msg.content }}
          </p>
          <p class="text-aux text-text-aux mt-[2px]">{{ msg.title }}</p>
        </div>
        <div class="flex-shrink-0 flex items-center gap-sm md:gap-md ml-md">
          <span class="text-tiny text-text-aux">{{ msg.time }}</span>
          <i class="fa-solid fa-chevron-right text-tiny text-text-aux"></i>
        </div>
      </div>

      <div v-if="filteredMessages.length === 0" class="flex flex-col items-center justify-center py-[80px]">
        <i class="fa-solid fa-inbox text-text-aux text-[48px]"></i>
        <p class="text-body text-text-aux mt-lg">暂无消息</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'

const store = useAppStore()
const activeMsgTab = ref('全部')

const msgTabs = ['全部', '赞和收藏', '评论', '@我', '关注']

const filteredMessages = computed(() => {
  if (activeMsgTab.value === '全部') return store.messages
  return store.messages.filter(m => m.title === activeMsgTab.value)
})

onMounted(() => {
  store.setActiveTab('messages')
  store.fetchMessages()
})
</script>
