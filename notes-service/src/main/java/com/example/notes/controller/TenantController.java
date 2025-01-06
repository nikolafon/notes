package com.example.notes.controller;

import com.example.notes.resource.Tenant;
import com.example.notes.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing tenants.
 */
@RestController()
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;


    /**
     * Find tenants.
     *
     * @param query - query expression to filter by
     * @param pageable - page request
     * @return list of elements in requested page
     */
    @GetMapping
    private PagedModel<Tenant> find(@RequestParam(required = false) String query, Pageable pageable) {
        return new PagedModel<>(tenantService.find(query, pageable));
    }

    @GetMapping(path = "/{id}")
    private Tenant get(@PathVariable String id) {
        return tenantService.get(id);
    }

    /**
     * Create tenant.
     *
     * @param tenant - tenant
     * @return tenant
     */
    @PostMapping
    private Tenant create(@RequestBody Tenant tenant) {
        return tenantService.create(tenant);
    }
    /**
     * Update tenant.
     *
     * @param tenant - tenant
     * @param id - tenant id
     * @return tenant
     */
    @PutMapping(path = "/{id}")
    private Tenant update(@RequestBody Tenant tenant, @PathVariable String id) {
        tenant.setId(id);
        return tenantService.update(tenant);
    }
    /**
     * Delete tenant.
     *
     * @param id - tenant id
     */
    @RequestMapping(path = "/{id}")
    private void delete(@PathVariable String id) {
        tenantService.delete(id);
    }


}
