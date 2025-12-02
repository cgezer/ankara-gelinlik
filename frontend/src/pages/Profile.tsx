import React, { useEffect, useState } from "react";
import { Card, Descriptions, Button, Form, Input, message } from "antd";
import api from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

interface ProfileData {
  id: string;
  email: string;
  role: string;
  firstName?: string;
  lastName?: string;
}

const Profile: React.FC = () => {
  const { user, refreshUser, logout } = useAuth();
  const [profile, setProfile] = useState<ProfileData | null>(null);
  const [loading, setLoading] = useState(false);
  const [formVisible, setFormVisible] = useState(false);
  const [form] = Form.useForm();

  const fetchProfile = async () => {
    console.log("[Profile] 🔄 Fetching profile...");
    setLoading(true);
    try {
      const res = await api.get("/api/users/me");
      console.log("[Profile] ✅ Profile fetched:", res.data);
      setProfile(res.data);
    } catch (err) {
      console.error("[Profile] ❌ Fetch failed:", err);
      if (String(err).includes("401")) {
        console.log("[Profile] ⚠️ Token expired, trying refresh...");
        await refreshUser();
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handlePasswordChange = async (values: { oldPassword: string; newPassword: string }) => {
    console.log("[Profile] 🔐 Changing password...");
    try {
      await api.post("/api/users/change-password", values);
      message.success("Şifre başarıyla değiştirildi");
      form.resetFields();
      setFormVisible(false);
    } catch (err) {
      console.error("[Profile] ❌ Password change failed:", err);
      message.error("Şifre değiştirme başarısız");
    }
  };

  const handleLogout = async () => {
    console.log("[Profile] 🚪 Logout clicked");
    await logout();
    message.info("Çıkış yapıldı");
    window.location.href = "/login";
  };

  return (
    <div style={{ padding: 20, maxWidth: 700, margin: "0 auto" }}>
      <Card title="Profil Bilgileri" loading={loading}>
        {profile ? (
          <>
            <Descriptions bordered column={1}>
              <Descriptions.Item label="Ad Soyad">
                {profile.firstName} {profile.lastName}
              </Descriptions.Item>
              <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
              <Descriptions.Item label="Rol">{profile.role}</Descriptions.Item>
            </Descriptions>

            <div style={{ marginTop: 20 }}>
              {!formVisible && (
                <Button type="default" onClick={() => setFormVisible(true)}>
                  Şifre Değiştir
                </Button>
              )}
              <Button
                danger
                style={{ marginLeft: 10 }}
                onClick={handleLogout}
              >
                Çıkış Yap
              </Button>
            </div>

            {formVisible && (
              <Form
                form={form}
                layout="vertical"
                style={{ marginTop: 20 }}
                onFinish={handlePasswordChange}
              >
                <Form.Item
                  name="oldPassword"
                  label="Mevcut Şifre"
                  rules={[{ required: true, message: "Mevcut şifrenizi girin" }]}
                >
                  <Input.Password />
                </Form.Item>
                <Form.Item
                  name="newPassword"
                  label="Yeni Şifre"
                  rules={[
                    { required: true, message: "Yeni şifreyi girin" },
                    { min: 3, message: "Şifre en az 3 karakter olmalı" },
                  ]}
                >
                  <Input.Password />
                </Form.Item>
                <Button type="primary" htmlType="submit">
                  Güncelle
                </Button>
              </Form>
            )}
          </>
        ) : (
          <div>Profil yüklenemedi.</div>
        )}
      </Card>
    </div>
  );
};

export default Profile;
