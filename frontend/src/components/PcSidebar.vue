<template>
  <aside
    class="h-full bg-white flex flex-col transition-all duration-300 overflow-hidden"
    :class="collapsed ? 'w-sidebar-collapsed' : 'w-sidebar'"
  >
    <div class="flex items-center justify-end px-3 h-[64px]">
      <button
        class="w-[32px] h-[32px] flex items-center justify-center rounded-md hover:bg-bg-page text-text-aux transition-colors"
        :class="[collapsed ? 'mx-auto' : '', 'hover:text-[#2385FF]']"
        @click="$emit('toggle')"
        :title="collapsed ? '展开菜单' : '收起菜单'"
      >
        <i :class="collapsed ? 'fa-solid fa-angles-right' : 'fa-solid fa-angles-left'" class="text-[15px]"></i>
      </button>
    </div>

    <nav class="flex-1 py-sm overflow-y-auto pc-scrollbar">
      <router-link
        v-for="item in menuItems"
        :key="item.path + item.label"
        :to="item.path"
        class="flex items-center gap-[10px] mx-sm mb-1 rounded-lg transition-all duration-200 cursor-pointer group relative"
        :class="[
          isActive(item.path) ? 'bg-nav text-primary' : 'text-text-aux hover:bg-nav',
          collapsed ? 'justify-center px-0 h-[44px]' : 'px-lg h-[44px]'
        ]"
        :title="collapsed ? item.label : ''"
      >
        <i :class="[item.icon, 'text-[18px] w-[24px] text-center', isActive(item.path) ? 'text-primary' : 'group-hover:text-primary']"></i>
        <span v-if="!collapsed" class="text-[16px] font-[500]">{{ item.label }}</span>
        <span v-if="!collapsed && item.badge" class="ml-auto bg-danger text-white text-[10px] h-[18px] min-w-[18px] px-[5px] rounded-full flex items-center justify-center">
          {{ item.badge > 99 ? '99+' : item.badge }}
        </span>
        <span v-if="collapsed && item.badge" class="absolute top-1.5 right-1.5 w-[14px] h-[14px] bg-danger rounded-full flex items-center justify-center">
          <span class="text-white text-[8px]">{{ item.badge > 99 ? '99+' : item.badge }}</span>
        </span>
      </router-link>

      <div class="mx-sm mb-1 relative">
        <div
          class="flex items-center gap-[10px] rounded-lg transition-all duration-200 cursor-pointer group"
          :class="[
            isCreationActive ? 'bg-nav text-primary' : 'text-text-aux hover:bg-nav',
            collapsed ? 'justify-center px-0 h-[44px]' : 'px-lg h-[44px]'
          ]"
          :title="collapsed ? '创作中心' : ''"
          @click="toggleCreationMenu"
        >
          <i class="fa-regular fa-pen-to-square text-[18px] w-[24px] text-center" :class="isCreationActive ? 'text-primary' : 'group-hover:text-primary'"></i>
          <span v-if="!collapsed" class="text-[16px] font-[500] flex-1">创作中心</span>
          <i
            v-if="!collapsed"
            :class="showCreationMenu ? 'fa-solid fa-chevron-down' : 'fa-solid fa-chevron-right'"
            class="text-[10px] text-text-aux transition-transform duration-200"
          ></i>
        </div>

        <div
          v-if="showCreationMenu && !collapsed"
          class="mt-sm"
        >
          <router-link
            to="/note-manage"
            class="flex items-center gap-[10px] px-lg h-[40px] rounded-lg cursor-pointer transition-colors group"
            :class="route.path.startsWith('/note-manage') ? 'bg-nav text-primary' : 'text-text-aux hover:bg-nav'"
          >
            <i class="fa-regular fa-folder text-[16px] w-[24px] text-center" :class="route.path.startsWith('/note-manage') ? 'text-primary' : 'group-hover:text-primary'"></i>
            <span class="text-[16px] font-[500] flex-1">笔记管理</span>
          </router-link>

          <router-link
            to="/publish"
            class="flex items-center gap-[10px] px-lg h-[40px] rounded-lg cursor-pointer transition-colors group"
            :class="route.path === '/publish' ? 'bg-nav text-primary' : 'text-text-aux hover:bg-nav'"
          >
            <i class="fa-regular fa-pen-to-square text-[16px] w-[24px] text-center" :class="route.path === '/publish' ? 'text-primary' : 'group-hover:text-primary'"></i>
            <span class="text-[16px] font-[500] flex-1">发布</span>
          </router-link>

          <router-link
            to="/data-center"
            class="flex items-center gap-[10px] px-lg h-[40px] rounded-lg cursor-pointer transition-colors group"
            :class="route.path === '/data-center' ? 'bg-nav text-primary' : 'text-text-aux hover:bg-nav'"
          >
            <i class="fa-regular fa-chart-bar text-[16px] w-[24px] text-center" :class="route.path === '/data-center' ? 'text-primary' : 'group-hover:text-primary'"></i>
            <span class="text-[16px] font-[500] flex-1">数据中心</span>
          </router-link>
        </div>

        <div
          v-if="showCreationMenu && collapsed"
          class="absolute left-full top-0 ml-sm w-[200px] bg-white rounded-lg shadow-heavy border border-border overflow-hidden z-50"
        >
          <div class="px-lg py-md">
            <p class="text-[16px] font-[500] text-text-primary">创作中心</p>
          </div>

          <router-link
            to="/note-manage"
            class="flex items-center gap-md px-lg py-md text-[16px] font-[500] hover:bg-nav cursor-pointer transition-colors"
            :class="route.path.startsWith('/note-manage') ? 'bg-nav text-primary' : 'text-text-aux'"
          >
            <i class="fa-regular fa-folder text-[15px] w-[20px]" :class="route.path.startsWith('/note-manage') ? 'text-primary' : 'text-text-aux'"></i> 笔记管理
          </router-link>

          <router-link
            to="/publish"
            class="flex items-center gap-md px-lg py-md text-[16px] font-[500] hover:bg-nav cursor-pointer transition-colors"
            :class="route.path === '/publish' ? 'bg-nav text-primary' : 'text-text-aux'"
          >
            <i class="fa-regular fa-pen-to-square text-[15px] w-[20px]" :class="route.path === '/publish' ? 'text-primary' : 'text-text-aux'"></i> 发布
          </router-link>

          <router-link
            to="/data-center"
            class="flex items-center gap-md px-lg py-md text-[16px] font-[500] hover:bg-nav cursor-pointer transition-colors"
            :class="route.path === '/data-center' ? 'bg-nav text-primary' : 'text-text-aux'"
          >
            <i class="fa-regular fa-chart-bar text-[15px] w-[20px]" :class="route.path === '/data-center' ? 'text-primary' : 'text-text-aux'"></i> 数据中心
          </router-link>
        </div>
      </div>
    </nav>

    <!-- 底部用户区域 -->
    <div
      class="mt-auto"
      :class="collapsed ? 'px-0 py-md flex justify-center' : 'px-md py-md'"
    >
      <div v-if="!isLoggedIn" class="w-full">
        <div
          class="flex items-center justify-center gap-md h-[44px] rounded-md bg-primary text-white text-[15px] transition-colors hover:bg-primary-dark cursor-pointer"
          :class="collapsed ? 'w-[44px] mx-auto' : 'w-full'"
          :title="'登录'"
          @click="openLoginModal"
        >
          <i class="fa-regular fa-right-to-bracket text-[16px]"></i>
          <span v-if="!collapsed">登录</span>
        </div>
      </div>
      <div v-else class="cursor-pointer group relative" @click="showUserMenu = !showUserMenu">
        <div v-if="!collapsed" class="flex items-center gap-md">
          <img
            :src="userAvatar"
            class="w-[36px] h-[36px] rounded-full object-cover border-2 border-transparent group-hover:border-primary transition-colors flex-shrink-0"
          />
          <div class="flex-1 min-w-0">
            <p class="text-[16px] font-[500] text-text-primary truncate">{{ userName }}</p>
          </div>
          <i class="fa-solid fa-ellipsis text-text-aux text-[14px]"></i>
        </div>
        <img v-else :src="userAvatar" class="w-[36px] h-[36px] rounded-full object-cover border-2 border-transparent group-hover:border-primary transition-colors" />
        <div
          v-if="showUserMenu"
          class="absolute bottom-full left-lg mb-sm w-[180px] bg-white rounded-lg shadow-heavy border border-border overflow-hidden z-50"
        >
          <div class="px-lg py-md">
            <p class="text-[16px] font-[500] text-text-primary">{{ userName }}</p>
          </div>
          <router-link to="/profile" class="flex items-center gap-md px-lg py-md text-[16px] font-[500] text-text-aux hover:bg-nav cursor-pointer transition-colors" @click="showUserMenu = false">
            <i class="fa-regular fa-user text-[15px] w-[20px] text-text-aux"></i> 我的主页
          </router-link>
          <div class="flex items-center gap-md px-lg py-md text-[16px] font-[500] text-text-aux hover:bg-nav cursor-pointer transition-colors" @click="showUserMenu = false">
            <i class="fa-regular fa-gear text-[15px] w-[20px] text-text-aux"></i> 设置
          </div>
          <div class="flex items-center gap-md px-lg py-md text-[16px] font-[500] text-text-aux hover:bg-nav cursor-pointer transition-colors border-t border-border" @click="handleLogout">
            <i class="fa-regular fa-right-from-bracket text-[15px] w-[20px]"></i> 退出登录
          </div>
        </div>
      </div>
    </div>
  </aside>

  <!-- LoginModal 作为独立根节点（Vue3 支持多根） -->
  <LoginModal :visible="loginModalVisible" @close="closeLoginModal" @login-success="fetchUserInfo" />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
// 以下 API 导入请根据实际路径修改；如果暂时没有，可以注释掉并用 mock 数据替换
import communityApi from '@/api/community'
import userApi from '@/api/user'
import authApi from '@/api/auth'
import LoginModal from '@/views/Login.vue'

defineProps({
  collapsed: { type: Boolean, default: false }
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const store = useAppStore()
const authStore = useAuthStore()

// ---------- 本地状态 ----------
const showUserMenu = ref(false)
const showCreationMenu = ref(false)
const sidebarUserAvatar = ref('')
const sidebarUserName = ref('')

// ---------- 修复1：为 store 中可能不存在的属性提供 fallback ----------
// 登录弹窗的显示状态（防止 store.showLoginModal 未定义）
const loginModalVisible = ref(false)

function openLoginModal() {
  loginModalVisible.value = true
  // 如果 store 也提供了 showLoginModal，可以同时更新
  if (store && typeof store.showLoginModal !== 'undefined') {
    store.showLoginModal = true
  }
}

function closeLoginModal() {
  loginModalVisible.value = false
  if (store && typeof store.showLoginModal !== 'undefined') {
    store.showLoginModal = false
  }
}

// ---------- 认证状态 ----------
const isLoggedIn = computed(() => authStore.isLoggedIn ?? false)

// 头像：从多个来源获取，提供默认头像
const userAvatar = computed(() => {
  if (sidebarUserAvatar.value) return sidebarUserAvatar.value
  // 修复：authStore.userAvatar 可能不存在，改为访问 authStore.user?.avatar
  if (authStore.user?.avatar) return authStore.user.avatar
  if (authStore.userAvatar) return authStore.userAvatar  // 兼容旧写法
  return 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=36&h=36&fit=crop'
})

// 用户名：从多个来源获取，最终兜底为“未登录用户”
const userName = computed(() => {
  if (sidebarUserName.value) return sidebarUserName.value
  // 修复：authStore.userNickname 可能不存在，改为 authStore.user?.nickname
  const nickname = authStore.user?.nickname ?? authStore.userNickname
  if (nickname && nickname !== '未登录用户') return nickname
  // 修复：store.user 可能为 undefined，使用可选链
  const storeNickname = store.user?.nickname
  if (storeNickname) return storeNickname
  return '未登录用户'
})

// ---------- 菜单项 ----------
const menuItems = [
  { path: '/home', label: '首页', icon: 'fa-regular fa-house' },
  { path: '/messages', label: '消息', icon: 'fa-regular fa-comment-dots', badge: 3 },
  { path: '/profile', label: '我的', icon: 'fa-regular fa-user' }
]

const isCreationActive = computed(() => {
  return route.path === '/publish' || route.path.startsWith('/note-manage') || route.path === '/data-center'
})

// 修复：isActive 函数必须定义，template 中使用了它
function isActive(path) {
  if (path === '/home' && route.path.startsWith('/home')) return true
  return route.path === path
}

function toggleCreationMenu() {
  showCreationMenu.value = !showCreationMenu.value
}

// ---------- 退出登录 ----------
async function handleLogout() {
  try {
    // 如果 authApi.logout 不存在，暂时不调用
    if (authApi && typeof authApi.logout === 'function') {
      await authApi.logout()
    }
  } catch (e) {
    console.warn('logout api error', e)
  }
  authStore.logout?.()  // 可选链防止 logout 未定义
  sidebarUserAvatar.value = ''
  sidebarUserName.value = ''
  showUserMenu.value = false
  router.push('/login')
}

// ---------- 获取用户信息（API 失败时不影响组件渲染）----------
async function fetchUserInfo() {
  try {
    // 如果 communityApi 不存在或方法缺失，静默失败
    if (!communityApi || typeof communityApi.checkLogin !== 'function') {
      console.warn('communityApi.checkLogin not available')
      return
    }
    const loginRes = await communityApi.checkLogin()
    if (loginRes.data === true) {
      // 同样检查 userApi
      if (userApi && typeof userApi.getProfile === 'function') {
        const profileRes = await userApi.getProfile()
        if (profileRes.data) {
          sidebarUserName.value = profileRes.data.username || ''
          authStore.setUser?.(profileRes.data)
        }
      }
      if (communityApi && typeof communityApi.getUserAvatar === 'function') {
        const avatarRes = await communityApi.getUserAvatar()
        if (avatarRes.data) {
          sidebarUserAvatar.value = avatarRes.data.icon || ''
        }
      }
    }
  } catch (e) {
    console.warn('fetchUserInfo failed', e)
    // 不抛出错误，组件依然渲染
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>