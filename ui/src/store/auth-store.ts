"use client";
import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { AuthResponse } from "@/types";

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userName: string | null;
  setTokens: (tokens: AuthResponse, userName: string) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      refreshToken: null,
      userName: null,
      setTokens: (tokens, userName) =>
        set({ accessToken: tokens.accessToken, refreshToken: tokens.refreshToken, userName }),
      logout: () => set({ accessToken: null, refreshToken: null, userName: null }),
      isAuthenticated: () => !!get().accessToken,
    }),
    { name: "auth-storage" }
  )
);
