<template>
  <aside class="w-right-panel h-full bg-white overflow-y-auto pc-scrollbar">
    <div class="p-lg">
      <div class="mb-xl">
        <div class="flex items-center justify-between mb-lg">
          <h3 class="text-h3 text-text-primary">推荐关注</h3>
          <span class="inline-flex items-center gap-1 text-aux text-primary cursor-pointer hover:text-primary-dark transition-colors select-none" @click="refreshUsers">
            <i class="fa-solid fa-arrows-rotate text-[13px]" :class="{ 'animate-spin': spinning }"></i>
            <span>换一批</span>
          </span>
        </div>
        <div class="flex flex-col" style="gap: 16px">
          <div v-for="user in recommendedUsers" :key="user.id" class="flex items-center gap-3">
            <img :src="user.avatar" class="w-[40px] h-[40px] rounded-full object-cover flex-shrink-0" />
            <div class="flex-1 min-w-0">
              <p class="text-[14px] text-text-primary truncate">{{ user.nickname }}</p>
              <p class="text-[11px] text-text-aux truncate mt-[2px]">{{ user.fans }} 粉丝</p>
            </div>
            <button
              class="flex-shrink-0 h-[28px] px-3 rounded-[14px] text-[12px] transition-colors"
              :class="user.followed ? 'border border-border text-text-aux bg-transparent hover:bg-bg-nav' : 'border border-primary text-primary bg-transparent hover:bg-primary-bg'"
              @click="user.followed = !user.followed"
            >
              {{ user.followed ? '已关注' : '关注' }}
            </button>
          </div>
        </div>
      </div>

      <div class="mb-xl">
        <div class="relative bg-gradient-to-br from-primary to-primary-light rounded-lg p-lg text-white cursor-pointer overflow-hidden">
          <div class="absolute inset-0 opacity-10" style="background-image: radial-gradient(circle at 20% 50%, rgba(255,255,255,0.8) 1px, transparent 1px); background-size: 20px 20px;"></div>
          <div class="relative">
            <p class="text-[16px] mb-3">成为创作者</p>
            <p class="text-[13px] leading-[1.5] mb-4" style="color: rgba(255,255,255,0.9)">分享你的美好生活，创作优质内容</p>
            <button class="bg-white text-primary text-[13px] h-[32px] px-lg rounded-[16px] hover:bg-bg-page transition-colors">
              立即创作
            </button>
          </div>
        </div>
      </div>

      <div>
        <h3 class="text-h3 text-text-primary mb-3">关于小蓝书</h3>
        <div class="flex flex-wrap" style="gap: 12px">
          <span v-for="(link, i) in footerLinks" :key="link" class="inline-flex items-center text-[12px] text-text-aux cursor-pointer hover:text-primary transition-colors">
            {{ link }}
            <span v-if="i < footerLinks.length - 1" class="text-text-aux ml-3">·</span>
          </span>
        </div>
        <p class="text-[11px] text-text-aux mt-4">© 2026 小蓝书 - 分享美好生活</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { reactive, ref } from 'vue'

const spinning = ref(false)

const recommendedUsers = reactive([
  { id: 1, followed: false, avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=40&h=40&fit=crop', nickname: '时尚穿搭达人', fans: '12.5w' },
  { id: 2, followed: false, avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=40&h=40&fit=crop', nickname: '旅行摄影师', fans: '8.9w' },
  { id: 3, followed: false, avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=40&h=40&fit=crop', nickname: '美食生活家', fans: '6.7w' }
])

const footerLinks = ['关于我们', '用户协议', '隐私政策', '帮助中心', '意见反馈', '商业合作']

const allUsers = [
  { id: 4, followed: false, avatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=40&h=40&fit=crop', nickname: '健身教练', fans: '5.6w' },
  { id: 5, followed: false, avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=40&h=40&fit=crop', nickname: '美妆新手', fans: '4.3w' },
  { id: 6, followed: false, avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=40&h=40&fit=crop', nickname: '数码控', fans: '3.8w' }
]

function refreshUsers() {
  spinning.value = true
  setTimeout(() => {
    spinning.value = false
    const shuffled = [...allUsers].sort(() => Math.random() - 0.5)
    recommendedUsers.splice(0, 3, ...shuffled.map(u => ({ ...u, followed: false })))
  }, 500)
}
</script>
