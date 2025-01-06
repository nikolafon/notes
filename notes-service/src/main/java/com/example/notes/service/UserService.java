package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseTenantService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceAuditEventService resourceAuditEventService;

    public List<User> find(String query) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query);
        return resourceRepository.find(mongoQuery, User.class);
    }

    public Page<User> find(String query, Pageable pageable) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query).with(pageable);
        return resourceRepository.find(mongoQuery, pageable, User.class);
    }

    public User get(String id) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{id : '%s'}", id));
        return resourceRepository.find(mongoQuery, User.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public User getByUsername(String username) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{username : '%s'}", username));
        return resourceRepository.find(mongoQuery, User.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    //@Transactional
    public User create(User user) {
        resourceAuditEventService.handleBeforeCreate(user);
        return resourceRepository.create(user);
    }

    //@Transactional
    public User update(User user) {
        get(user.getId());
        resourceAuditEventService.handleBeforeUpdate(user);
        return resourceRepository.update(user);
    }

    //@Transactional
    public void delete(String id) {
        User user = get(id);
        resourceAuditEventService.handleBeforeDelete(user);
        resourceRepository.delete(user.getId(), User.class);
    }

}
