import { defineStore } from "pinia";
import { ref } from "vue";
import { store } from "@/store";

// 使用setup模式定义
export const imStore = defineStore("imStore", () => {
  const userList = ref<Array<any>>([]);

  const message = ref<any>({});

  const countMessage = ref({
    chatCount: 0,
    likeOrCollectionCount: 0,
    commentCount: 0,
    followCount: 0,
  });

  const privateMessage = ref<any>(null);

  const moderationResult = ref<any>(null);


  const setUserList = (data: Array<any>) => {
    userList.value = data;
  };

  const setCountMessage = (data: any) => {
    countMessage.value = data;
  };

  const setMessage = (data: any) => {
    message.value = data;
  };

  const setPrivateMessage = (data: any) => {
    privateMessage.value = data;
  };

  const setModerationResult = (data: any) => {
    moderationResult.value = data;
  };

  return { userList, countMessage, message, privateMessage, moderationResult, setUserList, setCountMessage, setMessage, setPrivateMessage, setModerationResult };
});

export function useImStore() {
  return imStore(store);
}
