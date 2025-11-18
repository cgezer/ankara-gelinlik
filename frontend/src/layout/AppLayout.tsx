// ✅ src/context/AuthContext.tsx
import React, {
  createContext,
  useState,
  useEffect,
  useContext,
  ReactNode,
} from "react";

import api from "../api/axiosConfig"; // <-- axios değil api kullanılacak

interface AuthContextType {
  user: { email: string; role: string } | null;
  authLoaded: boolean;
  setUser: React.Dispatch<
    React.SetStateAction<{ email: string; role: string } | null>
  >;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  authLoaded: false,
  setUser: () => {},
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] =
    useState<{ email: string; role: string } | null>(null);
  const [authLoaded, setAuthLoaded] = useState(false);

  const fetchUser = async () => {
    try {
      // ✅ API çağrısı axiosConfig üzerinden yapılır
      const res = await api.get("/auth/me");

      setUser({
        email: res.data.email,
        role: res.data.role,
      });
    } catch (err) {
      setUser(null);
    } finally {
      setAuthLoaded(true);
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <AuthContext.Provider value={{ user, setUser, authLoaded }}>
      {authLoaded ? children : <div>Loading...</div>}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
