import type { Metadata } from "next";
import { Geist } from "next/font/google";
import "./globals.css";
import { ReactQueryProvider } from "@/provider/ReactQueryProvider";
import { SidebarProvider } from "@/provider/SidebarProvider";

const geist = Geist({ subsets: ["latin"], variable: "--font-geist-sans" });

export const metadata: Metadata = {
  title: "Issue Tracker",
  description: "Issue Tracker Microservices UI",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="tr">
      <body className={`${geist.variable} font-sans antialiased bg-gray-50`}>
        <ReactQueryProvider>
          <SidebarProvider>
            {children}
          </SidebarProvider>
        </ReactQueryProvider>
      </body>
    </html>
  );
}
