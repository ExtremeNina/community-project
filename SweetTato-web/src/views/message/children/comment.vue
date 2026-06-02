<template>
  <div>
    <ul class="message-container" v-infinite-scroll="loadMore">
      <li class="message-item" v-for="(item, index) in dataList" :key="index">
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
              <span v-if="item.pid === '0'">评论了您的笔记</span>
              <span v-if="item.replyUid === currentUid && item.pid !== '0'">回复了您的评论</span>
              <span v-if="item.replyUid !== currentUid && item.pid !== '0'">回复了{{ item.replyUsername }}的评论</span>
              &nbsp;<span>{{ item.time }}</span>
            </div>
            <div class="interaction-content">
              <span>{{ item.content }}</span>
            </div>
            <div class="quote-info" v-show="item.replyContent !== null">
              {{ item.replyContent }}
            </div>
            <!-- <div class="action">
              <div class="action-reply">
                <ChatRound style="width: 1.2em; height: 1.2em" />
                <div class="action-text">回复</div>
              </div>
              <div class="action-like">
                <i class="iconfont icon-follow" style="color: #333"></i>
              </div>
            </div> -->
          </div>
          <div class="extra" @click="toMain(item.nid)">
            <img class="extra-image" :src="item.noteCover" />
          </div>
        </div>
      </li>
      <!-- <li class="message-item">
        <a class="user-avatar">
          <img
            class="avatar-item"
            src="https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg"
          />
        </a>
        <div class="main">
          <div class="info">
            <div class="user-info">
              <a class>这是名词</a>
            </div>
            <div class="interaction-hint">
              <span>评论了您的笔记&nbsp;</span><span>2021-10-9</span>
            </div>
            <div class="interaction-content">
              <span>这是具体内容</span>
            </div>
            <div class="action">
              <div class="comment-wrapper action-comment">
                <div class="input-wrapper">
                  <textarea
                    rows="1"
                    class="comment-input"
                    type="text"
                    placeholder="回复 你好"
                    style="height: 40px"
                  ></textarea>
                  <div class="input-buttons">
                    <Star style="width: 1.2em; height: 1.2em; margin: 0 6px" />
                    <Star style="width: 1.2em; height: 1.2em; margin: 0 6px" />
                  </div>
                </div>
                <button class="submit">发送</button>
              </div>
              <div class="action-cancel">取消</div>
            </div>
          </div>
          <div class="extra">
            <img
              class="extra-image"
              src="https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg"
            />
          </div>
        </div>
      </li> -->
    </ul>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { useUserStore } from "@/store/userStore";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();
const dataList = ref<Array<any>>([]);
const currentUid = ref("");

const emit = defineEmits(["clickMain"]);

// 假数据
const mockData = [
  {
    uid: "1001",
    nid: "note_001",
    username: "旅行者小新",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    pid: "0",
    content: "拍得太美了！请问这个机位在哪里呀？",
    time: "刚刚",
    noteCover: "https://picsum.photos/seed/dali1/100/100",
  },
  {
    uid: "1002",
    nid: "note_002",
    username: "美食达人Lucy",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    pid: "0",
    content: "看起来好好吃！收藏了下次做",
    time: "5分钟前",
    noteCover: "https://picsum.photos/seed/food1/100/100",
  },
  {
    uid: "1003",
    nid: "note_003",
    username: "数码控阿杰",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    pid: "1",
    replyUid: "",
    replyUsername: "旅行者小新",
    content: "在洱海西岸，导航到才村码头就行",
    time: "8分钟前",
    noteCover: "https://picsum.photos/seed/dali2/100/100",
  },
  {
    uid: "1004",
    nid: "note_004",
    username: "读书笔记",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    pid: "1",
    replyUid: "",
    replyUsername: "旅行者小新",
    content: "同款机位！我也拍了一张",
    time: "30分钟前",
    noteCover: "https://picsum.photos/seed/dali3/100/100",
  },
  {
    uid: "1005",
    nid: "note_005",
    username: "健身教练Max",
    avatar: "https://cube.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
    pid: "0",
    content: "太强了吧，一个月就有这个效果！",
    time: "1小时前",
    noteCover: "https://picsum.photos/seed/fit1/100/100",
  },
  {
    uid: "1006",
    nid: "note_006",
    username: "宠物乐园",
    avatar: "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    pid: "0",
    content: "好可爱的小狗狗，想rua！",
    time: "2小时前",
    noteCover: "https://picsum.photos/seed/dog1/100/100",
  },
];

const loadMore = () => {
  // 假数据已全部加载
};

const toMain = (nid: string) => {
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

const initData = () => {
  currentUid.value = userStore.getUserInfo().id;
  getPageData();
};
initData();
</script>

<style lang="less" scoped>
textarea {
  overflow: auto;
}

.message-container {
  width: 40rem;
  height: 90vh;

  .message-item {
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

        .action {
          display: flex;
          color: rgba(51, 51, 51, 0.8);

          .action-reply {
            cursor: pointer;
            width: 88px;
            height: 40px;
            border: 1px solid rgba(0, 0, 0, 0.08);
            border-radius: 999px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(51, 51, 51, 0.8);

            .action-text {
              margin-left: 4px;
              font-size: 16px;
            }
          }

          .action-like {
            cursor: pointer;
            width: 40px;
            height: 40px;
            margin-left: 12px;
            border: 1px solid rgba(0, 0, 0, 0.08);
            border-radius: 999px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(51, 51, 51, 0.8);
          }

          .action-comment {
            flex-grow: 1;
            width: 100%;

            .input-wrapper {
              height: auto;
              display: flex;
              position: relative;
              width: calc(100% - 70px);
              flex-shrink: 0;
              transition: flex 0.3s;

              .comment-input:placeholder-shown {
                background-image: none;
                padding: 12px 92px 12px 36px;
                background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAMAAABg3Am1AAAANlBMVEUAAAA0NDQyMjIzMzM2NjY2NjYyMjI0NDQ1NTU1NTUzMzM1NTU1NTUzMzM1NTUzMzM1NTU1NTVl84gVAAAAEnRSTlMAmUyGEzlgc2AmfRx9aToKQzCSoXt+AAAAhElEQVRIx+3Uuw6DMAyF4XOcBOdCafv+L9vQkQFyJBak/JOHT7K8GLM7epuHusRhHwP/mejJ77i32CpZh33aD+lDFDzgZFE8+tgUv5BB9NxEb9NPL3i46JvoUUhXPBKZFQ/rTPHI3ZXt8xr12KX055LoAVtXz9kKHprxNMMxXqRvmAn9ACQ7A/tTXYAxAAAAAElFTkSuQmCC);
                background-repeat: no-repeat;
                background-size: 16px 16px;
                background-position: 16px 12px;
                color: rgba(51, 51, 51, 0.3);
              }

              .comment-input {
                padding: 12px 92px 12px 16px;
                width: 100%;
                height: 40px;
                line-height: 16px;
                background: rgba(0, 0, 0, 0.03);
                caret-color: rgba(51, 51, 51, 0.3);
                border-radius: 22px;
                border: none;
                outline: none;
                resize: none;
                color: #333;
              }

              .input-buttons {
                position: absolute;
                right: 0;
                top: 0;
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 92px;
                color: rgba(51, 51, 51, 0.3);
              }
            }

            .submit {
              margin-left: 8px;
              width: 60px;
              height: 40px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: #fff;
              font-weight: 600;
              cursor: pointer;
              flex-shrink: 0;
              background: #3d8af5;
              border-radius: 44px;
              font-size: 16px;
            }
          }

          .action-cancel {
            flex-shrink: 0;
            margin-left: 8px;
            cursor: pointer;
            height: 40px;
            border: 1px solid rgba(0, 0, 0, 0.08);
            padding: 10px 16px;
            border-radius: 999px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 16px;
            color: rgba(51, 51, 51, 0.8);
          }

          .comment-wrapper {
            display: flex;
            font-size: 16px;
            overflow: hidden;
          }
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
