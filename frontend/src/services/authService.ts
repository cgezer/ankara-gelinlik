import axios from "axios";

const API_URL = "http://localhost:8080/api/auth"; // backend login endpoint'ine göre güncelle

export interface LoginResponse {
  token: string;
  username: string; // backend'den email geliyor, istersen burayı username olarak da kullanabilirsin
  role: string;
}

export const login = async (email: string, password: string): Promise<LoginResponse> => {
  const response = await axios.post(
    `${API_URL}/login`,
    { email, password },
    { withCredentials: true } // 🔹 Credentials gönderiliyor
  );

  return {
    token: response.data.token,
    username: response.data.email, // backend'den gelen email
    role: response.data.role,
  };
};

export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("username");
};

export const getToken = () => localStorage.getItem("token");
export const getRole = () => localStorage.getItem("role");
