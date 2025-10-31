// src/api/axiosConfig.ts
import axios, { AxiosError } from "axios";
import type { AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { authService } from "../auth/authService";
import { message } from "antd";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

let isRefreshing = false;
let failedQueue: {
  resolve: (value?: unknown) => void;
  reject: (reason?: any) => void;
  config: InternalAxiosRequestConfig;
}[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      if (token && prom.config.headers) {
        prom.config.headers["Authorization"] = `Bearer ${token}`;
      }
      prom.resolve(api(prom.config));
    }
  });
  failedQueue = [];
};

// ✅ Her isteğe token ekle
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = authService.getToken();
    if (token && config.headers) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ Global errorCode → mesaj eşleme tablosu
const errorMessageMap: Record<string, string> = {
  USER_NOT_FOUND: "Kullanıcı bulunamadı.",
  USER_ALREADY_EXISTS: "Bu email adresi zaten kayıtlı.",
  INVALID_INPUT: "Eksik veya hatalı bilgi girdiniz.",
  ACCESS_DENIED: "Bu işlem için yetkiniz yok.",
  SERVER_ERROR: "Sunucu hatası oluştu.",
};

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError<any>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // ✅ Backend hata mesajı gösterme
    const errorCode = error.response?.data?.errorCode;
    const backendMessage = error.response?.data?.message;

    if (errorCode && errorMessageMap[errorCode]) {
      message.error(errorMessageMap[errorCode]); // Kullanıcı dostu mesaj
    } else if (backendMessage) {
      message.error(backendMessage);
    }

    // ✅ Silent refresh mekanizması
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject, config: originalRequest });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const success = await authService.silentRefresh();
        if (!success) throw new Error("Silent refresh başarısız");

        const newToken = authService.getToken();
        processQueue(null, newToken);

        if (originalRequest.headers && newToken) {
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        }

        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        await authService.logout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
