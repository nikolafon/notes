package com.example.notes.tenant;

import com.example.notes.resource.Tenant;

public class TenantHolder {

    private final static ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();

    public static Tenant getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(Tenant tenant) {
        currentTenant.set(tenant);
    }
}
