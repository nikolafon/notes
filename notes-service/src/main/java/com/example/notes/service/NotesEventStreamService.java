package com.example.notes.service;

import com.example.notes.repository.NoteRepository;
import com.example.notes.resource.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class NotesEventStreamService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    private ObjectMapper objectMapper;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, List<SseEmitter>> registeredSseEmitters = new ConcurrentHashMap<>();
    StreamMessageListenerContainer<String, ObjectRecord<String, Note>> container;

    StreamListener<String, ObjectRecord<String, Note>> streamListener = message ->
            sendNoteUpdate(message.getValue());

    @PostConstruct
    public void init() {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, Note>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofMillis(2000)).targetType(Note.class).build();
        StreamMessageListenerContainer<String, ObjectRecord<String, Note>> container = StreamMessageListenerContainer.create(connectionFactory, options);
        container.receive(StreamOffset.latest("notes"), streamListener);
        container.start();
    }

    @PreDestroy
    public void destroy() {
        container.stop();
    }

    public SseEmitter registerEventStream(String noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        if (!note.getCollaborators().contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new IllegalArgumentException("You are not a collaborator on this note");
        }
        SseEmitter sseEmitter = createSseEmitter(noteId);
        registeredSseEmitters.computeIfAbsent(noteId, k -> new ArrayList<>()).add(sseEmitter);
        return sseEmitter;
    }

    public void sendNoteUpdate(Note note) {
        if (note != null) {
            List<SseEmitter> sseEmitters = registeredSseEmitters.get(note.getId());
            if (sseEmitters != null) {
                sseEmitters.forEach(eventStream -> {
                    executor.execute(() -> {
                        try {
                            eventStream.send(note);
                        } catch (Exception e) {
                            eventStream.completeWithError(e);
                        }
                    });
                });
            }
        }
    }

    private SseEmitter createSseEmitter(String noteId) {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitter.onError((t) -> {
            registeredSseEmitters.get(noteId).remove(sseEmitter);
            if (registeredSseEmitters.get(noteId).isEmpty()) {
                registeredSseEmitters.remove(noteId);
            }
        });
        return sseEmitter;
    }

}
