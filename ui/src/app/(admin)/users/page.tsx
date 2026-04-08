import { UsersView } from "@/views/users/UsersView";

export default function UsersPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Kullanıcılar</h1>
      <UsersView />
    </div>
  );
}
