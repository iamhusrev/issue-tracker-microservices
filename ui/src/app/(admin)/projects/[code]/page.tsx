"use client";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useProject } from "@/hooks/useProjects";
import { useTasks } from "@/hooks/useTasks";
import { StatusBadge } from "@/components/ui/Badge";

export default function ProjectDetailPage() {
  const { code } = useParams<{ code: string }>();
  const { data: project, isLoading } = useProject(code);
  const { data: allTasks = [] } = useTasks();
  const tasks = allTasks.filter((t) => t.project?.projectCode === code);

  if (isLoading) return <div className="pt-6 text-gray-500">Yükleniyor...</div>;
  if (!project) return <div className="pt-6 text-gray-500">Proje bulunamadı.</div>;

  return (
    <div className="space-y-6 pt-6">
      <div className="flex items-center gap-2 text-sm text-gray-500">
        <Link href="/projects" className="hover:text-indigo-600">Projeler</Link>
        <span>/</span>
        <span className="text-gray-900 font-medium">{project.projectName}</span>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{project.projectName}</h1>
            <p className="text-sm text-gray-400 font-mono mt-1">{project.projectCode}</p>
          </div>
          {project.projectStatus && <StatusBadge status={project.projectStatus} />}
        </div>
        {project.projectDetail && (
          <p className="text-gray-600 mt-4">{project.projectDetail}</p>
        )}
        <div className="grid grid-cols-3 gap-4 mt-6 pt-6 border-t">
          <div>
            <p className="text-xs text-gray-400">Yönetici</p>
            <p className="text-sm font-medium text-gray-800 mt-1">
              {project.assignedManager
                ? `${project.assignedManager.firstName} ${project.assignedManager.lastName}`
                : "-"}
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-400">Başlangıç</p>
            <p className="text-sm font-medium text-gray-800 mt-1">{project.startDate ?? "-"}</p>
          </div>
          <div>
            <p className="text-xs text-gray-400">Bitiş</p>
            <p className="text-sm font-medium text-gray-800 mt-1">{project.endDate ?? "-"}</p>
          </div>
        </div>
      </div>

      <div>
        <h2 className="text-lg font-semibold text-gray-800 mb-3">Görevler ({tasks.length})</h2>
        {tasks.length === 0 ? (
          <div className="bg-white rounded-xl border p-6 text-center text-gray-400">
            Bu projeye ait görev bulunmuyor.
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
                <tr>
                  <th className="text-left px-4 py-3">Konu</th>
                  <th className="text-left px-4 py-3">Çalışan</th>
                  <th className="text-left px-4 py-3">Durum</th>
                  <th className="text-left px-4 py-3">Tarih</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {tasks.map((task) => (
                  <tr key={task.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-medium text-gray-900">{task.taskSubject}</td>
                    <td className="px-4 py-3 text-gray-600">
                      {task.assignedEmployee
                        ? `${task.assignedEmployee.firstName} ${task.assignedEmployee.lastName}`
                        : "-"}
                    </td>
                    <td className="px-4 py-3">
                      {task.taskStatus ? <StatusBadge status={task.taskStatus} /> : "-"}
                    </td>
                    <td className="px-4 py-3 text-gray-400 text-xs">{task.assignedDate ?? "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
