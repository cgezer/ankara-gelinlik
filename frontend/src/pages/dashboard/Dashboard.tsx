// src/pages/dashboard/Dashboard.tsx
import React from "react";
import { Row, Col, Card, Statistic, Grid } from "antd";
import {
  UserOutlined,
  TeamOutlined,
  FileTextOutlined,
  DollarOutlined,
} from "@ant-design/icons";

const { useBreakpoint } = Grid;

const Dashboard: React.FC = () => {
  const screens = useBreakpoint();
  const isMobile = !screens.md;

  return (
    <div>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Toplam Kullanıcı"
              value={128}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Admin Kullanıcılar"
              value={32}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Gönderilen Dosyalar"
              value={245}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="Gelir"
              value={12000}
              prefix={<DollarOutlined />}
              precision={2}
            />
          </Card>
        </Col>
      </Row>

      {/* Ekstra card-based grid */}
      <Row gutter={[16, 16]} style={{ marginTop: 20 }}>
        <Col xs={24} md={12}>
          <Card title="Son Kullanıcı Aktivitesi" style={{ minHeight: 200 }}>
            {/* Buraya table veya chart eklenebilir */}
            <p>Chart veya tablo ile aktivite gösterebilirsiniz.</p>
          </Card>
        </Col>

        <Col xs={24} md={12}>
          <Card title="Son İşlemler" style={{ minHeight: 200 }}>
            <p>Log veya bildirim tablosu eklenebilir.</p>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
