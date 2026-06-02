import request from "@/utils/request";

/**
 *
 * @param data
 * @returns
 */
export const login = (data: any) => {
  return request<any>({
    url: "/api/auth/login",
    method: "post",
    data: {
      username: data.phone || data.username,
      password: data.password,
      deviceId: data.deviceId,
    },
  });
};

/**
 *
 * @param accessToken
 * @returns
 */
export const getUserInfoByToken = () => {
  return request<any>({
    url: "/api/users/me",
    method: "get",
  });
};

/**
 * 登录后获取头像和用户ID（轻量接口）
 * @returns
 */
export const getUserAvatar = () => {
  return request<any>({
    url: "/api/community/user/avatar",
    method: "get",
  });
};

/**
 *
 * @param refreshToken
 * @returns
 */
export const refreshToken = (refreshToken: string) => {
  return request<any>({
    url: `/api/auth/refresh`,
    method: "post",
    data: { refreshToken },
  });
};

export const getTrendByUser = (currentPage:number,pageSize:number,userId:string,type:number) => {
  return request<any>({
    url: `/web/user/getTrendByUser/${currentPage}/${pageSize}`,
    method: "get",
    params: { userId, type },
  });
};

/**
 *
 * @param userId
 * @returns
 */
export const getUserById = (userId: string) => {
  return request<any>({
    url: `/api/users/me/${userId}`,
    method: "get",
  });
};

/**
 *
 * @param userId
 * @returns
 */
export const loginOut = (userId:string) => {
  return request<any>({
    url: `/api/auth/loginOut`,
    method: "post",
    params: {
      userId
    },
  });
};

export const updateUser = (data: any) => {
  return request<any>({
    url: "/api/users/me",
    method: "put",
    data,
  });
};

export const qqSendCode = (email: string) => {
  return request<any>({
    url: "/api/auth/QQ/sendCode",
    method: "post",
    data: { email },
  });
};

export const qqLogin = (mail: string, code: string) => {
  return request<any>({
    url: "/api/auth/QQ/login",
    method: "post",
    data: { mail, code },
  });
};

export const getUserByKeyword = (currentPage: number, pageSize: number, keyword: string) => {
  return request<any>({
    url: `/web/user/getUserByKeyword/${currentPage}/${pageSize}`,
    method: "get",
    params: { keyword },
  });
};

