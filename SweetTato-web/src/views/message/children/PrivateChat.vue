<template>
  <el-drawer
    v-model="visible"
    direction="rtl"
    size="600px"
    :with-header="false"
    @close="handleClose"
  >
    <div class="private-chat">
      <!-- 头部 -->
      <header class="chat-header">
        <div class="header-left" @click="handleClose">
          <DArrowRight style="width: 1.2em; height: 1.2em; cursor: pointer" />
        </div>
        <div class="header-user">
          <el-avatar :src="friend.icon" :size="36" />
          <span class="header-name">{{ friend.username }}</span>
        </div>
        <div class="header-right"></div>
      </header>

      <div class="chat-divider"></div>

      <!-- 聊天记录 -->
      <main class="chat-main" ref="chatBodyRef">
        <div class="chat-record">
          <div v-for="(item, index) in chatList" :key="index" class="msg-wrapper">
            <!-- 对方消息 -->
            <div v-if="!item.isOwn" class="message-other">
              <el-avatar :src="friend.icon" :size="36" class="msg-avatar" />
              <div class="msg-body">
                <div class="msg-content">{{ item.content }}</div>
                <span class="msg-time">{{ item.time }}</span>
              </div>
            </div>
            <!-- 自己的消息 -->
            <div v-else class="message-self">
              <div class="msg-body">
                <div class="msg-content">{{ item.content }}</div>
                <span class="msg-time">{{ item.time }}</span>
              </div>
              <el-avatar :src="myIcon" :size="36" class="msg-avatar" />
            </div>
          </div>
        </div>
      </main>

      <!-- 输入区域 -->
      <footer class="chat-footer">
        <div class="chat-divider"></div>
        <div class="input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            resize="none"
            @keydown.enter.exact.prevent="sendMessage"
          />
          <el-button type="primary" size="small" class="send-btn" @click="sendMessage">发送</el-button>
        </div>
      </footer>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, onUnmounted } from "vue";
import { DArrowRight } from "@element-plus/icons-vue";
import { getPrivateChatHistory } from "@/api/im";
import { useUserStore } from "@/store/userStore";
import { useImStore } from "@/store/imStore";

const props = defineProps<{
  modelValue: boolean;
  friend: {
    userId: string;
    username: string;
    icon: string;
  };
}>();

const emit = defineEmits<{
  (e: "update:modelValue", val: boolean): void;
  (e: "sendWsMessage", message: string): void;
}>();

const userStore = useUserStore();
const imStore = useImStore();
const visible = ref(props.modelValue);
const chatList = ref<Array<any>>([]);
const inputText = ref("");
const chatBodyRef = ref<HTMLElement | null>(null);

const myIcon = userStore.getUserInfo()?.avatar || "";
const currentUserId = userStore.getUserInfo()?.id || "";

watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
    if (val) {
      fetchHistory();
    }
  }
);

watch(visible, (val) => {
  emit("update:modelValue", val);
});

// 监听WebSocket推送的私聊消息
watch(() => imStore.privateMessage, (msg: any) => {
  if (!msg) return;
  const friendId = props.friend.userId;
  if (msg.senderId === Number(friendId) || msg.senderId === Number(currentUserId)) {
    const exists = chatList.value.some(item => 
      item.content === msg.content && item.time === msg.dateTime
    );
    if (!exists) {
      chatList.value.push({
        isOwn: msg.senderId === Number(currentUserId),
        content: msg.content,
        time: msg.dateTime,
      });
      scrollToBottom();
    }
  }
});

const fetchHistory = () => {
  if (!props.friend.userId) return;
  getPrivateChatHistory(props.friend.userId).then((res) => {
    chatList.value = res.data || [];
    scrollToBottom();
  });
};

const sendMessage = () => {
  const text = inputText.value.trim();
  if (!text) return;
  const message = {
    type: "text",
    receiveId: Number(props.friend.userId),
    receiveName: props.friend.username,
    senderId: Number(currentUserId),
    senderIcon: myIcon,
    content: text,
  };
  emit("sendWsMessage", JSON.stringify(message));
  chatList.value.push({
    isOwn: true,
    content: text,
    time: "刚刚",
  });
  inputText.value = "";
  scrollToBottom();
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
  });
};

const handleClose = () => {
  emit("sendWsMessage", JSON.stringify({
    type: "leave_chat",
    receiveId: Number(props.friend.userId),
    receiveName: props.friend.username,
    senderId: Number(currentUserId),
    senderIcon: myIcon,
    content: "",
  }));
  visible.value = false;
};

onUnmounted(() => {
  handleClose();
});
</script>

<style lang="less" scoped>
.private-chat {
  display: flex;
  flex-direction: column;
  height: 100%;

  .chat-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    flex-shrink: 0;

    .header-left {
      cursor: pointer;
      display: flex;
      align-items: center;
    }

    .header-user {
      display: flex;
      align-items: center;
      gap: 10px;

      .header-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    .header-right {
      width: 20px;
    }
  }

  .chat-divider {
    height: 1px;
    background-color: #f0f0f0;
    flex-shrink: 0;
  }

  .chat-main {
    flex: 1;
    overflow-y: auto;
    padding: 16px 20px;

    .chat-record {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .msg-wrapper {
      .message-other {
        display: flex;
        align-items: flex-start;
        gap: 10px;

        .msg-avatar {
          flex-shrink: 0;
        }

        .msg-body {
          max-width: 70%;

          .msg-content {
            background: #f5f5f5;
            padding: 10px 14px;
            border-radius: 4px 12px 12px 12px;
            font-size: 14px;
            color: #333;
            line-height: 1.5;
            word-break: break-word;
          }

          .msg-time {
            display: block;
            font-size: 12px;
            color: #999;
            margin-top: 4px;
            text-align: center;
          }
        }
      }

      .message-self {
        display: flex;
        align-items: flex-start;
        justify-content: flex-end;
        gap: 10px;

        .msg-avatar {
          flex-shrink: 0;
        }

        .msg-body {
          max-width: 70%;
          display: flex;
          flex-direction: column;
          align-items: flex-end;

          .msg-content {
            background: #ff2442;
            padding: 10px 14px;
            border-radius: 12px 4px 12px 12px;
            font-size: 14px;
            color: #fff;
            line-height: 1.5;
            word-break: break-word;
          }

          .msg-time {
            display: block;
            font-size: 12px;
            color: #999;
            margin-top: 4px;
            text-align: center;
          }
        }
      }
    }
  }

  .chat-footer {
    flex-shrink: 0;

    .input-area {
      padding: 12px 20px;
      display: flex;
      gap: 10px;
      align-items: flex-end;

      :deep(.el-textarea__inner) {
        font-size: 14px;
      }

      .send-btn {
        flex-shrink: 0;
        margin-bottom: 2px;
      }
    }
  }
}
</style>