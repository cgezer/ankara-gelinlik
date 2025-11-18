import React, { useEffect } from "react";
import { Modal, Form, Input, Select, Upload, Button } from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { meydaService } from "../../api/meydaService";

const MeydaFormModal = ({ open, onClose, reload, defaultData }: any) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (defaultData) {
      form.setFieldsValue(defaultData);
    } else {
      form.resetFields();
    }
  }, [defaultData]);

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const formData = new FormData();

    formData.append(
      "medya",
      new Blob([JSON.stringify(values)], { type: "application/json" })
    );

    if (values.file?.file) {
      formData.append("file", values.file.file);
    }

    if (defaultData) {
      await meydaService.update(defaultData.id, formData);
    } else {
      await meydaService.create(formData);
    }

    reload();
    onClose();
  };

  return (
    <Modal
      title={defaultData ? "Meyda Düzenle" : "Yeni Meyda"}
      open={open}
      onCancel={onClose}
      onOk={handleSubmit}
      okText="Kaydet"
      cancelText="İptal"
    >
      <Form layout="vertical" form={form}>
        <Form.Item label="Başlık" name="title" rules={[{ required: true }]}>
          <Input />
        </Form.Item>

        <Form.Item label="Tür" name="type" rules={[{ required: true }]}>
          <Select
            options={[
              { value: "IMAGE", label: "Resim" },
              { value: "VIDEO", label: "Video" }
            ]}
          />
        </Form.Item>

        <Form.Item label="Dosya" name="file">
          <Upload beforeUpload={() => false} maxCount={1}>
            <Button icon={<UploadOutlined />}>Dosya Seç</Button>
          </Upload>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default MeydaFormModal;
