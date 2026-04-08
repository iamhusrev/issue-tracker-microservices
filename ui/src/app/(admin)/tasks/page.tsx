import { TasksView } from "@/views/tasks/TasksView";

export default function TasksPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Görevler</h1>
      <TasksView />
    </div>
  );
}
