import api from "../api/axiosConfig";

export const meydaService = {
  getAll: () => api.get("/api/medya/public/all"),

  create: (data: FormData) =>
    api.post("/api/medya", data, {
      headers: { "Content-Type": "multipart/form-data" }
    }),

  update: (id: number, data: FormData) =>
    api.put(`/api/medya/${id}`, data, {
      headers: { "Content-Type": "multipart/form-data" }
    }),

  delete: (id: number) => api.delete(`/api/medya/${id}`)
};
