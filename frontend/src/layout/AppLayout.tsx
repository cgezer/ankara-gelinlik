import React, { useState } from "react";
import { Layout, Menu, Button, theme } from "antd";
import { MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, LogoutOutlined } from "@ant-design/icons";
import { Link, Outlet, useNavigate, useLocation } from "react-router-dom";
import { authService } from "../auth/authService";
import { useAuth } from "../context/AuthContext";

const { Header, Sider, Content } = Layout;

const AppLayoutContent: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const { logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  const menuItems = [
    { key: "users", icon: <UserOutlined />, label: <Link to="/users">Users</Link> },
    { key: "logout", icon: <LogoutOutlined />, label: "Logout", onClick: handleLogout },
  ];

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider trigger={null} collapsible collapsed={collapsed} style={{ background: "#001529", position: "sticky", top: 0, height: "100vh" }}>
        <div style={{ height: 64, display: "flex", alignItems: "center", justifyContent: "center", color: "#fff", fontWeight: "bold", fontSize: collapsed ? 18 : 22 }}>
          Admin
        </div>
        <Menu theme="dark" mode="inline" selectedKeys={[location.pathname.replace("/", "") || "users"]} items={menuItems} style={{ borderRight: 0 }} />
      </Sider>

      <Layout>
        <Header style={{ padding: "0 16px", background: colorBgContainer, display: "flex", alignItems: "center", justifyContent: "space-between", boxShadow: "0 2px 8px rgba(0,0,0,0.1)" }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} style={{ fontSize: "18px", width: 48, height: 48 }} />
          <div style={{ fontWeight: "500" }}>Yönetici Paneli</div>
        </Header>

        <Content style={{ margin: "24px 16px", padding: 24, minHeight: 360, background: colorBgContainer, borderRadius: borderRadiusLG }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

const AppLayout: React.FC = () => <AppLayoutContent />;

export default AppLayout;
