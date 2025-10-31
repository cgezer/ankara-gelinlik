import React, { createContext, useContext, ReactNode } from "react";
import { message } from "antd";
import {
  InfoCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
} from "@ant-design/icons";

type ToastType = "success" | "error" | "info" | "warning";

interface ToastContextProps {
  showMessage: (type: ToastType, content: string) => void;
}

const ToastContext = createContext<ToastContextProps>({
  showMessage: () => {},
});

interface Props {
  children: ReactNode;
}

export const ToastProvider: React.FC<Props> = ({ children }) => {
  // ✅ useMessage hook kullanıyoruz, daha güvenli
  const [messageApi, contextHolder] = message.useMessage();

  const showMessage = (type: ToastType, content: string) => {
    const iconMap = {
      success: <CheckCircleOutlined style={{ color: "#52c41a" }} />,
      error: <CloseCircleOutlined style={{ color: "#ff4d4f" }} />,
      info: <InfoCircleOutlined style={{ color: "#1890ff" }} />,
      warning: <ExclamationCircleOutlined style={{ color: "#faad14" }} />,
    };

    messageApi.open({
      content,
      duration: 3,
      icon: iconMap[type],
      style: { marginTop: 50 },
    });
  };

  return (
    <ToastContext.Provider value={{ showMessage }}>
      {contextHolder} {/* ✅ Ant Design mesaj context’i burada */}
      {children}
    </ToastContext.Provider>
  );
};

export const useToast = () => useContext(ToastContext);
