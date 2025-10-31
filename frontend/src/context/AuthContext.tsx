import React, { createContext, useContext, useEffect, useState } from "react";
import api from "../api/axiosConfig";
import { authService } from "../auth/authService";

interface AuthContextValue {
  isAuthenticated: boolean | null; // null = loading
  userRole: string | null;
  loading: boolean;
  refreshAuth: () => Promise<boolean>;
  setAuthFromLogin: (token: string, role: string, email?: string) => void;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue>({
  isAuthenticated: null,
  userRole: null,
  loading: true,
  refreshAuth: async () => false,
  setAuthFromLogin: () => {},
  logout: async () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // refreshAuth uses your backend /api/auth/refresh via authService.silentRefresh
  const refreshAuth = async (): Promise<boolean> => {
    setLoading(true);
    try {
      // authService.silentRefresh should call POST /api/auth/refresh with withCredentials
      const success = await authService.silentRefresh();
      if (success) {
        setIsAuthenticated(true);
        setUserRole(authService.getRole() || null);
        setLoading(false);
        return true;
      } else {
        setIsAuthenticated(false);
        setUserRole(null);
        setLoading(false);
        return false;
      }
    } catch (err) {
      setIsAuthenticated(false);
      setUserRole(null);
      setLoading(false);
      return false;
    }
  };

  // After login: set authService values and mark authenticated without waiting refresh
  const setAuthFromLogin = (token: string, role: string, email?: string) => {
    authService.setToken(token);
    authService.setRole(role);
    if (email) authService.setEmail(email);
    setIsAuthenticated(true);
    setUserRole(role);
    setLoading(false);
  };

  const logout = async () => {
    setLoading(true);
    try {
      await authService.logout(); // this should call POST /api/auth/logout and clear tokenData and redirect in authService
    } catch (err) {
      // ignore
    } finally {
      setIsAuthenticated(false);
      setUserRole(null);
      setLoading(false);
    }
  };

  useEffect(() => {
    // On mount, attempt refresh to verify cookie/session
    (async () => {
      await refreshAuth();
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        userRole,
        loading,
        refreshAuth,
        setAuthFromLogin,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
