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
              <span v-if="item.type == 1">赞了你的笔记</span>
              <span v-if="item.type == 2">赞了你的评论</span>
              <span v-if="item.type == 3">收藏你的笔记</span>
              <span v-if="item.type == 4">赞了你的{{ item.content }}专辑</span>
              &nbsp;<span>{{ item.time }}</span>
            </div>
            <!-- <div class="interaction-content">
              <span>这是具体内容</span>
            </div> -->
            <div class="quote-info" v-if="item.type == 2">{{ item.content }}</div>
          </div>
          <div class="extra" @click="toPage(item.itemId)">
            <img class="extra-image" :src="item.itemCover" />
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

const emit = defineEmits(["clickMain"]);

const dataList = ref<Array<any>>([]);

// 假数据
const mockData = [
  {
    uid: "1001",
    itemId: "note_001",
    username: "旅行者小新",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    type: 1,
    time: "刚刚",
    itemCover: "https://picsum.photos/seed/dali1/100/100",
  },
  {
    uid: "1002",
    itemId: "note_002",
    username: "美食达人Lucy",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    type: 3,
    time: "5分钟前",
    itemCover: "https://picsum.photos/seed/food1/100/100",
  },
  {
    uid: "1003",
    itemId: "note_003",
    username: "数码控阿杰",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    type: 1,
    time: "12分钟前",
    itemCover: "https://picsum.photos/seed/keyboard1/100/100",
  },
  {
    uid: "1004",
    itemId: "note_004",
    username: "读书笔记",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    type: 2,
    content: "说得太好了！我也超爱这本书",
    time: "1小时前",
    itemCover: "https://picsum.photos/seed/book1/100/100",
  },
  {
    uid: "1005",
    itemId: "note_005",
    username: "健身教练Max",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    type: 1,
    time: "2小时前",
    itemCover: "https://picsum.photos/seed/fit1/100/100",
  },
  {
    uid: "1006",
    itemId: "note_006",
    username: "宠物乐园",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    type: 3,
    time: "3小时前",
    itemCover: "https://picsum.photos/seed/dog1/100/100",
  },
];

const toPage = (nid: string) => {
  emit("clickMain", nid);
};

const toUser = (uid: string) => {
  router.push({ name: "user", query: { uid: uid } });
};

const getPageData = () => {
  setTimeout(() => {
    dataList.value = mockData;
  }, 200);
};

const loadMore = () => {
  // 假数据已全部加载
};

const initData = () => {
  getPageData();
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

      .extra {
        min-width: 48px;
        flex-shrink: 0;
        margin-left: 24px;

        .extra-image {
          cursor: pointer;
          width: 48px;
          height: 48px;
          border: 1px solid rgba(0, 0, 0, 0.02);
          border-radius: 6px;
          object-fit: cover;
        }
      }
    }
  }
}
</style>
