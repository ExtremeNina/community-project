<template>
  <div
    ref="cardRef"
    class="md:rounded-lg overflow-hidden cursor-pointer active:scale-[0.98] md:active:scale-100 transition-transform duration-200 hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)]"
    @click="openDetail"
  >
    <div class="relative overflow-hidden">
      <img :src="note.cover" :alt="note.title" class="w-full object-cover" style="min-height: 130px" loading="lazy" />
    </div>
    <div>
      <p class="text-[13px] font-medium text-text-primary line-clamp-2 leading-[1.4]">
        {{ note.title }}
      </p>
      <div class="flex items-center justify-between mt-sm">
        <div class="flex items-center gap-[6px] min-w-0">
          <img :src="note.avatar" class="w-[18px] h-[18px] rounded-full object-cover flex-shrink-0" />
          <span class="text-[12px] text-text-aux truncate">{{ note.nickname }}</span>
        </div>
        <div class="flex items-center gap-[4px] flex-shrink-0">
          <i class="fa-solid fa-heart text-text-aux text-[11px]"></i>
          <span class="text-[12px] text-text-aux">{{ formatLikes(note.likes) }}</span>
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
</script>