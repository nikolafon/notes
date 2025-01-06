package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.BaseResource;
import com.example.notes.resource.ResourceAction;
import com.example.notes.resource.ResourceAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ResourceAuditEventService {

    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * Handles before create event.
     *
     * @param entity entity
     */
    public void handleBeforeCreate(BaseResource entity) {
        validateAuthentication();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        entity.setCreatedOn(now);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        entity.setCreatedBy(username);
        ResourceAudit resourceAudit = new ResourceAudit();
        resourceAudit.setEntityName(entity.getClass().getSimpleName());
        resourceAudit.setAction(ResourceAction.CREATE);
        resourceAudit.setActionedOn(now);
        resourceAudit.setActionedBy(username);
        resourceAudit.setNewState(entity);
        resourceRepository.create(resourceAudit);
    }
    /**
     * Handles before update event.
     *
     * @param entity entity
     */
    public void handleBeforeUpdate(BaseResource entity) {
        validateAuthentication();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        entity.setUpdatedOn(now);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        entity.setUpdatedBy(username);
        ResourceAudit resourceAudit = new ResourceAudit();
        resourceAudit.setEntityName(entity.getClass().getSimpleName());
        resourceAudit.setAction(ResourceAction.UPDATE);
        resourceAudit.setActionedOn(now);
        resourceAudit.setActionedBy(username);
        BaseResource oldState = resourceRepository.find(Query.query(Criteria.expr(MongoExpression.create(String.format("{ id: '%s' }", entity.getId())))), entity.getClass()).stream().findFirst().orElseThrow(() -> new IllegalStateException("Resource not found"));
        resourceAudit.setOldState(oldState);
        resourceAudit.setNewState(entity);
        resourceRepository.create(resourceAudit);
    }
    /**
     * Handles before delete event.
     *
     * @param entity entity
     */
    public void handleBeforeDelete(BaseResource entity) {
        validateAuthentication();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResourceAudit resourceAudit = new ResourceAudit();
        resourceAudit.setEntityName(entity.getClass().getSimpleName());
        resourceAudit.setAction(ResourceAction.DELETE);
        resourceAudit.setActionedOn(now);
        resourceAudit.setActionedBy(username);
        resourceAudit.setOldState(entity);
        resourceAudit.setNewState(null);
        resourceRepository.create(resourceAudit);
    }

    private void validateAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new IllegalStateException("Authentication is required");
        }
    }
}
