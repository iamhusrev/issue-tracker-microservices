"use client";
import { useEmployeePendingTasks, useEmployeeUpdateTaskStatus } from "@/hooks/useTasks";
import { StatusBadge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import type { TaskDTO, Status } from "@/types";

const NEXT_STATUS: Partial<Record<Status, Status>> = {
  OPEN: "IN_PROGRESS",
  IN_PROGRESS: "UAT_TEST",
  UAT_TEST: "COMPLETE",
};

export function MyTasksView() {
  const { data: tasks = [], isLoading } = useEmployeePendingTasks();
  const updateStatus = useEmployeeUpdateTaskStatus();

  const handleUpdate = (task: TaskDTO) => {
    const next = task.taskStatus ? NEXT_STATUS[task.taskStatus] : undefined;
    if (next) updateStatus.mutate({ ...task, taskStatus: next });
  };

  if (isLoading) return <p className="text-gray-500 text-sm">Yükleniyor...</p>;

  return (
    <div className="space-y-3">
      {tasks.length === 0 ? (
        <div className="bg-white rounded-xl border p-8 text-center text-gray-400">
          Bekleyen göreviniz bulunmuyor.
        </div>
      ) : (
        tasks.map((task) => (
          <div key={task.id} className="bg-white rounded-xl border border-gray-200 p-5">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-medium text-gray-900">{task.taskSubject}</h3>
                {task.taskDetail && (
                  <p className="text-sm text-gray-500 mt-1">{task.taskDetail}</p>
                )}
                <div className="flex items-center gap-3 mt-2">
                  {task.taskStatus && <StatusBadge status={task.taskStatus} />}
                  <span className="text-xs text-gray-400">
                    {task.project?.projectName ?? task.project?.projectCode ?? "-"}
                  </span>
                  <span className="text-xs text-gray-400">{task.assignedDate ?? ""}</span>
                </div>
              </div>
              {task.taskStatus && NEXT_STATUS[task.taskStatus] && (
                <Button
                  size="sm"
                  loading={updateStatus.isPending}
                  onClick={() => handleUpdate(task)}
                >
                  → {NEXT_STATUS[task.taskStatus] === "IN_PROGRESS"
                    ? "Başlat"
                    : NEXT_STATUS[task.taskStatus] === "UAT_TEST"
                    ? "UAT'a Al"
                    : "Tamamla"}
                </Button>
              )}
            </div>
          </div>
        ))
      )}
    </div>
  );
}
