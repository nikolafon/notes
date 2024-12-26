package com.example.notes.controller;

import com.example.notes.service.NotesEventStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotesEventStreamController {

    @Autowired
    private NotesEventStreamService notesEventStreamService;

    @GetMapping(path = "/notes/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private SseEmitter registerNotesEventStream(@PathVariable String id) {
        return notesEventStreamService.registerEventStream(id);
    }

}
