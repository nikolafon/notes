package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseTenantService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;

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

    @Transactional
    public User create(User note) {
        return resourceRepository.create(note);
    }

    @Transactional
    public User update(User note) {
        get(note.getId());
        return resourceRepository.update(note);
    }

    @Transactional
    public void delete(String id) {
        resourceRepository.delete(get(id).getId(), User.class);
    }

}
