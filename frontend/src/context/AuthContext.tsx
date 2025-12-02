// src/context/AuthContext.tsx
import { createContext, useState, useEffect, useContext, ReactNode } from "react";
import api, { setLogoutHandler, backendAvailable } from "../api/axiosConfig";

interface UserType {
  ad: string;
  soyad: string;
  email: string;
  role: string;
}

interface AuthContextType {
  user: UserType | null;
  authLoaded: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  authLoaded: false,
  login: async () => {},
  logout: async () => {},
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<UserType | null>(null);
  const [authLoaded, setAuthLoaded] = useState(false);

  const fetchUser = async () => {
    if (!backendAvailable) {
      setAuthLoaded(true);
      return;
    }

    try {
      const res = await api.get("/auth/me", { withCredentials: true });

      setUser({
        ad: res.data.ad,
        soyad: res.data.soyad,
        email: res.data.email,
        role: res.data.role,
      });
    } catch {
      setUser(null);
    } finally {
      setAuthLoaded(true);
    }
  };

  const login = async (email: string, password: string) => {
    await api.post("/auth/login", { email, password }, { withCredentials: true });
    await fetchUser();
  };

  const logout = async () => {
    try {
      await api.post("/auth/logout", {}, { withCredentials: true });
      setUser(null);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchUser();
    setLogoutHandler(logout);
  }, []);

  return (
    <AuthContext.Provider value={{ user, authLoaded, login, logout }}>
      {authLoaded ? children : <div>Loading...</div>}
    </AuthContext.Provider>
  );
};
