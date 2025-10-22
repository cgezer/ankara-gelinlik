import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import AppLayout from "./layout/AppLayout";
import UserList from "./pages/Users/UserList";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import ProtectedRoute from "./routes/ProtectedRoute";

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login sayfası herkese açık */}
        <Route path="/login" element={<Login />} />

        {/* Layout içindeki sayfalar protected */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          {/* Admin kullanıcılar sadece /users sayfasına erişebilir */}
          <Route
            path="users"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <UserList />
              </ProtectedRoute>
            }
          />

          {/* Hem admin hem normal kullanıcılar profile sayfasına erişebilir */}
          <Route
            path="profile"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN", "ROLE_USER"]}>
                <Profile />
              </ProtectedRoute>
            }
          />

          {/* "/" path için redirect */}
          <Route index element={<Navigate to="/profile" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
