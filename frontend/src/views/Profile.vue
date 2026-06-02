<template>
  <div class="flex-1 overflow-y-auto pc-scrollbar">
    <div class="md:hidden">
      <div class="relative h-[200px] bg-gradient-to-b from-primary to-primary-light flex items-end">
        <div class="px-lg pb-lg w-full flex items-center justify-between">
          <div class="flex items-center gap-lg">
            <div class="w-[80px] h-[80px] rounded-full border-[3px] border-white overflow-hidden flex-shrink-0">
              <img :src="user.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&h=80&fit=crop'" class="w-full h-full object-cover" />
            </div>
            <div class="text-white">
              <p class="text-h2-m">{{ user.nickname }}</p>
              <p class="text-aux-m mt-[4px] opacity-80">{{ user.signature }}</p>
            </div>
          </div>
          <button class="h-[32px] px-lg rounded-md border border-white text-white text-aux-m flex-shrink-0 hover:bg-white/10 transition-colors">
            编辑资料
          </button>
        </div>
      </div>

      <div class="flex justify-around py-lg bg-white">
        <div class="flex flex-col items-center gap-[4px]">
          <span class="text-h2-m text-text-primary">{{ user.following }}</span>
          <span class="text-aux-m text-text-aux">关注</span>
        </div>
        <div class="flex flex-col items-center gap-[4px]">
          <span class="text-h2-m text-text-primary">{{ user.followers }}</span>
          <span class="text-aux-m text-text-aux">粉丝</span>
        </div>
        <div class="flex flex-col items-center gap-[4px]">
          <span class="text-h2-m text-text-primary">{{ formatLikes(user.likes) }}</span>
          <span class="text-aux-m text-text-aux">获赞</span>
        </div>
      </div>

      <div class="grid grid-cols-4 gap-0 bg-white mb-md">
        <div v-for="item in quickActions" :key="item.label" class="flex flex-col items-center py-lg cursor-pointer hover:bg-bg-page transition-colors">
          <i :class="[item.icon, 'text-[20px]', 'text-text-aux']"></i>
          <span class="text-aux-m text-text-primary mt-sm">{{ item.label }}</span>
        </div>
      </div>

      <div class="sticky top-0 bg-white z-30">
        <div class="flex px-lg py-sm gap-lg">
          <span
            v-for="tab in profileTabs"
            :key="tab"
            class="text-[14px] cursor-pointer pb-[6px] relative"
            :class="activeProfileTab === tab ? 'text-primary' : 'text-text-aux'"
            @click="activeProfileTab = tab"
          >
            {{ tab }}
          </span>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-[4px] p-[4px] bg-border">
        <div v-for="i in 9" :key="i" class="aspect-square bg-bg-page overflow-hidden cursor-pointer">
          <img
            :src="`https://images.unsplash.com/photo-${1500000000 + i * 100000}?w=200&h=200&fit=crop`"
            class="w-full h-full object-cover"
          />
        </div>
      </div>

      <div class="h-[80px]"></div>
    </div>

    <div class="hidden md:flex h-full">
      <div class="w-[320px] flex-shrink-0 overflow-y-auto pc-scrollbar bg-white">
        <div class="pt-xl px-xl text-center">
          <img
            :src="user.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=120&h=120&fit=crop'"
            class="w-[120px] h-[120px] rounded-full object-cover mx-auto border-[4px] border-primary-bg"
          />
          <h2 class="text-h2 text-text-primary mt-lg">{{ user.nickname }}</h2>
          <p class="text-aux text-text-aux mt-sm">{{ user.signature }}</p>

          <div class="flex justify-around mt-xl py-lg">
            <div class="flex flex-col items-center">
              <span class="text-h2 text-text-primary">{{ user.following }}</span>
              <span class="text-tiny text-text-aux mt-xs">关注</span>
            </div>
            <div class="flex flex-col items-center">
              <span class="text-h2 text-text-primary">{{ user.followers }}</span>
              <span class="text-tiny text-text-aux mt-xs">粉丝</span>
            </div>
            <div class="flex flex-col items-center">
              <span class="text-h2 text-text-primary">{{ formatLikes(user.likes) }}</span>
              <span class="text-tiny text-text-aux mt-xs">获赞</span>
            </div>
          </div>

          <div class="flex gap-md mt-lg">
            <button class="flex-1 h-[32px] rounded-md bg-white border border-border text-text-primary text-aux hover:bg-bg-nav transition-colors">
              编辑资料
            </button>
            <button class="flex-1 h-[36px] rounded-md bg-primary text-white text-aux hover:bg-primary-dark transition-colors px-lg">
              发消息
            </button>
          </div>

          <div class="mt-xl pt-lg text-left">
            <div class="flex flex-wrap gap-sm">
              <span class="inline-flex items-center h-[28px] px-md rounded-md bg-primary-bg text-primary text-tiny">可爱系</span>
              <span class="inline-flex items-center h-[28px] px-md rounded-md bg-primary-bg text-primary text-tiny">穿搭博主</span>
              <span class="inline-flex items-center h-[28px] px-md rounded-md bg-primary-bg text-primary text-tiny">蓝色控</span>
            </div>
          </div>

          <div class="mt-lg pt-lg">
            <div class="grid grid-cols-2 gap-md">
              <div v-for="item in quickActions" :key="item.label" class="flex flex-col items-center py-md cursor-pointer rounded-lg hover:bg-bg-page transition-colors">
                <i :class="[item.icon, 'text-[20px] text-text-aux mb-sm']"></i>
                <span class="text-tiny text-text-primary">{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto pc-scrollbar min-w-0">
        <div class="sticky top-0 bg-white z-30">
          <div class="flex items-center justify-between px-xl py-md">
            <div class="flex gap-lg">
              <span
                v-for="tab in profileTabs"
                :key="tab"
                class="text-[15px] cursor-pointer pb-[6px] relative"
                :class="activeProfileTab === tab ? 'text-primary' : 'text-text-aux'"
                @click="activeProfileTab = tab"
              >
                {{ tab }}
              </span>
            </div>

            <div class="flex items-center gap-md">
              <span class="text-aux text-text-aux cursor-pointer hover:text-primary transition-colors">
                <i class="fa-solid fa-arrow-down-wide-short mr-xs"></i>最新
              </span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-4 gap-[4px] p-[4px]">
          <div v-for="i in 16" :key="i" class="aspect-square bg-bg-page overflow-hidden cursor-pointer group relative">
            <img
              :src="`https://images.unsplash.com/photo-${1500000000 + i * 100000}?w=300&h=300&fit=crop`"
              class="w-full h-full object-cover"
            />
            <div class="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100">
              <div class="flex items-center gap-xl text-white">
                <span class="flex items-center gap-sm text-aux"><i class="fa-solid fa-heart"></i>{{ 100 + i * 23 }}</span>
                <span class="flex items-center gap-sm text-aux"><i class="fa-solid fa-comment"></i>{{ 10 + i * 3 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'

const store = useAppStore()
const user = computed(() => store.user)
const activeProfileTab = ref('笔记')

const profileTabs = ['笔记', '收藏', '赞过']

const quickActions = [
  { icon: 'fa-solid fa-clock-rotate-left', label: '浏览记录' },
  { icon: 'fa-solid fa-star', label: '我的收藏' },
  { icon: 'fa-solid fa-heart', label: '赞过' },
  { icon: 'fa-solid fa-gear', label: '设置' }
]

function formatLikes(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

onMounted(() => store.setActiveTab('profile'))
</script>
