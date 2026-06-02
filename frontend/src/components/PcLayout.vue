<template>
  <div class="flex h-screen overflow-hidden" @scroll.passive="onScroll">
    <div class="hidden md:flex h-full">
      <PcSidebar :collapsed="sidebarCollapsed" @toggle="sidebarCollapsed = !sidebarCollapsed" />
    </div>
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <PcTopNav :class="{ 'shadow-light': headerScrolled }" />
      <div class="hidden md:block flex-shrink-0">
        <CategoryTabs v-if="showCategoryTabs" @change="onCategoryChange" />
      </div>
      <div class="flex-1 flex overflow-hidden" ref="contentRef" @scroll="onContentScroll">
        <main class="flex-1 overflow-y-auto min-w-0 pc-content-area">
          <slot />
        </main>
      </div>
      <div class="md:hidden">
        <BottomNav />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import PcTopNav from './PcTopNav.vue'
import PcSidebar from './PcSidebar.vue'
import CategoryTabs from './CategoryTabs.vue'
import BottomNav from './BottomNav.vue'

const route = useRoute()
const store = useAppStore()
const sidebarCollapsed = ref(false)
const headerScrolled = ref(false)
const contentRef = ref(null)

const showCategoryTabs = computed(() => {
  return route.name === 'Home' || route.name === 'Detail'
})

function onCategoryChange(cat) {
  store.setActiveCategory(cat)
}

function onContentScroll() {
  headerScrolled.value = contentRef.value?.scrollTop > 0
}
</script>
