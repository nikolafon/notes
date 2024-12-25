package com.example.notes.listener;

import com.example.notes.entity.BaseEntity;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class AuditLogEventListener {

    @EventListener
    public void handleBeforeCreate(BeforeCreateEvent event) {
        if (event.getSource() instanceof BaseEntity entity) {
            validateAuthentication();
            entity.setCreatedOn(ZonedDateTime.now(ZoneId.of("UTC")));
            entity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    @EventListener
    public void handleBeforeSave(BeforeSaveEvent event) {
        if (event.getSource() instanceof BaseEntity entity) {
            validateAuthentication();
            entity.setUpdatedOn(ZonedDateTime.now(ZoneId.of("UTC")));
            entity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    private void validateAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new IllegalStateException("Authentication is required");
        }
    }
}
