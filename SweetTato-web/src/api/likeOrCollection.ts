import request from "@/utils/request";
import type { LikeOrCollectionDTO } from "@/type/likeOrCollection";
/**
 * 点赞或收藏
 * @param data 点赞收藏实体
 * @returns success
 */
export const likeOrCollectionByDTO = (data: LikeOrCollectionDTO) => {
  const body = {
    entityId: data.likeOrCollectionId,
    loveTypeId: data.type === 1 ? 1 : 3,
  };
  return request<any>({
    url: `user/api/Interaction/likes`,
    method: "post",
    data: body,
  });
};

/**
 * 是否点赞或收藏
 * @param data 点赞收藏实体
 * @returns 
 */
export const isLikeOrCollection = (data: LikeOrCollectionDTO) => {
  return request<any>({
    url: `user/api/Interaction/is-liked`,
    method: "get",
    params: {
      entityId: data.likeOrCollectionId,
      loveTypeId: data.type === 1 ? 1 : 3,
    },
  });
};

/**
 * 获取赞和收藏通知
 * @param currentPage 当前页
 * @param pageSize 每页大小
 */
export const getNoticeLikeOrCollection = (currentPage: number, pageSize: number) => {
  return request<any>({
    url: `/api/likeOrCollection/notice/${currentPage}/${pageSize}`,
    method: "get",
  });
};


