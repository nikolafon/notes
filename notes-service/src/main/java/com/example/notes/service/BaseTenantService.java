package com.example.notes.service;

import com.example.notes.tenant.TenantHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Base tenant service.
 */
public class BaseTenantService {

    @Autowired
    protected TenantService tenantService;

    protected Query createQueryWithAdditionalTenantFilter(String queryString) {
        Query query = new BasicQuery(queryString);
        if (TenantHolder.isCurrentTenantPresent()) {
            Criteria additionalCriteria = Criteria.where("tenantId").is(TenantHolder.getCurrentTenantId());
            query.addCriteria(additionalCriteria);
        }
        return query;
    }

}
