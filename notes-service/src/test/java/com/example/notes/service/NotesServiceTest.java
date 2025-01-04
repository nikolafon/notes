package com.example.notes.service;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotesServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private NotesService notesService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("admin");
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFind() {
        String query = "{ }";
        Pageable pageable = mock(Pageable.class);
        Page<Note> expectedPage = mock(Page.class);

        when(resourceRepository.find(any(Query.class), eq(pageable), eq(Note.class))).thenReturn(expectedPage);

        Page<Note> result = notesService.find(query, pageable);

        assertEquals(expectedPage, result);
        verify(resourceRepository).find(any(Query.class), eq(pageable), eq(Note.class));
    }

    @Test
    void testGet() {
        String id = "1";
        Note expectedNote = new Note();
        expectedNote.setId(id);

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(expectedNote));

        Note result = notesService.get(id);

        assertEquals(expectedNote, result);
        verify(resourceRepository).find(any(Query.class), eq(Note.class));
    }

    @Test
    void testGet_ThrowsExceptionWhenNoteNotFound() {
        String id = "1";

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalStateException.class, () -> notesService.get(id));

        assertEquals("Note not found", exception.getMessage());
    }

    @Test
    void testCreate() {
        Note note = new Note();
        Note expectedNote = new Note();
        note.setOwner("admin");
        note.setTenantId("tenant-id");

        when(resourceRepository.create(any(Note.class))).thenReturn(expectedNote);

        Note result = notesService.create(note);

        assertEquals(expectedNote, result);
        verify(resourceRepository).create(note);
    }

    @Test
    void testUpdate() {
        Note note = new Note();
        note.setId("1");
        note.setOwner("admin");
        Note oldState = new Note();
        oldState.setId("1");
        oldState.setOwner("admin");
        oldState.setCollaborators(Collections.singleton("another-user"));

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(oldState));
        when(resourceRepository.update(any(Note.class))).thenReturn(note);

        Note result = notesService.update(note);

        assertEquals(note, result);
        verify(redisTemplate).convertAndSend("notes", note);
        verify(resourceRepository).update(note);
    }

    @Test
    void testUpdate_ownerChange_throwException() {
        Note note = new Note();
        note.setId("1");
        note.setOwner("another-user");
        Note oldState = new Note();
        oldState.setId("1");
        oldState.setOwner("admin");

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(oldState));
        assertThrows(IllegalStateException.class, () -> notesService.update(note));
    }

    @Test
    void testUpdate_nonCollaboratorTryToUpdate_throwException() {
        Note note = new Note();
        note.setId("1");
        note.setOwner("another-user");
        note.setCollaborators(Collections.singleton("second-user"));
        Note oldState = new Note();
        oldState.setId("1");
        oldState.setOwner("admin");
        oldState.setCollaborators(Collections.singleton("second-user"));

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(oldState));
        assertThrows(IllegalStateException.class, () -> notesService.update(note));
    }

    @Test
    void testUpdate_nonOwnerTryToChangeCollaborators_throwException() {
        Note note = new Note();
        note.setId("1");
        note.setOwner("admin");
        note.setCollaborators(Collections.singleton("second-user"));
        Note oldState = new Note();
        oldState.setId("1");
        oldState.setOwner("another-owner");
        oldState.setCollaborators(Collections.singleton("third-user"));

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(oldState));
        assertThrows(IllegalStateException.class, () -> notesService.update(note));
    }

    @Test
    void testDelete() {
        String id = "1";
        Note note = new Note();
        note.setId(id);

        when(resourceRepository.find(any(Query.class), eq(Note.class))).thenReturn(Collections.singletonList(note));

        notesService.delete(id);

        verify(resourceRepository).delete(id, Note.class);
    }
}
