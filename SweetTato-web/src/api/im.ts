import request from "@/utils/request";

export const clearMessageCount = (sendUid: string, type: number) => {
  return request<any>({ url: "/web/im/chat/clearMessageCount", method: "get", params: { sendUid, type } });
};

export const getAllChatRecord = (currentPage: number, pageSize: number, acceptUid: string) => {
  return request<any>({ url: `/web/im/chat/getAllChatRecord/${currentPage}/${pageSize}`, method: "get", params: { acceptUid } });
};

export const sendMsg = (data: any) => {
  return request<any>({ url: "/web/im/chat/sendMsg", method: "post", data: data });
};

/**
 * 获取私信列表
 * @param userId 当前用户ID
 */
export const getPrivateMessage = (userId: string) => {
  return request<any>({
    url: `/api/private/${userId}`,
    method: "get",
  });
};

/**
 * 获取私聊聊天记录
 * @param id 好友用户ID
 */
export const getPrivateChatHistory = (id: string) => {
  return request<any>({
    url: `/api/private/ChatHistory/${id}`,
    method: "get",
  });
};