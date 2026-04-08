import { ProjectsView } from "@/views/projects/ProjectsView";

export default function ProjectsPage() {
  return (
    <div className="space-y-6 pt-6">
      <h1 className="text-2xl font-bold text-gray-900">Projeler</h1>
      <ProjectsView />
    </div>
  );
}
