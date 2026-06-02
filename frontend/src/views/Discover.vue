<template>
  <div class="flex-1 overflow-y-auto pc-scrollbar">
    <div class="max-w-[800px] mx-auto p-lg md:px-2xl">
      <h2 class="text-h2 text-text-primary mb-lg">发现精彩内容</h2>

      <div class="flex items-center bg-bg-page border border-border rounded-md h-[48px] px-lg mb-xl">
        <i class="fa-solid fa-magnifying-glass text-text-aux text-[16px]"></i>
        <input
          v-model="searchText"
          type="text"
          placeholder="搜索你感兴趣的内容..."
          class="flex-1 ml-md text-[15px] text-text-primary bg-transparent"
        />
      </div>

      <div class="mb-xl">
        <h3 class="text-h3 text-text-primary mb-lg">热门话题</h3>
        <div class="flex flex-wrap gap-md">
          <span v-for="topic in hotTopics" :key="topic" class="inline-flex items-center h-[36px] px-lg rounded-md bg-bg-page border border-border text-text-aux text-body cursor-pointer hover:bg-primary-bg hover:text-primary transition-colors">
            {{ topic }}
          </span>
        </div>
      </div>

      <div>
        <h3 class="text-h3 text-text-primary mb-lg">为你推荐</h3>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-lg">
          <div v-for="note in store.notes.slice(0, 8)" :key="note.id" class="bg-white border border-border rounded-md overflow-hidden cursor-pointer group" @click="$router.push(`/detail/${note.id}`)">
            <img :src="note.cover" :alt="note.title" class="w-full aspect-[3/4] object-cover" />
            <div class="p-md">
              <p class="text-body line-clamp-2 text-text-primary group-hover:text-primary transition-colors">{{ note.title }}</p>
              <div class="flex items-center justify-between mt-sm">
                <span class="text-aux text-text-aux">{{ note.nickname }}</span>
                <span class="text-aux text-text-aux"><i class="fa-solid fa-heart mr-xs"></i>{{ formatLikes(note.likes) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { useRouter } from 'vue-router'

const store = useAppStore()
const $router = useRouter()
const searchText = ref('')

const hotTopics = ['#蓝色系穿搭', '#夏日旅行', '#健身打卡', '#美食探店', '#读书分享', '#数码新品', '#今日穿搭', '#宠物日常']

function formatLikes(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

onMounted(() => {
  store.setActiveTab('discover')
  store.fetchNotes()
})
</script>
