import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { projectService } from "@/services/project-service";
import type { ProjectDTO } from "@/types";

const PROJECTS_KEY = ["projects"];

export function useProjects() {
  return useQuery({
    queryKey: PROJECTS_KEY,
    queryFn: () => projectService.list(),
    select: (res) => res.data ?? [],
  });
}

export function useProject(code: string) {
  return useQuery({
    queryKey: [...PROJECTS_KEY, code],
    queryFn: () => projectService.getByCode(code),
    select: (res) => res.data,
    enabled: !!code,
  });
}

export function useManagerProjects(userName: string) {
  return useQuery({
    queryKey: [...PROJECTS_KEY, "manager", userName],
    queryFn: () => projectService.detailsByManager(userName),
    select: (res) => res.data ?? [],
    enabled: !!userName,
  });
}

export function useCreateProject() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ProjectDTO) => projectService.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: PROJECTS_KEY }),
  });
}

export function useUpdateProject() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ProjectDTO) => projectService.update(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: PROJECTS_KEY }),
  });
}

export function useDeleteProject() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (code: string) => projectService.delete(code),
    onSuccess: () => qc.invalidateQueries({ queryKey: PROJECTS_KEY }),
  });
}

export function useCompleteProject() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (projectCode: string) => projectService.complete(projectCode),
    onSuccess: () => qc.invalidateQueries({ queryKey: PROJECTS_KEY }),
  });
}
