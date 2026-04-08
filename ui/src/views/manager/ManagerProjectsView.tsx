"use client";
import { useAuthStore } from "@/store/auth-store";
import { useManagerProjects, useCompleteProject } from "@/hooks/useProjects";
import { Button } from "@/components/ui/Button";
import { StatusBadge } from "@/components/ui/Badge";

export function ManagerProjectsView() {
  const userName = useAuthStore((s) => s.userName) ?? "";
  const { data: projects = [], isLoading } = useManagerProjects(userName);
  const completeProject = useCompleteProject();

  if (isLoading) return <p className="text-gray-500 text-sm">Yükleniyor...</p>;

  return (
    <div className="space-y-4">
      {projects.length === 0 ? (
        <div className="bg-white rounded-xl border p-8 text-center text-gray-400">
          Size atanmış proje bulunamadı.
        </div>
      ) : (
        <div className="grid gap-4">
          {projects.map((project) => (
            <div key={project.projectCode} className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-base font-semibold text-gray-900">{project.projectName}</h3>
                  <p className="text-xs text-gray-400 font-mono mt-0.5">{project.projectCode}</p>
                  {project.projectDetail && (
                    <p className="text-sm text-gray-600 mt-2">{project.projectDetail}</p>
                  )}
                </div>
                <div className="flex items-center gap-3">
                  {project.projectStatus && <StatusBadge status={project.projectStatus} />}
                  {project.projectStatus !== "COMPLETE" && (
                    <Button
                      size="sm"
                      variant="secondary"
                      loading={completeProject.isPending}
                      onClick={() => completeProject.mutate(project.projectCode)}
                    >
                      Tamamla
                    </Button>
                  )}
                </div>
              </div>
              <div className="mt-4 flex gap-4 text-sm">
                <span className="text-green-600">
                  ✓ {project.completeTaskCounts ?? 0} tamamlandı
                </span>
                <span className="text-amber-600">
                  ⏳ {project.unfinishedTaskCounts ?? 0} bekliyor
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
