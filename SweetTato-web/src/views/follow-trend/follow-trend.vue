<template>
  <div class="trend-page">
    <div class="trend-sticky-box">
      <div class="reds-sticky">
        <div class="reds-tabs-list">
          <div
            :class="activeTab === 'follow' ? 'reds-tab-item active' : 'reds-tab-item'"
            @click="activeTab = 'follow'"
          >
            <span>关注动态</span>
          </div>
          <div
            :class="activeTab === 'hot' ? 'reds-tab-item active' : 'reds-tab-item'"
            @click="activeTab = 'hot'"
          >
            <span>热门动态</span>
          </div>
        </div>
        <div class="divider"></div>
      </div>
    </div>

    <div class="trend-container" v-infinite-scroll="loadMoreData" :infinite-scroll-distance="50">
      <div class="feeds-loading-top animate__animated animate__zoomIn animate__delay-0.5s" v-show="topLoading">
        <Loading style="width: 1.2em; height: 1.2em"></Loading>
      </div>

      <template v-if="trendTotal > 0">
        <ul class="trend-list">
          <li class="trend-item" v-for="(item, index) in trendData" :key="index">
            <a class="user-avatar">
              <img class="avatar-item" :src="item.avatar" />
            </a>
            <div class="trend-main">
              <div class="trend-info">
                <div class="user-info">
                  <span class="username">{{ item.username }}</span>
                </div>
                <div class="time-hint">
                  <span>{{ item.time }}</span>
                </div>
                <div class="trend-content" @click="toMain(item.nid)">
                  {{ item.content }}
                </div>
                <div class="trend-images" v-if="item.imgUrls && item.imgUrls.length" @click="toMain(item.nid)">
                  <div
                    class="image-box"
                    v-for="(url, i) in item.imgUrls.slice(0, 3)"
                    :key="i"
                    :style="{ width: item.imgUrls.length === 1 ? '100%' : '32%' }"
                  >
                    <el-image
                      :src="url"
                      fit="cover"
                      class="trend-img"
                      :style="{ height: item.imgUrls.length === 1 ? '300px' : '200px' }"
                    ></el-image>
                    <div v-if="i === 2 && item.imgUrls.length > 3" class="img-overlay">
                      <span class="more-num">+{{ item.imgUrls.length - 3 }}</span>
                    </div>
                  </div>
                </div>
                <div class="trend-footer">
                  <div class="footer-item" @click="like(index, item.isLike ? -1 : 1)">
                    <i
                      class="iconfont icon-follow-fill"
                      :style="{ width: '1em', height: '1em', color: item.isLike ? 'red' : 'black' }"
                      v-if="item.isLike"
                    ></i>
                    <i class="iconfont icon-follow" style="width: 1em; height: 1em" v-else></i>
                    <span class="count">{{ item.likeCount }}</span>
                  </div>
                  <div class="footer-item">
                    <ChatRound style="width: 0.9em; height: 0.9em" />
                    <span class="count">{{ item.commentCount }}</span>
                  </div>
                  <div class="footer-item">
                    <More style="width: 1em; height: 1em" />
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
        <div class="feeds-loading" v-show="trendData.length < trendTotal">
          <Loading style="width: 1.2em; height: 1.2em"></Loading>
        </div>
      </template>

      <template v-else>
        <div class="el-empty">
          <div class="el-empty__image">
            <svg viewBox="0 0 79 86" version="1.1" xmlns="http://www.w3.org/2000/svg">
              <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                <g transform="translate(-1268.000000, -535.000000)">
                  <g transform="translate(1268.000000, 535.000000)">
                    <path d="M39.5,86 C61.3152476,86 79,83.9106622 79,81.3333333 C79,78.7560045 57.3152476,78 35.5,78 C13.6847524,78 0,78.7560045 0,81.3333333 C0,83.9106622 17.6847524,86 39.5,86 Z" fill="#f2f3f5"></path>
                    <polygon fill="#dcdde0" transform="translate(27.500000, 51.500000) scale(1, -1) translate(-27.500000, -51.500000) " points="13 58 53 58 42 45 2 45"></polygon>
                    <g transform="translate(34.500000, 31.500000) scale(-1, 1) rotate(-25.000000) translate(-34.500000, -31.500000) translate(7.000000, 10.000000)">
                      <polygon fill="#dcdde0" points="2.84078316e-14 3 18 3 23 7 5 7"></polygon>
                      <polygon fill="#e5e6eb" points="-3.69149156e-15 7 38 7 38 43 -3.69149156e-15 43"></polygon>
                      <rect fill="#e5e6eb" x="38" y="7" width="17" height="36"></rect>
                      <polygon fill="#f2f3f5" points="24 7 41 7 55 -3.63806207e-12 38 -3.63806207e-12"></polygon>
                    </g>
                    <rect fill="#f2f3f5" x="13" y="45" width="40" height="36"></rect>
                    <polygon fill="#f2f3f5" points="62 45 79 45 70 58 53 58"></polygon>
                  </g>
                </g>
              </g>
            </svg>
          </div>
          <div class="el-empty__description"><p>暂无动态～</p></div>
        </div>
      </template>
    </div>

    <FloatingBtn @click-refresh="refresh"></FloatingBtn>

    <Main
      v-show="mainShow"
      :nid="nid"
      :nowTime="new Date()"
      class="animate__animated animate__zoomIn animate__delay-0.5s"
      @click-main="closeDetail"
    ></Main>
  </div>
</template>

<script lang="ts" setup>
import { ChatRound, More, Loading } from "@element-plus/icons-vue";
import { ref } from "vue";
import { getNewArticles } from "@/api/dynamic";
import { formateTime, refreshTab } from "@/utils/util";
import FloatingBtn from "@/components/FloatingBtn.vue";
import Main from "@/views/main/main.vue";

const activeTab = ref("follow");
const trendData = ref<Array<any>>([]);
const trendTotal = ref(0);
const topLoading = ref(false);
const mainShow = ref(false);
const nid = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

const getFollowTrends = () => {
  getNewArticles(currentPage.value, pageSize.value).then((res) => {
    const { records, total } = res.data;
    records.forEach((item: any) => {
      // DyArticleVO 字段映射：id→nid, author→username, userIcon→avatar, publishTime→time,
      // title→content, coverImageUrl→imgUrls, love→likeCount, isLove→isLike, viewCount→viewCount
      trendData.value.push({
        nid: String(item.id),
        username: item.author,
        avatar: item.userIcon,
        time: formateTime(item.publishTime),
        content: item.title,
        imgUrls: item.coverImageUrl ? [item.coverImageUrl] : [],
        likeCount: item.love,
        isLike: item.isLove,
        viewCount: item.viewCount,
        commentCount: 0,
      });
    });
    trendTotal.value = total;
  });
};

const loadMoreData = () => {
  if (trendData.value.length >= trendTotal.value) return;
  currentPage.value += 1;
  getFollowTrends();
};

const toMain = (noteId: string) => {
  nid.value = noteId;
  mainShow.value = true;
};

const closeDetail = (_nid: string, val: any) => {
  const index = trendData.value.findIndex((item) => item.nid === _nid);
  const _data = trendData.value[index];
  if (_data && _data.isLike != val.isLike) {
    _data.isLike = val.isLike;
    _data.likeCount += val.isLike ? 1 : -1;
  }
  if (_data && val.isComment) {
    _data.commentCount += 1;
  }
  mainShow.value = false;
};

const refresh = () => {
  refreshTab(() => {
    topLoading.value = true;
    setTimeout(() => {
      currentPage.value = 1;
      trendData.value = [];
      getFollowTrends();
      topLoading.value = false;
    }, 500);
  });
};

const like = (index: number, val: number) => {
  if (val < 0 && trendData.value[index].likeCount == 0) return;
  trendData.value[index].isLike = val == 1;
  trendData.value[index].likeCount += val;
};

const initData = () => {
  getFollowTrends();
};

initData();
</script>

<style lang="less" scoped>
.trend-page {
  flex: 1;
  padding: 0 24px;
  padding-top: 72px;
  height: 100vh;

  .trend-sticky-box {
    top: 72px;
    position: fixed;
    z-index: 5;
    width: 67%;
    box-sizing: border-box;
    background: hsla(0, 0%, 100%, 0.98);

    .reds-sticky {
      display: flex;
      flex-direction: column;
      padding-top: 16px;
      justify-content: center;

      .reds-tabs-list {
        justify-content: flex-start;
        display: flex;
        flex-wrap: nowrap;
        position: relative;
        font-size: 16px;
        padding: 0 32px;

        .reds-tab-item {
          padding: 0px 16px;
          margin-right: 0px;
          font-size: 16px;
          display: flex;
          align-items: center;
          box-sizing: border-box;
          height: 40px;
          cursor: pointer;
          color: rgba(51, 51, 51, 0.8);
          white-space: nowrap;
          transition: transform 0.3s cubic-bezier(0.2, 0, 0.25, 1);
          z-index: 1;

          &:hover {
            cursor: pointer;
            transform: scale(1.1);
          }

          &.active {
            font-weight: 600;
            color: #333;
            background-color: rgba(0, 0, 0, 0.03);
            border-radius: 20px;
          }
        }
      }

      .divider {
        margin: 4px 8px;
        list-style: none;
        height: 0;
        border: solid rgba(0, 0, 0, 0.08);
        border-width: 1px 0 0;
      }
    }
  }

  .trend-container {
    width: 67%;
    margin: 0 auto;
    padding-top: 88px;

    .feeds-loading-top {
      text-align: center;
      line-height: 6vh;
      height: 6vh;
    }

    .feeds-loading {
      margin: 3vh;
      text-align: center;
    }

    .trend-list {
      .trend-item {
        display: flex;
        flex-direction: row;
        padding: 20px 0;
        max-width: 100%;

        .user-avatar {
          margin-right: 16px;
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

        .trend-main {
          flex: 1;
          min-width: 0;
          padding-bottom: 16px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.08);

          .trend-info {
            .user-info {
              display: flex;
              flex-direction: row;
              align-items: center;

              .username {
                font-size: 15px;
                font-weight: 600;
                color: #333;
                cursor: pointer;

                &:hover {
                  color: rgba(51, 51, 51, 0.8);
                }
              }
            }

            .time-hint {
              font-size: 13px;
              color: rgba(51, 51, 51, 0.6);
              margin-top: 4px;
              margin-bottom: 10px;
            }

            .trend-content {
              font-size: 14px;
              color: #333;
              line-height: 1.5;
              margin-bottom: 12px;
              cursor: pointer;
              white-space: pre-line;
            }

            .trend-images {
              display: flex;
              gap: 8px;
              margin-bottom: 12px;

              .image-box {
                position: relative;
                border-radius: 8px;
                overflow: hidden;
                cursor: pointer;

                .trend-img {
                  width: 100%;
                  display: block;
                  border-radius: 8px;
                }

                .img-overlay {
                  position: absolute;
                  top: 0;
                  left: 0;
                  width: 100%;
                  height: 100%;
                  background-color: rgba(0, 0, 0, 0.4);
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  border-radius: 8px;

                  .more-num {
                    color: #fff;
                    font-size: 20px;
                    font-weight: 500;
                  }
                }
              }
            }

            .trend-footer {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 0 4px;
              max-width: 280px;

              .footer-item {
                display: flex;
                align-items: center;
                color: rgba(51, 51, 51, 0.8);
                cursor: pointer;
                transition: transform 0.2s ease;

                &:hover {
                  transform: scale(1.15);
                }

                .count {
                  margin-left: 4px;
                  font-size: 13px;
                }
              }
            }
          }
        }
      }
    }

    .el-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 80px 0;

      .el-empty__image {
        width: 160px;
        height: 160px;
      }

      .el-empty__description {
        margin-top: 16px;

        p {
          font-size: 14px;
          color: rgba(51, 51, 51, 0.6);
          margin: 0;
        }
      }
    }
  }
}
</style>