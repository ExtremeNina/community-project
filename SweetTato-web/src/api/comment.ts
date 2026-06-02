import request from "@/utils/request";
import type { CommentDTO } from "@/type/comment";

/**
 * 保存评论
 * @param data 评论实体
 * @returns 增加后的评论实体
 */
export const saveCommentByDTO = (data: CommentDTO) => {
  return request<any>({
    url: `/api/comment/publish`,
    method: "post",
    data: {
      articleId: data.nid,
      parentId: data.pid || 0,
      content: data.content,
    },
  });
};

// ============================================================
// /web/ 路径遗留请求已注释
// ============================================================

// export const syncCommentByIds = (data: Array<string>) => {
//   return request<any>({
//     url: `/web/comment/syncCommentByIds`,
//     method: "post",
//     data: data,
//   });
// };

// export const getTwoCommentPageByOneCommentId = (
//   currentPage: number,
//   pageSize: number,
//   oneCommentId: string
// ) => {
//   return request<any>({
//     url: `/web/comment/getTwoCommentPageByOneCommentId/${currentPage}/${pageSize}`,
//     method: "get",
//     params: { oneCommentId },
//   });
// };

/**
 * 获取评论通知
 * @param currentPage 当前页
 * @param pageSize 每页大小
 */
export const getNoticeComment = (
  currentPage: number,
  pageSize: number,
) => {
  return request<any>({
    url: `/api/comment/notice/${currentPage}/${pageSize}`,
    method: "get",
  });
};