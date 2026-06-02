<template>
  <div class="message-page">
    <div class="message-wrapper">
      <ul class="message-container">
        <li class="message-item" v-for="(item, index) in dataList" :key="index" @click="toChat(item)">
          <a class="user-avatar">
            <img class="avatar-item" :src="item.avatar" />
          </a>
          <div class="content-area">
            <div class="username">{{ item.username }}</div>
            <div class="message-text">{{ item.content }}</div>
          </div>
          <div class="time-area">
            <span class="message-time">{{ item.time }}</span>
            <div class="msg-count" v-show="item.count > 0">{{ item.count }}</div>
          </div>
        </li>
      </ul>
    </div>

    <PrivateChat
      v-model="drawerVisible"
      :friend="currentFriend"
      @send-ws-message="handleSendWsMessage"
    />
  </div>
</template>

<script lang="ts" setup>
import { useImStore } from "@/store/imStore";
import { ref, watch, watchEffect } from "vue";
import { getPrivateMessage } from "@/api/im";
import { useUserStore } from "@/store/userStore";
import { formateTime } from "@/utils/util";
import PrivateChat from "@/views/message/children/PrivateChat.vue";

const imStore = useImStore();
const userStore = useUserStore();
const dataList = ref<Array<any>>([]);
const drawerVisible = ref(false);
const currentFriend = ref({
  userId: "",
  username: "",
  icon: "",
});

const fetchPrivateMessages = () => {
  const userInfo = userStore.getUserInfo();
  if (!userInfo || !userInfo.id) return;
  getPrivateMessage(userInfo.id).then((res) => {
    dataList.value = [];
    const _countMessage = imStore.countMessage;
    _countMessage.chatCount = 0;
    (res.data || []).forEach((item: any) => {
      dataList.value.push({
        uid: item.userId,
        username: item.userName,
        avatar: item.icon,
        content: item.lastText,
        time: item.lastTime,
        isOnline: item.isOnline,
        count: 0,
      });
    });
    imStore.setCountMessage(_countMessage);
  }).catch(() => {
    dataList.value = [];
  });
};

watchEffect(() => {
  if (imStore.userList.length > 0) {
    dataList.value = [];
    const _countMessage = imStore.countMessage;
    _countMessage.chatCount = 0;
    imStore.userList.forEach((item: any) => {
      item.time = formateTime(item.timestamp);
      _countMessage.chatCount += item.count;
      dataList.value.push(item);
    });
    imStore.setCountMessage(_countMessage);
  }
});

// 监听私聊消息，实时更新好友列表最后消息
watch(() => imStore.privateMessage, (msg: any) => {
  if (!msg) return;
  const target = dataList.value.find(
    (item) => item.uid === String(msg.senderId)
  );
  if (target) {
    target.content = msg.content;
    target.time = msg.dateTime;
  } else {
    fetchPrivateMessages();
  }
});

const handleSendWsMessage = (msg: string) => {
  if ((window as any).sendWsMessage) {
    (window as any).sendWsMessage(msg);
  }
};

const toChat = (item: any) => {
  currentFriend.value = {
    userId: item.uid,
    username: item.username,
    icon: item.avatar,
  };
  drawerVisible.value = true;
};

fetchPrivateMessages();
</script>

<style lang="less" scoped>
.message-page {
  display: flex;
  justify-content: center;
  width: 100%;
}

.message-wrapper {
  width: 40rem;
  max-width: 100%;

  @media screen and (min-width: 960px) and (max-width: 1191px) {
    width: calc(-36px + 50vw);
  }

  @media screen and (min-width: 1192px) and (max-width: 1423px) {
    width: calc(-33.6px + 40vw);
  }

  @media screen and (min-width: 1424px) and (max-width: 1727px) {
    width: calc(-42.66667px + 33.33333vw);
  }

  @media screen and (min-width: 1728px) {
    width: 533.33333px;
  }
}

.message-container {
  width: 100%;
  list-style: none;
  margin: 0;
  padding: 0;

  .message-item {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    height: 72px;
    padding: 16px 0;
    cursor: pointer;
    background: #FFFFFF;
    position: relative;
    box-sizing: border-box;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 16px;
      right: 16px;
      height: 1px;
      background: rgba(0, 0, 0, 0.06);
    }

    .user-avatar {
      flex-shrink: 0;
      margin-left: 16px;

      .avatar-item {
        width: 40px;
        height: 40px;
        display: block;
        border-radius: 50%;
        border: 1px solid rgba(0, 0, 0, 0.06);
        object-fit: cover;
      }
    }

    .content-area {
      flex-grow: 1;
      flex-shrink: 1;
      margin-left: 12px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      min-width: 0;
      height: 40px;

      .username {
        font-size: 15px;
        font-weight: 500;
        color: #333333;
        line-height: 1.4;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .message-text {
        font-size: 14px;
        font-weight: 400;
        color: #999999;
        line-height: 1.4;
        margin-top: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .time-area {
      flex-shrink: 0;
      margin-right: 16px;
      margin-left: 12px;
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      justify-content: center;
      height: 40px;
      position: relative;

      .message-time {
        font-size: 12px;
        font-weight: 400;
        color: #999999;
        line-height: 1.4;
        white-space: nowrap;
      }

      .msg-count {
        position: absolute;
        top: -2px;
        right: -8px;
        min-width: 18px;
        height: 18px;
        padding: 0 5px;
        line-height: 18px;
        font-size: 12px;
        color: #FFFFFF;
        background: #FF2442;
        border-radius: 50%;
        text-align: center;
        box-sizing: border-box;
      }
    }
  }
}
</style>
