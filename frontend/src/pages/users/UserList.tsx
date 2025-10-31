import React, { useEffect, useState } from "react";
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Select,
  Tag,
  Popconfirm,
  Tabs,
  Card,
  Space,
  Tooltip,
} from "antd";
import { UserAddOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined } from "@ant-design/icons";
import api from "../../api/axiosConfig";
import { useToast } from "../../context/ToastContext";

interface User {
  id: number;
  ad: string;
  soyad: string;
  email: string;
  role: string;
  sifre?: string;
}

const { Option } = Select;

const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [searchText, setSearchText] = useState("");
  const [form] = Form.useForm();

  const { showMessage } = useToast();

  useEffect(() => {
    fetchUsers();
    // no toast on mount
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const email = localStorage.getItem("email"); // if you still use localStorage for email
      const response = await api.get(`/yonetici/api/users?currentUserEmail=${email}`);
      setUsers(response.data);
    } catch (error: any) {
      showMessage("error", error.response?.data?.error || "Kullanıcılar yüklenemedi");
    } finally {
      setLoading(false);
    }
  };

  const confirmDelete = async (id: number) => {
    try {
      await api.delete(`/yonetici/api/users/${id}`);
      showMessage("success", "Kullanıcı silindi");
      fetchUsers();
    } catch (error: any) {
      showMessage("error", error.response?.data?.error || "Silme işlemi başarısız");
    }
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      if (editingUser) {
        await api.put(`/yonetici/api/users/${editingUser.id}`, values);
        showMessage("success", "Kullanıcı güncellendi");
      } else {
        await api.post(`/yonetici/api/users`, values);
        showMessage("success", "Yeni kullanıcı eklendi");
      }
      setModalVisible(false);
      setEditingUser(null);
      form.resetFields();
      fetchUsers();
    } catch (error: any) {
      showMessage("error", error.response?.data?.error || "İşlem başarısız");
    }
  };

  const handleEdit = (user: User) => {
    setEditingUser(user);
    form.setFieldsValue(user);
    setModalVisible(true);
  };

  const columns = [
    { title: "Ad", dataIndex: "ad", key: "ad" },
    { title: "Soyad", dataIndex: "soyad", key: "soyad" },
    { title: "Email", dataIndex: "email", key: "email" },
    {
      title: "Rol",
      dataIndex: "role",
      key: "role",
      render: (role: string) => (role === "ROLE_ADMIN" ? <Tag color="geekblue">Admin</Tag> : <Tag color="green">User</Tag>),
    },
    {
      title: "İşlemler",
      key: "actions",
      render: (_: any, record: User) => (
        <Space>
          <Tooltip title="Kullanıcı güncelle">
            <Button type="text" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
          </Tooltip>
          <Popconfirm title="Bu kullanıcıyı silmek istiyor musunuz?" onConfirm={() => confirmDelete(record.id)} okText="Evet" cancelText="Hayır">
            <Tooltip title="Kullanıcı sil">
              <Button type="text" danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const filteredUsers = users.filter(
    (u) => u.ad.toLowerCase().includes(searchText.toLowerCase()) || u.soyad.toLowerCase().includes(searchText.toLowerCase()) || u.email.toLowerCase().includes(searchText.toLowerCase())
  );

  const adminUsers = filteredUsers.filter((u) => u.role === "ROLE_ADMIN");
  const normalUsers = filteredUsers.filter((u) => u.role === "ROLE_USER");

  return (
    <div style={{ padding: "16px" }}>
      <Card
        title="👥 Kullanıcı Yönetimi"
        extra={
          <Space>
            <Input prefix={<SearchOutlined />} placeholder="Ara..." value={searchText} onChange={(e) => setSearchText(e.target.value)} allowClear style={{ width: 220 }} />
            <Tooltip title="Kullanıcıları yenile">
              <Button type="default" icon={<ReloadOutlined />} onClick={fetchUsers}>
                Yenile
              </Button>
            </Tooltip>
            <Tooltip title="Yeni kullanıcı ekle">
              <Button
                type="primary"
                icon={<UserAddOutlined />}
                onClick={() => {
                  setEditingUser(null);
                  form.resetFields();
                  setModalVisible(true);
                }}
              >
                Yeni Kullanıcı
              </Button>
            </Tooltip>
          </Space>
        }
        // antd v5 uyarısı: 'bordered' deprecated — saklayabilirsin ya da variant kullan
        bordered={false}
        style={{ borderRadius: 12, boxShadow: "0 4px 12px rgba(0,0,0,0.08)" }}
      >
        <Tabs
          defaultActiveKey="admin"
          items={[
            {
              key: "admin",
              label: "Admin Kullanıcılar",
              children: <Table dataSource={adminUsers} columns={columns} rowKey="id" loading={loading} pagination={{ pageSize: 5 }} />,
            },
            {
              key: "user",
              label: "Normal Kullanıcılar",
              children: <Table dataSource={normalUsers} columns={columns} rowKey="id" loading={loading} pagination={{ pageSize: 5 }} />,
            },
          ]}
        />
      </Card>

      <div style={{ marginTop: 16, textAlign: "center" }}>
        <Button onClick={() => showMessage("info", "Test toast!")}>Test Toast</Button>
      </div>

      <Modal title={editingUser ? "Kullanıcı Düzenle" : "Yeni Kullanıcı Ekle"} open={modalVisible} onOk={handleSave} onCancel={() => { setModalVisible(false); setEditingUser(null); form.resetFields(); }} okText="Kaydet" cancelText="İptal">
        <Form form={form} layout="vertical">
          <Form.Item name="ad" label="Ad" rules={[{ required: true, message: "Ad giriniz" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="soyad" label="Soyad" rules={[{ required: true, message: "Soyad giriniz" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="email" label="Email" rules={[{ required: true, message: "Email giriniz" }, { type: "email", message: "Geçerli bir email giriniz" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="role" label="Rol" rules={[{ required: true, message: "Rol seçiniz" }]}>
            <Select placeholder="Rol seçiniz">
              <Option value="ROLE_ADMIN">Admin</Option>
              <Option value="ROLE_USER">User</Option>
            </Select>
          </Form.Item>
          {!editingUser && (
            <Form.Item name="sifre" label="Şifre" rules={[{ required: true, message: "Şifre giriniz" }]}>
              <Input.Password />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;
