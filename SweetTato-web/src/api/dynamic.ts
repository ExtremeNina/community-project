import request from "@/utils/request";

/**
 * 获取最新动态文章
 * @param currentPage 当前页
 * @param pageSize 每页大小
 */
export const getNewArticles = (currentPage: number, pageSize: number) => {
  return request<any>({
    url: `/api/dynamic/newArticles`,
    method: "get",
    params: { currentPage, pageSize },
  });
};