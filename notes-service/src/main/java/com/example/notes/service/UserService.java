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

/**
 * Service for managing users.
 */
@Service
public class UserService extends BaseTenantService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceAuditEventService resourceAuditEventService;

    /**
     * Finds users.
     *
     * @param query query
     * @return users
     */
    public List<User> find(String query) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query);
        return resourceRepository.find(mongoQuery, User.class);
    }

    /**
     * Finds users.
     *
     * @param query    query
     * @param pageable pageable
     * @return users
     */
    public Page<User> find(String query, Pageable pageable) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query).with(pageable);
        return resourceRepository.find(mongoQuery, pageable, User.class);
    }

    /**
     * Gets a user by id.
     *
     * @param id id
     * @return user
     */
    public User get(String id) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{id : '%s'}", id));
        return resourceRepository.find(mongoQuery, User.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    /**
     * Gets a user by username.
     *
     * @param username username
     * @return user
     */
    public User getByUsername(String username) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{username : '%s'}", username));
        return resourceRepository.find(mongoQuery, User.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    /**
     * Creates a user.
     *
     * @param user user
     * @return created user
     */
    //@Transactional
    public User create(User user) {
        resourceAuditEventService.handleBeforeCreate(user);
        return resourceRepository.create(user);
    }

    /**
     * Updates a user.
     *
     * @param user user
     * @return updated user
     */
    //@Transactional
    public User update(User user) {
        get(user.getId());
        resourceAuditEventService.handleBeforeUpdate(user);
        return resourceRepository.update(user);
    }

    /**
     * Deletes a user.
     *
     * @param id id
     */
    //@Transactional
    public void delete(String id) {
        User user = get(id);
        resourceAuditEventService.handleBeforeDelete(user);
        resourceRepository.delete(user.getId(), User.class);
    }

}
