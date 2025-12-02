import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import UserList from "./pages/users/UserList";
import Login from "./pages/Login";
import Profile from "./pages/Profile"; // Profil sayfasını import et
import ProtectedRoute from "./routes/ProtectedRoute";
import AppLayout from "./layout/AppLayout";
import { checkBackend, backendAvailable } from "./api/axiosConfig";
import "antd/dist/reset.css";
import "./index.css";
import "./App.css";

import { Toaster } from "react-hot-toast";
import { ToastProvider } from "./context/ToastContext";

const Root = () => {
  const [ready, setReady] = useState(false);

  useEffect(() => {
    const init = async () => {
      await checkBackend();
      setReady(true);
    };
    init();
  }, []);

  if (!ready) return <div>Loading...</div>;
  if (!backendAvailable) return <div style={{ padding: 20, color: "red" }}>Backend unreachable</div>;

  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />

            {/* Admin paneli */}
            <Route
              element={
                <ProtectedRoute roles={["ROLE_ADMIN"]}>
                  <AppLayout />
                </ProtectedRoute>
              }
            >
              <Route path="/users" element={<UserList />} />
            </Route>

            {/* Normal kullanıcı profili */}
            <Route
              path="/profile"
              element={
                <ProtectedRoute roles={["ROLE_USER", "ROLE_ADMIN"]}>
                  <Profile />
                </ProtectedRoute>
              }
            />

            {/* Default redirect: login veya /profile */}
            <Route
              path="*"
              element={
                <ProtectedRoute roles={["ROLE_USER", "ROLE_ADMIN"]}>
                  <Navigate to="/users" replace />
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </ToastProvider>
    </AuthProvider>
  );
};

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Toaster position="top-right" />
    <Root />
  </React.StrictMode>
);
