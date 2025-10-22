import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import "antd/dist/reset.css";

import AppLayout from "./layout/AppLayout"; // Düzeltilmiş yol
import Login from "./pages/Login"; // Mevcut yapıya uygun
import UserList from "./pages/Users/UserList"; // Mevcut yapıya uygun
import MedyaList from "./pages/MedyaList"; // Mevcut
import Profile from "./pages/Profile"; // Mevcut
import ProtectedRoute from "./routes/ProtectedRoute"; // Mevcut

const Home = () => <h2>Ana Sayfa</h2>;

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AppLayout />}>
          <Route index element={<Home />} />

          <Route
            path="users"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <UserList />
              </ProtectedRoute>
            }
          />

          <Route
            path="medya"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <MedyaList />
              </ProtectedRoute>
            }
          />

          <Route
            path="profile"
            element={
              <ProtectedRoute allowedRoles={["ROLE_USER", "ROLE_ADMIN"]}>
                <Profile />
              </ProtectedRoute>
            }
          />
        </Route>

        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
