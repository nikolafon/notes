package com.example.notes.tenant;

/**
 * A simple holder for the current tenant of a request.
 */
public class TenantHolder {

    private final static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static String getCurrentTenantId() {
        return currentTenant.get();
    }

    public static boolean isCurrentTenantPresent() {
        return currentTenant.get() != null;
    }

    public static void setCurrentTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }
}
