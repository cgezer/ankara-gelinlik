import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Input, Button, message } from "antd";
import api from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

interface LoginForm {
  email: string;
  password: string;
}

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { setAuthFromLogin, refreshAuth } = useAuth();

  const redirectUser = (role: string) => {
    switch (role) {
      case "ROLE_ADMIN":
        navigate("/users");
        break;
      case "ROLE_USER":
        navigate("/profile");
        break;
      default:
        navigate("/profile");
    }
  };

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      // axiosConfig has withCredentials true — backend will set HttpOnly cookie
      const res = await api.post("/api/auth/login", values);
      const { token, role, email } = res.data || {};

      // Set local authService + update context
      if (token && role) {
        setAuthFromLogin(token, role, email);
        // Optionally call refreshAuth to verify cookie/server side state (not strictly required)
        await refreshAuth();
        message.success("Giriş başarılı!");
        redirectUser(role);
      } else {
        // fallback: still refresh to see if cookie set
        const ok = await refreshAuth();
        if (ok) {
          message.success("Giriş başarılı!");
          redirectUser((await api.post("/api/auth/refresh").then(r => r.data.role).catch(()=> "ROLE_USER")) as string);
        } else {
          message.error("Giriş başarılı ancak token eksik.");
        }
      }
    } catch (err: any) {
      console.error(err);
      message.error(err.response?.data?.error || "Giriş başarısız!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: "50px auto" }}>
      <h2>Login</h2>
      <Form name="loginForm" onFinish={onFinish} layout="vertical">
        <Form.Item label="Email" name="email" rules={[{ required: true, message: "Email giriniz" }]}>
          <Input type="email" />
        </Form.Item>

        <Form.Item label="Password" name="password" rules={[{ required: true, message: "Şifre giriniz" }]}>
          <Input.Password />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            Giriş Yap
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default Login;
