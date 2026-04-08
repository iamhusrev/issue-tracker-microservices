import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import tr from "@/messages/tr.json";
import en from "@/messages/en.json";

if (!i18n.isInitialized) {
  i18n.use(initReactI18next).init({
    resources: { tr: { translation: tr }, en: { translation: en } },
    lng: typeof window !== "undefined" ? (localStorage.getItem("lang") ?? "tr") : "tr",
    fallbackLng: "tr",
    interpolation: { escapeValue: false },
  });
}

export default i18n;
