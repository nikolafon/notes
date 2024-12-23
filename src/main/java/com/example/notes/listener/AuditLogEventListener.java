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
            entity.setCreatedOn(ZonedDateTime.now(ZoneId.of("UTC")));
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                entity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }
    }

    @EventListener
    public void handleBeforeSave(BeforeSaveEvent event) {
        if (event.getSource() instanceof BaseEntity entity) {
            entity.setUpdatedOn(ZonedDateTime.now(ZoneId.of("UTC")));
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                entity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }
    }
}
