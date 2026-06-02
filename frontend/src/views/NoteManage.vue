<template>
  <div class="flex-1 overflow-y-auto pc-scrollbar">
    <div class="max-w-[1000px] mx-auto p-lg md:p-xl">
      <h2 class="text-h2 text-text-primary mb-lg">笔记管理</h2>

      <div class="flex items-center bg-white rounded-md border border-border mb-xl overflow-hidden">
        <span
          v-for="tab in tabs"
          :key="tab.key"
          class="flex-1 text-center h-[44px] leading-[44px] text-[14px] cursor-pointer transition-colors relative"
          :class="activeTab === tab.key ? 'text-primary' : 'text-text-secondary hover:text-primary'"
          @click="switchTab(tab.key)"
        >
          {{ tab.label }}
        </span>
      </div>

      <div class="flex items-center justify-between mb-lg">
        <div class="flex items-center gap-md">
          <span class="text-body text-text-secondary">{{ sortedNotes.length }} 篇笔记</span>
          <span v-if="loading" class="text-[12px] text-text-aux flex items-center gap-xs">
            <i class="fa-solid fa-spinner animate-spin"></i> 加载中...
          </span>
        </div>
        <div class="flex items-center gap-md">
          <div class="flex items-center bg-white border border-border rounded-md h-[36px] px-md">
            <i class="fa-solid fa-magnifying-glass text-text-aux text-[14px]"></i>
            <input
              v-model="searchText"
              type="text"
              placeholder="搜索笔记标题..."
              class="ml-sm text-[14px] text-text-primary bg-transparent w-[180px]"
            />
          </div>
          <select
            v-model="sortBy"
            class="h-[36px] px-md rounded-md border border-border text-[14px] text-text-secondary bg-white cursor-pointer"
          >
            <option value="newest">最新发布</option>
            <option value="oldest">最早发布</option>
            <option value="mostLikes">最多点赞</option>
            <option value="mostViews">最多浏览</option>
          </select>
        </div>
      </div>

      <div v-if="loading && notes.length === 0" class="flex flex-col items-center justify-center py-[80px]">
        <i class="fa-solid fa-spinner animate-spin text-primary text-[36px]"></i>
        <p class="text-body text-text-aux mt-lg">加载中...</p>
      </div>

      <div v-else-if="sortedNotes.length === 0" class="flex flex-col items-center justify-center py-[80px] rounded-md border border-border">
        <i class="fa-solid fa-file-lines text-text-aux text-[48px]"></i>
        <p class="text-body text-text-aux mt-lg">暂无{{ activeTabLabel }}笔记</p>
      </div>

      <div v-else class="flex flex-col gap-md">
        <div
          v-for="note in sortedNotes"
          :key="note.id"
          class="bg-white rounded-md border border-border p-lg flex gap-lg cursor-pointer transition-all"
          @click="goToDetail(note)"
        >
          <img
            :src="note.cover"
            class="w-[100px] h-[100px] rounded-lg object-cover flex-shrink-0"
            @error="(e) => e.target.style.display = 'none'"
          />
          <div class="flex-1 min-w-0 flex flex-col justify-between">
            <div>
              <h3 class="text-[16px] text-text-primary line-clamp-1">{{ note.title }}</h3>
              <p class="text-[13px] text-text-aux mt-xs line-clamp-2">{{ note.summary || note.content }}</p>
            </div>
            <div class="flex items-center justify-between mt-sm">
              <div class="flex items-center gap-lg">
                <span class="flex items-center gap-[4px] text-[12px] text-text-aux">
                  <i class="fa-regular fa-eye"></i>{{ formatCount(note.views) }}
                </span>
                <span class="flex items-center gap-[4px] text-[12px] text-text-aux">
                  <i class="fa-regular fa-heart"></i>{{ formatCount(note.likes) }}
                </span>
                <span class="flex items-center gap-[4px] text-[12px] text-text-aux">
                  <i class="fa-regular fa-comment"></i>{{ formatCount(note.comments) }}
                </span>
              </div>
              <div class="flex items-center gap-md">
                <span class="text-[12px] text-text-aux">{{ note.date }}</span>
                <span
                  class="inline-flex items-center h-[24px] px-sm rounded-[12px] text-[11px]"
                  :class="statusClass(note.status)"
                >
                  {{ note.statusLabel }}
                </span>
              </div>
            </div>
          </div>
          <div class="flex flex-col gap-sm items-center justify-center">
            <button
              class="w-[32px] h-[32px] rounded-md flex items-center justify-center hover:bg-primary-bg text-text-aux hover:text-primary transition-colors"
              @click.stop="editNote(note)"
              title="编辑"
            >
              <i class="fa-regular fa-pen-to-square text-[14px]"></i>
            </button>
            <button
              class="w-[32px] h-[32px] rounded-md flex items-center justify-center hover:bg-danger-bg text-text-aux hover:text-danger transition-colors"
              @click.stop="deleteNote(note)"
              title="删除"
            >
              <i class="fa-regular fa-trash-can text-[14px]"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import createCenterApi from '@/api/createCenter'
import { mapArticleList } from '@/utils/fieldMapper'

const route = useRoute()
const $router = useRouter()

const activeTab = ref('all')
const searchText = ref('')
const sortBy = ref('newest')
const notes = ref([])
const loading = ref(false)

const tabs = [
  { key: 'all', label: '全部笔记' },
  { key: 'published', label: '已发布' },
  { key: 'reviewing', label: '审核中' },
  { key: 'draft', label: '草稿' }
]

const activeTabLabel = computed(() => {
  const tab = tabs.find(t => t.key === activeTab.value)
  return tab ? tab.label : ''
})

const filteredNotes = computed(() => {
  let result = notes.value
  if (searchText.value.trim()) {
    const keyword = searchText.value.trim().toLowerCase()
    result = result.filter(n => n.title.toLowerCase().includes(keyword))
  }
  return result
})

const sortedNotes = computed(() => {
  const list = [...filteredNotes.value]
  switch (sortBy.value) {
    case 'oldest': return list.sort((a, b) => a.date.localeCompare(b.date))
    case 'mostLikes': return list.sort((a, b) => b.likes - a.likes)
    case 'mostViews': return list.sort((a, b) => b.views - a.views)
    default: return list.sort((a, b) => b.date.localeCompare(a.date))
  }
})

const statusClassMap = {
  published: 'bg-green-50 text-green-600',
  draft: 'bg-yellow-50 text-yellow-600',
  reviewing: 'bg-blue-50 text-blue-600'
}

function statusClass(status) {
  return statusClassMap[status] || 'bg-gray-100 text-gray-500'
}

function formatCount(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

async function fetchNotes() {
  loading.value = true
  try {
    if (activeTab.value === 'all') {
      const [publishedRes, pendingRes, draftsRes] = await Promise.all([
        createCenterApi.getPublished(),
        createCenterApi.getPending(),
        createCenterApi.getDrafts()
      ])
      const all = [
        ...(publishedRes.data || []),
        ...(pendingRes.data || []),
        ...(draftsRes.data || [])
      ]
      notes.value = mapArticleList(all)
    } else {
      let res
      if (activeTab.value === 'published') {
        res = await createCenterApi.getPublished()
      } else if (activeTab.value === 'reviewing') {
        res = await createCenterApi.getPending()
      } else if (activeTab.value === 'draft') {
        res = await createCenterApi.getDrafts()
      }
      notes.value = mapArticleList(res.data || [])
    }
  } catch (e) {
    console.error('获取笔记列表失败:', e)
    notes.value = []
  } finally {
    loading.value = false
  }
}

async function deleteNote(note) {
  if (!confirm(`确定删除「${note.title}」吗？`)) return
  try {
    if (note.status === 'draft') {
      await createCenterApi.deleteDraft(note.id)
    } else {
      await createCenterApi.deleteArticle(note.id)
    }
    notes.value = notes.value.filter(n => n.id !== note.id)
  } catch (e) {
    console.error('删除失败:', e)
  }
}

function editNote(note) {
  $router.push(`/publish?id=${note.id}`)
}

function goToDetail(note) {
  $router.push(`/detail/${note.id}`)
}

function switchTab(key) {
  activeTab.value = key
  $router.replace({ path: '/note-manage', query: { tab: key } })
}

watch(activeTab, () => {
  searchText.value = ''
  fetchNotes()
})

onMounted(() => {
  const tab = route.query.tab
  const validKeys = tabs.map(t => t.key)
  if (tab && validKeys.includes(tab)) {
    activeTab.value = tab
  }
  fetchNotes()
})
</script>