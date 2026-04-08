import { ManagerProjectsView } from "@/views/manager/ManagerProjectsView";

export default function ManagerProjectsPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Projelerim</h1>
      <ManagerProjectsView />
    </div>
  );
}
