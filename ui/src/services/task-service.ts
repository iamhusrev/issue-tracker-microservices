import apiClient from "./api-client";
import { API } from "@/utils/api-endpoints";
import type { ApiResponse, TaskDTO } from "@/types";

export const taskService = {
  list: () =>
    apiClient.get<ApiResponse<TaskDTO[]>>(API.task.list).then((r) => r.data),

  getById: (id: number) =>
    apiClient.get<ApiResponse<TaskDTO>>(API.task.byId(id)).then((r) => r.data),

  create: (data: TaskDTO) =>
    apiClient.post<ApiResponse<null>>(API.task.create, data).then((r) => r.data),

  update: (data: TaskDTO) =>
    apiClient.put<ApiResponse<TaskDTO>>(API.task.update, data).then((r) => r.data),

  delete: (id: number) =>
    apiClient.delete<ApiResponse<null>>(API.task.delete(id)).then((r) => r.data),

  employeePending: () =>
    apiClient.get<ApiResponse<TaskDTO[]>>(API.task.employeePending).then((r) => r.data),

  employeeArchive: () =>
    apiClient.get<ApiResponse<TaskDTO[]>>(API.task.employeeArchive).then((r) => r.data),

  employeeUpdateStatus: (data: TaskDTO) =>
    apiClient.put<ApiResponse<null>>(API.task.employeeUpdate, data).then((r) => r.data),
};
