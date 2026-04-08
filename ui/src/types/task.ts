import type { Status } from "./common";
import type { UserDTO } from "./user";
import type { ProjectDTO } from "./project";

export interface TaskDTO {
  id?: number;
  project?: ProjectDTO;
  assignedEmployee?: UserDTO;
  taskSubject: string;
  taskDetail?: string | null;
  taskStatus?: Status;
  assignedDate?: string | null;
}
