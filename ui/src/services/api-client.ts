import axios from "axios";
import { API } from "@/utils/api-endpoints";

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: { "Content-Type": "application/json" },
});

// Attach USER_TOKEN on every request
apiClient.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const raw = localStorage.getItem("auth-storage");
    if (raw) {
      const { state } = JSON.parse(raw) as { state: { accessToken: string | null } };
      if (state?.accessToken) {
        config.headers["USER_TOKEN"] = state.accessToken;
      }
    }
  }
  return config;
});

let isRefreshing = false;
let failedQueue: Array<{ resolve: (v: unknown) => void; reject: (e: unknown) => void }> = [];

const processQueue = (error: unknown, token: string | null) => {
  failedQueue.forEach((prom) => (token ? prom.resolve(token) : prom.reject(error)));
  failedQueue = [];
};

// 401 → refresh → retry
apiClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;

    if (
      error.response?.status === 401 &&
      !original._retry &&
      !original.url?.includes("/auth/")
    ) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          original.headers["USER_TOKEN"] = token;
          return apiClient(original);
        });
      }

      original._retry = true;
      isRefreshing = true;

      try {
        const raw = localStorage.getItem("auth-storage");
        const { state } = JSON.parse(raw ?? "{}") as { state?: { refreshToken?: string } };
        const refreshToken = state?.refreshToken;

        if (!refreshToken) throw new Error("No refresh token");

        const { data } = await axios.post(API.auth.refresh, { refreshToken });
        const newToken = data.accessToken;

        const stored = JSON.parse(localStorage.getItem("auth-storage") ?? "{}");
        stored.state.accessToken = newToken;
        stored.state.refreshToken = data.refreshToken;
        localStorage.setItem("auth-storage", JSON.stringify(stored));

        apiClient.defaults.headers["USER_TOKEN"] = newToken;
        processQueue(null, newToken);

        original.headers["USER_TOKEN"] = newToken;
        return apiClient(original);
      } catch (err) {
        processQueue(err, null);
        localStorage.removeItem("auth-storage");
        window.location.href = "/login";
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
