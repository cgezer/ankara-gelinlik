// src/pages/users/UserList.tsx
import React, { useEffect, useState } from "react";
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Select,
  Tag,
  Card,
  Space,
  Tooltip,
  Grid,
} from "antd";
import {
  UserAddOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
  SearchOutlined,
  MailOutlined,
  UserOutlined,
} from "@ant-design/icons";
import api from "../../api/axiosConfig";
import toast from "react-hot-toast";

const { useBreakpoint } = Grid;

const UserList = () => {
  const screens = useBreakpoint();
  const isMobile = !screens.md;

  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  const [modalOpen, setModalOpen] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedUser, setSelectedUser] = useState<any>(null);

  const [form] = Form.useForm();

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get("/yonetici/users");
      setUsers(res.data);
    } catch (e) {
      toast.error("Kullanıcılar alınırken hata oluştu");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // ------------------------------
  // MODAL & CRUD FONKSİYONLARI
  // ------------------------------

  const openCreateModal = () => {
    setEditMode(false);
    setSelectedUser(null);
    form.resetFields();
    setModalOpen(true);
  };

  const openEditModal = (record: any) => {
    setEditMode(true);
    setSelectedUser(record);
    form.setFieldsValue({
      ...record,
      sifre: "",
      sifreConfirm: "",
    });
    setModalOpen(true);
  };

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: "Kullanıcıyı silmek istediğinize emin misiniz?",
      okText: "Evet",
      okType: "danger",
      cancelText: "Hayır",
      onOk: async () => {
        try {
          await api.delete(`/yonetici/users/${id}`);
          toast.success("Silindi");
          fetchUsers();
        } catch {
          toast.error("Silinemedi");
        }
      },
    });
  };

  // Helper: email duplication checks on client (better UX)
  const isEmailDuplicateOnCreate = (email: string) => {
    if (!email) return false;
    return users.some((u) => u.email?.toLowerCase() === email.toLowerCase());
  };

  const isEmailDuplicateOnUpdate = (email: string, id: number) => {
    if (!email) return false;
    return users.some(
      (u) =>
        u.email?.toLowerCase() === email.toLowerCase() &&
        Number(u.id) !== Number(id)
    );
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      // normalize email string
      if (values.email) values.email = values.email.trim();

      // password handling (keep existing behaviour)
      if (editMode && !values.sifre) {
        delete values.sifre;
        delete values.sifreConfirm;
      }

      if (!editMode) {
        if (values.sifre !== values.sifreConfirm) {
          toast.error("Şifreler eşleşmiyor!");
          return;
        }
      }

      // Client-side duplicate check for immediate feedback
      if (!editMode) {
        if (isEmailDuplicateOnCreate(values.email)) {
          toast.error("Kayıt işlemi tamamlanamadı. Lütfen bilgilerinizi kontrol edin!");
          return;
        }
      } else {
        if (isEmailDuplicateOnUpdate(values.email, selectedUser?.id)) {
          toast.error("İşlem tamamlanamadı. Lütfen bilgilerinizi kontrol edin.");
          return;
        }
      }

      // Prepare payload: remove confirm fields before sending
      const payload = { ...values };
      delete payload.sifreConfirm;
      delete payload.passwordConfirm; // in case different names used in edit flow

      // Server request and server-side 409 handling
      if (editMode) {
        try {
          await api.put(`/yonetici/users/${selectedUser.id}`, payload);
          toast.success("Güncellendi");
          setModalOpen(false);
          fetchUsers();
        } catch (err: any) {
          const status = err?.response?.status;
          if (status === 409) {
            toast.error("Güncelleme tamamlanamadı. Lütfen bilgilerinizi kontrol edin.!");
            // Do not close modal
            return;
          } else {
            toast.error("Güncelleme sırasında hata oluştu");
            return;
          }
        }
      } else {
        try {
          await api.post("/yonetici/users", payload);
          toast.success("Kullanıcı oluşturuldu");
          setModalOpen(false);
          fetchUsers();
        } catch (err: any) {
          const status = err?.response?.status;
          if (status === 409) {
            toast.error("Bu email zaten kayıtlı!");
            return;
          } else {
            toast.error("Kullanıcı oluşturulurken hata oluştu");
            return;
          }
        }
      }
    } catch (err) {
      // validation veya diğer hatalar buraya düşer
      // form.validateFields hata verirse zaten input feedback gösterir
      // biz genel olarak burada bir toast gösterebiliriz
      // ama gereksiz toast spam'ından kaçınalım
      // toast.error("Bir hata oluştu");
    }
  };

  const roleTag = (role: string) => {
    return role === "ROLE_ADMIN" ? (
      <Tag color="red">ADMIN</Tag>
    ) : (
      <Tag color="blue">USER</Tag>
    );
  };

  // ------------------------------
  // DESKTOP: TABLE COLUMNS
  // ------------------------------
  const columns = [
    {
      title: "Ad Soyad",
      dataIndex: "ad",
      render: (_: any, r: any) => `${r.ad} ${r.soyad}`,
    },
    {
      title: "Email",
      dataIndex: "email",
    },
    {
      title: "Rol",
      dataIndex: "role",
      render: (role: string) => roleTag(role),
    },
    {
      title: "İşlemler",
      render: (_: any, record: any) => (
        <Space>
          <Tooltip title="Düzenle">
            <Button icon={<EditOutlined />} onClick={() => openEditModal(record)} />
          </Tooltip>

          <Tooltip title="Sil">
            <Button danger icon={<DeleteOutlined />} onClick={() => handleDelete(record.id)} />
          </Tooltip>
        </Space>
      ),
    },
  ];

  // ------------------------------
  // MOBILE: CARD VIEW
  // ------------------------------
  const renderMobileCard = (user: any) => (
    <Card
      key={user.id}
      style={{
        marginBottom: 16,
        borderRadius: 12,
        boxShadow: "0 2px 10px rgba(0,0,0,0.05)",
      }}
    >
      <Space direction="vertical" style={{ width: "100%" }}>
        <Space>
          <UserOutlined style={{ fontSize: 20 }} />
          <span style={{ fontSize: 18, fontWeight: 600 }}>
            {user.ad} {user.soyad}
          </span>
        </Space>

        <Space>
          <MailOutlined />
          <span>{user.email}</span>
        </Space>

        <Space>Rol: {roleTag(user.role)}</Space>

        <Space style={{ marginTop: 10 }}>
          <Button icon={<EditOutlined />} type="primary" onClick={() => openEditModal(user)}>
            Düzenle
          </Button>

          <Button danger icon={<DeleteOutlined />} onClick={() => handleDelete(user.id)}>
            Sil
          </Button>
        </Space>
      </Space>
    </Card>
  );

  return (
    <div style={{ padding: 20 }}>
      {/* ÜST BUTONLAR */}
      <Space style={{ marginBottom: 20 }} wrap>
        <Button type="primary" icon={<UserAddOutlined />} onClick={openCreateModal}>
          Yeni Kullanıcı
        </Button>

        <Button icon={<ReloadOutlined />} onClick={fetchUsers}>
          Yenile
        </Button>
      </Space>

      {/* ARAMA BAR */}
      <Input
        prefix={<SearchOutlined />}
        placeholder="Kullanıcı arayın..."
        style={{
          marginBottom: 20,
          maxWidth: 400,
          borderRadius: 8,
        }}
      />

      {isMobile ? (
        <div>{users.map((u) => renderMobileCard(u))}</div>
      ) : (
        <Table dataSource={users} columns={columns} loading={loading} rowKey="id" style={{ background: "white", borderRadius: 10 }} />
      )}

      {/* MODAL */}
      <Modal title={editMode ? "Kullanıcı Düzenle" : "Yeni Kullanıcı"} open={modalOpen} onCancel={() => setModalOpen(false)} onOk={handleSubmit}>
        <Form form={form} layout="vertical">
          <Form.Item label="Ad" name="ad" rules={[{ required: true }]}>
            <Input />
          </Form.Item>

          <Form.Item label="Soyad" name="soyad" rules={[{ required: true }]}>
            <Input />
          </Form.Item>

          <Form.Item label="Email" name="email" rules={[{ required: true, type: "email" }]}>
            <Input />
          </Form.Item>

          {!editMode && (
            <>
              <Form.Item label="Şifre" name="sifre" rules={[{ required: true, min: 3 }]}>
                <Input.Password />
              </Form.Item>

              <Form.Item
                label="Yeni Şifre Tekrar"
                name="sifreConfirm"
                dependencies={["sifre"]}
                rules={[
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!getFieldValue("sifre") || getFieldValue("sifre") === value) return Promise.resolve();
                      return Promise.reject("Şifreler eşleşmiyor!");
                    },
                  }),
                ]}
              >
                <Input.Password placeholder="Boş bırakabilirsiniz" />
              </Form.Item>
            </>
          )}

          {editMode && (
            <>
              <Form.Item label="Yeni Şifre (Opsiyonel)" name="password" rules={[{ min: 3, message: "Şifre en az 3 karakter olmalı" }]}>
                <Input.Password placeholder="Boş bırakırsanız değişmez" />
              </Form.Item>

              <Form.Item
                label="Yeni Şifre Tekrar"
                name="passwordConfirm"
                dependencies={["password"]}
                rules={[
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!getFieldValue("password") || getFieldValue("password") === value) return Promise.resolve();
                      return Promise.reject("Şifreler eşleşmiyor!");
                    },
                  }),
                ]}
              >
                <Input.Password placeholder="Boş bırakabilirsiniz" />
              </Form.Item>
            </>
          )}

          <Form.Item label="Rol" name="role" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="ROLE_ADMIN">Admin</Select.Option>
              <Select.Option value="ROLE_USER">User</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;
