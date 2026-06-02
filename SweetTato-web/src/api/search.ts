import request from "@/utils/request";
import { NoteDTO } from "@/type/note"

/**
 * 
 * @param currentPage 
 * @param pageSize 
 * @returns 
 */
export const getRecommendNote = () => {
  return request<any>({
    url: `/api/community/Recommend`,
    method: "get",
  });
};

export const getRecommendFeed = (params: {
  userId?: number;
  offset?: number;
  limit?: number;
}) => {
  return request<any>({
    url: `/api/Recommend/feed`,
    method: "get",
    params,
  });
};

/**
 * 
 * @param currentPage 
 * @param pageSize 
 * @param data 
 * @returns 
 */
export const getNoteByDTO = (data: NoteDTO) => {
  return request<any>({
    url: `/api/search/articles`,
    method: "get",
    params: {
      keyword: data.keyword,
    },
  });
};

export const getCategoryAgg = (data: NoteDTO) => {
  return request<any>({
    url: `/web/es/note/getCategoryAgg`,
    method: "post",
    data: data,
  });
};

export const addRecord = (keyword: string) => {
  return request<any>({
    url: `/web/es/record/addRecord`,
    method: "get",
    params: { keyword }
  });
};