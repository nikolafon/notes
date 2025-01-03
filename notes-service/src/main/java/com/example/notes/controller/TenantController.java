package com.example.notes.controller;

import com.example.notes.resource.Tenant;
import com.example.notes.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;


    @GetMapping
    private PagedModel<Tenant> find(@RequestParam(required = false) String query, Pageable pageable) {
        return new PagedModel<>(tenantService.find(query, pageable));
    }

    @GetMapping(path = "/{id}")
    private Tenant get(@PathVariable String id) {
        return tenantService.get(id);
    }

    @PostMapping
    private Tenant create(@RequestBody Tenant tenant) {
        return tenantService.create(tenant);
    }

    @PutMapping(path = "/{id}")
    private Tenant update(@RequestBody Tenant tenant, @PathVariable String id) {
        tenant.setId(id);
        return tenantService.update(tenant);
    }

    @RequestMapping(path = "/{id}")
    private void delete(@PathVariable String id) {
        tenantService.delete(id);
    }


}
