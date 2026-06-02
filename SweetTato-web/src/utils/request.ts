import axios, { InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { useUserStore } from "@/store/userStore";
import { storage } from "./storage";
import { ElMessage } from "element-plus";

let isRefreshing: boolean = false;
let requestsQueue: ((token: string) => void)[] = [];

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
});

const doRefresh = async (refreshTokenVal: string): Promise<{ accessToken: string; refreshToken: string; expiresIn: number } | null> => {
  try {
    const res = await axios.post(import.meta.env.VITE_APP_BASE_API + '/api/auth/refresh', { refreshToken: refreshTokenVal });
    const tokens = res.data?.data;
    if (tokens?.accessToken && tokens?.refreshToken) {
      storage.set("accessToken", tokens.accessToken);
      storage.set("refreshToken", tokens.refreshToken);
      if (tokens.expiresIn) {
        storage.set("expireAt", Date.now() + tokens.expiresIn * 1000);
      }
      return tokens;
    }
    return null;
  } catch {
    return null;
  }
};

service.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const accessToken = storage.get<string>("accessToken");
    const refreshTokenVal = storage.get<string>("refreshToken");
    const expireAt = storage.get<number>("expireAt");

    if (accessToken && refreshTokenVal && expireAt) {
      const remainingMs = expireAt - Date.now();
      if (remainingMs < 5 * 60 * 1000 && remainingMs > 0) {
        if (!isRefreshing) {
          isRefreshing = true;
          try {
            const tokens = await doRefresh(refreshTokenVal);
            if (tokens) {
              config.headers.Authorization = `Bearer ${tokens.accessToken}`;
              requestsQueue.forEach(cb => cb(tokens.accessToken));
              requestsQueue = [];
            } else {
              if (!config.headers.Authorization) {
                config.headers.Authorization = `Bearer ${accessToken}`;
              }
            }
          } finally {
            isRefreshing = false;
          }
        } else {
          return new Promise<void>((resolve) => {
            requestsQueue.push((token: string) => {
              config.headers.Authorization = `Bearer ${token}`;
              resolve();
            });
          }).then(() => service.request(config));
        }
      } else {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }

      const userInfo = useUserStore().getUserInfo();
      if (userInfo) {
        config.headers.userId = userInfo.id;
      }
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

const handleUnauthorized = async (config: InternalAxiosRequestConfig): Promise<AxiosResponse> => {
  if (isRefreshing) {
    return new Promise((resolve) => {
      requestsQueue.push((token: string) => {
        config.headers.Authorization = `Bearer ${token}`;
        resolve(service.request(config));
      });
    });
  }

  isRefreshing = true;
  try {
    const refreshTokenVal = storage.get<string>("refreshToken");
    if (!refreshTokenVal) {
      throw new Error("No refresh token");
    }
    const tokens = await doRefresh(refreshTokenVal);
    if (tokens) {
      config.headers.Authorization = `Bearer ${tokens.accessToken}`;
      requestsQueue.forEach(cb => cb(tokens.accessToken));
      requestsQueue = [];
      return service.request(config);
    }
    throw new Error("Refresh failed");
  } catch {
    ElMessage.warning("登录过期，请重新登录");
    window.localStorage.clear();
    window.location.href = "/login";
    throw new Error("登录过期");
  } finally {
    isRefreshing = false;
  }
};

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code } = response.data;
    if (code === 1 || code === 200) {
      return response.data;
    }
    if (code === 401 || response.status === 401) {
      return handleUnauthorized(response.config);
    }
    return Promise.reject(response.data);
  },
  async (error: any) => {
    if (error.response && error.response.status === 401) {
      try {
        return await handleUnauthorized(error.config);
      } catch {
        return Promise.reject(error);
      }
    }
    ElMessage.error(error.message || "请求失败");
    return Promise.reject(error);
  }
);

export default service;