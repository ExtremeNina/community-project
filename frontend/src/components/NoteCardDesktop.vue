<template>
  <div
    ref="cardRef"
    class="rounded-lg overflow-hidden cursor-pointer group transition-all duration-200 hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]"
    @click="openDetail"
  >
    <div class="relative overflow-hidden bg-bg-page">
      <img
        v-if="!imgError"
        :src="note.cover"
        :alt="note.title"
        class="w-full object-cover"
        style="aspect-ratio: 3/4"
        loading="lazy"
        @error="imgError = true"
      />
      <div
        v-if="imgError"
        class="w-full flex items-center justify-center bg-primary-bg"
        style="aspect-ratio: 3/4"
      >
        <div class="flex flex-col items-center">
          <i class="fa-solid fa-book text-primary/30 text-[40px]"></i>
          <span class="text-tiny text-text-aux mt-sm">加载失败</span>
        </div>
      </div>
      <div class="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors duration-200 flex items-center justify-center gap-3 opacity-0 group-hover:opacity-100">
        <button class="w-[36px] h-[36px] bg-white/90 rounded-full flex items-center justify-center hover:bg-white transition-colors" style="box-shadow: 0 2px 6px rgba(0,0,0,0.15)" @click.stop="onBookmark">
          <i class="fa-regular fa-bookmark text-text-primary text-[16px]"></i>
        </button>
        <button class="w-[36px] h-[36px] bg-white/90 rounded-full flex items-center justify-center hover:bg-white transition-colors" style="box-shadow: 0 2px 6px rgba(0,0,0,0.15)" @click.stop="onShare">
          <i class="fa-regular fa-share-from-square text-text-primary text-[15px]"></i>
        </button>
      </div>
    </div>
    <div>
      <p class="text-[14px] font-medium text-text-primary line-clamp-2 leading-[1.4] mb-[6px]">
        {{ note.title }}
      </p>
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-[6px] min-w-0">
          <img :src="note.avatar" class="w-[20px] h-[20px] rounded-full object-cover flex-shrink-0" />
          <span class="text-[12px] text-text-aux truncate">{{ note.nickname }}</span>
        </div>
        <div class="flex items-center gap-3 flex-shrink-0">
          <span class="flex items-center gap-[4px] text-[12px] text-text-aux">
            <i class="fa-regular fa-heart text-[11px]"></i>{{ formatLikes(note.likes) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

const $router = useRouter()
const store = useAppStore()
const imgError = ref(false)
const cardRef = ref(null)

const props = defineProps({
  note: { type: Object, required: true }
})

function formatLikes(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

function openDetail() {
  if (cardRef.value) {
    const rect = cardRef.value.getBoundingClientRect()
    store.cardRect = { left: rect.left, top: rect.top, width: rect.width, height: rect.height }
  }
  $router.push(`/detail/${props.note.id}`)
}

function onBookmark() {}
function onShare() {}
</script>