import api from "../api/axiosConfig";

type TokenData = {
  accessToken: string;
  role: string;
  email: string;
};

let tokenData: TokenData | null = null;

// 🔹 Global toast fonksiyonu
let toastFn: ((type: "success" | "error" | "info" | "warning", msg: string) => void) | null = null;

export const setGlobalToast = (fn: typeof toastFn) => {
  toastFn = fn;
};

// 🔹 authService için kullanılacak toast
export const showToast = (type: "success" | "error" | "info" | "warning", msg: string) => {
  if (toastFn) toastFn(type, msg);
  else console[type === "error" ? "error" : "log"](msg);
};

export const authService = {
  setToken: (token: string) => {
    if (!tokenData) tokenData = { accessToken: token, role: "", email: "" };
    else tokenData.accessToken = token;
  },
  getToken: (): string | null => tokenData?.accessToken || null,

  setRole: (role: string) => {
    if (!tokenData) tokenData = { accessToken: "", role, email: "" };
    else tokenData.role = role;
  },
  getRole: (): string | null => tokenData?.role || null,

  setEmail: (email: string) => {
    if (!tokenData) tokenData = { accessToken: "", role: "", email };
    else tokenData.email = email;
  },
  getEmail: (): string | null => tokenData?.email || null,

  logout: async () => {
    try {
      await api.post("/api/auth/logout", {}, { withCredentials: true });
      showToast("success", "Oturum kapatıldı.");
    } catch (err) {
      showToast("error", "Çıkış yapılırken bir hata oluştu");
      console.warn("Logout sırasında hata:", err);
    } finally {
      tokenData = null;
      window.location.href = "/login";
    }
  },

  silentRefresh: async (): Promise<boolean> => {
    try {
      const res = await api.post("/api/auth/refresh", {}, { withCredentials: true });
      const { token, role, email } = res.data;

      if (!token) {
        showToast("error", "Token yenilenemedi.");
        return false;
      }

      authService.setToken(token);
      authService.setRole(role);
      authService.setEmail(email);

      return true;
    } catch (err: any) {
      showToast("error", "Silent refresh başarısız oldu.");
      console.warn("Silent refresh başarısız:", err.response?.data || err);
      return false;
    }
  },

  showToast, // 🔹 authService üzerinden direkt toast çağrılabilir
};
