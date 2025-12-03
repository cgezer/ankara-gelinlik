// src/pages/Login.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Form, Input, Button, message } from "antd";
import "../Login.css"; // CSS ayrı dosyada

import LogoImg from "../assets/logo.png"; // Yüklediğin logo

const Login: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { email: string; password: string }) => {
    setLoading(true);
    try {
      await login(values.email, values.password);
      message.success("Giriş başarılı");
      navigate("/users");
    } catch {
      message.error("Giriş başarısız");
    } finally { setLoading(false); }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <img src={LogoImg} alt="Logo" className="login-logo" />
        <h1 className="login-title">Gelinlik & Abiye & Kuaför </h1>

        <Form onFinish={onFinish} layout="vertical" className="login-form">
          <Form.Item
            name="email"
            label="Email"
            rules={[{ required: true, message: "Email giriniz" }]}
          >
            <Input size="large" />
          </Form.Item>
          <Form.Item
            name="password"
            label="Şifre"
            rules={[{ required: true, message: "Şifre giriniz" }]}
          >
            <Input.Password size="large" />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
              className="login-button"
            >
              Giriş Yap
            </Button>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default Login;
