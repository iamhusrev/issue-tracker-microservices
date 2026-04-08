import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { userService } from "@/services/user-service";
import type { UserDTO } from "@/types";

const USERS_KEY = ["users"];

export function useUsers() {
  return useQuery({
    queryKey: USERS_KEY,
    queryFn: () => userService.list(),
    select: (res) => res.data ?? [],
  });
}

export function useUser(userName: string) {
  return useQuery({
    queryKey: [...USERS_KEY, userName],
    queryFn: () => userService.getByUserName(userName),
    select: (res) => res.data,
    enabled: !!userName,
  });
}

export function useCreateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: UserDTO) => userService.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: USERS_KEY }),
  });
}

export function useUpdateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: UserDTO) => userService.update(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: USERS_KEY }),
  });
}

export function useDeleteUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (userName: string) => userService.delete(userName),
    onSuccess: () => qc.invalidateQueries({ queryKey: USERS_KEY }),
  });
}
