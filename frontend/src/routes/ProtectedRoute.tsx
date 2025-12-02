import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface ProtectedRouteProps {
  roles?: string[];
  children: JSX.Element;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ roles, children }) => {
  const { user, authLoaded } = useAuth();

  if (!authLoaded) return <div>Loading...</div>;
  if (!user) return <Navigate to="/login" replace />;

  // Eğer route özel role ister ve kullanıcı bu roller arasında değilse
  if (roles && !roles.includes(user.role)) {
    // ROLE_USER ise profile sayfasına gönder
    if (user.role === "ROLE_USER") {
      return <Navigate to="/profile" replace />;
    }
    // Diğer yetkisiz roller login sayfasına
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
