import React, { useState } from "react";
import { Form, Input, Button, message } from "antd";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";

interface LoginForm {
  email: string;
  password: string;
}

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Rol bazlı yönlendirme fonksiyonu
  const redirectUser = (role: string) => {
    switch (role) {
      case "ROLE_ADMIN":
        navigate("/users");
        break;
      case "ROLE_USER":
        navigate("/profile");
        break;
      default:
        navigate("/login");
    }
  };

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      const response = await api.post("/api/auth/login", values);
      const { token, role, email } = response.data;

      // Token ve kullanıcı bilgilerini kaydet
      localStorage.setItem("token", token);
      localStorage.setItem("role", role);
      localStorage.setItem("email", email);

      message.success("Giriş başarılı!");
      redirectUser(role);
    } catch (error: any) {
      console.error(error);
      message.error(error.response?.data?.error || "Giriş başarısız!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: "50px auto" }}>
      <h2>Login</h2>
      <Form name="loginForm" onFinish={onFinish} layout="vertical">
        <Form.Item
          label="Email"
          name="email"
          rules={[{ required: true, message: "Email giriniz" }]}
        >
          <Input type="email" />
        </Form.Item>

        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: "Şifre giriniz" }]}
        >
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
