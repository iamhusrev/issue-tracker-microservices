"use client";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useTasks, useCreateTask, useUpdateTask, useDeleteTask } from "@/hooks/useTasks";
import { useUsers } from "@/hooks/useUsers";
import { useProjects } from "@/hooks/useProjects";
import { Modal } from "@/components/ui/Modal";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { StatusBadge } from "@/components/ui/Badge";
import type { TaskDTO, Status } from "@/types";

const schema = z.object({
  taskSubject: z.string().min(1, "Konu zorunlu"),
  taskDetail: z.string().optional(),
  taskStatus: z.string().optional(),
  employeeUserName: z.string().optional(),
  projectCode: z.string().optional(),
});
type FormValues = z.infer<typeof schema>;

const STATUS_OPTIONS = [
  { value: "OPEN", label: "Açık" },
  { value: "IN_PROGRESS", label: "Devam Ediyor" },
  { value: "UAT_TEST", label: "UAT Test" },
  { value: "COMPLETE", label: "Tamamlandı" },
];

export function TasksView() {
  const { data: tasks = [], isLoading } = useTasks();
  const { data: users = [] } = useUsers();
  const { data: projects = [] } = useProjects();
  const createTask = useCreateTask();
  const updateTask = useUpdateTask();
  const deleteTask = useDeleteTask();

  const [modal, setModal] = useState<{ open: boolean; task?: TaskDTO }>({ open: false });
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);
  const [filterStatus, setFilterStatus] = useState<string>("");
  const [search, setSearch] = useState("");

  const employees = users.filter((u) => u.role?.description === "Employee");

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const openCreate = () => {
    reset({});
    setModal({ open: true });
  };

  const openEdit = (task: TaskDTO) => {
    reset({
      taskSubject: task.taskSubject,
      taskDetail: task.taskDetail ?? "",
      taskStatus: task.taskStatus ?? "",
      employeeUserName: task.assignedEmployee?.userName ?? "",
      projectCode: task.project?.projectCode ?? "",
    });
    setModal({ open: true, task });
  };

  const onSubmit = async (values: FormValues) => {
    const employee = users.find((u) => u.userName === values.employeeUserName);
    const project = projects.find((p) => p.projectCode === values.projectCode);
    const payload: TaskDTO = {
      taskSubject: values.taskSubject,
      taskDetail: values.taskDetail,
      taskStatus: values.taskStatus as Status,
      assignedEmployee: employee,
      project,
    };
    if (modal.task) {
      await updateTask.mutateAsync({ ...modal.task, ...payload });
    } else {
      await createTask.mutateAsync(payload);
    }
    setModal({ open: false });
  };

  const filtered = tasks.filter((t) => {
    const matchSearch =
      !search || t.taskSubject?.toLowerCase().includes(search.toLowerCase());
    const matchStatus = !filterStatus || t.taskStatus === filterStatus;
    return matchSearch && matchStatus;
  });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3 flex-wrap">
        <div className="flex items-center gap-3">
          <input
            placeholder="Ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="border rounded-lg px-3 py-2 text-sm w-48 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
          >
            <option value="">Tüm Durumlar</option>
            {STATUS_OPTIONS.map((s) => (
              <option key={s.value} value={s.value}>{s.label}</option>
            ))}
          </select>
        </div>
        <Button onClick={openCreate}>+ Yeni Görev</Button>
      </div>

      {isLoading ? (
        <p className="text-gray-500 text-sm">Yükleniyor...</p>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="text-left px-4 py-3">Konu</th>
                <th className="text-left px-4 py-3">Proje</th>
                <th className="text-left px-4 py-3">Çalışan</th>
                <th className="text-left px-4 py-3">Durum</th>
                <th className="text-left px-4 py-3">Tarih</th>
                <th className="text-left px-4 py-3">İşlemler</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((task) => (
                <tr key={task.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-900">{task.taskSubject}</td>
                  <td className="px-4 py-3 text-gray-600 text-xs font-mono">
                    {task.project?.projectCode ?? "-"}
                  </td>
                  <td className="px-4 py-3 text-gray-600">
                    {task.assignedEmployee
                      ? `${task.assignedEmployee.firstName} ${task.assignedEmployee.lastName}`
                      : "-"}
                  </td>
                  <td className="px-4 py-3">
                    {task.taskStatus ? <StatusBadge status={task.taskStatus} /> : "-"}
                  </td>
                  <td className="px-4 py-3 text-gray-500 text-xs">{task.assignedDate ?? "-"}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <Button variant="secondary" size="sm" onClick={() => openEdit(task)}>
                        Düzenle
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => task.id !== undefined && setDeleteConfirm(task.id)}
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
        title={modal.task ? "Görev Düzenle" : "Yeni Görev"}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Konu" {...register("taskSubject")} error={errors.taskSubject?.message} />
          <Input label="Detay" {...register("taskDetail")} />
          <Select
            label="Proje"
            {...register("projectCode")}
            options={projects.map((p) => ({ value: p.projectCode, label: p.projectName }))}
            placeholder="Seçiniz"
          />
          <Select
            label="Çalışan"
            {...register("employeeUserName")}
            options={employees.map((e) => ({
              value: e.userName,
              label: `${e.firstName} ${e.lastName}`,
            }))}
            placeholder="Seçiniz"
          />
          {modal.task && (
            <Select label="Durum" {...register("taskStatus")} options={STATUS_OPTIONS} placeholder="Seçiniz" />
          )}
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="secondary" onClick={() => setModal({ open: false })}>
              İptal
            </Button>
            <Button type="submit" loading={createTask.isPending || updateTask.isPending}>
              Kaydet
            </Button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={deleteConfirm !== null}
        onClose={() => setDeleteConfirm(null)}
        title="Görevi Sil"
      >
        <p className="text-gray-600 mb-6">Bu görevi silmek istediğinizden emin misiniz?</p>
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
            İptal
          </Button>
          <Button
            variant="danger"
            loading={deleteTask.isPending}
            onClick={async () => {
              if (deleteConfirm !== null) {
                await deleteTask.mutateAsync(deleteConfirm);
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
