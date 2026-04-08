import apiClient from "./api-client";
import { API } from "@/utils/api-endpoints";
import type { ApiResponse, UserDTO } from "@/types";

export const userService = {
  list: () =>
    apiClient.get<ApiResponse<UserDTO[]>>(API.user.list).then((r) => r.data),

  getByUserName: (userName: string) =>
    apiClient.get<ApiResponse<UserDTO>>(API.user.byUserName(userName)).then((r) => r.data),

  create: (data: UserDTO) =>
    apiClient.post<ApiResponse<null>>(API.user.create, data).then((r) => r.data),

  update: (data: UserDTO) =>
    apiClient.put<ApiResponse<UserDTO>>(API.user.update, data).then((r) => r.data),

  delete: (userName: string) =>
    apiClient.delete<ApiResponse<null>>(API.user.delete(userName)).then((r) => r.data),
};
