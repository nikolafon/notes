package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.Note;
import com.example.notes.tenant.TenantHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NotesService extends BaseTenantService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceAuditEventService resourceAuditEventService;


    public Page<Note> find(String query, Pageable pageable) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(query);
        addNoteFilter(mongoQuery);
        return resourceRepository.find(mongoQuery, pageable, Note.class);
    }

    public Note get(String id) {
        Query mongoQuery = createQueryWithAdditionalTenantFilter(String.format("{id : '%s'}", id));
        addNoteFilter(mongoQuery);
        return resourceRepository.find(mongoQuery, Note.class).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Note not found"));
    }

    //@Transactional
    public Note create(Note note) {
        note.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
        note.setTenantId(TenantHolder.getCurrentTenantId());
        resourceAuditEventService.handleBeforeCreate(note);
        return resourceRepository.create(note);
    }

    //@Transactional
    public Note update(Note note) {
        Note oldState = get(note.getId());
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!oldState.getCollaborators().equals(note.getCollaborators()) && !oldState.getOwner().equals(currentUser)) {
            throw new IllegalStateException("Only the owner can change collaborators");
        }
        if (!oldState.getCollaborators().contains(currentUser) && !oldState.getOwner().equals(currentUser)) {
            throw new IllegalStateException("You can't update a note that you are not an owner or collaborator");
        }
        if (!oldState.getOwner().equals(note.getOwner())) {
            throw new IllegalStateException("Owner can't be changed");
        }
        note.getCollaborators().forEach(username -> userService.getByUsername(username));
        note.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());

//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                redisTemplate.convertAndSend("notes", note);
//            }
//        });
        redisTemplate.convertAndSend("notes", note);
        resourceAuditEventService.handleBeforeUpdate(note);
        return resourceRepository.update(note);
    }

    //@Transactional
    public void delete(String id) {
        resourceAuditEventService.handleBeforeDelete(get(id));
        resourceRepository.delete(get(id).getId(), Note.class);
    }

    private static void addNoteFilter(Query mongoQuery) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        mongoQuery.addCriteria(new Criteria().orOperator(Criteria.where(Note.Fields.owner).is(user), Criteria.where(Note.Fields.collaborators).is(user)));
    }

}
