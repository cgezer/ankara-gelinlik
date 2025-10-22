import React, { useState } from "react";
import { Table, Button, Modal, Form, Input, message, Space } from "antd";

interface Medya {
  id: number;
  title: string;
  description: string;
  url: string;
}

const initialMedya: Medya[] = [
  { id: 1, title: "Gelinlik Foto 1", description: "Çok şık gelinlik", url: "https://example.com/media1.jpg" },
  { id: 2, title: "Gelinlik Foto 2", description: "Modern tasarım", url: "https://example.com/media2.jpg" },
];

const MedyaList: React.FC = () => {
  const [medyaList, setMedyaList] = useState<Medya[]>(initialMedya);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingMedya, setEditingMedya] = useState<Medya | null>(null);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [selectedMedyaForDelete, setSelectedMedyaForDelete] = useState<Medya | null>(null);
  const [form] = Form.useForm();

  // Modal aç
  const openModal = (medya?: Medya) => {
    if (medya) {
      setEditingMedya(medya);
      form.setFieldsValue(medya);
    } else {
      setEditingMedya(null);
      form.resetFields();
    }
    setIsModalVisible(true);
  };

  const closeModal = () => {
    setIsModalVisible(false);
    setEditingMedya(null);
  };

  // Kaydet
  const handleSave = () => {
    form.validateFields().then((values) => {
      if (editingMedya) {
        setMedyaList((prev) =>
          prev.map((m) => (m.id === editingMedya.id ? { ...m, ...values } : m))
        );
        message.success("Medya güncellendi!");
      } else {
        const newMedya: Medya = {
          id: medyaList.length > 0 ? Math.max(...medyaList.map((m) => m.id)) + 1 : 1,
          ...values,
        };
        setMedyaList([...medyaList, newMedya]);
        message.success("Medya eklendi!");
      }
      closeModal();
    });
  };

  // Silme modal aç
  const openDeleteModal = (medya: Medya) => {
    setSelectedMedyaForDelete(medya);
    setDeleteModalVisible(true);
  };

  const handleDelete = () => {
    if (selectedMedyaForDelete) {
      setMedyaList((prev) => prev.filter((m) => m.id !== selectedMedyaForDelete.id));
      message.success("Medya silindi!");
      setDeleteModalVisible(false);
      setSelectedMedyaForDelete(null);
    }
  };

  const handleCancelDelete = () => {
    setDeleteModalVisible(false);
    setSelectedMedyaForDelete(null);
  };

  const columns = [
    { title: "ID", dataIndex: "id", key: "id" },
    { title: "Başlık", dataIndex: "title", key: "title" },
    { title: "Açıklama", dataIndex: "description", key: "description" },
    { title: "URL", dataIndex: "url", key: "url" },
    {
      title: "İşlemler",
      key: "actions",
      render: (_: any, record: Medya) => (
        <Space>
          <Button type="link" onClick={() => openModal(record)}>Düzenle</Button>
          <Button type="link" danger onClick={() => openDeleteModal(record)}>Sil</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2>Medya Yönetimi</h2>
      <Button type="primary" style={{ marginBottom: 16 }} onClick={() => openModal()}>
        Yeni Medya
      </Button>

      <Table rowKey="id" columns={columns} dataSource={medyaList} />

      {/* Medya ekle/düzenle Modal */}
      <Modal
        title={editingMedya ? "Medya Düzenle" : "Yeni Medya"}
        open={isModalVisible}
        onCancel={closeModal}
        onOk={handleSave}
        okText="Kaydet"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Başlık"
            name="title"
            rules={[{ required: true, message: "Başlık girin!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Açıklama"
            name="description"
            rules={[{ required: true, message: "Açıklama girin!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="URL"
            name="url"
            rules={[{ required: true, message: "URL girin!" }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      {/* Silme onay modal */}
      <Modal
        title="Medya Sil"
        open={deleteModalVisible}
        onOk={handleDelete}
        onCancel={handleCancelDelete}
        okText="Evet"
        cancelText="Hayır"
      >
        <p>{selectedMedyaForDelete?.title} silinecek. Emin misiniz?</p>
      </Modal>
    </div>
  );
};

export default MedyaList;
