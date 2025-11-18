import React, { useEffect, useState } from "react";
import { Table, Button, Popconfirm } from "antd";
import { authService } from "../../auth/authService";
import { useAuth } from "../../context/AuthContext";

interface User {
  id: number;
  email: string;
  role: string;
}

const UserList = () => {
  const [users, setUsers] = useState<User[]>([]);
  const { refreshUser } = useAuth();

  const fetchUsers = async () => {
    try {
      const data = await authService.getUsers();
      setUsers(data);
    } catch (err) {
      console.error(err);
    }
  };

  const deleteUser = async (id: number) => {
    try {
      await authService.getUsers(); // Eğer delete endpoint yoksa oluştur
      fetchUsers();
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const columns = [
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Role", dataIndex: "role", key: "role" },
    {
      title: "Actions",
      key: "actions",
      render: (_: any, record: User) => (
        <Popconfirm
          title="Delete user?"
          onConfirm={() => deleteUser(record.id)}
        >
          <Button danger>Delete</Button>
        </Popconfirm>
      ),
    },
  ];

  return <Table dataSource={users} columns={columns} rowKey="id" />;
};

export default UserList;
