<template>
  <div class="oauth-callback-container">
    <div class="loading-spinner"></div>
    <p class="loading-text">GitHub 登录中，请稍候...</p>
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { storage } from "@/utils/storage";
import { getUserAvatar } from "@/api/user";
import { useUserStore } from "@/store/userStore";

const router = useRouter();
const userStore = useUserStore();

onMounted(() => {
  const params = new URLSearchParams(window.location.search);
  const token = params.get("token");

  if (!token) {
    router.push("/login");
    return;
  }

  storage.set("accessToken", token);
  storage.set("refreshToken", token);

  getUserAvatar()
    .then((avatarRes: any) => {
      const avatarData = avatarRes.data;
      const currentUser: any = {
        id: String(avatarData.id || ''),
        avatar: avatarData.icon || '',
      };
      userStore.setUserInfo(currentUser);
      router.push({ path: "/", query: { date: Date.now() } });
    })
    .catch(() => {
      router.push("/login");
    });
});
</script>

<style scoped>
.oauth-callback-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #fff;
}
.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ff2442;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.loading-text {
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}
</style>