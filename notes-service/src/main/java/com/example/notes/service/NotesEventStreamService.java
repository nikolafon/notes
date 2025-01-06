package com.example.notes.service;

import com.example.notes.resource.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class NotesEventStreamService {

    @Autowired
    private NotesService notesService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    private ObjectMapper objectMapper;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, List<SseEmitter>> registeredSseEmitters = new ConcurrentHashMap<>();

    private RedisMessageListenerContainer redisMessageListenerContainer;

    @PostConstruct
    public void init() {
        redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory);
        redisMessageListenerContainer.addMessageListener((message, pattern) ->
                {
                    try {
                        sendNoteUpdate(objectMapper.readValue(message.getBody(), Note.class));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
                , ChannelTopic.of("notes"));
        redisMessageListenerContainer.afterPropertiesSet();
        redisMessageListenerContainer.start();
    }

    @PreDestroy
    public void destroy() throws Exception {
        redisMessageListenerContainer.destroy();
    }

    public SseEmitter registerEventStream(String noteId) {
        Note note = notesService.get(noteId);
        if (!note.getCollaborators().contains(SecurityContextHolder.getContext().getAuthentication().getName()) &&
                !note.getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new IllegalArgumentException("You are not an owner or collaborator on this note");
        }
        SseEmitter sseEmitter = createSseEmitter(noteId);
        registeredSseEmitters.computeIfAbsent(noteId, k -> new ArrayList<>()).add(sseEmitter);
        return sseEmitter;
    }

    public void sendNoteUpdate(Note note) {
        List<SseEmitter> sseEmitters = registeredSseEmitters.get(note.getId());
        if (sseEmitters != null) {
            sseEmitters.forEach(eventStream -> {
                executor.execute(() -> {
                    try {
                        eventStream.send(note);
                    } catch (IOException ignored) {

                    }
                });
            });
        }
    }

    private SseEmitter createSseEmitter(String noteId) {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitter.onError((error) ->
                cleanupStreams(noteId, sseEmitter)
        );
        sseEmitter.onCompletion(() ->
                cleanupStreams(noteId, sseEmitter)
        );
        sseEmitter.onTimeout(() ->
                cleanupStreams(noteId, sseEmitter)
        );
        return sseEmitter;
    }

    private void cleanupStreams(String noteId, SseEmitter sseEmitter) {
        registeredSseEmitters.get(noteId).remove(sseEmitter);
        if (registeredSseEmitters.get(noteId).isEmpty()) {
            registeredSseEmitters.remove(noteId);
        }
    }

}
