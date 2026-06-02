import request from "@/utils/request";

// ============================================================
// 图片上传
// ============================================================
export const uploadImage = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return request<any>({
    url: "/api/create-center/upload/image",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
};

// ============================================================
// 删除图片
// ============================================================
export const deleteImage = (imageUrl: string) => {
  return request<any>({
    url: "/api/create-center/upload/image/delete",
    method: "post",
    params: { imageUrl },
  });
};

// ============================================================
// 发布文章
// ============================================================
export const publishArticle = (data: any) => {
  return request<any>({
    url: "/api/create-center",
    method: "post",
    data,
  });
};

export const getDraftArticles = () => {
  return request<any>({
    url: "/api/create-center/drafts",
    method: "get",
  });
};

export const getPendingArticles = () => {
  return request<any>({
    url: "/api/create-center/pending",
    method: "get",
  });
};

export const getPublishedArticles = () => {
  return request<any>({
    url: "/api/create-center/published",
    method: "get",
  });
};

export const getRejectedArticles = () => {
  return request<any>({
    url: "/api/create-center/rejected",
    method: "get",
  });
};

export const getAllArticles = () => {
  return request<any>({
    url: "/api/create-center/all",
    method: "get",
  });
};

export const searchByTitle = (title: string, status: number = 1) => {
  return request<any>({
    url: "/api/create-center/by-title",
    method: "get",
    params: { title, status },
  });
};

export const searchByDate = (date: string, status: number = 1) => {
  return request<any>({
    url: "/api/create-center/by-date",
    method: "get",
    params: { date, status },
  });
};

// ============================================================
// 通用编辑回显接口（替代原 /web/category/getCategoryTreeData）
// 支持草稿(1)/待审核(2)/已发布(3)三种状态
// ============================================================
export const getArticleForEdit = (articleId: string) => {
  return request<any>({
    url: `/api/create-center/edit/${articleId}`,
    method: "get",
  });
};

// ============================================================
// 获取所有分类列表（替代原 /web/category/getCategoryTreeData）
// ============================================================
export const getAllCategories = () => {
  return request<any>({
    url: "/api/create-center/categories",
    method: "get",
  });
};