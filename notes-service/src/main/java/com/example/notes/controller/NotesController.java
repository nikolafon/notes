package com.example.notes.controller;

import com.example.notes.resource.Note;
import com.example.notes.service.NotesEventStreamService;
import com.example.notes.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    private NotesEventStreamService notesEventStreamService;
    @Autowired
    private NotesService notesService;

    @RequestMapping(path = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private SseEmitter getNotesEventStream(@PathVariable String id) {
        return notesEventStreamService.registerEventStream(id);
    }

    @GetMapping
    private PagedModel<Note> find(@RequestParam(required = false) String query, Pageable pageable) {
        return new PagedModel<>(notesService.find(query, pageable));
    }

    @GetMapping(path = "/{id}")
    private Note get(@PathVariable String id) {
        return notesService.get(id);
    }

    @PostMapping
    private Note create(@RequestBody Note note) {
        return notesService.create(note);
    }

    @PutMapping(path = "/{id}")
    private Note update(@RequestBody Note note, @PathVariable String id) {
        note.setId(id);
        return notesService.update(note);
    }

    @RequestMapping(path = "/{id}")
    private void delete(@PathVariable String id) {
        notesService.delete(id);
    }


}
