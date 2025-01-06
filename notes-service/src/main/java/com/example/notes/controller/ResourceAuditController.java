package com.example.notes.controller;

import com.example.notes.resource.ResourceAudit;
import com.example.notes.service.ResourceAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing resource audits.
 */
@RestController()
@RequestMapping("/api/resourceaudit")
public class ResourceAuditController {

    @Autowired
    private ResourceAuditService resourceAuditService;

    /**
     * Find resource audits.
     *
     * @param query - query expression to filter by
     * @param pageable - page request
     * @return list of elements in requested page
     */
    @GetMapping
    private PagedModel<ResourceAudit> find(@RequestParam(required = false) String query, Pageable pageable) {
        return new PagedModel<>(resourceAuditService.find(query, pageable));
    }

    /**
     * Get resource audit by id.
     *
     * @param id - resource audit id
     * @return resource audit
     */
    @GetMapping(path = "/{id}")
    private ResourceAudit get(@PathVariable String id) {
        return resourceAuditService.get(id);
    }

}
