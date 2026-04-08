import apiClient from "./api-client";
import { API } from "@/utils/api-endpoints";
import type { ApiResponse, ProjectDTO } from "@/types";

export const projectService = {
  list: () =>
    apiClient.get<ApiResponse<ProjectDTO[]>>(API.project.list).then((r) => r.data),

  getByCode: (code: string) =>
    apiClient.get<ApiResponse<ProjectDTO>>(API.project.byCode(code)).then((r) => r.data),

  create: (data: ProjectDTO) =>
    apiClient.post<ApiResponse<null>>(API.project.create, data).then((r) => r.data),

  update: (data: ProjectDTO) =>
    apiClient.put<ApiResponse<ProjectDTO>>(API.project.update, data).then((r) => r.data),

  delete: (code: string) =>
    apiClient.delete<ApiResponse<null>>(API.project.delete(code)).then((r) => r.data),

  detailsByManager: (userName: string) =>
    apiClient
      .get<ApiResponse<ProjectDTO[]>>(API.project.detailsByManager(userName))
      .then((r) => r.data),

  complete: (projectCode: string) =>
    apiClient.put<ApiResponse<null>>(API.project.complete(projectCode)).then((r) => r.data),
};
