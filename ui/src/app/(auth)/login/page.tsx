"use client";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { authService } from "@/services/auth-service";
import { useAuthStore } from "@/store/auth-store";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";

const schema = z.object({
  userName: z.string().min(1, "Kullanıcı adı zorunlu"),
  passWord: z.string().min(1, "Şifre zorunlu"),
});
type FormValues = z.infer<typeof schema>;

export default function LoginPage() {
  const router = useRouter();
  const setTokens = useAuthStore((s) => s.setTokens);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (values: FormValues) => {
    setError("");
    setLoading(true);
    try {
      const res = await authService.login(values);
      setTokens(res, values.userName);
      router.push("/dashboard");
    } catch {
      setError("Geçersiz kullanıcı adı veya şifre");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-blue-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-8">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Issue Tracker</h1>
          <p className="text-gray-500 text-sm mt-1">Hesabınıza giriş yapın</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Kullanıcı Adı"
            {...register("userName")}
            error={errors.userName?.message}
            placeholder="kullanici.adi"
          />
          <Input
            label="Şifre"
            type="password"
            {...register("passWord")}
            error={errors.passWord?.message}
            placeholder="••••••••"
          />

          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <Button type="submit" loading={loading} className="w-full justify-center">
            Giriş Yap
          </Button>
        </form>
      </div>
    </div>
  );
}
