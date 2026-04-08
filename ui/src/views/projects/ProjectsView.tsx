"use client";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Link from "next/link";
import {
  useProjects,
  useCreateProject,
  useUpdateProject,
  useDeleteProject,
} from "@/hooks/useProjects";
import { useUsers } from "@/hooks/useUsers";
import { Modal } from "@/components/ui/Modal";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { StatusBadge } from "@/components/ui/Badge";
import type { ProjectDTO, Status } from "@/types";

const schema = z.object({
  projectName: z.string().min(1),
  projectCode: z.string().min(1),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  projectDetail: z.string().optional(),
  projectStatus: z.string().optional(),
  managerUserName: z.string().optional(),
});
type FormValues = z.infer<typeof schema>;

const STATUS_OPTIONS = [
  { value: "OPEN", label: "Açık" },
  { value: "IN_PROGRESS", label: "Devam Ediyor" },
  { value: "UAT_TEST", label: "UAT Test" },
  { value: "COMPLETE", label: "Tamamlandı" },
];

export function ProjectsView() {
  const { data: projects = [], isLoading } = useProjects();
  const { data: users = [] } = useUsers();
  const createProject = useCreateProject();
  const updateProject = useUpdateProject();
  const deleteProject = useDeleteProject();

  const [modal, setModal] = useState<{ open: boolean; project?: ProjectDTO }>({ open: false });
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);
  const [search, setSearch] = useState("");

  const managers = users.filter((u) => u.role?.description === "Manager");

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const openCreate = () => {
    reset({});
    setModal({ open: true });
  };

  const openEdit = (project: ProjectDTO) => {
    reset({
      projectName: project.projectName,
      projectCode: project.projectCode,
      startDate: project.startDate ?? undefined,
      endDate: project.endDate ?? undefined,
      projectDetail: project.projectDetail ?? undefined,
      projectStatus: project.projectStatus,
      managerUserName: project.assignedManager?.userName ?? "",
    });
    setModal({ open: true, project });
  };

  const onSubmit = async (values: FormValues) => {
    const manager = users.find((u) => u.userName === values.managerUserName);
    const payload: ProjectDTO = {
      projectName: values.projectName,
      projectCode: values.projectCode,
      startDate: values.startDate,
      endDate: values.endDate,
      projectDetail: values.projectDetail,
      projectStatus: values.projectStatus as Status,
      assignedManager: manager,
    };
    if (modal.project) {
      await updateProject.mutateAsync(payload);
    } else {
      await createProject.mutateAsync(payload);
    }
    setModal({ open: false });
  };

  const filtered = projects.filter(
    (p) =>
      p.projectName?.toLowerCase().includes(search.toLowerCase()) ||
      p.projectCode?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <input
          placeholder="Ara..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border rounded-lg px-3 py-2 text-sm w-64 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <Button onClick={openCreate}>+ Yeni Proje</Button>
      </div>

      {isLoading ? (
        <p className="text-gray-500 text-sm">Yükleniyor...</p>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="text-left px-4 py-3">Proje Adı</th>
                <th className="text-left px-4 py-3">Kod</th>
                <th className="text-left px-4 py-3">Yönetici</th>
                <th className="text-left px-4 py-3">Durum</th>
                <th className="text-left px-4 py-3">Görevler</th>
                <th className="text-left px-4 py-3">İşlemler</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((project) => (
                <tr key={project.projectCode} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-900">
                    <Link
                      href={`/projects/${project.projectCode}`}
                      className="text-indigo-600 hover:underline"
                    >
                      {project.projectName}
                    </Link>
                  </td>
                  <td className="px-4 py-3 text-gray-600 font-mono text-xs">{project.projectCode}</td>
                  <td className="px-4 py-3 text-gray-600">
                    {project.assignedManager
                      ? `${project.assignedManager.firstName} ${project.assignedManager.lastName}`
                      : "-"}
                  </td>
                  <td className="px-4 py-3">
                    {project.projectStatus ? <StatusBadge status={project.projectStatus} /> : "-"}
                  </td>
                  <td className="px-4 py-3 text-gray-600 text-xs">
                    <span className="text-green-600">{project.completeTaskCounts ?? 0} ✓</span>
                    {" / "}
                    <span className="text-amber-600">{project.unfinishedTaskCounts ?? 0} ⏳</span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <Button variant="secondary" size="sm" onClick={() => openEdit(project)}>
                        Düzenle
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => setDeleteConfirm(project.projectCode)}
                      >
                        Sil
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-400">
                    Veri bulunamadı
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      <Modal
        isOpen={modal.open}
        onClose={() => setModal({ open: false })}
        title={modal.project ? "Proje Düzenle" : "Yeni Proje"}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Proje Adı" {...register("projectName")} error={errors.projectName?.message} />
          <Input
            label="Proje Kodu"
            {...register("projectCode")}
            error={errors.projectCode?.message}
            readOnly={!!modal.project}
          />
          <Select
            label="Yönetici"
            {...register("managerUserName")}
            options={managers.map((m) => ({
              value: m.userName,
              label: `${m.firstName} ${m.lastName}`,
            }))}
            placeholder="Seçiniz"
          />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Başlangıç" type="date" {...register("startDate")} />
            <Input label="Bitiş" type="date" {...register("endDate")} />
          </div>
          <Select label="Durum" {...register("projectStatus")} options={STATUS_OPTIONS} placeholder="Seçiniz" />
          <Input label="Açıklama" {...register("projectDetail")} />
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="secondary" onClick={() => setModal({ open: false })}>
              İptal
            </Button>
            <Button
              type="submit"
              loading={createProject.isPending || updateProject.isPending}
            >
              Kaydet
            </Button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Projeyi Sil"
      >
        <p className="text-gray-600 mb-6">Bu projeyi silmek istediğinizden emin misiniz?</p>
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
            İptal
          </Button>
          <Button
            variant="danger"
            loading={deleteProject.isPending}
            onClick={async () => {
              if (deleteConfirm) {
                await deleteProject.mutateAsync(deleteConfirm);
                setDeleteConfirm(null);
              }
            }}
          >
            Sil
          </Button>
        </div>
      </Modal>
    </div>
  );
}
