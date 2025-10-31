import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AppLayout from "./layout/AppLayout";
import UserList from "./pages/Users/UserList";
import MedyaList from "./pages/MedyaList";
import Profile from "./pages/Profile";
import Login from "./pages/Login";
import ProtectedRoute from "./routes/ProtectedRoute";
import { authService } from "./auth/authService";
import { ToastProvider } from "./context/ToastContext";

const App: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!authService.getToken());

  useEffect(() => {
    const initAuth = async () => {
      const success = await authService.silentRefresh();
      setLoading(false);
      setIsAuthenticated(success);

      if (!success) {
        // Kullanıcı login değilse login sayfasına yönlendir
        if (window.location.pathname !== "/login") {
          window.history.replaceState({}, "", "/login");
        }
      }
      // Login başarılıysa mevcut path korunur, redirect yok
    };

    initAuth();
  }, []);

  if (loading) {
    return <div style={{ textAlign: "center", marginTop: "100px" }}>Loading...</div>;
  }

  return (
    <ToastProvider>
      <BrowserRouter>
        <Routes>
          {/* Login sayfası */}
          <Route
            path="/login"
            element={
              isAuthenticated ? <Navigate to={window.location.pathname !== "/login" ? window.location.pathname : "/profile"} replace /> : <Login />
            }
          />

          {/* Protected Routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute loading={loading} allowedRoles={["ROLE_ADMIN", "ROLE_USER"]}>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route
              path="users"
              element={
                <ProtectedRoute allowedRoles={["ROLE_ADMIN"]} loading={loading}>
                  <UserList />
                </ProtectedRoute>
              }
            />
            <Route
              path="medya"
              element={
                <ProtectedRoute allowedRoles={["ROLE_ADMIN"]} loading={loading}>
                  <MedyaList />
                </ProtectedRoute>
              }
            />
            <Route
              path="profile"
              element={
                <ProtectedRoute allowedRoles={["ROLE_ADMIN", "ROLE_USER"]} loading={loading}>
                  <Profile />
                </ProtectedRoute>
              }
            />
            <Route index element={<Navigate to="/profile" replace />} />
          </Route>

          {/* Catch-all */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </ToastProvider>
  );
};

export default App;
