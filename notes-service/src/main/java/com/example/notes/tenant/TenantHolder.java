package com.example.notes.tenant;

import com.example.notes.resource.Tenant;

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
