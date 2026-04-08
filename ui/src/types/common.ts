export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  code?: number;
}

export type Gender = "MALE" | "FEMALE";

export type Status = "OPEN" | "IN_PROGRESS" | "UAT_TEST" | "COMPLETE";

export const STATUS_LABELS: Record<Status, string> = {
  OPEN: "Açık",
  IN_PROGRESS: "Devam Ediyor",
  UAT_TEST: "UAT Test",
  COMPLETE: "Tamamlandı",
};

export const GENDER_LABELS: Record<Gender, string> = {
  MALE: "Erkek",
  FEMALE: "Kadın",
};
