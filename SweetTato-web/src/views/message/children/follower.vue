<template>
  <div>
    <ul class="agree-container" v-infinite-scroll="loadMore">
      <li class="agree-item" v-for="(item, index) in dataList" :key="index">
        <a class="user-avatar">
          <!-- https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png -->
          <img class="avatar-item" :src="item.avatar" @click="toUser(item.uid)" />
        </a>
        <div class="main">
          <div class="info">
            <div class="user-info">
              <a class>{{ item.username }}</a>
            </div>
            <div class="interaction-hint">
              <span>开始关注你了&nbsp;</span><span>{{ item.time }}</span>
            </div>
          </div>
          <div class="extra">
            <el-button
              class="button"
              type="info"
              round
              size="large"
              v-if="item.isFollow"
              @click="follow(item.uid, index, 1)"
              >互相关注</el-button
            >
            <el-button class="button" type="danger" round size="large" v-else @click="follow(item.uid, index, -1)"
              >回关</el-button
            >
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const dataList = ref<Array<any>>([]);

// 假数据
const mockData = [
  {
    uid: "1001",
    username: "旅行者小新",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    time: "刚刚",
    isFollow: false,
  },
  {
    uid: "1002",
    username: "美食达人Lucy",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    time: "5分钟前",
    isFollow: true,
  },
  {
    uid: "1003",
    username: "数码控阿杰",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    time: "12分钟前",
    isFollow: false,
  },
  {
    uid: "1004",
    username: "读书笔记",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    time: "30分钟前",
    isFollow: false,
  },
  {
    uid: "1005",
    username: "健身教练Max",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    time: "1小时前",
    isFollow: true,
  },
  {
    uid: "1006",
    username: "宠物乐园",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    time: "2小时前",
    isFollow: false,
  },
];

const follow = (_fid: string, index: number, type: number) => {
  dataList.value[index].isFollow = type == -1;
};

const loadMore = () => {
  // 假数据已全部加载
};

const toUser = (uid: string) => {
  router.push({ name: "user", query: { uid: uid } });
};

const initData = () => {
  setTimeout(() => {
    dataList.value = mockData;
  }, 200);
};
initData();
</script>

<style lang="less" scoped>
textarea {
  overflow: auto;
}

.agree-container {
  width: 40rem;
  height: 90vh;

  .agree-item {
    display: flex;
    flex-direction: row;
    padding-top: 24px;

    .user-avatar {
      margin-right: 24px;
      flex-shrink: 0;

      .avatar-item {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        border-radius: 100%;
        border: 1px solid rgba(0, 0, 0, 0.08);
        object-fit: cover;
      }
    }

    .main {
      flex-grow: 1;
      flex-shrink: 1;
      display: flex;
      flex-direction: row;
      padding-bottom: 12px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.08);

      .info {
        flex-grow: 1;
        flex-shrink: 1;

        .user-info {
          display: flex;
          flex-direction: row;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 4px;

          a {
            color: #333;
            font-size: 16px;
            font-weight: 600;
          }
        }

        .interaction-hint {
          font-size: 14px;
          color: rgba(51, 51, 51, 0.6);
          margin-bottom: 8px;
        }

        .interaction-content {
          display: flex;
          font-size: 14px;
          color: #333;
          line-height: 140%;
          cursor: pointer;
          margin-bottom: 12px;

          .msg-count {
            width: 20px;
            height: 20px;
            line-height: 20px;
            font-size: 13px;
            color: #fff;
            background-color: red;
            text-align: center;
            border-radius: 100%;
          }
        }

        .quote-info {
          font-size: 12px;
          display: flex;
          align-items: center;
          color: rgba(51, 51, 51, 0.6);
          margin-bottom: 12px;
          cursor: pointer;
        }

        .quote-info::before {
          content: "";
          display: inline-block;
          border-radius: 8px;
          margin-right: 6px;
          width: 4px;
          height: 17px;
          background: rgba(0, 0, 0, 0.08);
        }
      }

      .button {
        width: 100px;
        height: 40px;
        font-size: 14px;
      }

      .extra {
        min-width: 48px;
        flex-shrink: 0;
        margin-left: 24px;

        .follow-button {
          width: 96px;
        }

        .reds-button-new.large {
          font-size: 16px;
          font-weight: 600;
          line-height: 16px;
          padding: 0 24px;
          height: 40px;
        }

        .reds-button-new.primary {
          background-color: #ff2e4d;
          color: #fff;
        }

        .reds-button-new {
          position: relative;
          cursor: pointer;
          -webkit-user-select: none;
          user-select: none;
          white-space: nowrap;
          outline: none;
          background: none;
          border: none;
          vertical-align: middle;
          text-align: center;
          display: inline-block;
          padding: 0;
          border-radius: 100px;
          font-weight: 500;
        }
      }
    }
  }
}
</style>
