"use client";
import { useEmployeeArchiveTasks } from "@/hooks/useTasks";
import { StatusBadge } from "@/components/ui/Badge";

export function ArchiveView() {
  const { data: tasks = [], isLoading } = useEmployeeArchiveTasks();

  if (isLoading) return <p className="text-gray-500 text-sm">Yükleniyor...</p>;

  return (
    <div className="space-y-3">
      {tasks.length === 0 ? (
        <div className="bg-white rounded-xl border p-8 text-center text-gray-400">
          Arşivde görev bulunmuyor.
        </div>
      ) : (
        tasks.map((task) => (
          <div key={task.id} className="bg-white rounded-xl border border-gray-200 p-5 opacity-75">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-medium text-gray-700">{task.taskSubject}</h3>
                {task.taskDetail && (
                  <p className="text-sm text-gray-400 mt-1">{task.taskDetail}</p>
                )}
                <div className="flex items-center gap-3 mt-2">
                  {task.taskStatus && <StatusBadge status={task.taskStatus} />}
                  <span className="text-xs text-gray-400">
                    {task.project?.projectName ?? task.project?.projectCode ?? "-"}
                  </span>
                  <span className="text-xs text-gray-400">{task.assignedDate ?? ""}</span>
                </div>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
}
