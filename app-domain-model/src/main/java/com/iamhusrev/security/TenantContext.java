package com.iamhusrev.security;

import lombok.*;

public final class TenantContext {

    private TenantContext() {}

    private static final ThreadLocal<TenantInfo> CONTEXT = new ThreadLocal<>();

    public static void set(TenantInfo info) {
        CONTEXT.set(info);
    }

    public static TenantInfo get() {
        return CONTEXT.get();
    }

    public static Long getOrganizationId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getOrganizationId() : null;
    }

    public static Long getUserId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getUserId() : null;
    }

    public static String getUserName() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getUserName() : null;
    }

    public static String getRole() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.getRole() : null;
    }

    public static void clear() {
        CONTEXT.remove();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TenantInfo {
        private Long userId;
        private String userName;
        private Long organizationId;
        private String role;
    }
}
