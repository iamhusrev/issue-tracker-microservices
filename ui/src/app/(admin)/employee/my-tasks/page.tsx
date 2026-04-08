import { MyTasksView } from "@/views/employee/MyTasksView";

export default function MyTasksPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Görevlerim</h1>
      <MyTasksView />
    </div>
  );
}
