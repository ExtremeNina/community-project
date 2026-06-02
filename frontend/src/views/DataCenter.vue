<template>
  <div class="flex-1 overflow-y-auto pc-scrollbar">
    <div class="max-w-[1000px] mx-auto p-lg md:p-xl">
      <h2 class="text-h2 text-text-primary mb-lg">数据中心</h2>

      <div class="flex items-center gap-md mb-xl">
        <select
          v-model="dateRange"
          class="h-[36px] px-md rounded-md border border-border text-[14px] text-text-secondary bg-white cursor-pointer"
        >
          <option value="7d">最近7天</option>
          <option value="30d">最近30天</option>
          <option value="90d">最近90天</option>
          <option value="all">全部</option>
        </select>
      </div>

      <div class="grid grid-cols-2 md:grid-cols-4 gap-lg mb-xl">
        <div class="bg-white rounded-md border border-border p-lg">
          <div class="flex items-center gap-md mb-md">
            <div class="w-[44px] h-[44px] rounded-lg bg-blue-50 flex items-center justify-center">
              <i class="fa-regular fa-file-lines text-blue-500 text-[20px]"></i>
            </div>
            <div>
              <p class="text-[12px] text-text-aux">总笔记数</p>
              <p class="text-h2 text-text-primary">{{ stats.totalNotes }}</p>
            </div>
          </div>
          <div class="flex items-center gap-xs">
            <i class="fa-solid fa-arrow-up text-[10px] text-green-500"></i>
            <span class="text-[12px] text-green-500">{{ stats.notesGrowth }}%</span>
            <span class="text-[12px] text-text-aux ml-xs">较上期</span>
          </div>
        </div>

        <div class="bg-white rounded-md border border-border p-lg">
          <div class="flex items-center gap-md mb-md">
            <div class="w-[44px] h-[44px] rounded-lg bg-green-50 flex items-center justify-center">
              <i class="fa-regular fa-eye text-green-500 text-[20px]"></i>
            </div>
            <div>
              <p class="text-[12px] text-text-aux">总浏览量</p>
              <p class="text-h2 text-text-primary">{{ formatCount(stats.totalViews) }}</p>
            </div>
          </div>
          <div class="flex items-center gap-xs">
            <i class="fa-solid fa-arrow-up text-[10px] text-green-500"></i>
            <span class="text-[12px] text-green-500">{{ stats.viewsGrowth }}%</span>
            <span class="text-[12px] text-text-aux ml-xs">较上期</span>
          </div>
        </div>

        <div class="bg-white rounded-md border border-border p-lg">
          <div class="flex items-center gap-md mb-md">
            <div class="w-[44px] h-[44px] rounded-lg bg-red-50 flex items-center justify-center">
              <i class="fa-regular fa-heart text-red-400 text-[20px]"></i>
            </div>
            <div>
              <p class="text-[12px] text-text-aux">总获赞数</p>
              <p class="text-h2 text-text-primary">{{ formatCount(stats.totalLikes) }}</p>
            </div>
          </div>
          <div class="flex items-center gap-xs">
            <i class="fa-solid fa-arrow-up text-[10px] text-green-500"></i>
            <span class="text-[12px] text-green-500">{{ stats.likesGrowth }}%</span>
            <span class="text-[12px] text-text-aux ml-xs">较上期</span>
          </div>
        </div>

        <div class="bg-white rounded-md border border-border p-lg">
          <div class="flex items-center gap-md mb-md">
            <div class="w-[44px] h-[44px] rounded-lg bg-purple-50 flex items-center justify-center">
              <i class="fa-regular fa-comment-dots text-purple-500 text-[20px]"></i>
            </div>
            <div>
              <p class="text-[12px] text-text-aux">总评论数</p>
              <p class="text-h2 text-text-primary">{{ formatCount(stats.totalComments) }}</p>
            </div>
          </div>
          <div class="flex items-center gap-xs">
            <i class="fa-solid fa-arrow-up text-[10px] text-green-500"></i>
            <span class="text-[12px] text-green-500">{{ stats.commentsGrowth }}%</span>
            <span class="text-[12px] text-text-aux ml-xs">较上期</span>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-lg mb-xl">
        <div class="bg-white rounded-md border border-border p-xl">
          <h3 class="text-h3 text-text-primary mb-lg">浏览量趋势</h3>
          <div class="flex items-end gap-xs" style="height: 180px">
            <div
              v-for="(bar, i) in chartData"
              :key="i"
              class="flex-1 flex flex-col items-center gap-xs"
            >
              <span class="text-[11px] text-text-aux">{{ formatCount(bar.value) }}</span>
              <div
                class="w-full rounded-t-md transition-all duration-300"
                :style="{ height: barHeight(bar.value) + 'px', background: 'linear-gradient(180deg, #2385FF, #5AA7FF)' }"
              ></div>
              <span class="text-[11px] text-text-aux">{{ bar.label }}</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-md border border-border p-xl">
          <h3 class="text-h3 text-text-primary mb-lg">互动数据分布</h3>
          <div class="flex items-center justify-center" style="height: 180px">
            <div class="relative w-[160px] h-[160px]">
              <svg viewBox="0 0 160 160" class="w-full h-full -rotate-90">
                <circle cx="80" cy="80" r="64" fill="none" stroke="#F0F0F0" stroke-width="16" />
                <circle
                  cx="80" cy="80" r="64" fill="none"
                  stroke="#1677FF" stroke-width="16"
                  :stroke-dasharray="`${(stats.totalLikes / stats.maxVal) * 380} 380`"
                  stroke-linecap="round"
                />
                <circle
                  cx="80" cy="80" r="48" fill="none"
                  stroke="#FF4D4F" stroke-width="16"
                  :stroke-dasharray="`${(stats.totalComments / stats.maxVal) * 280} 280`"
                  stroke-linecap="round"
                />
              </svg>
              <div class="absolute inset-0 flex flex-col items-center justify-center">
                <span class="text-h2 text-text-primary">{{ stats.interactionRate }}%</span>
                <span class="text-[12px] text-text-aux">互动率</span>
              </div>
            </div>
          </div>
          <div class="flex items-center justify-center gap-xl mt-md">
            <div class="flex items-center gap-xs">
              <span class="w-[10px] h-[10px] rounded-full bg-[#1677FF]"></span>
              <span class="text-[12px] text-text-aux">点赞</span>
            </div>
            <div class="flex items-center gap-xs">
              <span class="w-[10px] h-[10px] rounded-full bg-[#FF4D4F]"></span>
              <span class="text-[12px] text-text-aux">评论</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-md border border-border p-xl mb-xl">
        <h3 class="text-h3 text-text-primary mb-lg">表现最佳笔记 TOP5</h3>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="border-b border-border">
                <th class="text-left text-[12px] text-text-aux py-md px-md">排名</th>
                <th class="text-left text-[12px] text-text-aux py-md px-md">笔记标题</th>
                <th class="text-right text-[12px] text-text-aux py-md px-md">浏览量</th>
                <th class="text-right text-[12px] text-text-aux py-md px-md">点赞</th>
                <th class="text-right text-[12px] text-text-aux py-md px-md">评论</th>
                <th class="text-right text-[12px] text-text-aux py-md px-md">互动率</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(item, i) in topNotes"
                :key="item.id"
                class="border-b border-border hover:bg-bg-page transition-colors cursor-pointer"
                @click="$router.push(`/detail/${item.id}`)"
              >
                <td class="py-md px-md">
                  <span
                    class="w-[24px] h-[24px] rounded-full flex items-center justify-center text-[12px] text-white"
                    :class="i < 3 ? 'bg-primary' : 'bg-gray-300'"
                  >
                    {{ i + 1 }}
                  </span>
                </td>
                <td class="py-md px-md">
                  <p class="text-[14px] text-text-primary line-clamp-1">{{ item.title }}</p>
                </td>
                <td class="py-md px-md text-right text-[14px] text-text-secondary">{{ formatCount(item.views) }}</td>
                <td class="py-md px-md text-right text-[14px] text-text-secondary">{{ formatCount(item.likes) }}</td>
                <td class="py-md px-md text-right text-[14px] text-text-secondary">{{ formatCount(item.comments) }}</td>
                <td class="py-md px-md text-right text-[14px] text-primary">{{ item.rate }}%</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="bg-white rounded-md border border-border p-xl">
        <h3 class="text-h3 text-text-primary mb-lg">发布趋势</h3>
        <div class="flex items-end gap-md" style="height: 160px">
          <div
            v-for="(day, i) in publishTrend"
            :key="i"
            class="flex-1 flex flex-col items-center gap-xs"
          >
            <span class="text-[12px] text-text-primary">{{ day.count > 0 ? day.count : '' }}</span>
            <div
              class="w-full rounded-t-md transition-all duration-300"
              :style="{
                height: (day.count / publishMax) * 120 + 'px',
                background: day.count > 0 ? 'linear-gradient(180deg, #52C41A, #B7EB8F)' : 'transparent',
                minHeight: day.count > 0 ? '4px' : '0'
              }"
            ></div>
            <span class="text-[11px] text-text-aux">{{ day.label }}</span>
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
const dateRange = ref('7d')

const stats = computed(() => ({
  totalNotes: 8,
  totalViews: 87560,
  totalLikes: 8345,
  totalComments: 586,
  notesGrowth: 12.5,
  viewsGrowth: 23.8,
  likesGrowth: 18.2,
  commentsGrowth: 9.6,
  maxVal: 10000,
  interactionRate: 4.8
}))

const chartData = computed(() => {
  const ranges = {
    '7d': [
      { label: '05/16', value: 3200 },
      { label: '05/17', value: 4500 },
      { label: '05/18', value: 3800 },
      { label: '05/19', value: 5200 },
      { label: '05/20', value: 6100 },
      { label: '05/21', value: 4800 },
      { label: '05/22', value: 7200 }
    ],
    '30d': [
      { label: 'W1', value: 18500 },
      { label: 'W2', value: 22000 },
      { label: 'W3', value: 19800 },
      { label: 'W4', value: 27200 }
    ],
    '90d': [
      { label: '2月', value: 32000 },
      { label: '3月', value: 28500 },
      { label: '4月', value: 41000 },
      { label: '5月', value: 38000 }
    ],
    'all': [
      { label: 'Q1', value: 42000 },
      { label: 'Q2', value: 45600 }
    ]
  }
  return ranges[dateRange.value] || ranges['7d']
})

const maxChartValue = computed(() => Math.max(...chartData.value.map(d => d.value)))

function barHeight(value) {
  return Math.max((value / maxChartValue.value) * 140, 4)
}

const topNotes = [
  { id: 3, title: '蓝莓芝士蛋糕做法新手也能搞定', views: 34560, likes: 3456, comments: 234, rate: 5.8 },
  { id: 2, title: '三亚旅行攻略五天四晚玩转海岛', views: 23450, likes: 2345, comments: 156, rate: 5.2 },
  { id: 1, title: '夏日清新蓝色连衣裙穿搭分享', views: 12340, likes: 1234, comments: 89, rate: 4.6 },
  { id: 12, title: '杭州探店隐藏在西湖边的蓝色咖啡馆', views: 8920, likes: 823, comments: 67, rate: 3.8 },
  { id: 5, title: '每日健身打卡30天减脂塑形计划', views: 5860, likes: 487, comments: 40, rate: 3.2 }
]

const publishTrend = computed(() => {
  const ranges = {
    '7d': [
      { label: '16', count: 1 }, { label: '17', count: 0 }, { label: '18', count: 2 },
      { label: '19', count: 1 }, { label: '20', count: 1 }, { label: '21', count: 1 }, { label: '22', count: 2 }
    ],
    '30d': [
      { label: 'W1', count: 3 }, { label: 'W2', count: 2 }, { label: 'W3', count: 4 }, { label: 'W4', count: 3 }
    ],
    '90d': [
      { label: '2月', count: 6 }, { label: '3月', count: 8 }, { label: '4月', count: 5 }, { label: '5月', count: 7 }
    ],
    'all': [
      { label: 'Q1', count: 14 }, { label: 'Q2', count: 12 }
    ]
  }
  return ranges[dateRange.value] || ranges['7d']
})

const publishMax = computed(() => Math.max(...publishTrend.value.map(d => d.count), 1))

function formatCount(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

onMounted(() => {
  store.setActiveTab('profile')
})
</script>
