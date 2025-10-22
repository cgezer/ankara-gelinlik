import React, { useEffect, useState } from "react";
import { Table, message, Spin, Button, Modal, Form, Input, Select } from "antd";
import api from "../../api/axiosConfig"; // ✅ Ortak axios instance kullanılıyor

interface User {
  id: number;
  ad: string;
  soyad: string;
  email: string;
  role: string;
  sifre?: string; // sadece yeni eklemede kullanılır
}

const { Option } = Select;

const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchUsers();
  }, []);

  // 🔹 Kullanıcıları getir
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const email = localStorage.getItem("email"); // Şu anki kullanıcının maili
      const response = await api.get(`/yonetici/api/users?currentUserEmail=${email}`);
      setUsers(response.data);
    } catch (error: any) {
      console.error("Kullanıcılar alınamadı:", error);
      message.error(error.response?.data?.error || "Kullanıcılar yüklenemedi");
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Kullanıcı sil
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: "Kullanıcıyı silmek istediğinize emin misiniz?",
      okText: "Evet",
      cancelText: "Hayır",
      onOk: async () => {
        try {
          await api.delete(`/yonetici/api/users/${id}`);
          message.success("Kullanıcı silindi");
          fetchUsers();
        } catch (error: any) {
          console.error("Silme hatası:", error);
          message.error(error.response?.data?.error || "Silme işlemi başarısız");
        }
      },
    });
  };

  // 🔹 Kullanıcı düzenleme
  const handleEdit = (user: User) => {
    setEditingUser(user);
    form.setFieldsValue(user);
    setModalVisible(true);
  };

  // 🔹 Yeni kullanıcı ekleme veya düzenleme kaydetme
  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      if (editingUser) {
        // Mevcut kullanıcıyı güncelle
        await api.put(`/yonetici/api/users/${editingUser.id}`, values);
        message.success("Kullanıcı güncellendi");
      } else {
        // Yeni kullanıcı oluştur
        await api.post(`/yonetici/api/users`, values);
        message.success("Yeni kullanıcı eklendi");
      }
      setModalVisible(false);
      setEditingUser(null);
      form.resetFields();
      fetchUsers();
    } catch (error: any) {
      console.error("Kayıt işlemi hatası:", error);
      message.error(error.response?.data?.error || "İşlem başarısız");
    }
  };

  // 🔹 Tablo sütunları
  const columns = [
    { title: "Ad", dataIndex: "ad", key: "ad" },
    { title: "Soyad", dataIndex: "soyad", key: "soyad" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Rol", dataIndex: "role", key: "role" },
    {
      title: "İşlemler",
      key: "actions",
      render: (_: any, record: User) => (
        <>
          <Button type="link" onClick={() => handleEdit(record)}>
            Düzenle
          </Button>
          <Button type="link" danger onClick={() => handleDelete(record.id)}>
            Sil
          </Button>
        </>
      ),
    },
  ];

  // 🔹 Role göre kullanıcıları ayır
  const adminUsers = users.filter((u) => u.role === "ROLE_ADMIN");
  const normalUsers = users.filter((u) => u.role === "ROLE_USER");

  return (
    <div style={{ padding: "20px" }}>
      <h2>Yönetim Paneli</h2>

      <Button
        type="primary"
        style={{ marginBottom: 16 }}
        onClick={() => {
          setModalVisible(true);
          setEditingUser(null);
          form.resetFields();
        }}
      >
        Yeni Kullanıcı Ekle
      </Button>

      <h3>Admin Kullanıcılar</h3>
      {loading ? (
        <Spin />
      ) : (
        <Table
          dataSource={adminUsers}
          columns={columns}
          rowKey="id"
          pagination={{ pageSize: 5 }}
        />
      )}

      <h3>Normal Kullanıcılar</h3>
      {loading ? (
        <Spin />
      ) : (
        <Table
          dataSource={normalUsers}
          columns={columns}
          rowKey="id"
          pagination={{ pageSize: 5 }}
        />
      )}

      {/* 🔹 Kullanıcı Ekle/Düzenle Modal */}
      <Modal
        title={editingUser ? "Kullanıcı Düzenle" : "Yeni Kullanıcı Ekle"}
        open={modalVisible}
        onOk={handleSave}
        onCancel={() => {
          setModalVisible(false);
          setEditingUser(null);
          form.resetFields();
        }}
        okText="Kaydet"
        cancelText="İptal"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="ad"
            label="Ad"
            rules={[{ required: true, message: "Ad giriniz" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="soyad"
            label="Soyad"
            rules={[{ required: true, message: "Soyad giriniz" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Email giriniz" },
              { type: "email", message: "Geçerli bir email giriniz" },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="role"
            label="Rol"
            rules={[{ required: true, message: "Rol seçiniz" }]}
          >
            <Select placeholder="Rol seçiniz">
              <Option value="ROLE_ADMIN">Admin</Option>
              <Option value="ROLE_USER">User</Option>
            </Select>
          </Form.Item>

          {/* Yeni kullanıcı ekleniyorsa şifre alanı gösterilir */}
          {!editingUser && (
            <Form.Item
              name="sifre"
              label="Şifre"
              rules={[{ required: true, message: "Şifre giriniz" }]}
            >
              <Input.Password />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;
