<template>
  <div class="container" id="container">
    <div v-if="isLogin" class="push-container">
      <div class="header"><span class="header-icon"></span><span class="header-title">发布图文</span></div>
      <div class="img-list">
        <el-upload
          v-model:file-list="fileList"
          action="#"
          list-type="picture-card"
          multiple
          :limit="9"
          :http-request="handleUpload"
          :before-remove="handleRemove"
          :auto-upload="true"
        >
          <el-icon>
            <Plus />
          </el-icon>
        </el-upload>

        <el-dialog v-model="dialogVisible">
          <img w-full :src="dialogImageUrl" alt="Preview Image" />
        </el-dialog>
      </div>
      <el-divider style="margin: 0.75rem; width: 96%" />
      <div class="push-content">
        <el-input
          v-model="note.title"
          maxlength="20"
          show-word-limit
          type="text"
          placeholder="请输入标题"
          class="input-title"
        />
        <el-input
          v-model="note.content"
          maxlength="250"
          show-word-limit
          :autosize="{ minRows: 4, maxRows: 5 }"
          type="textarea"
          placeholder="填写更全面的描述信息，让更多的人看到你吧❤️"
        />
        <div class="btns">
          <button class="css-fm44j css-osq2ks dyn">
            <span class="btn-content" @click="addTag"># 话题</span></button
          ><button class="css-fm44j css-osq2ks dyn">
            <span class="btn-content"><span>@</span> 用户</span></button
          ><button class="css-fm44j css-osq2ks dyn">
            <span class="btn-content">
              <div class="smile"></div>
              表情
            </span>
          </button>
        </div>
        <div class="tag-list">
          <el-tag
            v-for="tag in dynamicTags"
            :key="tag.id"
            closable
            :disable-transitions="false"
            @close="handleClose(tag)"
            class="tag-item"
            type="danger"
          >
            {{ tag.name }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            style="width: 3.125rem"
            size="small"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputBlur"
          />
          <el-button
            v-else
            type="warning"
            size="small"
            @click="showInput"
            plain
            id="tagContainer"
            :disabled="dynamicTags.length >= 5"
          >
            + 标签
          </el-button>
        </div>
        <div class="hot-tag">
          <span class="tag-title-text">推荐标签：</span>
          <el-tag
            v-for="tag in hotTags"
            :key="tag.id"
            class="hot-tag-item"
            type="warning"
            @click="selectHotTag(tag)"
            :disabled="dynamicTags.length >= 5"
          >
            {{ tag.name }}
          </el-tag>
        </div>
      </div>
      <el-divider style="margin: 0.75rem; width: 96%" />
      <div class="categorys">
        <el-cascader
          ref="CascaderRef"
          v-model="categoryList"
          :options="options"
          @change="handleChange"
          :props="props"
          placeholder="请选择分类"
        />
      </div>
      <div class="submit">
        <el-button type="danger" loading :disabled="true" v-if="pushLoading">发布</el-button>
        <button class="publishBtn" @click="pubslish()" v-else>
          <span class="btn-content">发布</span>
        </button>
        <button class="clearBtn">
          <span class="btn-content" @click="resetData">取消</span>
        </button>
      </div>
    </div>
    <div v-else>
      <el-empty description="用户未登录" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, onMounted } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { useRoute } from "vue-router";
import type { UploadUserFile, CascaderProps, ElInput } from "element-plus";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/userStore";
import { getAllCategories, getArticleForEdit, uploadImage, deleteImage, publishArticle } from "@/api/createCenter";
import { getTagByKeyword } from "@/api/tag";
import { getFileFromUrl } from "@/utils/util";
// import Schema from "async-validator";
// import Crop from "@/components/Crop.vue";
const props: CascaderProps = {
  label: "title",
  value: "id",
  checkStrictly: true, // 允许选择父级节点
};
const CascaderRef = ref<any>(null);

// const rules = {
//   title: { required: true, message: "标题不能为空" },
//   content: { required: true, message: "内容不能为空" },
//   category: { required: true, message: "分类不能为空" },
// };
// const validator = new Schema(rules);

const userStore = useUserStore();
const route = useRoute();

const fileList = ref<UploadUserFile[]>([]);

const dialogImageUrl = ref("");
const dialogVisible = ref(false);
const categoryList = ref<Array<any>>([]);
const options = ref<Array<any>>([]);
const note = ref<any>({});
const showTagState = ref(false);
const selectTagList = ref<Array<any>>([]);
const currentPage = ref(1);
const pageSize = 10;
const tagTotal = ref(0);
const pushLoading = ref(false);
const isLogin = ref(false);
const inputValue = ref("");
const dynamicTags = ref<Array<{ id: number; name: string }>>([]);
const inputVisible = ref(false);
const InputRef = ref<InstanceType<typeof ElInput>>();
const hotTags = ref<Array<{ id: number; name: string }>>([]);

const handleClose = (tag: { id: number; name: string }) => {
  const index = dynamicTags.value.findIndex((t) => t.id === tag.id);
  if (index > -1) {
    dynamicTags.value.splice(index, 1);
  }
};

const handleInputBlur = () => {
  inputVisible.value = false;
  // showTagState.value = false;
};

const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    InputRef.value!.input!.focus();
    addTag();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value) {
    dynamicTags.value.push({ id: 0, name: inputValue.value });
  }
  inputVisible.value = false;
  inputValue.value = "";
  showTagState.value = false;
};

watch(
  () => inputValue.value,
  () => {
    addTag();
  }
);

// 监听外部点击
onMounted(() => {
  if (!isLogin.value) {
    return;
  }
  document.getElementById("container")!.addEventListener("click", function (e) {
    var event = e || window.event;
    var target = event.target || (event.srcElement as any);
    // if(target.id == "name") {
    const tagContainer = document.getElementById("tagContainer");
    if (tagContainer == null) return;

    if (tagContainer.contains(target)) {
      console.log("in");
    } else {
      showTagState.value = false;
    }
  });
});

const addTag = () => {
  selectTagList.value = [];
  currentPage.value = 1;
  setData();
  showTagState.value = true;
};

const setData = () => {
  getTagByKeyword(currentPage.value, pageSize, inputValue.value).then((res) => {
    const { records, total } = res.data;
    selectTagList.value.push(...records);
    tagTotal.value = total;
  });
};

const selectHotTag = (val: { id: number; name: string }) => {
  if (dynamicTags.value.length < 5) {
    dynamicTags.value.push(val);
  } else {
    ElMessage.warning("最多只能选择 5 个标签");
  }
};

const handleChange = (ids: Array<any>) => {
  categoryList.value = ids;
  // 选中后关闭下拉框
  CascaderRef.value.togglePopperVisible();
};

const getHotTag = () => {
  getTagByKeyword(1, pageSize, "").then((res) => {
    const { records } = res.data;
    records.forEach((item: any) => {
      hotTags.value.push({ id: item.id, name: item.title || item.name });
    });
  });
};

const findCategoryPath = (categoryId: number) => {
  for (const parent of options.value) {
    if (parent.children) {
      for (const child of parent.children) {
        if (child.id === categoryId) {
          categoryList.value = [parent.id, child.id];
          return;
        }
      }
    }
    if (parent.id === categoryId) {
      categoryList.value = [parent.id];
      return;
    }
  }
};

const getNoteByIdMethod = (noteId: string) => {
  getArticleForEdit(noteId).then((res) => {
    const { data } = res;
    note.value = data;

    if (data.coverImageUrl) {
      const fileName = data.coverImageUrl.substring(data.coverImageUrl.lastIndexOf("/") + 1);
      getFileFromUrl(data.coverImageUrl, fileName).then((res: any) => {
        fileList.value.push({ name: fileName, url: data.coverImageUrl, raw: res });
      });
    }

    if (data.categoryId && options.value.length > 0) {
      findCategoryPath(data.categoryId);
    }

    if (data.label && data.label.length) {
      data.label.forEach((item: any) => {
        const tagId = typeof item === 'object' ? item.id : 0;
        const tagName = typeof item === 'object' ? (item.name || item.title) : item;
        dynamicTags.value.push({ id: tagId, name: tagName });
      });
    }
  });
};

// 自定义上传
const handleUpload = (options: any) => {
  uploadImage(options.file)
    .then((res: any) => {
      // 后端返回 Result.success(imageUrl)，data 是图片 URL 字符串
      const imageUrl = res.data;
      options.file.url = imageUrl;
      options.onSuccess(res, options.file);
      ElMessage.success("图片上传成功");
    })
    .catch((err: any) => {
      options.onError(err);
      ElMessage.error("图片上传失败");
    });
};

// 删除图片
const handleRemove = (file: any) => {
  const url = file.url;
  if (url) {
    deleteImage(url).catch(() => {
      ElMessage.error("图片删除失败");
    });
  }
  return true;
};

// 发布文章
const pubslish = () => {
  // 验证
  const uploadedFiles = fileList.value.filter((f: any) => f.url && f.status === 'success');
  if (uploadedFiles.length <= 0) {
    ElMessage.error("请上传图片");
    return;
  }
  if (!note.value.title) {
    ElMessage.error("请输入标题");
    return;
  }
  if (categoryList.value.length <= 0) {
    ElMessage.error("请选择分类");
    return;
  }

  pushLoading.value = true;

  const articleData: any = {
    title: note.value.title,
    content: note.value.content || '',
    coverImageUrl: uploadedFiles[0].url,
    categoryId: categoryList.value[categoryList.value.length - 1],
    label: dynamicTags.value.map((t) => t.id).filter((id) => id > 0),
    customLabels: dynamicTags.value.filter((t) => t.id === 0).map((t) => t.name),
    summary: note.value.content ? note.value.content.substring(0, 100) : '',
    isCommented: 1,
  };

  // 编辑时带上 id
  if (note.value.id) {
    articleData.id = note.value.id;
  }

  publishArticle(articleData)
    .then(() => {
      resetData();
      ElMessage.success(note.value.id ? "修改成功" : "发布成功，请等待审核结果");
    })
    .catch(() => {
      ElMessage.error(note.value.id ? "修改失败" : "发布失败");
    })
    .finally(() => {
      pushLoading.value = false;
    });
};

const resetData = () => {
  note.value = {};
  categoryList.value = [];
  fileList.value = [];
  pushLoading.value = false;
  dynamicTags.value = [];
};

const initData = () => {
  isLogin.value = userStore.isLogin();
  if (isLogin.value) {
    const noteId = route.query.noteId as string;
    if (noteId !== "" && noteId !== undefined) {
      getNoteByIdMethod(noteId);
    }
    getAllCategories().then((res) => {
      options.value = res.data;
    });
    getHotTag();
  }
};

watch(
  () => options.value,
  (newOptions) => {
    if (newOptions.length > 0 && note.value?.categoryId) {
      findCategoryPath(note.value.categoryId);
    }
  }
);

initData();
</script>

<style lang="less" scoped>
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item.is-success .el-upload-list__item-status-label) {
  display: none;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

a {
  text-decoration: none;
}

.container {
  flex: 1;
  padding-top: 72px;

  .push-container {
    margin-left: 11vw;
    margin-top: 1vw;
    width: 720px;
    border-radius: 8px;
    box-sizing: border-box;
    box-shadow: var(--el-box-shadow-lighter);

    .header {
      padding: 15px 20px;
      line-height: 16px;
      font-size: 16px;
      font-weight: 400;

      .header-icon {
        position: relative;
        top: 2px;
        display: inline-block;
        width: 6px;
        height: 16px;
        background: #3a64ff;

        border-radius: 3px;
        margin-right: 3px;
      }
    }

    .img-list {
      width: 650px;
      margin: auto;
      padding: 6px 6px 6px 6px;
    }

    .push-content {
      padding: 6px 12px 6px 12px;
      position: relative;

      .hot-tag {
        .tag-title-text {
          font-size: 0.875rem;
          color: #484848;
          margin: 0.125rem 0;
        }
        .hot-tag-item {
          cursor: pointer;
          margin: 0.3125rem 0.3125rem 0 0.3125rem;
        }
        :hover {
          transform: scale(1.2); /* 鼠标移入时按钮稍微放大 */
        }
      }

      .tag-list {
        margin: 0.825rem 0;
        .tag-item {
          margin-right: 0.3125rem;
        }
      }

      .scroll-tag-container {
        position: absolute;
        width: 98%;
        background-color: #fff;
        z-index: 99999;
        border: 1px solid #f4f4f4;
        height: 300px;
        overflow: auto;

        .scrollbar-tag-item {
          display: flex;
          align-items: center;
          height: 30px;
          margin: 10px;
          text-align: center;
          border-radius: 4px;
          padding-left: 2px;
          color: #484848;
          font-size: 14px;
        }

        .scrollbar-tag-item:hover {
          background-color: #f8f8f8;
        }
      }

      .input-title {
        margin-bottom: 12px;
        font-size: 15px;
      }

      .input-content {
        font-size: 12px;
      }

      .post-content:empty::before {
        content: attr(placeholder);
        color: #ccc;
        font-size: 14px;
      }

      .post-content {
        cursor: text;
        margin-top: 10px;
        width: 100%;
        min-height: 90px;
        max-height: 300px;
        margin-bottom: 10px;
        background: #fff;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        padding: 6px 12px 22px;
        outline: none;
        overflow-y: auto;
        text-rendering: optimizeLegibility;
        font-size: 14px;
        line-height: 22px;
      }

      .post-content:focus,
      .post-content:hover {
        border: 1px solid #3a64ff;
      }
    }

    .btns {
      padding: 5px 12px 10px 2px;

      button {
        min-width: 62px;
        width: 62px;
        margin: 0 6px 0 0;
        height: 18px;
      }

      .css-fm44j {
        -webkit-font-smoothing: antialiased;
        appearance: none;
        font-family:
          RedNum,
          RedZh,
          RedEn,
          -apple-system;
        vertical-align: middle;
        text-decoration: none;
        border: 1px solid rgb(217, 217, 217);
        outline: none;
        user-select: none;
        cursor: pointer;
        display: inline-flex;
        -webkit-box-pack: center;
        justify-content: center;
        -webkit-box-align: center;
        align-items: center;
        margin-right: 16px;
        border-radius: 4px;
        background-color: white;
        color: rgb(38, 38, 38);
        height: 24px;
        font-size: 12px;
      }
    }

    .categorys {
      padding: 0 12px 10px 12px;
    }

    .submit {
      padding: 10px 12px 10px 12px;
      margin-top: 10px;

      button {
        width: 100px;
        height: 36px;
        font-size: 15px;
        display: inline-block;
        margin-left: 250px;
        margin-bottom: 2px;
        cursor: pointer; /* 显示小手指针 */
        transition:
          background-color 0.3s,
          color 0.3s; /* 添加过渡效果 */
      }
      button:hover {
        transform: scale(1.05); /* 鼠标移入时按钮稍微放大 */
      }

      .publishBtn {
        background-color: #ff2442;
        color: #fff;
        border-radius: 24px;
      }

      .clearBtn {
        border-radius: 24px;
        margin-left: 10px;
        border: 1px solid rgb(217, 217, 217);
      }
    }
  }
}
</style>
