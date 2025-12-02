import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Form, Input, Button, message, Card } from "antd";

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
    <Card title="Login" style={{ maxWidth: 400, margin: "50px auto" }}>
      <Form onFinish={onFinish} layout="vertical">
        <Form.Item name="email" label="Email" rules={[{ required: true }]}>
          <Input />
        </Form.Item>
        <Form.Item name="password" label="Password" rules={[{ required: true }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>Login</Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default Login;
