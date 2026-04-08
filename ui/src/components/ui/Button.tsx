import { ButtonHTMLAttributes } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "danger" | "secondary" | "ghost";
  size?: "sm" | "md";
  loading?: boolean;
}

const variantClasses = {
  primary: "bg-indigo-600 text-white hover:bg-indigo-700",
  danger: "bg-red-600 text-white hover:bg-red-700",
  secondary: "bg-gray-200 text-gray-800 hover:bg-gray-300",
  ghost: "text-gray-600 hover:bg-gray-100",
};

const sizeClasses = { sm: "px-3 py-1.5 text-sm", md: "px-4 py-2 text-sm" };

export function Button({
  variant = "primary",
  size = "md",
  loading,
  children,
  className = "",
  disabled,
  ...props
}: ButtonProps) {
  return (
    <button
      {...props}
      disabled={disabled || loading}
      className={`inline-flex items-center gap-2 font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
    >
      {loading && (
        <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
        </svg>
      )}
      {children}
    </button>
  );
}
