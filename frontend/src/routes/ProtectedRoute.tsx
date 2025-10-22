import React from "react";
import { Navigate } from "react-router-dom";

interface ProtectedRouteProps {
  allowedRoles?: string[]; // Belirtilmezse sadece token kontrolü
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles, children }) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  // 🔹 Token yoksa login sayfasına yönlendir
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // 🔹 Rol kontrolü varsa ve kullanıcı yetkisizse login sayfasına yönlendir
  if (allowedRoles && role && !allowedRoles.includes(role)) {
    return <Navigate to="/login" replace />;
  }

  // 🔹 Yetkili ise children render edilir
  return <>{children}</>;
};

export default ProtectedRoute;
