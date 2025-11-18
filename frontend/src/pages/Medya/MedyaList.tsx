import React, { useEffect, useState } from "react";
import { Button, Card, Col, Row, Tag, Popconfirm, Image, Space } from "antd";
import { PlusOutlined, EditOutlined, DeleteOutlined } from "@ant-design/icons";
import { meydaService } from "../../api/meydaService";
import MeydaFormModal from "../../components/Meyda/MeydaFormModal";

const MeydaList: React.FC = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const [selected, setSelected] = useState<any>(null);
  const [open, setOpen] = useState(false);

  const loadData = async () => {
    setLoading(true);
    const res = await meydaService.getAll();
    setData(res.data);
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleDelete = async (id: number) => {
    await meydaService.delete(id);
    loadData();
  };

  return (
    <>
      <Button
        type="primary"
        icon={<PlusOutlined />}
        style={{ marginBottom: 20 }}
        onClick={() => {
          setSelected(null);
          setOpen(true);
        }}
      >
        Yeni Medya Ekle
      </Button>

      {/** GRID LAYOUT */}
      <Row gutter={[16, 16]}>
        {data.map((item: any) => (
          <Col xs={24} sm={12} md={8} lg={6} xl={4} key={item.id}>
            <Card
              hoverable
              cover={<Image src={item.fileUrl} preview={true} />}
              actions={[
                <EditOutlined
                  key="edit"
                  onClick={() => {
                    setSelected(item);
                    setOpen(true);
                  }}
                />,
                <Popconfirm
                  title="Silmek istediğine emin misin?"
                  onConfirm={() => handleDelete(item.id)}
                >
                  <DeleteOutlined key="delete" style={{ color: "red" }} />
                </Popconfirm>,
              ]}
            >
              <Card.Meta
                title={item.title}
                description={<Tag color="blue">{item.type}</Tag>}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <MeydaFormModal
        open={open}
        onClose={() => {
          setOpen(false);
          setSelected(null);
        }}
        reload={loadData}
        defaultData={selected}
      />
    </>
  );
};

export default MeydaList;
