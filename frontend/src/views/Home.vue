<template>
  <div class="flex flex-col h-full">
    <div class="flex-1 overflow-y-auto px-lg" ref="scrollRef">
      <div class="flex md:hidden gap-[40px]">
        <div class="flex-1 flex flex-col gap-[40px]">
          <NoteCardMobile v-for="note in column(0, 2)" :key="note.id" :note="note" />
        </div>
        <div class="flex-1 flex flex-col gap-[40px]">
          <NoteCardMobile v-for="note in column(1, 2)" :key="note.id" :note="note" />
        </div>
      </div>

      <div class="hidden md:flex lg:hidden gap-[40px]">
        <div class="flex-1 flex flex-col gap-[40px]" v-for="col in 3" :key="col">
          <NoteCardDesktop v-for="note in column(col - 1, 3)" :key="note.id" :note="note" />
        </div>
      </div>

      <div class="hidden lg:flex 2xl:hidden justify-center gap-[40px]">
        <div class="flex max-w-[1400px] gap-[40px]" style="display: contents">
          <div class="flex-1 flex flex-col gap-[40px]" v-for="col in 4" :key="col">
            <NoteCardDesktop v-for="note in column(col - 1, 4)" :key="note.id" :note="note" />
          </div>
        </div>
      </div>

      <div class="hidden 2xl:flex justify-center gap-[40px]">
        <div class="flex max-w-[1400px] gap-[40px]" style="display: contents">
          <div class="flex-1 flex flex-col gap-[40px]" v-for="col in 5" :key="col">
            <NoteCardDesktop v-for="note in column(col - 1, 5)" :key="note.id" :note="note" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import NoteCardMobile from '@/components/NoteCardMobile.vue'
import NoteCardDesktop from '@/components/NoteCardDesktop.vue'

const store = useAppStore()
const route = useRoute()
const scrollRef = ref(null)

const notes = computed(() => store.currentCategoryNotes)

function column(colIndex, totalCols) {
  return notes.value.filter((_, i) => i % totalCols === colIndex)
}

onMounted(() => {
  store.setActiveTab('home')
  store.fetchNotes()
  if (route.query.category) {
    store.setActiveCategory(route.query.category)
  }
  nextTick(() => {
    if (scrollRef.value && store.homeScrollTop > 0) {
      scrollRef.value.scrollTop = store.homeScrollTop
    }
  })
})

onBeforeUnmount(() => {
  if (scrollRef.value) {
    store.homeScrollTop = scrollRef.value.scrollTop
  }
})
</script>
