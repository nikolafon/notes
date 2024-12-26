package com.example.notes.tenant;

import org.springframework.stereotype.Component;

@Component
public class TenantCollectionNameResolver {
    public String getTenantCollectionName(String collectionName) {
        String tenantId = TenantHolder.getCurrentTenantId();
        if (tenantId != null) {
            return tenantId + "_" + collectionName;
        }
        return collectionName;
    }
}
