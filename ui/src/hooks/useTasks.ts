import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { taskService } from "@/services/task-service";
import type { TaskDTO } from "@/types";

const TASKS_KEY = ["tasks"];

export function useTasks() {
  return useQuery({
    queryKey: TASKS_KEY,
    queryFn: () => taskService.list(),
    select: (res) => res.data ?? [],
  });
}

export function useTask(id: number) {
  return useQuery({
    queryKey: [...TASKS_KEY, id],
    queryFn: () => taskService.getById(id),
    select: (res) => res.data,
    enabled: !!id,
  });
}

export function useEmployeePendingTasks() {
  return useQuery({
    queryKey: [...TASKS_KEY, "employee-pending"],
    queryFn: () => taskService.employeePending(),
    select: (res) => res.data ?? [],
  });
}

export function useEmployeeArchiveTasks() {
  return useQuery({
    queryKey: [...TASKS_KEY, "employee-archive"],
    queryFn: () => taskService.employeeArchive(),
    select: (res) => res.data ?? [],
  });
}

export function useCreateTask() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: TaskDTO) => taskService.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: TASKS_KEY }),
  });
}

export function useUpdateTask() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: TaskDTO) => taskService.update(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: TASKS_KEY }),
  });
}

export function useDeleteTask() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => taskService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: TASKS_KEY }),
  });
}

export function useEmployeeUpdateTaskStatus() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: TaskDTO) => taskService.employeeUpdateStatus(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: TASKS_KEY }),
  });
}
