package com.example.notes.tenant;

import com.example.notes.resource.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantCollectionNameResolver {

    public String getTenantCollectionName(String collectionName) {
        Tenant tenant = TenantHolder.getCurrentTenant();
        if (tenant != null) {
            return tenant.getTenantId() + "_" + collectionName;
        }
        return collectionName;
    }
}
