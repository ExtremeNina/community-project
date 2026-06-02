<template>
  <div class="flex flex-col h-full">
    <!-- 桌面端：Home 作为背景 -->
    <div class="hidden md:block h-full">
      <Home />
    </div>

    <div class="md:hidden flex flex-col h-full">
      <div class="flex-1 overflow-y-auto">
        <div class="relative">
          <img :src="note.cover" :alt="note.title" class="w-full object-cover" style="max-height: 480px" />
          <div class="absolute bottom-lg right-lg flex gap-xs">
            <span class="w-[8px] h-[8px] rounded-full bg-white shadow-light" v-for="i in noteImages.length" :key="i" :class="currentImage === i - 1 ? 'bg-white' : 'bg-white/50'"></span>
          </div>
        </div>
        <div class="p-lg">
          <h1 class="text-h1-m text-text-primary mb-md">{{ note.title }}</h1>
          <p class="text-body text-text-aux leading-relaxed mb-lg">{{ noteBody }}</p>
          <div class="flex flex-wrap gap-sm mb-lg">
            <span v-for="tag in tags" :key="tag" class="text-primary text-aux">{{ tag }}</span>
          </div>
        </div>
        <div class="px-lg pb-lg">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-md cursor-pointer" @click="$router.push('/profile')">
              <img :src="note.avatar" class="w-[48px] h-[48px] rounded-full object-cover" />
              <div>
                <p class="text-h3 text-text-primary">{{ note.nickname }}</p>
                <p class="text-aux text-text-aux mt-[2px]">1234 粉丝</p>
              </div>
            </div>
            <button class="h-[32px] px-lg rounded-md bg-primary text-white text-h3 hover:bg-primary-dark transition-colors">关注</button>
          </div>
        </div>
        <div>
          <div class="p-lg">
            <div class="flex items-center justify-between mb-lg">
              <span class="text-h3 text-text-primary">评论 {{ comments.length }}</span>
            </div>
            <div v-for="comment in sortedComments" :key="comment.id" class="mb-lg">
              <div class="flex gap-sm">
                <img :src="comment.icon" class="w-[36px] h-[36px] rounded-full object-cover flex-shrink-0" />
                <div class="flex-1">
                  <div class="flex items-center gap-sm">
                    <span class="text-aux text-text-primary">{{ comment.username }}</span>
                    <span class="text-tiny-m text-text-aux">{{ comment.createTime }}</span>
                  </div>
                  <p class="text-body-m text-text-aux mt-xs">{{ comment.content }}</p>
                  <div class="flex items-center gap-lg mt-sm">
                    <span class="text-aux text-text-aux cursor-pointer">
                      <i class="fa-regular fa-heart mr-[4px]"></i>{{ comment.love }}
                    </span>
                    <span class="text-aux text-text-aux cursor-pointer">
                      <i class="fa-regular fa-comment mr-[4px]"></i>回复
                    </span>
                  </div>
                </div>
              </div>
              <!-- 回复列表 -->
              <div v-if="comment.replyList && comment.replyList.length > 0" class="mt-md ml-[48px]">
                <div v-for="reply in comment.replyList" :key="reply.id" class="flex gap-sm mb-md">
                  <img :src="reply.icon" class="w-[32px] h-[32px] rounded-full object-cover flex-shrink-0" />
                  <div class="flex-1">
                    <div class="flex items-center gap-sm">
                      <span class="text-aux text-text-primary">{{ reply.username }}</span>
                      <span v-if="reply.replyName" class="text-tiny-m text-text-aux">回复 {{ reply.replyName }}</span>
                      <span class="text-tiny-m text-text-aux">{{ reply.createTime }}</span>
                    </div>
                    <p class="text-body-m text-text-aux mt-xs">{{ reply.content }}</p>
                    <div class="flex items-center gap-lg mt-sm">
                      <span class="text-aux text-text-aux cursor-pointer">
                        <i class="fa-regular fa-heart mr-[4px]"></i>{{ reply.love }}
                      </span>
                      <span class="text-aux text-text-aux cursor-pointer">
                        <i class="fa-regular fa-comment mr-[4px]"></i>回复
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="sticky bottom-0 bg-white px-lg h-[56px] flex items-center gap-md z-50">
        <img :src="userAvatar" class="w-[36px] h-[36px] rounded-full object-cover flex-shrink-0" />
        <div class="flex-1 flex items-center bg-white border border-border rounded-md h-[36px] px-md">
          <input type="text" placeholder="说点什么..." class="flex-1 text-[14px] bg-transparent" v-model="commentText" />
          <button
            class="text-[14px] ml-md flex-shrink-0 transition-colors"
            :class="commentText.trim() ? 'text-primary cursor-pointer' : 'text-text-aux cursor-not-allowed'"
            :disabled="!commentText.trim()"
            @click="addComment"
          >
            发送
          </button>
        </div>
        <div class="flex items-center gap-lg">
          <div class="flex items-center gap-[4px] cursor-pointer" :class="{ 'text-danger': liked }" @click="liked = !liked">
            <i :class="[liked ? 'fa-solid' : 'fa-regular', 'fa-heart text-[20px]']"></i>
            <span class="text-aux-m">{{ liked ? formatLikes(note.likes + 1) : formatLikes(note.likes) }}</span>
          </div>
          <div class="flex items-center gap-[4px] cursor-pointer" @click="collected = !collected">
            <i :class="[collected ? 'fa-solid' : 'fa-regular', 'fa-star text-[20px]']" :style="{ color: collected ? '#FFC53D' : '#8C8C8C' }"></i>
          </div>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div class="hidden md:block">
        <div v-if="modalVisible">
          <!-- 只在抽屉左侧可点击区域（不覆盖右侧抽屉） -->
          <div
            class="fixed inset-y-0 left-0 cursor-pointer"
            :style="{
              right: isWideViewport ? 'min(45vw, 600px)' : '60vw'
            }"
            @click="closeModal"
          ></div>

          <div
            class="fixed top-0 right-0 h-full bg-white flex flex-col z-[200]"
            :style="{
              width: isWideViewport ? 'min(45vw, 600px)' : '60vw',
              maxWidth: '600px',
              transform: `translateX(${drawerOffset})`,
              opacity: drawerOpacity,
              transition: 'transform 0.3s ease-out, opacity 0.3s ease-out',
              boxShadow: '-2px 0 12px rgba(0,0,0,0.1)'
            }"
          >
            <button
              class="absolute top-[16px] left-[16px] w-[36px] h-[36px] rounded-full bg-black/60 hover:bg-black/80 flex items-center justify-center z-20 transition-colors"
              @click="closeModal"
            >
              <i class="fa-solid fa-chevron-left text-white text-[18px]"></i>
            </button>

            <div class="flex-1 overflow-y-auto pc-scrollbar">
              <div class="w-full relative overflow-hidden bg-bg-page" :style="isTextNote ? { backgroundColor: pastelColor, minHeight: '400px' } : {}">
                <template v-if="isTextNote">
                  <i class="fa-solid fa-quote-right absolute text-white/20 text-[120px] left-xl top-lg"></i>
                  <div class="flex items-center justify-center min-h-[400px] px-2xl py-2xl">
                    <p class="text-[48px] text-text-primary text-center leading-[1.3]" style="font-weight: 500">
                      {{ textNoteDisplay }}
                    </p>
                  </div>
                </template>
                <template v-else>
                  <img
                    :src="noteImages[currentImage]"
                    class="w-full object-cover"
                    style="max-height: 50vh"
                  />
                  <button
                    v-if="noteImages.length > 1"
                    class="absolute left-lg top-1/2 -translate-y-1/2 w-[40px] h-[40px] rounded-full bg-white/90 flex items-center justify-center hover:bg-white transition-colors"
                    style="box-shadow: 0 2px 8px rgba(0,0,0,0.1)"
                    @click.stop="prevImage"
                  >
                    <i class="fa-solid fa-chevron-left text-text-primary text-[16px]"></i>
                  </button>
                  <button
                    v-if="noteImages.length > 1"
                    class="absolute right-lg top-1/2 -translate-y-1/2 w-[40px] h-[40px] rounded-full bg-white/90 flex items-center justify-center hover:bg-white transition-colors"
                    style="box-shadow: 0 2px 8px rgba(0,0,0,0.1)"
                    @click.stop="nextImage"
                  >
                    <i class="fa-solid fa-chevron-right text-text-primary text-[16px]"></i>
                  </button>
                  <div v-if="noteImages.length > 1" class="absolute bottom-lg left-1/2 -translate-x-1/2 flex gap-sm">
                    <span
                      v-for="(img, i) in noteImages"
                      :key="i"
                      class="w-[8px] h-[8px] rounded-full transition-colors cursor-pointer"
                      :class="i === currentImage ? 'bg-primary' : 'bg-white/60'"
                      @click="currentImage = i"
                    ></span>
                  </div>
                </template>
              </div>

              <div class="px-xl pt-xl pb-2xl">
                <div class="flex items-center justify-between pb-lg">
                  <div class="flex items-center gap-md cursor-pointer" @click="$router.push('/profile')">
                    <img :src="note.avatar" class="w-[48px] h-[48px] rounded-full object-cover" />
                    <span class="text-[16px] text-text-primary">{{ note.nickname }}</span>
                  </div>
                  <button
                    class="h-[32px] px-lg rounded-md text-[14px] transition-colors"
                    :class="followed ? 'bg-white border border-border text-text-primary hover:bg-bg-nav' : 'bg-primary text-white hover:bg-primary-dark'"
                    @click="followed = !followed"
                  >
                    {{ followed ? '已关注' : '关注' }}
                  </button>
                </div>

                <h1
                  class="text-[18px] text-text-primary leading-[1.4] mb-md"
                  style="word-break: break-word; font-weight: 500"
                >
                  {{ note.title }}
                </h1>

                <p
                  class="text-[14px] text-text-primary leading-[1.6] mb-md"
                  style="white-space: pre-wrap; word-break: break-word"
                >
                  {{ noteBody }}
                </p>

                <div class="flex flex-wrap gap-md mb-md">
                  <span v-for="tag in tags" :key="tag" class="text-[14px] text-primary cursor-pointer hover:text-primary-dark transition-colors">
                    #{{ tag }}
                  </span>
                </div>

                <div class="flex items-center justify-between pt-md pb-lg">
                  <div class="flex-1 flex flex-col items-center gap-[4px] cursor-pointer group" @click="liked = !liked">
                    <i :class="[liked ? 'fa-solid' : 'fa-regular', 'fa-heart text-[20px] group-hover:text-[#FF3B4A] transition-colors']" :style="{ color: liked ? '#FF3B4A' : '#8C8C8C' }"></i>
                    <span class="text-[12px]" :style="{ color: liked ? '#FF3B4A' : '#8C8C8C' }">{{ liked ? formatLikes(note.likes + 1) : formatLikes(note.likes) }}</span>
                  </div>
                  <div class="flex-1 flex flex-col items-center gap-[4px] cursor-pointer group" @click="collected = !collected">
                    <i :class="[collected ? 'fa-solid' : 'fa-regular', 'fa-star text-[20px] group-hover:text-[#FFC53D] transition-colors']" :style="{ color: collected ? '#FFC53D' : '#8C8C8C' }"></i>
                    <span class="text-[12px]" :style="{ color: collected ? '#FFC53D' : '#8C8C8C' }">{{ collected ? formatLikes(345 + 1) : 345 }}</span>
                  </div>
                  <div class="flex-1 flex flex-col items-center gap-[4px] cursor-pointer group">
                    <i class="fa-regular fa-comment text-[20px] text-text-aux group-hover:text-primary transition-colors"></i>
                    <span class="text-[12px] text-text-aux">评论</span>
                  </div>
                  <div class="flex-1 flex flex-col items-center gap-[4px] cursor-pointer group">
                    <i class="fa-regular fa-share-from-square text-[20px] text-text-aux group-hover:text-primary transition-colors"></i>
                    <span class="text-[12px] text-text-aux">分享</span>
                  </div>
                </div>

                <div class="flex flex-col">
                  <div class="flex items-center gap-md mb-lg">
                    <img
                      :src="userAvatar"
                      class="w-[36px] h-[36px] rounded-full object-cover flex-shrink-0"
                    />
                    <div class="flex-1 flex items-center bg-white border border-border rounded-md h-[40px] px-lg">
                      <input
                        type="text"
                        placeholder="说点什么..."
                        class="flex-1 text-[14px] bg-transparent"
                        v-model="commentText"
                      />
                      <button
                        class="text-[14px] ml-md flex-shrink-0 transition-colors"
                        :class="commentText.trim() ? 'text-primary cursor-pointer' : 'text-text-aux cursor-not-allowed'"
                        :disabled="!commentText.trim()"
                        @click="addComment"
                      >
                        发送
                      </button>
                    </div>
                  </div>

                  <div class="flex items-center gap-lg mb-md">
                    <span
                      class="text-[14px] cursor-pointer transition-colors"
                      :class="commentSort === 'hot' ? 'text-primary' : 'text-text-aux hover:text-text-secondary'"
                      @click="commentSort = 'hot'"
                    >最热</span>
                    <span
                      class="text-[14px] cursor-pointer transition-colors"
                      :class="commentSort === 'new' ? 'text-primary' : 'text-text-aux hover:text-text-secondary'"
                      @click="commentSort = 'new'"
                    >最新</span>
                  </div>

                  <div class="flex-1">
                    <div v-for="comment in sortedComments" :key="comment.id" class="mb-lg">
                      <div class="flex gap-md">
                        <img :src="comment.icon" class="w-[36px] h-[36px] rounded-full object-cover flex-shrink-0" />
                        <div class="flex-1 min-w-0">
                          <div class="flex items-center gap-sm">
                            <span class="text-[14px] text-text-primary">{{ comment.username }}</span>
                            <span class="text-[12px] text-text-aux">{{ comment.createTime }}</span>
                          </div>
                          <p class="text-[14px] text-text-primary leading-[1.6] mt-[4px]" style="word-break: break-word">{{ comment.content }}</p>
                          <div class="flex items-center gap-lg mt-sm">
                            <span class="text-[12px] text-text-aux cursor-pointer hover:text-[#FF3B4A] transition-colors flex items-center gap-[4px]">
                              <i class="fa-regular fa-heart text-[12px]"></i>{{ comment.love }}
                            </span>
                            <span class="text-[12px] text-text-aux cursor-pointer hover:text-primary transition-colors">
                              回复
                            </span>
                          </div>
                        </div>
                      </div>
                      <!-- 回复列表 -->
                      <div v-if="comment.replyList && comment.replyList.length > 0" class="mt-md ml-[52px]">
                        <div v-for="reply in comment.replyList" :key="reply.id" class="flex gap-md mb-md">
                          <img :src="reply.icon" class="w-[32px] h-[32px] rounded-full object-cover flex-shrink-0" />
                          <div class="flex-1 min-w-0">
                            <div class="flex items-center gap-sm">
                              <span class="text-[14px] text-text-primary">{{ reply.username }}</span>
                              <span v-if="reply.replyName" class="text-[12px] text-text-aux">回复 {{ reply.replyName }}</span>
                              <span class="text-[12px] text-text-aux">{{ reply.createTime }}</span>
                            </div>
                            <p class="text-[14px] text-text-primary leading-[1.6] mt-[4px]" style="word-break: break-word">{{ reply.content }}</p>
                            <div class="flex items-center gap-lg mt-sm">
                              <span class="text-[12px] text-text-aux cursor-pointer hover:text-[#FF3B4A] transition-colors flex items-center gap-[4px]">
                                <i class="fa-regular fa-heart text-[12px]"></i>{{ reply.love }}
                              </span>
                              <span class="text-[12px] text-text-aux cursor-pointer hover:text-primary transition-colors">
                                回复
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="mt-lg pt-lg">
                    <h3 class="text-[14px] text-text-primary mb-md">相关推荐</h3>
                    <div class="flex flex-col gap-md pb-xl">
                      <div v-for="related in relatedNotes" :key="related.id" class="flex gap-md cursor-pointer group" @click="openRelated(related.id)">
                        <img :src="related.cover" class="w-[72px] h-[72px] rounded-[8px] object-cover flex-shrink-0" />
                        <div class="flex-1 min-w-0">
                          <p class="text-[14px] text-text-primary line-clamp-2 group-hover:text-primary transition-colors leading-[1.4]">{{ related.title }}</p>
                          <p class="text-[12px] text-text-aux mt-[6px]">{{ related.nickname }} · {{ formatLikes(related.likes) }}赞</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import Home from './Home.vue'
import communityApi from '@/api/community'

const route = useRoute()
const $router = useRouter()
const store = useAppStore()

const noteId = computed(() => Number(route.params.id))
const detailArticle = ref(null)
const detailTags = ref([])
const noteLoading = ref(false)

const note = computed(() => {
  if (detailArticle.value) {
    const profile = detailArticle.value.userProfileVO || {}
    return {
      id: detailArticle.value.id,
      title: detailArticle.value.title || '',
      cover: detailArticle.value.coverImageUrl || '',
      avatar: profile.icon || '',
      nickname: profile.username || detailArticle.value.author || '',
      likes: detailArticle.value.loveCount || 0,
      category: (detailArticle.value.label && detailArticle.value.label.length > 0) ? detailArticle.value.label[0] : (detailArticle.value.category || '')
    }
  }
  return store.notes.find(n => n.id === noteId.value) || store.notes[0]
})

const modalVisible = ref(false)
const liked = ref(false)
const collected = ref(false)
const followed = ref(false)
const commentText = ref('')
const currentImage = ref(0)
const commentSort = ref('new')

const drawerOffset = ref('100%')
const drawerOpacity = ref(0)

const isWideViewport = computed(() => window.innerWidth >= 1200)

const userAvatar = computed(() => currentUserProfile.value?.icon || store.user.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=36&h=36&fit=crop')

const tags = computed(() => {
  if (detailTags.value.length > 0) return detailTags.value
  if (detailArticle.value?.label && detailArticle.value.label.length > 0) return detailArticle.value.label
  if (!note.value) return []
  return note.value.category ? [note.value.category] : []
})

const isTextNote = computed(() => note.value.id % 3 === 0)

const pastelColors = ['#E8E0F0', '#D8EAF0', '#F5E6E8', '#E0F0E8', '#F0EDD8']
const pastelColor = computed(() => pastelColors[(detailArticle.value?.id || note.value.id) % pastelColors.length])

const noteBody = computed(() => {
  if (detailArticle.value?.content) {
    return detailArticle.value.content
  }
  if (detailArticle.value?.summary) {
    return detailArticle.value.summary
  }
  const bodies = {
    1: '蓝色是属于天空和海洋的颜色，优雅又沉静。今年夏天，各种蓝色调的连衣裙、衬衫、半身裙纷纷登场，成为街拍中最吸睛的存在。\n\n搭配建议：\n• 蓝色连衣裙 + 白色凉鞋 + 草编包 = 满分夏日感\n• 蓝白条纹衬衫 + 牛仔短裤 = 休闲日常\n• 雾霾蓝针织上衣 + 米色阔腿裤 = 通勤必备',
    2: '三亚，这个被上帝宠坏的地方，五天四晚的旅程让我彻底爱上了这里。\n\nDAY1：抵达三亚 → 亚龙湾沙滩漫步\nDAY2：蜈支洲岛一日游 → 潜水体验\nDAY3：南山文化旅游区 → 天涯海角\nDAY4：免税店购物 → 椰梦长廊落日\nDAY5：酒店泳池 → 返程',
    3: '这款蓝莓芝士蛋糕真的超级好吃！口感细腻，入口即化，做法也超简单~\n\n材料：奶油奶酪250g、蓝莓150g、消化饼干100g、黄油50g、细砂糖60g、淡奶油200ml、吉利丁片10g\n\n步骤超详细，新手也能一次成功！',
    4: '把自己喜欢的北欧风搬回家，让蓝色成为家的主色调。\n\n蓝色调的墙面搭配原木色的家具，简约又不失温馨。沙发上的蓝色抱枕、墙上的海洋画、茶几上的蓝色花瓶，每一个细节都充满了小心思。',
    5: '30天健身打卡第15天！今天主要训练核心力量~\n\n训练内容：\n1. 平板支撑 3组 × 60秒\n2. 卷腹 4组 × 20个\n3. 俄罗斯转体 3组 × 20个\n4. 仰卧抬腿 3组 × 15个\n5. 有氧慢跑 30分钟',
    6: '夏日清透妆容教程来啦！这款蓝色系的眼妆真的太适合夏天了。\n\n重点：\n→ 底妆要轻薄透气\n→ 眼影用淡淡的蓝色系打底\n→ 眼线用深棕色更自然\n→ 唇妆选择水蜜桃色',
    7: '最新入手的蓝色款平板电脑，颜值真的太高了！深度使用一周后，给大家分享真实的体验感受。\n\n优点：屏幕素质优秀、系统流畅、续航给力\n缺点：配件太贵、充电速度一般',
    8: '分享五本近期读到的好书，每一本都值得反复阅读。\n\n1.《百年孤独》- 马尔克斯\n2.《活着》- 余华\n3.《小王子》- 圣埃克苏佩里\n4.《三体》- 刘慈欣\n5.《追风筝的人》- 卡勒德·胡赛尼',
    9: '养猫两年了，分享一些新手的注意事项。布偶猫真的超级粘人，每天回家都会在门口等着，一天的疲惫瞬间就被治愈了！\n\n新手养猫必备：猫砂盆、猫抓板、逗猫棒、营养膏、化毛膏...',
    10: '隐藏在西湖边的小众蓝色咖啡馆，真的太好拍了！整个店都是蓝色调的设计，从墙面到杯具，从菜单到手绘黑板，每一个角落都充满了蓝色的小浪漫。',
    11: '蓝色衬衫真的是衣柜里最百搭的单品了！今天来分享蓝色衬衫的N种穿法。\n\n1. 单穿配白裤子 → 清爽干练\n2. 叠穿在毛衣里 → 学院风\n3. 敞开当外套 → 慵懒随性\n4. 系个结配高腰裤 → 时髦有型',
    12: '秋天的京都，美得像一幅画。整理了十个最值得去的赏枫地，每一个都让你流连忘返。\n\n必去榜单：\n🍁 清水寺 - 夜枫绝美\n🍁 岚山 - 竹林与红叶\n🍁 永观堂 - 枫叶隧道'
  }
  return bodies[note.value.id] || '这是一篇精彩的分享笔记，记录了生活中的美好瞬间。快来一起探索更多精彩内容吧！'
})

const textNoteDisplay = computed(() => {
  const id = detailArticle.value?.id || note.value.id
  const phrases = {
    3: '超好吃的蓝莓\n芝士蛋糕',
    6: '蓝色系美妆教程\n夏日清透妆',
    9: '超萌布偶猫日常\n养猫新手必看',
    12: '京都赏枫攻略\n秋天最美的打卡地'
  }
  return phrases[id] || note.value.title
})

const noteImages = computed(() => {
  if (!note.value) return ['']
  if (detailArticle.value) {
    return [detailArticle.value.coverImageUrl || note.value.cover]
  }
  const covers = [
    'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=800&h=800&fit=crop',
    'https://images.unsplash.com/photo-1485968579580-b6d095142e6e?w=800&h=800&fit=crop',
    'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800&h=800&fit=crop'
  ]
  const alt = [
    'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=800&h=800&fit=crop',
    'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=800&h=800&fit=crop'
  ]
  if (note.value.id % 4 === 0) return [note.value.cover, ...alt.slice(0, 1)]
  if (note.value.id % 4 === 1) return [note.value.cover, ...alt]
  return [note.value.cover]
})

const relatedNotes = computed(() => {
  return store.notes.filter(n => n.id !== (detailArticle.value?.id || note.value.id)).slice(0, 5)
})

const comments = ref([])
const currentUserProfile = ref(null)

const sortedComments = computed(() => {
  const list = [...comments.value]
  if (commentSort.value === 'new') {
    list.sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
  } else {
    list.sort((a, b) => (b.love || 0) - (a.love || 0))
  }
  return list
})

function prevImage() {
  if (currentImage.value > 0) currentImage.value--
  else currentImage.value = noteImages.value.length - 1
}

function nextImage() {
  if (currentImage.value < noteImages.value.length - 1) currentImage.value++
  else currentImage.value = 0
}

function closeModal() {
  drawerOffset.value = '100%'
  drawerOpacity.value = 0
  setTimeout(() => {
    modalVisible.value = false
    document.body.style.overflow = ''
    $router.replace('/home')
  }, 330)
}

function openRelated(id) {
  currentImage.value = 0
  commentText.value = ''
  modalVisible.value = false
  document.body.style.overflow = ''
  $router.push(`/detail/${id}`)
}

async function addComment() {
  if (!commentText.value.trim()) return
  try {
    const res = await communityApi.publishComment({
      articleId: noteId.value,
      content: commentText.value.trim()
    })
    if (res.data) {
      comments.value.unshift(res.data)
    }
  } catch (e) {
    console.error('发表评论失败:', e)
  }
  commentText.value = ''
}

async function fetchComments() {
  try {
    const res = await communityApi.getCommentList(noteId.value)
    console.log('评论列表原始响应:', res)
    if (res.data?.list) {
      comments.value = res.data.list
    } else if (Array.isArray(res.data)) {
      comments.value = res.data
    }
  } catch (e) {
    console.error('获取评论列表失败:', e)
  }
}

async function fetchCurrentUserProfile() {
  try {
    const res = await communityApi.getCommentUserProfile()
    if (res.data) {
      currentUserProfile.value = res.data
    }
  } catch (e) {
    console.error('获取当前用户信息失败:', e)
  }
}

function onKeydown(e) {
  if (e.key === 'Escape') closeModal()
}

function formatLikes(num) {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

watch(noteId, () => {
  if (!modalVisible.value) return
  currentImage.value = 0
  commentText.value = ''
  liked.value = false
  collected.value = false
  followed.value = false
  commentSort.value = 'new'
  detailArticle.value = null
  detailTags.value = []
  comments.value = []
  fetchDetail()
  fetchComments()
})

onMounted(async () => {
  modalVisible.value = true
  drawerOffset.value = '100%'
  drawerOpacity.value = 0

  await nextTick()
  requestAnimationFrame(() => {
    drawerOffset.value = '0'
    drawerOpacity.value = 1
  })

  document.body.style.overflow = 'hidden'
  window.addEventListener('keydown', onKeydown)
  store.fetchNotes()
  fetchDetail()
  fetchComments()
  fetchCurrentUserProfile()
})

async function fetchDetail() {
  noteLoading.value = true
  try {
    const res = await communityApi.getArticleDetail(noteId.value)
    if (res.data) {
      detailArticle.value = res.data || null
      detailTags.value = res.data.label || []
    }
  } catch (e) {
    console.error('获取笔记详情失败:', e)
  } finally {
    noteLoading.value = false
  }
}

onUnmounted(() => {
  document.body.style.overflow = ''
  window.removeEventListener('keydown', onKeydown)
  store.cardRect = null
})
</script>