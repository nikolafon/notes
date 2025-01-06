package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.ResourceAudit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Service for managing resource audits.
 */
@Service
public class ResourceAuditService extends BaseTenantService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * Finds resource audits.
     *
     * @param query    query
     * @param pageable pageable
     * @return resource audits
     */
    public Page<ResourceAudit> find(String query, Pageable pageable) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query);
        return resourceRepository.find(mongoQuery, pageable, ResourceAudit.class);
    }

    /**
     * Gets a resource audit by id.
     *
     * @param id id
     * @return resource audit
     */
    public ResourceAudit get(String id) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{id : '%s'}", id));
        return resourceRepository.find(mongoQuery, ResourceAudit.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ResourceAudit not found"));
    }
}
