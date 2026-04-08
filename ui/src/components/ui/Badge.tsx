import type { Status } from "@/types";

const colorMap: Record<Status, string> = {
  OPEN: "bg-blue-100 text-blue-800",
  IN_PROGRESS: "bg-yellow-100 text-yellow-800",
  UAT_TEST: "bg-purple-100 text-purple-800",
  COMPLETE: "bg-green-100 text-green-800",
};

const labelMap: Record<Status, string> = {
  OPEN: "Açık",
  IN_PROGRESS: "Devam Ediyor",
  UAT_TEST: "UAT Test",
  COMPLETE: "Tamamlandı",
};

export function StatusBadge({ status }: { status: Status }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${colorMap[status]}`}>
      {labelMap[status]}
    </span>
  );
}
