<template>
  <div class="sticky top-[56px] md:hidden z-40 bg-white h-[44px]">
    <div class="flex overflow-x-auto px-lg items-center h-full whitespace-nowrap gap-sm">
      <span
        v-for="cat in categories"
        :key="cat"
        class="flex-shrink-0 text-[14px] cursor-pointer transition-colors duration-200 px-md"
        :class="activeCategory === cat ? 'text-primary font-semibold' : 'text-secondary hover:text-primary'"
        @click="$emit('change', cat)"
      >
        {{ cat }}
      </span>
    </div>
  </div>

  <div class="hidden md:flex items-center bg-white relative overflow-hidden h-[44px]">
    <div class="flex overflow-x-auto px-lg items-center h-full whitespace-nowrap gap-sm">
      <span
        v-for="cat in categories"
        :key="cat"
        class="flex-shrink-0 text-[14px] cursor-pointer transition-colors duration-200 px-md"
        :class="activeCategory === cat ? 'text-primary font-semibold' : 'text-secondary hover:text-primary'"
        @click="$emit('change', cat)"
      >
        {{ cat }}
      </span>
    </div>
    <div class="absolute right-0 top-0 bottom-0 w-[40px] pointer-events-none" style="background: linear-gradient(to right, transparent, white)"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'

const store = useAppStore()

defineEmits(['change'])

const categories = computed(() => store.categories)
const activeCategory = computed(() => store.activeCategory)
</script>
