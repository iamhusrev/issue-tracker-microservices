"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/auth-store";
import { AppSidebar } from "@/layout/AppSidebar";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  useEffect(() => {
    if (!isAuthenticated()) {
      router.replace("/login");
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated()) return null;

  return (
    <div className="flex min-h-screen">
      <AppSidebar />
      <main className="flex-1 ml-64 min-h-screen">
        <div className="pt-4 px-6 pb-8">
          {children}
        </div>
      </main>
    </div>
  );
}
