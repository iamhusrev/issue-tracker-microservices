"use client";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from "@/hooks/useUsers";
import { Modal } from "@/components/ui/Modal";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import type { UserDTO } from "@/types";

const schema = z.object({
  firstName: z.string().min(1, "Ad zorunlu"),
  lastName: z.string().min(1, "Soyad zorunlu"),
  userName: z.string().min(1, "Kullanıcı adı zorunlu"),
  passWord: z.string().optional(),
  phone: z.string().optional(),
  gender: z.enum(["MALE", "FEMALE"]).optional(),
  enabled: z.boolean().optional(),
});

type FormValues = z.infer<typeof schema>;

export function UsersView() {
  const { data: users = [], isLoading } = useUsers();
  const createUser = useCreateUser();
  const updateUser = useUpdateUser();
  const deleteUser = useDeleteUser();

  const [modal, setModal] = useState<{ open: boolean; user?: UserDTO }>({ open: false });
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);
  const [search, setSearch] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const openCreate = () => {
    reset({});
    setModal({ open: true });
  };

  const openEdit = (user: UserDTO) => {
    reset({
      firstName: user.firstName,
      lastName: user.lastName,
      userName: user.userName,
      passWord: "",
      phone: user.phone ?? undefined,
      gender: user.gender ?? undefined,
      enabled: user.enabled,
    });
    setModal({ open: true, user });
  };

  const onSubmit = async (values: FormValues) => {
    const payload: UserDTO = { ...values };
    if (modal.user) {
      await updateUser.mutateAsync({ ...modal.user, ...payload });
    } else {
      await createUser.mutateAsync(payload);
    }
    setModal({ open: false });
  };

  const filtered = users.filter(
    (u) =>
      u.firstName?.toLowerCase().includes(search.toLowerCase()) ||
      u.lastName?.toLowerCase().includes(search.toLowerCase()) ||
      u.userName?.toLowerCase().includes(search.toLowerCase())
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
        <Button onClick={openCreate}>+ Yeni Kullanıcı</Button>
      </div>

      {isLoading ? (
        <p className="text-gray-500 text-sm">Yükleniyor...</p>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="text-left px-4 py-3">Ad Soyad</th>
                <th className="text-left px-4 py-3">Kullanıcı Adı</th>
                <th className="text-left px-4 py-3">Rol</th>
                <th className="text-left px-4 py-3">Durum</th>
                <th className="text-left px-4 py-3">İşlemler</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((user) => (
                <tr key={user.userName} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-900">
                    {user.firstName} {user.lastName}
                  </td>
                  <td className="px-4 py-3 text-gray-600">{user.userName}</td>
                  <td className="px-4 py-3 text-gray-600">{user.role?.description ?? "-"}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                        user.enabled ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                      }`}
                    >
                      {user.enabled ? "Aktif" : "Pasif"}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <Button variant="secondary" size="sm" onClick={() => openEdit(user)}>
                        Düzenle
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => setDeleteConfirm(user.userName)}
                      >
                        Sil
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-gray-400">
                    Veri bulunamadı
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Create / Edit Modal */}
      <Modal
        isOpen={modal.open}
        onClose={() => setModal({ open: false })}
        title={modal.user ? "Kullanıcı Düzenle" : "Yeni Kullanıcı"}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="Ad" {...register("firstName")} error={errors.firstName?.message} />
            <Input label="Soyad" {...register("lastName")} error={errors.lastName?.message} />
          </div>
          <Input
            label="Kullanıcı Adı"
            {...register("userName")}
            error={errors.userName?.message}
            readOnly={!!modal.user}
          />
          <Input
            label={modal.user ? "Yeni Şifre (değiştirmek için)" : "Şifre"}
            type="password"
            {...register("passWord")}
            error={errors.passWord?.message}
          />
          <Input label="Telefon" {...register("phone")} />
          <Select
            label="Cinsiyet"
            {...register("gender")}
            options={[
              { value: "MALE", label: "Erkek" },
              { value: "FEMALE", label: "Kadın" },
            ]}
            placeholder="Seçiniz"
          />
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="secondary" onClick={() => setModal({ open: false })}>
              İptal
            </Button>
            <Button
              type="submit"
              loading={createUser.isPending || updateUser.isPending}
            >
              Kaydet
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirm */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Kullanıcıyı Sil"
      >
        <p className="text-gray-600 mb-6">Bu kullanıcıyı silmek istediğinizden emin misiniz?</p>
        <div className="flex justify-end gap-2">
          <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
            İptal
          </Button>
          <Button
            variant="danger"
            loading={deleteUser.isPending}
            onClick={async () => {
              if (deleteConfirm) {
                await deleteUser.mutateAsync(deleteConfirm);
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
