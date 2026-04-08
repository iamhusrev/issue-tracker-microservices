import { DashboardView } from "@/views/dashboard/DashboardView";

export default function DashboardPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
      <DashboardView />
    </div>
  );
}
