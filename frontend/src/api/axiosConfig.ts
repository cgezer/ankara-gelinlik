// src/api/axiosConfig.ts
import axios from "axios";

export let backendAvailable = false;
const IS_PROD = false;
const BASE_URL = IS_PROD ? "https://api.meyda.com" : "";

const api = axios.create({
  baseURL: BASE_URL || undefined,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

let logoutHandler = () => {
  window.location.href = "/login";
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error?.response?.status;

    if (!backendAvailable) return Promise.reject(error);

    if (status === 401 && originalRequest.url !== "/auth/refresh" && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        await api.post("/auth/refresh", {}, { withCredentials: true });
        return api(originalRequest);
      } catch {
        logoutHandler();
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  }
);

export const setLogoutHandler = (fn: () => void) => {
  logoutHandler = fn;
};

export const checkBackend = async () => {
  try {
    await api.get("/auth/me", { withCredentials: true });
    backendAvailable = true;
  } catch (err: any) {
    const status = err?.response?.status;
    backendAvailable = status === 401 || status === 403;
  }
};

export default api;
