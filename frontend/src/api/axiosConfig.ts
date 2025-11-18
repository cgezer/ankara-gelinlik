// src/api/axiosConfig.ts
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // HTTP-only cookie gönderimi için
  headers: {
    "Content-Type": "application/json",
  },
});

// --- INTERCEPTOR: Token süresi dolduğunda refresh et ---
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 401 ve retry edilmemişse
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      // Eğer /auth/refresh isteği değilse refresh token çağır
      if (!originalRequest.url?.includes("/auth/refresh")) {
        try {
          await api.post("/auth/refresh", {}, { withCredentials: true });
          return api(originalRequest); // orijinal isteği tekrar dene
        } catch (err) {
          return Promise.reject(err);
        }
      }
    }

    return Promise.reject(error);
  }
});

export const authService = {
  login: async (email: string, password: string) => {
    const res = await api.post("/auth/login", { email, password });
    return { email: res.data.email, role: res.data.role };
  },

  refresh: async () => {
    await api.post("/auth/refresh", {}, { withCredentials: true });
  },

  getCurrentUser: async () => {
    const res = await api.get("/auth/me", { withCredentials: true });
    return { email: res.data.email, role: res.data.role };
  },

  getUsers: async () => {
    const res = await api.get("/yonetici/api/users", { withCredentials: true });
    return res.data;
  },

  logout: async () => {
    // opsiyonel: backend logout endpoint varsa burada çağrılabilir
  },
};

export default api;
