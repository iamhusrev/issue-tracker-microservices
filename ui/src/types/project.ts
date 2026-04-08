import type { Status, UserDTO } from "./index";

export interface ProjectDTO {
  id?: number;
  projectName: string;
  projectCode: string;
  assignedManager?: UserDTO;
  startDate?: string | null;
  endDate?: string | null;
  projectDetail?: string | null;
  projectStatus?: Status;
  completeTaskCounts?: number;
  unfinishedTaskCounts?: number;
}
