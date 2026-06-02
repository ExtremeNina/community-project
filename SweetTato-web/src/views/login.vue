<template>
  <div class="login-modal" role="presentation">
    <div class="login-backdrop" @click="close"></div>
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-card__side login-card__side--left">
          <div class="login-card__banner">
            <div class="login-card__banner-tag">登录后推荐更懂你的笔记</div>
            <img class="login-card__logo" src="@/assets/logo.png" />
            <div class="login-card__socials">
              <button class="social-btn social-btn--github" @click="githubLogin">
                <svg class="social-icon" viewBox="0 0 24 24" fill="none">
                  <path fill="currentColor" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.009-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.463-1.11-1.463-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.268 2.75 1.026A9.576 9.576 0 0 1 12 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.026 2.747-1.026.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.338-.012 2.419-.012 2.747 0 .267.18.578.688.48C19.138 20.163 22 16.418 22 12c0-5.523-4.477-10-10-10z"/>
                </svg>
                <span>GitHub 登录</span>
              </button>
            </div>
          </div>
        </div>

        <div class="login-card__side login-card__side--right">
          <div class="login-card__close" @click="close">
            <Close />
          </div>

          <div class="login-card__tabs">
            <button
              class="tab-btn"
              :class="{ active: loginTab === 'account' }"
              @click="loginTab = 'account'"
            >
              用户名密码登录
            </button>
            <button
              class="tab-btn"
              :class="{ active: loginTab === 'qq' }"
              @click="loginTab = 'qq'"
            >
              QQ邮箱登录
            </button>
          </div>

          <div class="login-card__content">
            <!-- 账号密码 -->
            <div v-show="loginTab === 'account'" class="login-form">
              <div class="input-group">
                <input
                  type="text"
                  class="input"
                  v-model="userLogin.phone"
                  placeholder="请输入用户名"
                />
              </div>
              <div class="input-group">
                <input
                  type="password"
                  class="input"
                  v-model="userLogin.password"
                  placeholder="请输入密码"
                />
              </div>
              <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>
              <button class="submit-btn" @click="loginMethod">登录</button>
            </div>

            <!-- QQ 邮箱 -->
            <div v-show="loginTab === 'qq'" class="login-form">
              <div class="input-group">
                <input
                  type="text"
                  class="input"
                  v-model="qqEmail"
                  placeholder="请输入 QQ 邮箱"
                />
              </div>
              <div class="input-group input-group--has-suffix">
                <input
                  type="text"
                  class="input"
                  v-model="qqCode"
                  placeholder="请输入验证码"
                />
                <button
                  class="input-suffix-btn"
                  :disabled="qqCountdown > 0"
                  @click="sendQqCode"
                >
                  {{ qqCountdown > 0 ? `${qqCountdown}s` : '获取验证码' }}
                </button>
              </div>
              <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>
              <button class="submit-btn" @click="qqLoginMethod">登录</button>
            </div>
          </div>

          <div class="login-card__agreement">
            <label class="checkbox-label">
              <input type="checkbox" v-model="agreedToTerms" class="checkbox-input" />
              <span class="checkbox-box"></span>
              <span class="agreement-text">我已阅读并同意</span>
            </label>
            <div class="agreement-links">
              <a href="https://agree.xiaohongshu.com/h5/terms/ZXXY20220331001/-1" target="_blank">《用户协议》</a>
              <a href="https://agree.xiaohongshu.com/h5/terms/ZXXY20220509001/-1" target="_blank">《隐私政策》</a>
              <a href="https://oa.xiaohongshu.com/h5/terms/ZXXY20220516001/-1" target="_blank">《儿童/青少年个人信息保护规则》</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Close } from "@element-plus/icons-vue";
import type { UserLogin } from "@/type/user";
import { login, getUserAvatar, qqSendCode, qqLogin } from "@/api/user";
import { ref } from "vue";
import { storage } from "@/utils/storage";
import { useUserStore } from "@/store/userStore";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const userStore = useUserStore();
const router = useRouter();

const agreedToTerms = ref(false);
const loginTab = ref<'account' | 'qq'>('account');
const errorMsg = ref('');

const userLogin = ref<UserLogin>({
  phone: "",
  email: "",
  code: "",
  username: "",
  password: "",
});

const qqEmail = ref('');
const qqCode = ref('');
const qqCountdown = ref(0);
let qqTimer: ReturnType<typeof setInterval> | null = null;

const emit = defineEmits(["clickChild"]);
const close = () => {
  if (qqTimer) clearInterval(qqTimer);
  emit("clickChild", false);
};

const DEVICE_ID_KEY = 'device_id';
const getDeviceId = (): string => {
  let deviceId = storage.get<string>(DEVICE_ID_KEY);
  if (!deviceId) {
    deviceId = 'web_' + Date.now() + '_' + Math.random().toString(36).substring(2, 11);
    storage.set(DEVICE_ID_KEY, deviceId);
  }
  return deviceId;
};

const afterLogin = (tokens: any) => {
  storage.set("accessToken", tokens.accessToken);
  storage.set("refreshToken", tokens.refreshToken);
  if (tokens.expiresIn) {
    storage.set("expireAt", Date.now() + tokens.expiresIn * 1000);
  }
  return getUserAvatar().then((avatarRes: any) => {
    const avatarData = avatarRes.data;
    const currentUser: any = {
      id: String(avatarData.id || ''),
      avatar: avatarData.icon || '',
    };
    userStore.setUserInfo(currentUser);
    router.push({ path: "/", query: { date: Date.now() } });
    emit("clickChild", false);
  });
};

const loginMethod = () => {
  if (!agreedToTerms.value) {
    ElMessage.warning("请先阅读并同意用户协议、隐私政策和个人信息保护规则");
    return;
  }
  const account = userLogin.value.phone;
  const password = userLogin.value.password;
  if (!account) {
    errorMsg.value = "请输入用户名";
    return;
  }
  if (!password) {
    errorMsg.value = "请输入密码";
    return;
  }
  errorMsg.value = '';
  login({ ...userLogin.value, deviceId: getDeviceId() })
    .then((res: any) => afterLogin(res.data))
    .catch((err: any) => {
      errorMsg.value = err?.msg || '登录失败，请检查账号密码';
    });
};

const sendQqCode = () => {
  if (qqCountdown.value > 0) return;
  const email = qqEmail.value.trim();
  if (!email) {
    ElMessage.warning("请输入 QQ 邮箱");
    return;
  }
  if (!/@qq\.com$/.test(email)) {
    ElMessage.warning("请输入有效的 QQ 邮箱");
    return;
  }
  qqSendCode(email).then(() => {
    ElMessage.success("验证码已发送");
    qqCountdown.value = 60;
    qqTimer = setInterval(() => {
      qqCountdown.value--;
      if (qqCountdown.value <= 0) {
        if (qqTimer) clearInterval(qqTimer);
      }
    }, 1000);
  }).catch(() => {
    ElMessage.error("验证码发送失败");
  });
};

const qqLoginMethod = () => {
  if (!agreedToTerms.value) {
    ElMessage.warning("请先阅读并同意用户协议、隐私政策和个人信息保护规则");
    return;
  }
  const email = qqEmail.value.trim();
  const code = qqCode.value.trim();
  if (!email) {
    errorMsg.value = "请输入 QQ 邮箱";
    return;
  }
  if (!code) {
    errorMsg.value = "请输入验证码";
    return;
  }
  errorMsg.value = '';
  qqLogin(email, code)
    .then((res: any) => afterLogin(res.data))
    .catch(() => {
      errorMsg.value = '登录失败，请检查邮箱和验证码';
    });
};

const githubLogin = () => {
  window.location.href = "http://localhost:8081/oauth2/authorization/github";
};
</script>

<style lang="less" scoped>
a {
  text-decoration: none;
  color: inherit;
}

.login-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
}

.login-wrapper {
  position: relative;
  z-index: 1;
  animation: slideUp 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.login-card {
  width: 860px;
  height: 520px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 20px 60px -10px rgba(0, 0, 0, 0.15);
  display: flex;
  overflow: hidden;
  position: relative;
}

.login-card__side {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.login-card__side--left {
  background: linear-gradient(135deg, #ffedeb 0%, #fff 100%);
  border-right: 1px solid rgba(0, 0, 0, 0.03);
  padding: 48px;
  align-items: center;
  justify-content: center;
}

.login-card__banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.login-card__banner-tag {
  padding: 10px 20px;
  background: linear-gradient(135deg, #ffe4e0 0%, #fff0ef 100%);
  color: #ff2442;
  font-size: 14px;
  font-weight: 600;
  border-radius: 999px;
  margin-bottom: 32px;
}

.login-card__logo {
  height: 40px;
  margin-bottom: 64px;
  user-select: none;
  pointer-events: none;
}

.login-card__socials {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.social-btn {
  width: 100%;
  height: 48px;
  border: 1.5px solid rgba(0, 0, 0, 0.08);
  background: #fff;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 15px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;

  &:hover {
    border-color: #ff2442;
    color: #ff2442;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255, 36, 66, 0.15);
  }

  &:active {
    transform: translateY(0);
  }
}

.social-icon {
  width: 22px;
  height: 22px;
  color: #333;
}

.login-card__side--right {
  padding: 48px 56px;
  position: relative;
}

.login-card__close {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  border-radius: 50%;
  transition: all 0.2s;

  &:hover {
    background: #f5f5f5;
    color: #333;
  }
}

.login-card__tabs {
  display: flex;
  gap: 32px;
  margin-bottom: 40px;
}

.tab-btn {
  position: relative;
  padding: 0;
  background: none;
  border: none;
  font-size: 16px;
  color: #999;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    color: #ff2442;
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: -10px;
      left: 50%;
      transform: translateX(-50%);
      width: 40px;
      height: 3px;
      background: #ff2442;
      border-radius: 2px;
    }
  }

  &:hover:not(.active) {
    color: #666;
  }
}

.login-card__content {
  flex: 1;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  position: relative;
  width: 100%;

  &--has-suffix {
    display: flex;
    gap: 12px;

    .input {
      flex: 1;
    }
  }
}

.input {
  width: 100%;
  height: 50px;
  padding: 0 20px;
  border: 1.5px solid rgba(0, 0, 0, 0.06);
  background: #fafafa;
  border-radius: 999px;
  font-size: 15px;
  color: #333;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;

  &::placeholder {
    color: #bbb;
  }

  &:focus {
    border-color: #ff2442;
    background: #fff;
    box-shadow: 0 0 0 4px rgba(255, 36, 66, 0.08);
  }
}

.input-suffix-btn {
  flex-shrink: 0;
  height: 50px;
  padding: 0 16px;
  background: #fff5f6;
  border: 1.5px solid #ffd7dc;
  border-radius: 999px;
  font-size: 14px;
  color: #ff2442;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  white-space: nowrap;

  &:not(:disabled):hover {
    background: #ff2442;
    color: #fff;
    border-color: #ff2442;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.error-msg {
  font-size: 13px;
  color: #ff2442;
  text-align: center;
  padding-top: 4px;
}

.submit-btn {
  margin-top: 24px;
  height: 50px;
  background: linear-gradient(135deg, #ff2442 0%, #ff4d56 100%);
  color: #fff;
  border: none;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 8px 20px rgba(255, 36, 66, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 28px rgba(255, 36, 66, 0.35);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 4px 12px rgba(255, 36, 66, 0.25);
  }
}

.login-card__agreement {
  margin-top: auto;
  padding-top: 24px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.checkbox-box {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border: 1.5px solid #ddd;
  border-radius: 4px;
  position: relative;
  transition: all 0.2s;

  &::after {
    content: '';
    position: absolute;
    top: 4px;
    left: 6px;
    width: 4px;
    height: 8px;
    border: solid #fff;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg) scale(0);
    transition: transform 0.2s;
  }
}

.checkbox-input:checked + .checkbox-box {
  border-color: #ff2442;
  background: #ff2442;

  &::after {
    transform: rotate(45deg) scale(1);
  }
}

.agreement-text {
  font-size: 12px;
  color: #999;
}

.agreement-links {
  margin-top: 8px;
  margin-left: 26px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #13386c;
}
</style>
