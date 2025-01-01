package com.example.notes.controller;

import com.example.notes.resource.ResourceAudit;
import com.example.notes.service.ResourceAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/resourceaudit")
public class ResourceAuditController {

    @Autowired
    private ResourceAuditService resourceAuditService;

    @GetMapping
    private Page<ResourceAudit> find(@RequestParam(required = false) String query, Pageable pageable) {
        return resourceAuditService.find(query, pageable);
    }

    @GetMapping(path = "/{id}")
    private ResourceAudit get(@PathVariable String id) {
        return resourceAuditService.get(id);
    }

}
