"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useSidebar } from "@/provider/SidebarProvider";
import { useAuthStore } from "@/store/auth-store";
import { useRouter } from "next/navigation";

const navItems = [
  { label: "Dashboard", href: "/dashboard", icon: "📊" },
  { section: "Yönetim" },
  { label: "Kullanıcılar", href: "/users", icon: "👥" },
  { label: "Projeler", href: "/projects", icon: "📁" },
  { label: "Görevler", href: "/tasks", icon: "✅" },
  { section: "Yönetici" },
  { label: "Projelerim", href: "/manager/projects", icon: "🗂️" },
  { section: "Çalışan" },
  { label: "Görevlerim", href: "/employee/my-tasks", icon: "📋" },
  { label: "Arşiv", href: "/employee/archive", icon: "🗃️" },
];

export function AppSidebar() {
  const { isOpen } = useSidebar();
  const pathname = usePathname();
  const logout = useAuthStore((s) => s.logout);
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  if (!isOpen) return null;

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-white border-r border-gray-200 flex flex-col z-40">
      <div className="p-6 border-b">
        <h1 className="text-xl font-bold text-indigo-600">Issue Tracker</h1>
      </div>

      <nav className="flex-1 overflow-y-auto p-4 space-y-1">
        {navItems.map((item, i) => {
          if ("section" in item) {
            return (
              <p key={i} className="text-xs font-semibold text-gray-400 uppercase tracking-wider pt-4 pb-1 px-2">
                {item.section}
              </p>
            );
          }
          const active = pathname === item.href || pathname.startsWith(item.href + "/");
          return (
            <Link
              key={item.href}
              href={item.href!}
              className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${
                active
                  ? "bg-indigo-50 text-indigo-700 font-medium"
                  : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
              }`}
            >
              <span>{item.icon}</span>
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t">
        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-600 hover:bg-gray-50 rounded-lg transition-colors"
        >
          <span>🚪</span>
          Çıkış Yap
        </button>
      </div>
    </aside>
  );
}
