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
    setLoading(true);
    try {
      const res = await api.get("/api/users/me");
      setProfile(res.data);
    } catch (err: any) {
      console.error("[Profile] Fetch failed:", err);
      if (String(err).includes("401")) {
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
    try {
      await api.post("/api/users/change-password", values);
      message.success("Şifre başarıyla değiştirildi");
      form.resetFields();
      setFormVisible(false);
    } catch (err) {
      console.error("[Profile] Password change failed:", err);
      message.error("Şifre değiştirme başarısız");
    }
  };

  const handleLogout = async () => {
    await logout();
    message.info("Çıkış yapıldı");
    window.location.href = "/login";
  };

  return (
    <div style={{ padding: 20, maxWidth: 700, margin: "0 auto" }}>
      <Card
        title="Profil Bilgileri"
        extra={
          <Button danger type="primary" onClick={handleLogout}>
            Çıkış Yap
          </Button>
        }
      >
        {user ? (
          <>
            <p><strong>Ad:</strong> {user.ad}</p>
            <p><strong>Soyad:</strong> {user.soyad}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Rol:</strong> {user.role}</p>

            <Button type="default" onClick={() => setFormVisible(true)}>
              Şifre Değiştir
            </Button>
          </>
        ) : (
          <p>Profil yüklenemedi.</p>
        )}
      </Card>

    </div>
  );
};

export default Profile;
