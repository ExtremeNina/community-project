<template>
  <div class="flex flex-col h-full">
    <div class="md:hidden flex flex-col h-full">
      <div class="flex-1 overflow-y-auto">
        <div class="p-lg">
          <div class="border-2 border-dashed border-border rounded-lg h-[200px] flex flex-col items-center justify-center cursor-pointer bg-bg-page">
            <i class="fa-solid fa-plus text-text-aux text-[32px]"></i>
            <span class="text-text-aux text-body mt-md">点击上传图片 / 视频</span>
          </div>

          <div class="mt-lg">
            <textarea
              v-model="content"
              placeholder="分享你的美好生活..."
              class="w-full min-h-[120px] text-body text-text-primary placeholder:text-text-aux resize-none outline-none leading-relaxed border border-border rounded-md p-md bg-white"
            ></textarea>
          </div>

          <div class="mt-lg flex flex-wrap gap-sm">
            <span class="inline-flex items-center h-[32px] px-lg rounded-md bg-primary-bg text-primary text-aux cursor-pointer">
              <i class="fa-solid fa-location-dot mr-sm text-[12px]"></i>添加地点
            </span>
            <span class="inline-flex items-center h-[32px] px-lg rounded-md bg-primary-bg text-primary text-aux cursor-pointer">
              <i class="fa-solid fa-hashtag mr-sm text-[12px]"></i>添加话题
            </span>
            <span class="inline-flex items-center h-[32px] px-lg rounded-md bg-primary-bg text-primary text-aux cursor-pointer">
              <i class="fa-solid fa-user-group mr-sm text-[12px]"></i>@好友
            </span>
          </div>
        </div>
      </div>

      <div class="sticky bottom-0 bg-white p-lg z-50">
        <button
          class="w-full h-[44px] rounded-md bg-primary text-white text-h3 hover:bg-primary-dark transition-colors"
          :class="canPublish ? '' : 'bg-text-aux cursor-not-allowed'"
          :disabled="!canPublish"
        >
          发布笔记
        </button>
      </div>
    </div>

    <div class="hidden md:flex flex-1 overflow-hidden">
      <div class="flex-1 overflow-y-auto pc-scrollbar min-w-0">
        <div class="px-2xl max-w-[900px] mx-auto">
          <div class="border-2 border-dashed border-border rounded-lg flex flex-col items-center justify-center cursor-pointer bg-bg-page hover:border-primary hover:bg-primary-bg/30 transition-colors" style="height: 360px">
            <i class="fa-solid fa-cloud-arrow-up text-text-aux/50 text-[64px]"></i>
            <span class="text-text-aux text-h3 mt-lg">拖拽图片/视频到此处或点击上传</span>
            <span class="text-aux text-text-aux/60 mt-sm">支持 JPG、PNG、GIF、MP4 格式，最多 9 张图片</span>
          </div>

          <div class="flex gap-md mt-xl">
            <div v-for="i in 3" :key="i" class="w-[120px] h-[120px] rounded-lg bg-bg-page border border-border flex items-center justify-center cursor-pointer hover:border-primary transition-colors">
              <i class="fa-solid fa-plus text-text-aux text-[24px]"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="w-[400px] flex-shrink-0 overflow-y-auto pc-scrollbar bg-white">
        <div class="p-xl">
          <div class="mb-xl">
            <label class="text-body text-text-primary mb-md block">标题</label>
            <input
              v-model="title"
              type="text"
              placeholder="给笔记起个吸引人的标题..."
              class="w-full h-[44px] px-lg rounded-md border border-border bg-white text-body text-text-primary placeholder:text-text-aux focus:border-primary transition-colors"
            />
          </div>

          <div class="mb-xl">
            <label class="text-body text-text-primary mb-md block">正文</label>
            <textarea
              v-model="content"
              placeholder="分享你的美好生活..."
              class="w-full min-h-[200px] p-lg rounded-md border border-border bg-white text-body text-text-primary placeholder:text-text-aux resize-none focus:border-primary transition-colors"
            ></textarea>
          </div>

          <div class="mb-xl">
            <label class="text-body text-text-primary mb-md block">话题标签</label>
            <div class="flex flex-wrap gap-sm mb-sm">
              <span v-for="(tag, i) in tags" :key="i" class="inline-flex items-center h-[32px] px-md rounded-md bg-primary-bg text-primary text-aux">
                {{ tag }}
                <i class="fa-solid fa-xmark ml-sm text-[10px] cursor-pointer hover:text-danger" @click="tags.splice(i, 1)"></i>
              </span>
            </div>
            <div class="flex items-center h-[36px] px-md rounded-md border border-border text-body">
              <i class="fa-solid fa-hashtag text-text-aux text-[14px]"></i>
              <input
                type="text"
                placeholder="输入标签后按回车添加"
                class="flex-1 ml-sm bg-transparent text-[14px]"
                @keydown.enter.prevent="addTag"
                v-model="tagInput"
              />
            </div>
          </div>

          <div class="mb-xl">
            <label class="text-body text-text-primary mb-md block">发布设置</label>
            <div class="space-y-md">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-md">
                  <i class="fa-solid fa-location-dot text-text-aux w-[20px]"></i>
                  <span class="text-body text-text-aux">添加位置</span>
                </div>
                <i class="fa-solid fa-chevron-right text-text-aux"></i>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-md">
                  <i class="fa-solid fa-eye text-text-aux w-[20px]"></i>
                  <span class="text-body text-text-aux">公开可见</span>
                </div>
                <i class="fa-solid fa-chevron-right text-text-aux"></i>
              </div>
            </div>
          </div>

          <div class="flex gap-md">
            <button
              class="flex-1 h-[32px] rounded-md border border-border text-text-primary text-body hover:bg-bg-nav transition-colors"
            >
              存草稿
            </button>
            <button
              class="flex-1 h-[44px] rounded-md bg-primary text-white text-body hover:bg-primary-dark transition-colors"
              :class="canPublish ? '' : 'bg-text-aux cursor-not-allowed'"
              :disabled="!canPublish"
            >
              发布笔记
            </button>
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

const content = ref('')
const title = ref('')
const tags = ref([])
const tagInput = ref('')

const canPublish = computed(() => content.value.trim().length > 0)

function addTag() {
  const val = tagInput.value.trim()
  if (val && !tags.value.includes(val)) {
    tags.value.push(val)
  }
  tagInput.value = ''
}

onMounted(() => store.setActiveTab('publish'))
</script>
