import type { Gender } from "./common";

export interface RoleDTO {
  id: number;
  description: string;
}

export interface UserDTO {
  id?: number;
  firstName: string;
  lastName: string;
  userName: string;
  passWord?: string;
  confirmPassword?: string;
  enabled?: boolean;
  phone?: string | null;
  role?: RoleDTO;
  gender?: Gender | null;
}
