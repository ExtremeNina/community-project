import request from "@/utils/request";

/**
 * 获取关注动态
 * @param currentPage 当前页
 * @param pageSize 每页大小
 */
export const getFollowTrend = (currentPage: number, pageSize: number) => {
  return request<any>({
    url: `/api/follower/trend/${currentPage}/${pageSize}`,
    method: "get",
  });
};

/**
 * 关注用户
 * @param followerId 关注用户id
 * @returns 
 */
export const followById = (followerId: string) => {
  return request<any>({
    url: `/api/Interaction/follow/${followerId}`,
    method: "post",
  });
};

/**
 * 当前用户是否关注
 * @param followerId 关注的用户id
 * @returns 
 */
export const isFollow = (followerId: string) => {
  return request<any>({
    url: `/api/Interaction/follow/is-following/${followerId}`,
    method: "get",
  });
};

/**
 * 获取新增关注通知
 * @param currentPage 当前页
 * @param pageSize 每页大小
 */
export const getNoticeFollower = (currentPage: number, pageSize: number) => {
  return request<any>({
    url: `/api/follower/notice/${currentPage}/${pageSize}`,
    method: "get",
  });
};
