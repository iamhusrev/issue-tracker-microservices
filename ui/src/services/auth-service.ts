import axios from "axios";
import { API } from "@/utils/api-endpoints";
import type { AuthResponse, LoginRequest, RefreshRequest } from "@/types";

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const res = await axios.post<AuthResponse>(API.auth.login, data);
    return res.data;
  },
  refresh: async (data: RefreshRequest): Promise<AuthResponse> => {
    const res = await axios.post<AuthResponse>(API.auth.refresh, data);
    return res.data;
  },
};
