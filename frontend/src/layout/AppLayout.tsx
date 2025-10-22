import React from "react";
import { Layout, Menu } from "antd";
import { Link, useNavigate, Outlet } from "react-router-dom";

const { Header, Content } = Layout;

const AppLayout: React.FC = () => {
  const navigate = useNavigate();

  const items = [
    { key: "users", label: <Link to="/users">Users</Link> },
    {
      key: "logout",
      label: "Logout",
      onClick: () => {
        localStorage.clear();
        navigate("/login");
      }
    },
  ];

  return (
    <Layout>
      <Header>
        <Menu theme="dark" mode="horizontal" items={items} />
      </Header>
      <Content style={{ padding: "24px" }}>
        <Outlet />
      </Content>
    </Layout>
  );
};

export default AppLayout;
