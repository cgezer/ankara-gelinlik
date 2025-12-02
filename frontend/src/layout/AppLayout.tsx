// src/layouts/AppLayout.tsx
import React, { useState, useMemo } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  LogoutOutlined,
  UserOutlined,
  HomeOutlined,
} from "@ant-design/icons";

import { Layout, Menu, Button, Avatar, Space, Typography } from "antd";

const { Header, Sider, Content, Footer } = Layout;
const { Text } = Typography;

const HEADER_HEIGHT = 72;

const AppLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const [collapsed, setCollapsed] = useState(false);

  const fullName = `${user?.ad ?? ""} ${user?.soyad ?? ""}`.trim();

  const handleLogout = async () => {
    try {
      await logout();
    } finally {
      navigate("/login", { replace: true });
    }
  };

  const menuItems = useMemo(
    () => [
      {
        key: "dashboard",
        icon: <HomeOutlined />,
        label: "Dashboard",
        onClick: () => navigate("/dashboard"),
      },
      {
        key: "users",
        icon: <UserOutlined />,
        label: "Kullanıcılar",
        onClick: () => navigate("/users"),
      },
    ],
    [navigate]
  );

  const selectedKey = location.pathname.startsWith("/users")
    ? "users"
    : "dashboard";

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        collapsedWidth={72}
        width={240}
        style={{
          background: "#0f1724",
          boxShadow: "0 6px 18px rgba(2,6,23,0.35)",
        }}
      >
        <div
          style={{
            height: 64,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#fff",
            fontWeight: 700,
            fontSize: 18,
          }}
        >
          {collapsed ? "MA" : "Admin User Panel"}
        </div>

        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          style={{ background: "transparent", borderRight: 0 }}
        />
      </Sider>

      <Layout>
        <Header
          style={{
            height: HEADER_HEIGHT,
            padding: "0 20px",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            background: "#ffffff",
            boxShadow: "0 2px 8px rgba(0,0,0,0.06)",
            position: "sticky",
            top: 0,
            zIndex: 1200,
          }}
        >
          <Space align="center">
            <Button
              type="text"
              onClick={() => setCollapsed(!collapsed)}
              style={{
                height: 40,
                width: 40,
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </Button>

            <Text strong style={{ fontSize: 18, color: "#0f1724" }}>
              Admin User Panel
            </Text>
          </Space>

          {/* RIGHT SIDE */}
          <Space align="center" size="large">
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <Avatar
                size="large"
                style={{ backgroundColor: "#1677ff" }}
                icon={<UserOutlined />}
              />

              {/* FULL NAME ALWAYS NEXT TO AVATAR */}
              <Text strong style={{ fontSize: 16, color: "#0f1724" }}>
                {fullName || "Hoş Geldin"}
              </Text>
            </div>

            <Button
              onClick={handleLogout}
              icon={<LogoutOutlined />}
              danger
              type="default"
              style={{ borderRadius: 8 }}
            >
              Logout
            </Button>
          </Space>
        </Header>

        <Content
          style={{
            padding: 24,
            paddingTop: 24,
            minHeight: `calc(100vh - ${HEADER_HEIGHT}px)`,
            background: "#f8fafc",
          }}
        >
          <Outlet />
        </Content>

        <Footer
          style={{
            textAlign: "center",
            background: "#fff",
            borderTop: "1px solid rgba(0,0,0,0.04)",
          }}
        >
          © {new Date().getFullYear()} Meyda
        </Footer>
      </Layout>
    </Layout>
  );
};

export default AppLayout;
