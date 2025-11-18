import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../auth/authService";
import { useAuth } from "../context/AuthContext";
import { Form, Input, Button, message, Card } from "antd";

const Login: React.FC = () => {
  const { fetchUser } = useAuth(); // artık refreshUser değil
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { email: string; password: string }) => {
    setLoading(true);
    try {
      await authService.login(values.email, values.password);
      await fetchUser(); // burada fetchUser kullan
      navigate("/users");
      message.success("Giriş başarılı");
    } catch (err) {
      message.error("Giriş başarısız. Bilgilerinizi kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Login" style={{ maxWidth: 400, margin: "50px auto" }}>
      <Form onFinish={onFinish} layout="vertical">
        <Form.Item
          name="email"
          label="Email"
          rules={[{ required: true, message: "Email gerekli" }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="password"
          label="Password"
          rules={[{ required: true, message: "Şifre gerekli" }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            Login
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default Login;
