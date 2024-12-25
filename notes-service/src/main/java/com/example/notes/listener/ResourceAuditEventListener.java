package com.example.notes.listener;

import com.example.notes.resource.BaseResource;
import com.example.notes.resource.ResourceAction;
import com.example.notes.resource.ResourceAudit;
import com.example.notes.repository.ResourceAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ResourceAuditEventListener {

    @Autowired
    private ResourceAuditRepository resourceAuditRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener
    public void handleBeforeCreate(BeforeCreateEvent event) {
        if (event.getSource() instanceof BaseResource entity) {
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
            resourceAuditRepository.save(resourceAudit);
        }
    }

    @EventListener
    public void handleBeforeSave(BeforeSaveEvent event) {
        if (event.getSource() instanceof BaseResource entity && entity.getId() != null) {
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
            BaseResource oldState = mongoTemplate.findOne(Query.query(Criteria.expr(MongoExpression.create(String.format("{ id: '%s' }", entity.getId())))), entity.getClass());
            resourceAudit.setOldState(oldState);
            resourceAudit.setNewState(entity);
            resourceAuditRepository.save(resourceAudit);
        }
    }

    @EventListener
    public void handleBeforeDelete(BeforeDeleteEvent event) {
        if (event.getSource() instanceof BaseResource entity) {
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
            resourceAuditRepository.save(resourceAudit);
        }
    }

    private void validateAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new IllegalStateException("Authentication is required");
        }
    }
}
