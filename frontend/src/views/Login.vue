<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div
        v-if="visible"
        class="fixed inset-0 z-[200] flex items-center justify-center p-xl"
        @click.self="close"
      >
        <div class="absolute inset-0" style="background: rgba(0,0,0,0.5)"></div>

        <div
          class="relative bg-white rounded-md shadow-heavy z-10 w-full max-w-[420px]"
        >
          <button
            class="absolute -top-[14px] -right-[14px] w-[32px] h-[32px] rounded-full bg-primary flex items-center justify-center z-20 hover:bg-primary-dark transition-colors"
            style="box-shadow: 0 2px 6px rgba(0,0,0,0.2)"
            @click="close"
          >
            <i class="fa-solid fa-xmark text-white text-[14px]"></i>
          </button>

          <div class="text-center pt-xl pb-sm">
            <div class="flex items-center justify-center gap-sm mb-xs">
              <i class="fa-solid fa-book text-primary text-[28px]"></i>
              <span class="text-[22px] font-semibold text-primary">小蓝书</span>
            </div>
            <p class="text-[13px] text-text-aux">分享美好生活</p>
          </div>

          <div class="mx-xl">
            <div class="flex border-b border-border">
              <span
                v-for="(tab, i) in authTabs"
                :key="i"
                class="flex-1 text-center h-[44px] leading-[44px] text-[14px] cursor-pointer transition-colors"
                :class="authTab === i ? 'text-primary border-b-2 border-primary' : 'text-text-secondary hover:text-primary'"
                @click="authTab = i"
              >
                {{ tab }}
              </span>
            </div>
          </div>

          <div class="p-xl">
            <div v-if="authTab === 0">
              <div class="flex flex-col gap-lg">
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">用户名</label>
                  <input
                    v-model="loginForm.username"
                    type="text"
                    placeholder="请输入用户名"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                    @keyup.enter="handleLogin"
                  />
                </div>
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">密码</label>
                  <input
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                    @keyup.enter="handleLogin"
                  />
                </div>
                <div v-if="loginError" class="text-[13px] text-danger mt-xs">{{ loginError }}</div>
                <button
                  class="w-full h-[44px] rounded-md text-white text-[15px] transition-colors"
                  :class="loginLoading ? 'bg-primary/60 cursor-not-allowed' : 'bg-primary hover:bg-primary-dark'"
                  :disabled="loginLoading"
                  @click="handleLogin"
                >
                  <span v-if="loginLoading" class="flex items-center justify-center gap-sm">
                    <i class="fa-solid fa-spinner animate-spin"></i> 登录中...
                  </span>
                  <span v-else>登录</span>
                </button>
              </div>
            </div>

            <div v-if="authTab === 1">
              <div class="flex flex-col gap-lg">
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">用户名</label>
                  <input
                    v-model="registerForm.username"
                    type="text"
                    placeholder="请输入用户名"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                  />
                </div>
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">邮箱</label>
                  <input
                    v-model="registerForm.mailbox"
                    type="email"
                    placeholder="请输入邮箱地址"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                  />
                </div>
                <div class="flex gap-md">
                  <div class="flex-1">
                    <label class="text-[13px] text-text-aux mb-xs block">验证码</label>
                    <input
                      v-model="registerForm.code"
                      type="text"
                      placeholder="验证码"
                      class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                    />
                  </div>
                  <div class="flex-shrink-0 pt-[24px]">
                    <button
                      class="h-[44px] px-lg border border-primary text-primary rounded-md text-[14px] transition-colors whitespace-nowrap"
                      :class="codeSending ? 'opacity-50 cursor-not-allowed' : 'hover:bg-primary-bg'"
                      :disabled="codeSending"
                      @click="sendRegisterCode"
                    >
                      <span v-if="codeSending">发送中...</span>
                      <span v-else-if="codeCountdown > 0">{{ codeCountdown }}s</span>
                      <span v-else>发送验证码</span>
                    </button>
                  </div>
                </div>
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">密码</label>
                  <input
                    v-model="registerForm.onePassword"
                    type="password"
                    placeholder="请设置密码"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                  />
                </div>
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">确认密码</label>
                  <input
                    v-model="registerForm.twoPassword"
                    type="password"
                    placeholder="请再次输入密码"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                  />
                </div>
                <div v-if="registerError" class="text-[13px] text-danger">{{ registerError }}</div>
                <button
                  class="w-full h-[44px] rounded-md text-white text-[15px] transition-colors"
                  :class="registerLoading ? 'bg-primary/60 cursor-not-allowed' : 'bg-primary hover:bg-primary-dark'"
                  :disabled="registerLoading"
                  @click="handleRegister"
                >
                  <span v-if="registerLoading" class="flex items-center justify-center gap-sm">
                    <i class="fa-solid fa-spinner animate-spin"></i> 注册中...
                  </span>
                  <span v-else>注册</span>
                </button>
              </div>
            </div>

          </div>

          <div class="px-xl pb-xl">
            <div class="flex items-center gap-md mb-lg">
              <div class="flex-1 h-[1px] bg-border"></div>
              <span class="text-[12px] text-text-aux">其他方式</span>
              <div class="flex-1 h-[1px] bg-border"></div>
            </div>
            <div class="flex flex-col gap-md">
              <button
                class="w-full h-[44px] border border-border rounded-md text-[15px] text-text-primary transition-colors flex items-center justify-center gap-md"
                :class="githubLoading ? 'opacity-50 cursor-not-allowed' : 'hover:bg-bg-nav'"
                :disabled="githubLoading"
                @click="handleGithubLogin"
              >
                <span v-if="githubLoading">
                  <i class="fa-solid fa-spinner animate-spin"></i>
                </span>
                <i v-else class="fa-brands fa-github text-[20px]"></i>
                通过 GitHub 登录
              </button>
              <button
                class="w-full h-[44px] border border-border rounded-md text-[15px] text-text-primary transition-colors flex items-center justify-center gap-md"
                :class="qqLoading ? 'opacity-50 cursor-not-allowed' : 'hover:bg-bg-nav'"
                :disabled="qqLoading"
                @click="showQQForm = !showQQForm"
              >
                <i class="fa-brands fa-qq text-[20px]"></i>
                QQ 邮箱登录
              </button>
            </div>

            <div v-if="showQQForm" class="mt-lg pt-lg border-t border-border">
              <div class="flex flex-col gap-lg">
                <p class="text-[13px] text-text-aux">使用 QQ 邮箱验证码登录</p>
                <div>
                  <label class="text-[13px] text-text-aux mb-xs block">QQ 邮箱</label>
                  <input
                    v-model="qqForm.mail"
                    type="email"
                    placeholder="name@qq.com"
                    class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                    @keyup.enter="handleQQLogin"
                  />
                </div>
                <div class="flex gap-md">
                  <div class="flex-1">
                    <label class="text-[13px] text-text-aux mb-xs block">验证码</label>
                    <input
                      v-model="qqForm.code"
                      type="text"
                      placeholder="验证码"
                      class="w-full h-[44px] px-md border border-border rounded-md text-[15px] text-text-primary bg-white"
                      @keyup.enter="handleQQLogin"
                    />
                  </div>
                  <div class="flex-shrink-0 pt-[24px]">
                    <button
                      class="h-[44px] px-lg border border-primary text-primary rounded-md text-[14px] transition-colors whitespace-nowrap"
                      :class="qqCodeSending ? 'opacity-50 cursor-not-allowed' : 'hover:bg-primary-bg'"
                      :disabled="qqCodeSending"
                      @click="sendQQCode"
                    >
                      <span v-if="qqCodeSending">发送中...</span>
                      <span v-else-if="qqCodeCountdown > 0">{{ qqCodeCountdown }}s</span>
                      <span v-else>发送验证码</span>
                    </button>
                  </div>
                </div>
                <div v-if="qqError" class="text-[13px] text-danger">{{ qqError }}</div>
                <div class="flex gap-md">
                  <button
                    class="flex-1 h-[44px] rounded-md text-white text-[15px] transition-colors"
                    :class="qqLoading ? 'bg-primary/60 cursor-not-allowed' : 'bg-primary hover:bg-primary-dark'"
                    :disabled="qqLoading"
                    @click="handleQQLogin"
                  >
                    <span v-if="qqLoading" class="flex items-center justify-center gap-sm">
                      <i class="fa-solid fa-spinner animate-spin"></i> 登录中...
                    </span>
                    <span v-else>登录</span>
                  </button>
                  <button
                    class="h-[44px] px-lg border border-border rounded-md text-[14px] text-text-aux transition-colors hover:bg-bg-nav"
                    @click="showQQForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import authApi from '@/api/auth'

const props = defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'login-success'])

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const authTab = ref(0)
const authTabs = ['登录', '注册']
const showQQForm = ref(false)

const loginForm = reactive({ username: '', password: '' })
const loginLoading = ref(false)
const loginError = ref('')

const registerForm = reactive({ username: '', mailbox: '', code: '', onePassword: '', twoPassword: '' })
const registerLoading = ref(false)
const registerError = ref('')
const codeSending = ref(false)
const codeCountdown = ref(0)
let codeTimer = null

const qqForm = reactive({ mail: '', code: '' })
const qqLoading = ref(false)
const qqError = ref('')
const qqCodeSending = ref(false)
const qqCodeCountdown = ref(0)
let qqCodeTimer = null

const githubLoading = ref(false)

function close() {
  emit('close')
  appStore.showLoginModal = false
}

function onLoginSuccess() {
  close()
  router.push('/home')
}

async function handleLogin() {
  loginError.value = ''
  if (!loginForm.username || !loginForm.password) {
    loginError.value = '请输入用户名和密码'
    return
  }
  loginLoading.value = true
  try {
    const res = await authApi.login(loginForm.username, loginForm.password)
    authStore.setToken(res.data)
    emit('login-success')
    onLoginSuccess()
  } catch (e) {
    loginError.value = e.message || '登录失败'
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  registerError.value = ''
  const { username, mailbox, code, onePassword, twoPassword } = registerForm
  if (!username || !mailbox || !code || !onePassword || !twoPassword) {
    registerError.value = '请填写所有字段'
    return
  }
  if (onePassword !== twoPassword) {
    registerError.value = '两次密码输入不一致'
    return
  }
  registerLoading.value = true
  try {
    const res = await authApi.register(registerForm)
    authStore.setToken(res.data)
    emit('login-success')
    onLoginSuccess()
  } catch (e) {
    registerError.value = e.message || '注册失败'
  } finally {
    registerLoading.value = false
  }
}

async function sendRegisterCode() {
  if (!registerForm.mailbox) {
    registerError.value = '请先输入邮箱'
    return
  }
  codeSending.value = true
  try {
    await authApi.sendVerifyCode(registerForm)
    startCodeCountdown('register')
  } catch (e) {
    registerError.value = e.message || '验证码发送失败'
  } finally {
    codeSending.value = false
  }
}

async function sendQQCode() {
  if (!qqForm.mail) {
    qqError.value = '请先输入 QQ 邮箱'
    return
  }
  qqCodeSending.value = true
  try {
    await authApi.sendQQEmail(qqForm.mail)
    startCodeCountdown('qq')
  } catch (e) {
    qqError.value = e.message || '验证码发送失败'
  } finally {
    qqCodeSending.value = false
  }
}

async function handleQQLogin() {
  qqError.value = ''
  if (!qqForm.mail || !qqForm.code) {
    qqError.value = '请输入邮箱和验证码'
    return
  }
  qqLoading.value = true
  try {
    const res = await authApi.qqLogin(qqForm.mail, qqForm.code)
    authStore.setToken(res.data)
    emit('login-success')
    onLoginSuccess()
  } catch (e) {
    qqError.value = e.message || '登录失败'
  } finally {
    qqLoading.value = false
  }
}

async function handleGithubLogin() {
  githubLoading.value = true
  try {
    const res = await authApi.githubLogin()
    const loginUrl = res.data?.github_login_url
    if (loginUrl) {
      window.location.href = loginUrl
    }
  } catch (e) {
    // ignore
  } finally {
    githubLoading.value = false
  }
}

function startCodeCountdown(type) {
  const count = 60
  if (type === 'register') {
    codeCountdown.value = count
    codeTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(codeTimer)
        codeTimer = null
        codeCountdown.value = 0
      }
    }, 1000)
  } else {
    qqCodeCountdown.value = count
    qqCodeTimer = setInterval(() => {
      qqCodeCountdown.value--
      if (qqCodeCountdown.value <= 0) {
        clearInterval(qqCodeTimer)
        qqCodeTimer = null
        qqCodeCountdown.value = 0
      }
    }, 1000)
  }
}

function onKeydown(e) {
  if (e.key === 'Escape' && props.visible) {
    close()
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    authTab.value = 0
    showQQForm.value = false
    loginError.value = ''
    registerError.value = ''
    qqError.value = ''
  }
})

onMounted(() => {
  window.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
  if (codeTimer) clearInterval(codeTimer)
  if (qqCodeTimer) clearInterval(qqCodeTimer)
})
</script>

<style scoped>
.modal-fade-enter-active {
  transition: opacity 0.25s ease;
}
.modal-fade-enter-active > div:last-child {
  transition: transform 0.25s ease, opacity 0.25s ease;
}
.modal-fade-leave-active {
  transition: opacity 0.2s ease;
}
.modal-fade-leave-active > div:last-child {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.modal-fade-enter-from {
  opacity: 0;
}
.modal-fade-enter-from > div:last-child {
  transform: scale(0.95);
  opacity: 0;
}
.modal-fade-leave-to {
  opacity: 0;
}
.modal-fade-leave-to > div:last-child {
  transform: scale(0.95);
  opacity: 0;
}
</style>