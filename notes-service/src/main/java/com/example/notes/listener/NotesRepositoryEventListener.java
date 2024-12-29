package com.example.notes.listener;

import com.example.notes.repository.NoteRepository;
import com.example.notes.resource.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotesRepositoryEventListener extends AbstractRepositoryEventListener<Note> {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onBeforeCreate(Note entity) {
        entity.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
        entity.setCollaborators(Set.of(entity.getOwner()));
    }

    @Override
    public void onBeforeSave(Note entity) {
        String currentCollaborator = SecurityContextHolder.getContext().getAuthentication().getName();
        Note oldState = noteRepository.findById(entity.getId()).orElseThrow(() -> new IllegalStateException("Note not found"));
        if (!oldState.getCollaborators().contains(currentCollaborator)) {
            throw new IllegalStateException("You can't update a note that you are not a collaborator");
        }
        if (!oldState.getCollaborators().equals(entity.getCollaborators()) && !oldState.getOwner().equals(currentCollaborator)) {
            throw new IllegalStateException("Only the owner can change collaborators");
        }
        if (!entity.getCollaborators().contains(oldState.getOwner())) {
            throw new IllegalStateException("Owner must be a collaborator");
        }
        redisTemplate.convertAndSend("notes", entity);
    }

}