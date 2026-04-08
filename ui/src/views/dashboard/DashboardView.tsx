"use client";
import dynamic from "next/dynamic";
import { useUsers } from "@/hooks/useUsers";
import { useProjects } from "@/hooks/useProjects";
import { useTasks } from "@/hooks/useTasks";
import type { Status } from "@/types";

const Chart = dynamic(() => import("react-apexcharts"), { ssr: false });

function KpiCard({ label, value, color }: { label: string; value: number; color: string }) {
  return (
    <div className={`rounded-xl p-6 text-white ${color}`}>
      <p className="text-sm opacity-90">{label}</p>
      <p className="text-3xl font-bold mt-1">{value}</p>
    </div>
  );
}

export function DashboardView() {
  const { data: users = [], isLoading: loadingUsers } = useUsers();
  const { data: projects = [], isLoading: loadingProjects } = useProjects();
  const { data: tasks = [], isLoading: loadingTasks } = useTasks();

  const activeProjects = projects.filter((p) => p.projectStatus !== "COMPLETE").length;
  const pendingTasks = tasks.filter((t) => t.taskStatus === "OPEN" || t.taskStatus === "IN_PROGRESS").length;
  const completedTasks = tasks.filter((t) => t.taskStatus === "COMPLETE").length;

  const statusCounts = (["OPEN", "IN_PROGRESS", "UAT_TEST", "COMPLETE"] as Status[]).map((s) => ({
    name: s === "OPEN" ? "Açık" : s === "IN_PROGRESS" ? "Devam" : s === "UAT_TEST" ? "UAT" : "Tamamlandı",
    count: tasks.filter((t) => t.taskStatus === s).length,
  }));

  const projectStatusCounts = (["OPEN", "IN_PROGRESS", "UAT_TEST", "COMPLETE"] as Status[]).map(
    (s) => projects.filter((p) => p.projectStatus === s).length
  );

  const isLoading = loadingUsers || loadingProjects || loadingTasks;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-500">
        Yükleniyor...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard label="Toplam Kullanıcı" value={users.length} color="bg-indigo-500" />
        <KpiCard label="Aktif Proje" value={activeProjects} color="bg-blue-500" />
        <KpiCard label="Bekleyen Görev" value={pendingTasks} color="bg-amber-500" />
        <KpiCard label="Tamamlanan Görev" value={completedTasks} color="bg-green-500" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl p-6 border border-gray-200">
          <h3 className="text-base font-semibold text-gray-800 mb-4">Görev Dağılımı</h3>
          <Chart
            type="donut"
            height={280}
            series={statusCounts.map((s) => s.count)}
            options={{
              labels: statusCounts.map((s) => s.name),
              colors: ["#6366f1", "#f59e0b", "#8b5cf6", "#10b981"],
              legend: { position: "bottom" },
              plotOptions: { pie: { donut: { size: "65%" } } },
            }}
          />
        </div>

        <div className="bg-white rounded-xl p-6 border border-gray-200">
          <h3 className="text-base font-semibold text-gray-800 mb-4">Proje Durumları</h3>
          <Chart
            type="bar"
            height={280}
            series={[{ name: "Proje Sayısı", data: projectStatusCounts }]}
            options={{
              chart: { toolbar: { show: false } },
              xaxis: { categories: ["Açık", "Devam", "UAT", "Tamamlandı"] },
              colors: ["#6366f1"],
              plotOptions: { bar: { borderRadius: 6, columnWidth: "50%" } },
              dataLabels: { enabled: false },
            }}
          />
        </div>
      </div>
    </div>
  );
}
