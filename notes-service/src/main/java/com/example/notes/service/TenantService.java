package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.Tenant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;

    public Page<Tenant> find(String query, Pageable pageable) {
        return resourceRepository.find(new BasicQuery(query).with(pageable), pageable, Tenant.class);
    }

    public Tenant get(String id) {
        return resourceRepository.find(new BasicQuery(String.format("{id : '%s'}", id)), Tenant.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));
    }

    @Transactional
    public Tenant create(Tenant note) {
        return resourceRepository.create(note);
    }

    @Transactional
    public Tenant update(Tenant note) {
        get(note.getId());
        return resourceRepository.update(note);
    }

    @Transactional
    public void delete(String id) {
        resourceRepository.delete(get(id).getId(), Tenant.class);
    }

}
