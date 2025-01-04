package com.example.notes.service;

import com.example.notes.tenant.TenantHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class BaseTenantService {

    @Autowired
    protected TenantService tenantService;

    protected Query createQueryWithAdditionalTenantFilter(String queryString) {
        Query query = new BasicQuery(queryString);
        Criteria additionalCriteria = Criteria.where("tenantId").is(TenantHolder.getCurrentTenantId());
        query.addCriteria(additionalCriteria);
        return query;
    }

    protected void tenantExists(String tenantId) {
        tenantService.find(String.format("{tenantId:'%s'}", tenantId)).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));
    }
}
