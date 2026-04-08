"use client";
import { useSidebar } from "@/provider/SidebarProvider";
import { useAuthStore } from "@/store/auth-store";

export function AppHeader({ title }: { title?: string }) {
  const { toggle } = useSidebar();
  const userName = useAuthStore((s) => s.userName);

  return (
    <header className="fixed top-0 left-64 right-0 h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6 z-30">
      <div className="flex items-center gap-3">
        <button
          onClick={toggle}
          className="text-gray-500 hover:text-gray-700 transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        {title && <h2 className="text-lg font-semibold text-gray-800">{title}</h2>}
      </div>
      <div className="flex items-center gap-2">
        <span className="text-sm text-gray-500">👤</span>
        <span className="text-sm font-medium text-gray-700">{userName}</span>
      </div>
    </header>
  );
}
