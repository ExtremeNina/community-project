<template>
  <div class="feeds-container" v-infinite-scroll="loadMoreData" :infinite-scroll-distance="50">
    <Waterfall
      :list="noteList"
      :width="options.width"
      :gutter="options.gutter"
      :hasAroundGutter="options.hasAroundGutter"
      :animation-effect="options.animationEffect"
      :animation-duration="options.animationDuration"
      :animation-delay="options.animationDelay"
      :breakpoints="options.breakpoints"
      style="min-width: 740px"
    >
      <template #item="{ item }">
        <el-skeleton style="width: 240px" :loading="!item.isLoading" animated>
          <template #template>
            <el-image
              :src="item.noteCover"
              :style="{
                width: '240px',
                maxHeight: '300px',
                height: item.noteCoverHeight + 'px',
                borderRadius: '8px',
              }"
              @load="handleLoad(item)"
            >
            </el-image>

            <div style="padding: 14px">
              <el-skeleton-item variant="h3" style="width: 100%" />
              <div style="display: flex; align-items: center; margin-top: 2px; height: 16px">
                <el-skeleton style="--el-skeleton-circle-size: 20px">
                  <template #template>
                    <el-skeleton-item variant="circle" />
                  </template>
                </el-skeleton>
                <el-skeleton-item variant="text" style="margin-left: 10px" />
              </div>
            </div>
          </template>

          <template #default>
            <div class="card" style="max-width: 240px">
              <div class="image-container">
                <el-image
                  :src="item.noteCover"
                  :style="{
                    width: '240px',
                    maxHeight: '300px',
                    height: item.noteCoverHeight + 'px',
                    borderRadius: '8px',
                  }"
                  fit="cover"
                  @click="toMain(item.id)"
                >
                </el-image>
                <div v-if="item.auditStatus === '2'" class="overlay">审核中</div>
                <div v-if="item.auditStatus === '1'" class="overlay draft">草稿</div>
                <div v-if="item.auditStatus === '0'" class="overlay rejected">未通过⚠️</div>
              </div>
              <div class="footer">
                <a class="title">
                  <span>{{ item.title }}</span>
                </a>
                <div class="author-wrapper">
                  <a class="author">
                    <img class="author-avatar" :src="item.avatar" />
                    <span class="name">{{ item.username }}</span>
                  </a>
                  <span class="like-wrapper like-active">
                    <i
                      class="iconfont icon-follow-fill"
                      :style="{ width: '1em', height: '1em', color: item.isLike ? 'red' : 'black' }"
                      v-if="item.isLike"
                    >
                    </i>
                    <i class="iconfont icon-follow" style="width: 1em; height: 1em" v-else></i>
                    <span class="count">{{ item.likeCount }}</span>
                  </span>
                </div>
                </div>
            </div>
          </template>
        </el-skeleton>
      </template>
    </Waterfall>
  </div>

  <Main
    v-show="mainShow"
    :nid="nid"
    :nowTime="new Date()"
    class="animate__animated animate__zoomIn animate__delay-0.5s"
    @click-main="close"
  ></Main>
</template>

<script lang="ts" setup>
import { Waterfall } from "vue-waterfall-plugin-next";
import "vue-waterfall-plugin-next/dist/style.css";
import { ref, onMounted, watch } from "vue";
import { getTrendByUser } from "@/api/user";
import { getAllArticles, searchByTitle, searchByDate } from "@/api/createCenter";
import Main from "@/views/main/main.vue";
import { options } from "@/constant/constant";
import { useRoute } from "vue-router";
import { useUserStore } from "@/store/userStore";
const route = useRoute();

const props = defineProps({
  type: {
    type: Number,
    default: 1,
  },
  articleStatus: {
    type: String,
    default: "",
  },
  searchKeyword: {
    type: String,
    default: "",
  },
  searchDate: {
    type: String,
    default: "",
  },
});

watch(
  () => [props.type, props.articleStatus],
  ([newType, newStatus]) => {
    currentPage.value = 1;
    noteList.value = [] as Array<any>;
    if (props.searchKeyword || props.searchDate) return;
    if (newStatus !== undefined) {
      getArticleListByStatus(newStatus as string);
    } else {
      getNoteList(newType as number);
    }
  }
);

watch(
  () => [props.searchKeyword, props.searchDate],
  ([newKeyword, newDate]) => {
    if (!newKeyword && !newDate) return;
    currentPage.value = 1;
    noteList.value = [] as Array<any>;
    const status = props.articleStatus ? parseInt(props.articleStatus) : 1;
    if (newKeyword) {
      searchByTitle(newKeyword, status).then((res) => {
        noteList.value = (res.data || []).map(mapArticleToNoteItem);
      });
    } else if (newDate) {
      searchByDate(newDate, status).then((res) => {
        noteList.value = (res.data || []).map(mapArticleToNoteItem);
      });
    }
  }
);

const noteList = ref<Array<any>>([]);
const noteTotal = ref(0);
const uid = route.query.uid as string;
const currentPage = ref(1);
const pageSize = 10;
const nid = ref("");
const mainShow = ref(false);
const userStore = useUserStore();
const currentUid = userStore.getUserInfo().id;
const currentUser = userStore.getUserInfo();

const handleLoad = (item: any) => {
  item.isLoading = true;
};

const close = () => {
  mainShow.value = false;
};

const toMain = (noteId: string) => {
  nid.value = noteId;
  mainShow.value = true;
};

const mapArticleToNoteItem = (article: any) => {
  const status = String(article.status);
  return {
    id: String(article.id),
    title: article.title,
    noteCover: article.noteCover || article.coverImageUrl || '',
    coverImageUrl: article.noteCover || article.coverImageUrl || '',
    avatar: article.avatar || currentUser.avatar || '',
    username: article.username || currentUser.username || '',
    likeCount: article.likeCount || article.loveCount || 0,
    count: article.viewCount || article.count || 0,
    isLike: false,
    auditStatus: status,
    pinned: '0',
    isLoading: true,
    noteCoverHeight: 280,
    labels: article.labels || [],
    publishTime: article.publishTime || '',
  };
};

const getArticleListByStatus = (status: string) => {
  getAllArticles().then((res) => {
    let articles = res.data || [];
    if (status !== "") {
      articles = articles.filter((a: any) => String(a.status) === status);
    }
    noteList.value = articles.map(mapArticleToNoteItem);
  });
};

const setData = (res: any) => {
  const { records, total } = res.data;
  noteTotal.value = total;
  const filteredRecords = records.filter((item: any) => {
    return item.uid === currentUid || (item.auditStatus !== "0" && item.auditStatus !== "2");
  });
  noteList.value.push(...filteredRecords);
};

const getNoteList = (type: number) => {
  getTrendByUser(currentPage.value, pageSize, uid, type).then((res: any) => {
    setData(res);
  });
};

const loadMoreData = () => {
  currentPage.value += 1;
  if (props.type === 1) {
    getArticleListByStatus(props.articleStatus);
  } else {
    getNoteList(props.type);
  }
};

const initData = () => {
  if (props.type === 1) {
    getArticleListByStatus(props.articleStatus);
  } else {
    getNoteList(1);
  }
};

onMounted(() => {
  initData();
});
</script>

<style lang="less" scoped>
.image-container {
  position: relative;
  display: inline-block;
}
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 20px;
  z-index: 2;
  pointer-events: none;
}
.overlay.rejected {
  color: #ff4757;
  background: rgba(0, 0, 0, 0.4);
}
.overlay.draft {
  color: #ffa502;
  background: rgba(0, 0, 0, 0.4);
}
.labels-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin: 4px 12px 0;
}
.label-tag {
  font-size: 11px;
  color: #999;
  background: #f5f5f5;
  padding: 1px 6px;
  border-radius: 4px;
  line-height: 18px;
}
.time-row {
  font-size: 11px;
  color: #bbb;
  margin: 2px 12px 0;
  line-height: 16px;
}
.feeds-container {
  position: relative;
  transition: width 0.5s;
  margin: 0 auto;

  .noteImg {
    width: 240px;
    max-height: 300px;
    object-fit: cover;
    border-radius: 8px;
  }

  .card {
    position: relative;
  }

  .footer {
    padding: 12px;

    .title {
      margin-bottom: 8px;
      word-break: break-all;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
      font-weight: 500;
      font-size: 14px;
      line-height: 140%;
      color: #333;
    }

    .author-wrapper {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 20px;
      color: rgba(51, 51, 51, 0.8);
      font-size: 12px;
      transition: color 1s;

      .author {
        display: flex;
        align-items: center;
        color: inherit;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-right: 12px;

        .author-avatar {
          margin-right: 6px;
          width: 20px;
          height: 20px;
          border-radius: 20px;
          border: 1px solid rgba(0, 0, 0, 0.08);
          flex-shrink: 0;
          object-fit: cover;
        }

        .name {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .like-wrapper {
        position: relative;
        cursor: pointer;
        display: flex;
        align-items: center;

        .count {
          margin-left: 2px;
        }
      }
    }
  }
}
</style>
